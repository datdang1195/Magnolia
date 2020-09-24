package vn.ekino.certificate.service;

import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import vn.ekino.certificate.dto.CommentDto;
import vn.ekino.certificate.dto.ImageDto;
import vn.ekino.certificate.dto.UserDto;
import vn.ekino.certificate.model.data.Comment;
import vn.ekino.certificate.repository.CommentRepository;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.MapperUtils;
import vn.ekino.certificate.util.NodeUtils;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CommentService {
    final CommentRepository commentRepository;
    final NodeNameHelper nodeNameHelper;

    @Inject
    public CommentService(CommentRepository commentRepository, NodeNameHelper nodeNameHelper) {
        this.commentRepository = commentRepository;
        this.nodeNameHelper = nodeNameHelper;
    }

    public List<CommentDto> getListCommentByCourse(String courseId) {
        return commentRepository.findByCourse(courseId)
                .stream()
                .map(commentLv1Node -> {
                    var commentLv1 = MapperUtils.nodeToObject(commentLv1Node, CommentDto.class).get();
                    var commentLv2s = NodeUtils.getSubNodes(commentLv1Node)
                            .stream()
                            .map(commentLv2Node -> {
                                var commentLv2 = MapperUtils.nodeToObject(commentLv2Node, CommentDto.class).get();

                                UserDto userDtoLv2 = new UserDto();
                                userDtoLv2.setUuid(commentLv2.getUuid());
                                userDtoLv2.setHeaderThumbnail(commentLv2.getUser().getHeaderThumbnail());
                                userDtoLv2.setFullName(commentLv2.getUser().getFullName());

                                commentLv2.setUser(userDtoLv2);
                                commentLv2.setLevel((long) 2);
                                commentLv2.setCommentTime(getCommentTime(commentLv2.getDateTime()));

                                ImageDto imageDtoLv2 = commentLv2.getUser().getImage();
                                if (imageDtoLv2 == null) {
                                    commentLv2.getUser().setImage(new ImageDto());
                                } else {
                                    commentLv2.getUser().getImage().setLink(commentLv2.getUser().getImage().getLink());
                                }
                                return commentLv2;
                            })
                            .collect(Collectors.toList());

                    UserDto userDtoLv1 = new UserDto();
                    userDtoLv1.setUuid(commentLv1.getUser().getUuid());
                    userDtoLv1.setHeaderThumbnail(commentLv1.getUser().getHeaderThumbnail());
                    userDtoLv1.setFullName(commentLv1.getUser().getFullName());

                    commentLv1.setUser(userDtoLv1);
                    commentLv1.setCommentList(commentLv2s);
                    commentLv1.setLevel((long) 1);
                    commentLv1.setCommentTime(getCommentTime(commentLv1.getDateTime()));

                    ImageDto imageDtoLv1 = commentLv1.getUser().getImage();
                    if (imageDtoLv1 == null) {
                        commentLv1.getUser().setImage(new ImageDto());
                    } else {
                        commentLv1.getUser().getImage().setLink(imageDtoLv1.getLink());
                    }
                    return commentLv1;
                })
                .collect(Collectors.toList());
    }

    public int getTotalComment(String courseId) {
        int totalComments = 0;
        List<CommentDto> commentsLv1 = getListCommentByCourse(courseId);
        List<CommentDto> commentsLv2;
        for (int i = 0; i < commentsLv1.size(); i++) {
            if (commentsLv1.get(i).getCommentList() == null) {
                totalComments++;
            } else {
                totalComments++;
                commentsLv2 = commentsLv1.get(i).getCommentList();
                totalComments += commentsLv2.size();
            }
        }
        return totalComments;
    }

    public Comment postComment(Comment comment) {
        int level = comment.getLevel();
        String nodeName = nodeNameHelper.getValidatedName(String.format("level-%s-%s", level,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.TIME_STAMP_PATTERN))));
        String parentId = comment.getParentId();
        Node nodeComment = null;
        if (StringUtils.isEmpty(parentId)) {
            var node = commentRepository.createNode(nodeName).get();
            nodeComment = setComment(node, comment);
        } else {
            String nodeType = level > 1 ? CommentRepository.NODE_TYPE : Constants.CONTENT_NODE;
            var node = commentRepository.findById(CommentRepository.WORKSPACE, nodeType, comment.getParentId());
            if (node.isPresent()) {
                var newNode = NodeUtils.addNode(node.get(), nodeName, Constants.CONTENT_NODE).get();
                nodeComment = setComment(newNode, comment);
            }
        }
        if (nodeComment != null) {
            commentRepository.save(nodeComment);
            return buildCommentResponse(nodeComment);
        }
        return null;
    }

    private Node setComment(Node node, Comment comment) {
        try {
            PropertyUtil.setProperty(node, "user", comment.getUserId());
            PropertyUtil.setProperty(node, "comment", comment.getComment());
            PropertyUtil.setProperty(node, "dateTime", Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            PropertyUtil.setProperty(node, "level", comment.getLevel());
            PropertyUtil.setProperty(node, "courseId", comment.getCourseId());
        } catch (RepositoryException e) {
            log.warn("Can't set property because {}", e.getMessage());
        }
        return node;
    }

    private Comment buildCommentResponse(Node node) {
        return Comment.builder()
                .userId(PropertyUtil.getString(node, "user"))
                .level(Math.toIntExact(PropertyUtil.getLong(node, "level")))
                .id(PropertyUtil.getString(node, JcrConstants.JCR_UUID))
                .build();

    }

    public String getCommentTime(LocalDateTime dateTime) {
        var now = LocalDateTime.now();
        var duration = Duration.between(dateTime, now);
        var minutes = duration.toMinutes();
        if (minutes < 61) {
            return String.format("%s minutes ago", minutes);
        }
        var hours = duration.toHours();
        if (hours < 24) {
            return String.format("%s hours ago", hours);
        }
        return TimeUtils.toString(dateTime, Constants.DATE_TIME_PATTERN_OF_PROGRAM_DETAIL);
    }

}
