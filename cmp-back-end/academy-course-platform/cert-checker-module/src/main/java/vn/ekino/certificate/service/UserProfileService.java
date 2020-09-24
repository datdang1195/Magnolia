package vn.ekino.certificate.service;

import info.magnolia.cms.security.MgnlUserManager;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.User;
import info.magnolia.cms.security.UserManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.dam.jcr.AssetNodeTypes;
import info.magnolia.dam.jcr.DamConstants;
import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.devlib.schmidt.imageinfo.ImageInfo;
import org.jooq.lambda.Seq;
import vn.ekino.certificate.dto.CategoryDto;
import vn.ekino.certificate.dto.ImageDto;
import vn.ekino.certificate.dto.UserDto;
import vn.ekino.certificate.model.data.SelectField;
import vn.ekino.certificate.model.data.UserProfile;
import vn.ekino.certificate.repository.CategoryRepository;
import vn.ekino.certificate.repository.DamRepository;
import vn.ekino.certificate.repository.UserRepository;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.MapperUtils;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class UserProfileService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DamRepository damRepository;
    private final PublishingService publishingService;

    @Inject
    public UserProfileService(UserRepository userRepository, CategoryRepository categoryRepository, DamRepository damRepository, PublishingService publishingService) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.damRepository = damRepository;
        this.publishingService = publishingService;
    }

    public UserProfile loadUserProfile() {
        var currentUser = MgnlContext.getUser();
        return userRepository.findById(currentUser.getIdentifier())
                .map(itm -> MapperUtils.nodeToObject(itm, UserDto.class)).get()
                .map(this::mapUserProfile)
                .orElse(new UserProfile());
    }

    public Map<String, String> saveAvatar(Map<String, Object> map) throws IOException {
        String userId = (String) map.get("userId");
        Map<String, String> imageIdMap = new HashMap<>();
        var userNode = userRepository.findById(userId).get();
        if (map.get("file") != null) {
            String oldHeaderThumbnailId = PropertyUtil.getString(userNode, "headerThumbnail", StringUtils.EMPTY);
            String oldUserProfileThumbnailId = PropertyUtil.getString(userNode, "userProfileThumbnail", StringUtils.EMPTY);
            if (StringUtils.isNotEmpty(oldHeaderThumbnailId)) {
                damRepository.findById(oldHeaderThumbnailId).ifPresent(damRepository::deleteNode);
            }
            if (StringUtils.isNotEmpty(oldUserProfileThumbnailId)) {
                damRepository.findById(oldUserProfileThumbnailId).ifPresent(damRepository::deleteNode);
            }
            File originalAvatar = (File) map.get("file");
            String imageName = originalAvatar.getName();

            ByteArrayOutputStream headerThumbnailOs = createThumbnail(originalAvatar, 45, 45);
            ByteArrayOutputStream userProfileThumbnailOs = createThumbnail(originalAvatar, 130, 130);

            imageIdMap.put(Constants.HEADER_THUMBNAIL, saveImage(headerThumbnailOs, imageName, Constants.HEADER_THUMBNAIL));
            imageIdMap.put(Constants.USER_PROFILE_THUMBNAIL, saveImage(userProfileThumbnailOs, imageName, Constants.USER_PROFILE_THUMBNAIL));
        }
        return imageIdMap;
    }

    public void saveUserProfile(UserProfile profile) throws RepositoryException {
        var userNode = userRepository.findById(profile.getUuid()).get();
        if (StringUtils.isNotEmpty(profile.getImage())) {
            PropertyUtil.setProperty(userNode, "image", profile.getImage());
        }
        PropertyUtil.setProperty(userNode, "title", profile.getName());
        PropertyUtil.setProperty(userNode, "phone", profile.getPhone());
        PropertyUtil.setProperty(userNode, "profile", profile.getProfileId());
        PropertyUtil.setProperty(userNode, "department", profile.getDepartmentId());
        PropertyUtil.setProperty(userNode, "headerThumbnail", profile.getHeaderThumbnail());
        PropertyUtil.setProperty(userNode, "userProfileThumbnail", profile.getUserProfileThumbnail());
        userRepository.save(userNode);
        publishingService.publish(List.of(userNode));
    }

    public void changePassword(UserProfile profile) throws RepositoryException {
        UserManager userManager = SecuritySupport.Factory.getInstance().getUserManager();
        var userNode = userRepository.findById(profile.getUuid()).get();
        User user = userManager.getUser(userNode.getName());
        userManager.setProperty(user, MgnlUserManager.PROPERTY_PASSWORD, profile.getPwd());
        publishingService.publish(List.of(userRepository.findById(profile.getUuid()).get()));
    }

    public void forgotPassword(UserProfile profile) throws RepositoryException {
        UserManager userManager = SecuritySupport.Factory.getInstance().getUserManager();
        var userNode = userRepository.findById(profile.getUuid()).get();
        User user = userManager.getUser(userNode.getName());
        userManager.setProperty(user, "verifiedCodeForForgotPass", profile.getCode());
        publishingService.publish(List.of(userRepository.findById(profile.getUuid()).get()));
    }

    public void resetPassword(UserProfile profile) throws RepositoryException {
        UserManager userManager = SecuritySupport.Factory.getInstance().getUserManager();
        var userNode = userRepository.findById(profile.getUuid()).get();
        User user = userManager.getUser(userNode.getName());
        userRepository.removeVerifiedCodePropertyByNode(userNode);
        userManager.setProperty(user, MgnlUserManager.PROPERTY_PASSWORD, profile.getPwd());
        publishingService.publish(List.of(userRepository.findById(profile.getUuid()).get()));
    }

    private UserProfile mapUserProfile(UserDto dto) {
        return UserProfile.builder()
                .uuid(dto.getUuid())
                .name(dto.getFullName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .headerThumbnail(Optional.ofNullable(dto.getHeaderThumbnail()).map(ImageDto::getLink).orElse(StringUtils.EMPTY))
                .userProfileThumbnail(Optional.ofNullable(dto.getUserProfileThumbnail()).map(ImageDto::getLink).orElse(StringUtils.EMPTY))
                .image(Optional.ofNullable(dto.getUserProfileThumbnail()).map(ImageDto::getLink).orElse(StringUtils.EMPTY))
                .department(mapSelectField("department", dto.getDepartment()))
                .profile(mapSelectField("profile", dto.getProfile()))
                .build();
    }

    private List<SelectField> mapSelectField(String path, CategoryDto dto) {
        var categoryList = categoryRepository.findAllByPath(path).stream()
                .map(itm -> MapperUtils.nodeToObject(itm, CategoryDto.class).get())
                .collect(Collectors.toList());

        Seq<CategoryDto> s1 = Seq.seq(categoryList);
        Seq<CategoryDto> s2 = Seq.seq(List.of(dto));
        var list = s1.leftOuterJoin(s2, (v1, v2) -> v1.getUuid().equals(v2.getUuid()))
                .map(itm -> mapSelectField(itm.v1, itm.v2 != null))
                .collect(Collectors.toList());
        return list;
    }

    private SelectField mapSelectField(CategoryDto dto, boolean selected) {
        return SelectField.builder()
                .value(dto.getUuid())
                .label(dto.getDisplayName())
                .selected(selected).build();
    }

//    private ByteArrayOutputStream createThumbnail(File image, int width, int height) throws IOException {
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        try {
//            BufferedImage bufferedImage = Thumbnails.of(image).size(width, height).asBufferedImage();
//            ImageIO.write(bufferedImage, "jpg", os);
//        } catch (IOException e){
//            log.warn("Can't create thumbnail for user image because {}", e.getMessage());
//        } finally {
//            return os;
//        }
//    }

    public ByteArrayOutputStream createThumbnail(File image, int scaledWidth, int scaledHeight) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            BufferedImage inputImage = ImageIO.read(image);

            BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());

            // scales the input image to the output image
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
            g2d.dispose();

            // writes to output file
            ImageIO.write(outputImage, "png", os);
        } catch (IOException e){
            log.warn("Can not resize image because {}", e.getMessage());
        } finally {
            return os;
        }

    }

//    public File createThumbnail(File image, int scaledWidth, int scaledHeight) throws IOException {
//        BufferedImage inputImage = ImageIO.read(image);
//
//        // creates output image
//        BufferedImage outputImage = new BufferedImage(scaledWidth,
//                scaledHeight, inputImage.getType());
//
//        // scales the input image to the output image
//        Graphics2D g2d = outputImage.createGraphics();
//        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
//        g2d.dispose();
//
//        File output = null;
//        ImageIO.write(outputImage, "jpg", output);
//        return output;
//    }

    private String saveImage(ByteArrayOutputStream os, String imageName, String folderName) {
        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
        String imageIdentifier = "";
        try {
            Session session = MgnlContext.getJCRSession(DamConstants.WORKSPACE);
            var folder = damRepository.getOrAddFolder(session.getRootNode(), folderName);
            var assetNode = JcrUtils.getOrAddNode(folder, imageName, AssetNodeTypes.Asset.NAME);
            assetNode.setProperty(AssetNodeTypes.Asset.ASSET_NAME, imageName);
            assetNode.setProperty(DamRepository.PROPERTY_META_DATA, GeneratedCertificateService.META_DATA);

            // Create asset resource node
            var assetResourceNode = JcrUtils.getOrAddNode(assetNode, AssetNodeTypes.AssetResource.RESOURCE_NAME, AssetNodeTypes.AssetResource.NAME);
            assetResourceNode.setProperty(AssetNodeTypes.AssetResource.DATA, session.getValueFactory().createBinary(inputStream));
            assetResourceNode.setProperty(AssetNodeTypes.AssetResource.FILENAME, imageName);
            assetResourceNode.setProperty(AssetNodeTypes.AssetResource.EXTENSION, FilenameUtils.getExtension(imageName));
            assetResourceNode.setProperty(AssetNodeTypes.AssetResource.SIZE, os.toByteArray().length);
            var imageInfo = new ImageInfo();
            imageInfo.setInput(inputStream);
            assetResourceNode.setProperty(AssetNodeTypes.AssetResource.MIMETYPE, imageInfo.getMimeType());
            assetResourceNode.setProperty(AssetNodeTypes.AssetResource.WIDTH, Long.toString(imageInfo.getWidth()));
            assetResourceNode.setProperty(AssetNodeTypes.AssetResource.HEIGHT, Long.toString(imageInfo.getHeight()));
            session.save();
            imageIdentifier = assetNode.getIdentifier();
        } catch (RepositoryException e){
            log.warn("Can not save image because {}", e.getMessage());
        } finally {
            return imageIdentifier;
        }
    }
}
