package vn.ekino.certificate.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.PdfWriter;
import info.magnolia.cms.core.Path;
import info.magnolia.context.MgnlContext;
import info.magnolia.dam.jcr.AssetNodeTypes;
import info.magnolia.dam.jcr.DamConstants;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.jcr.ModelConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.devlib.schmidt.imageinfo.ImageInfo;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.GeneratedCertificateDto;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.dto.UserDto;
import vn.ekino.certificate.repository.DamRepository;
import vn.ekino.certificate.repository.GeneratedCertificateRepository;
import vn.ekino.certificate.repository.WebsiteRepository;
import vn.ekino.certificate.util.MapperUtils;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Singleton
@Slf4j
public class GeneratedCertificateService {
    //region Variables
    private final DamRepository damRepository;
    private final WebsiteRepository websiteRepository;
    private final DamTemplatingFunctions damFunctions;
    private final CertificateServicesModule certificateServicesModule;
    private final PublishingService publishingService;

    private static final String IMAGE_FORMAT = "png";
    private static final String PDF_FORMAT = "pdf";
    private static final String PDF_MIME_TYPE = "application/pdf";
    private static final String CERTIFICATES = "Certificates";
    private static final String FONT_NAME = "Kadwa";
    private static final String PAGE_TEMPLATE = "cert-checker-module:pages/certificate-checker-page";
    private static final Color PRIMARY_COLOR = Color.decode("#374b4b");
    private static final Color ON_COLOR = Color.decode("#374b4b");
    public static final String META_DATA = "Simple Dublin Core Metadata Element Set (DCMES)";

    GeneratedCertificateDto dto;
    UserDto user;
    ProgramDto programDto;
    String imageId;
    String path;
    //endregion

    //region Constructor
    @Inject
    public GeneratedCertificateService(DamRepository damRepository, WebsiteRepository websiteRepository, DamTemplatingFunctions damFunctions, CertificateServicesModule certificateServicesModule, PublishingService publishingService) {
        this.damRepository = damRepository;
        this.websiteRepository = websiteRepository;
        this.damFunctions = damFunctions;
        this.certificateServicesModule = certificateServicesModule;
        this.publishingService = publishingService;
    }
    //endregion

    //region Execute generate certificate files, page and publish
    public void executeGenerateCertificate(Node node, boolean isPublish) throws RepositoryException {
        generateCertificate(node);
        String pageId = createCertificatePage(imageId);
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(node);
        if (isPublish) {
            Node nodeDam = damRepository.getOrAddFolder(MgnlContext.getJCRSession(DamConstants.WORKSPACE).getRootNode(), CERTIFICATES);
            nodeList.add(nodeDam);
            nodeList.add(websiteRepository.findById(pageId).get());
            publishingService.publish(nodeList);
        }
    }

    private void generateCertificate(Node node) throws RepositoryException {
        dto = MapperUtils.nodeToObject(node, GeneratedCertificateDto.class).get();
        user = dto.getEnrolProgram().getUser();
        programDto = dto.getEnrolProgram().getProgram();
        path = PropertyUtil.getString(node, GeneratedCertificateRepository.PROPERTY_PATH,"");
        if (StringUtils.isEmpty(path)) {
            path = getPath();
        }

        List<String> generatedFiles = createCertificate();

        String nodeName = String.format("%s-%s", programDto.getNodeName(), user.getNodeName());
        PropertyUtil.setProperty(node, AssetNodeTypes.Asset.ASSET_NAME, nodeName);
        PropertyUtil.setProperty(node, GeneratedCertificateRepository.PROPERTY_PATH, path);
        PropertyUtil.setProperty(node, GeneratedCertificateRepository.PROPERTY_GENERATED_FILES, generatedFiles);
        PropertyUtil.setProperty(node, GeneratedCertificateRepository.PROPERTY_FULL_PAGE_PATH,
                String.format("%s/%s", certificateServicesModule.getServerPath(), path));
        setNodeName(node);
        node.getSession().save();
    }

    private String createCertificatePage(String imageId) {
        try {
            Node siteNode = websiteRepository.getOrAddNode(path).get();
            PropertyUtil.setProperty(siteNode, WebsiteRepository.PROPERTY_TEMPLATE, PAGE_TEMPLATE);
            PropertyUtil.setProperty(siteNode, WebsiteRepository.PROPERTY_TITLE, programDto.getNodeName());
            PropertyUtil.setProperty(siteNode, WebsiteRepository.PROPERTY_FULL_NAME, dto.getEnrolProgram().getUser().getFullName());
            PropertyUtil.setProperty(siteNode, WebsiteRepository.PROPERTY_EMAIL, dto.getEnrolProgram().getUser().getEmail());
            PropertyUtil.setProperty(siteNode, WebsiteRepository.PROPERTY_CERTIFICATE_IMAGE, imageId);
            PropertyUtil.setProperty(siteNode, WebsiteRepository.PROPERTY_CERTIFICATE_URL, String.format("%s/%s", certificateServicesModule.getServerPath(), path));
            websiteRepository.save(siteNode);
            return siteNode.getIdentifier();
        } catch (RepositoryException e) {
            log.warn("Can't set property because {}", e.getMessage());
        }
        return null;
    }

    //endregion

    //region Private Methods
    private List<String> createCertificate() {
        List<String> generatedFiles = new ArrayList<>();
        try {
            BufferedImage imageQR = ImageIO.read(generateQR());
            BufferedImage imageTemplate = ImageIO.read(getCertificateTemplate());

            //add text to image
            String fullName = dto.getEnrolProgram().getUser().getFullName();
            String startDate = formatDate(programDto.getPhase().getStartDate());
            String endDate = formatDate(programDto.getPhase().getEndDate());
            String domain = certificateServicesModule.getServerPath();
            domain = domain.substring(domain.indexOf(':') + 3) + "/";
            String path = getPath();
            final int length = path.length() - 3;
            int x = 250;
            if (length >6 && length <= 8) {
                x = 215;
            } else if (length > 8) {
                x = 180;
            }
            Graphics2D graphics2D = imageTemplate.createGraphics();
            drawString(imageTemplate, fullName, new Font(FONT_NAME, Font.BOLD, 120), graphics2D, 0, 990);
            drawString(imageTemplate, "\"" + programDto.getGroup().getDisplayName().toUpperCase() + "\"", new Font(FONT_NAME, Font.BOLD, 36), graphics2D, 0, 1140);
            drawString(imageTemplate, "conducted from " + startDate, new Font(FONT_NAME, Font.PLAIN, 36), graphics2D, 0, 1200);
            drawString(imageTemplate, "to " + endDate, new Font(FONT_NAME, Font.PLAIN, 36), graphics2D, 0, 1260);
            drawString(imageTemplate, domain + path, new Font(FONT_NAME, Font.PLAIN, 36), graphics2D, x, 1625);
            graphics2D.drawImage(imageQR,376, 1305, null);
            graphics2D.dispose();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(imageTemplate, IMAGE_FORMAT, os);
            InputStream input = new ByteArrayInputStream(os.toByteArray());

            imageId = saveCertificate(input, IMAGE_FORMAT, os.toByteArray().length);
            generatedFiles.add(imageId);

            //pdf file
            var pageSize = new RectangleReadOnly(imageTemplate.getWidth(),imageTemplate.getHeight());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(pageSize, 0, 0, 0, 0);
            PdfWriter.getInstance(document, out);
            document.open();

            var image = com.itextpdf.text.Image.getInstance(os.toByteArray());
            document.add(image);
            document.close();
            InputStream in = new ByteArrayInputStream(out.toByteArray());
            generatedFiles.add(saveCertificate(in, PDF_FORMAT, out.toByteArray().length));

        } catch (IOException e) {
            log.warn("Can't create image because {}", e.getMessage());
        } catch (DocumentException e) {
            log.warn("Can't create pdf file because {}", e.getMessage());
        }
        return generatedFiles;
    }

    private InputStream generateQR() {
        InputStream inputStream = null;
        try {
            int onValue = ON_COLOR.getRGB();
            int offValue = new Color(255,255,255, 0).getRGB();
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            String qrString = String.format("%s/%s", certificateServicesModule.getServerPath(), path);

            BitMatrix bitMatrix = qrCodeWriter.encode(qrString, BarcodeFormat.QR_CODE, 240, 240);
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, IMAGE_FORMAT, outputStream, new MatrixToImageConfig(onValue, offValue));
            byte[] pngByteArray = outputStream.toByteArray();
            inputStream = new ByteArrayInputStream(pngByteArray);
        } catch (WriterException | IOException e) {
            log.warn("Can't generate QR code image because {}", e.getMessage() );
        }
        return inputStream;
    }

    private InputStream getCertificateTemplate() {
        String itemKey = "jcr:" + programDto.getCertificateTemplateImage().getUuid();
        var template = damFunctions.getAsset(itemKey);
        return template.getContentStream();
    }

    private String saveCertificate(InputStream input, String format, int size) {
        String assetId = null;
        try {
            Session session = MgnlContext.getJCRSession(DamConstants.WORKSPACE);
            // "Navigate" to the assets folder node
            Node certificatesNode = damRepository.getOrAddFolder(session.getRootNode(), CERTIFICATES);
            Node assetFolderNode = damRepository.getOrAddFolder(certificatesNode, user.getNodeName());

            // Create asset node
            String fileName = String.format("%s.%s",
                    programDto.getNodeName(),
                    format);
            if (assetFolderNode.hasNode(fileName)) {
                assetFolderNode.getNode(fileName).remove();
            }
            Node assetNode = JcrUtils.getOrAddNode(assetFolderNode, fileName, AssetNodeTypes.Asset.NAME);
            assetNode.setProperty(AssetNodeTypes.Asset.ASSET_NAME, fileName);
            assetNode.setProperty(DamRepository.PROPERTY_META_DATA, META_DATA);

            // Create asset resource node
            Node assetResourceNode = JcrUtils.getOrAddNode(assetNode, AssetNodeTypes.AssetResource.RESOURCE_NAME, AssetNodeTypes.AssetResource.NAME);
            assetResourceNode.setProperty(AssetNodeTypes.AssetResource.DATA, session.getValueFactory().createBinary(input));
            assetResourceNode.setProperty(AssetNodeTypes.AssetResource.FILENAME, fileName);
            assetResourceNode.setProperty(AssetNodeTypes.AssetResource.EXTENSION, format);
            assetResourceNode.setProperty(AssetNodeTypes.AssetResource.SIZE, Long.toString(size));
            assetResourceNode.setProperty(AssetNodeTypes.AssetResource.MIMETYPE, PDF_MIME_TYPE);
            if (IMAGE_FORMAT.equals(format)) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setInput(input);
                assetResourceNode.setProperty(AssetNodeTypes.AssetResource.MIMETYPE, imageInfo.getMimeType());
                assetResourceNode.setProperty(AssetNodeTypes.AssetResource.WIDTH, Long.toString(imageInfo.getWidth()));
                assetResourceNode.setProperty(AssetNodeTypes.AssetResource.HEIGHT, Long.toString(imageInfo.getHeight()));
            }
            session.save();
            assetId = "jcr:" + assetNode.getIdentifier();
        } catch (RepositoryException e) {
            log.warn("Can't get or set property because {}", e.getMessage());
        } finally {
            IOUtils.closeQuietly(input);
        }
        return assetId;
    }

    private String formatDate(LocalDateTime dateTime) {
        String result = dateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int date = dateTime.getDayOfMonth();
        result += " " + getDayOfMonthSuffix(date);
        result += " " + dateTime.getYear();
        return result;
    }

    private String getDayOfMonthSuffix(int d) {
        String date = String.valueOf(d);
        String suffix;
        if (d >= 11 && d <= 13) {
            suffix = "th";
        }
        else {
            switch (d % 10) {
                case 1:  suffix = "st";
                case 2:  suffix = "nd";
                case 3:  suffix = "rd";
                default: suffix = "th";
            }
        }
        return date + suffix;
    }

    private void drawString(BufferedImage image, String text, Font font, Graphics2D graphics2D, int x, int y) {
        graphics2D.setFont(font);
        graphics2D.setColor(PRIMARY_COLOR);
        TextLayout textLayout = new TextLayout(text, font, graphics2D.getFontRenderContext());
        double textWidth = textLayout.getBounds().getWidth();
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        x = x == 0 ? image.getWidth() / 2 - (int) textWidth / 2 : x;
        graphics2D.drawString(text, x, y);
    }

    private String getPath() {
        return "EA-" + dto.getCode().toUpperCase();
    }

    public void setNodeName(Node node) throws RepositoryException {
        String propertyName = "name";
        if (node.hasProperty(propertyName) && !node.hasProperty(ModelConstants.JCR_NAME)) {
            Property property = node.getProperty(propertyName);
            String newNodeName = property.getString();
            if (!node.getName().equals(Path.getValidatedLabel(newNodeName))) {
                newNodeName = Path.getUniqueLabel(node.getSession(), node.getParent().getPath(), Path.getValidatedLabel(newNodeName));
                NodeUtil.renameNode(node, newNodeName);
            }
        }
    }

    public boolean validateForm(EditorValidator validator) {
        boolean isValid = validator.isValid();
        validator.showValidation(!isValid);
        if (!isValid) {
            log.warn("Validation error(s) occurred. No save performed.");
        }
        return isValid;
    }
    //endregion
}
