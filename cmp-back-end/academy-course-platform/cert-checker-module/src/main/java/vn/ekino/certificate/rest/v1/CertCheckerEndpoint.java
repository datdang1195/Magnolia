package vn.ekino.certificate.rest.v1;

import info.magnolia.rest.AbstractEndpoint;
import info.magnolia.rest.EndpointDefinition;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import vn.ekino.certificate.dto.EnrolDto;
import vn.ekino.certificate.model.data.Comment;
import vn.ekino.certificate.model.data.Notification;
import vn.ekino.certificate.model.data.UserProfile;
import vn.ekino.certificate.service.CommentService;
import vn.ekino.certificate.service.CourseService;
import vn.ekino.certificate.service.EnrolService;
import vn.ekino.certificate.service.NotificationService;
import vn.ekino.certificate.service.UserProfileService;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "/certChecker/v1")
@Path("/certChecker/v1")
@Slf4j
public class CertCheckerEndpoint<D extends EndpointDefinition> extends AbstractEndpoint<D> {

    private static final String STATUS_MESSAGE_OK = "OK";
    private static final String STATUS_MESSAGE_NO_CONTENT = "No Content";
    private static final String STATUS_MESSAGE_CREATED = "Created";
    private static final String STATUS_MESSAGE_UNAUTHORIZED = "Unauthorized";
    private static final String STATUS_MESSAGE_NODE_NOT_FOUND = "Node not found";
    private static final String STATUS_MESSAGE_ERROR_OCCURRED = "Error occurred";
    private static final String STATUS_MESSAGE_ERROR_BAD_REQUEST = "Bad request";
    private static final String REST_ENDPOINT = "/.rest/certChecker/v1/";

    private final EnrolService enrolService;
    private final UserProfileService userProfileService;
    private final NotificationService notificationService;
    private final CommentService commentService;
    private final CourseService courseService;


    @Inject
    public CertCheckerEndpoint(D endpointDefinition, EnrolService enrolService, UserProfileService userProfileService, NotificationService notificationService, CommentService commentService, CourseService courseService) {
        super(endpointDefinition);
        this.enrolService = enrolService;
        this.userProfileService = userProfileService;
        this.notificationService = notificationService;
        this.commentService = commentService;
        this.courseService = courseService;
    }

    @Path("/enrol")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Enrol program by user.", notes = "Enrol program by user.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_NODE_NOT_FOUND),
            @ApiResponse(code = 400, message = STATUS_MESSAGE_ERROR_BAD_REQUEST),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response enrolProgram(@Valid EnrolDto model) {
        int result = enrolService.enrolProgram(model);
        return Response.status(result).build();
    }

    @Path("/profile")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update user profile.", notes = "Update user profile.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_NODE_NOT_FOUND),
            @ApiResponse(code = 400, message = STATUS_MESSAGE_ERROR_BAD_REQUEST),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response updateUserProfile(UserProfile profile) throws RepositoryException {
        userProfileService.saveUserProfile(profile);
        return Response.ok().build();
    }

    @Path("/changePassword")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Change user password.", notes = "Change user password.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_NODE_NOT_FOUND),
            @ApiResponse(code = 400, message = STATUS_MESSAGE_ERROR_BAD_REQUEST),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response changePassword(UserProfile profile) throws RepositoryException {
        userProfileService.changePassword(profile);
        return Response.ok().build();
    }

    @Path("/forgotPassword")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Forgot password.", notes = "Forgot password.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_NODE_NOT_FOUND),
            @ApiResponse(code = 400, message = STATUS_MESSAGE_ERROR_BAD_REQUEST),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response forgotPassword(UserProfile profile) throws RepositoryException {
        userProfileService.forgotPassword(profile);
        return Response.ok().build();
    }

    @Path("/resetPassword")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Reset password.", notes = "Reset password.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_NODE_NOT_FOUND),
            @ApiResponse(code = 400, message = STATUS_MESSAGE_ERROR_BAD_REQUEST),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response resetPassword(UserProfile profile) throws RepositoryException {
        userProfileService.resetPassword(profile);
        return Response.ok().build();
    }

    @Path("/viewNotification")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "view notification.", notes = "view notification.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_NODE_NOT_FOUND),
            @ApiResponse(code = 400, message = STATUS_MESSAGE_ERROR_BAD_REQUEST),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response viewNotification(Notification data) {
        notificationService.viewNotification(data);
        return Response.noContent().build();
    }

    @Path("/comment")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Post comment.", notes = "Post comment.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_NODE_NOT_FOUND),
            @ApiResponse(code = 400, message = STATUS_MESSAGE_ERROR_BAD_REQUEST),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response postComment(Comment comment) {
        var result = commentService.postComment(comment);
        return Response.status(HttpStatus.SC_CREATED).entity(result).build();
    }

    @Path("/courseResult")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Post comment.", notes = "Post comment.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_NODE_NOT_FOUND),
            @ApiResponse(code = 400, message = STATUS_MESSAGE_ERROR_BAD_REQUEST),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response dataMigration() {
        courseService.dataMigration();
        return Response.status(HttpStatus.SC_NO_CONTENT).build();
    }
}
