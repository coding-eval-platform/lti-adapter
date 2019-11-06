package ar.edu.itba.cep.lti_service.rest.controller.endpoints;

import ar.edu.itba.cep.lti.constants.Paths;
import ar.edu.itba.cep.lti.dtos.*;
import ar.edu.itba.cep.lti_service.services.LtiService;
import com.bellotapps.webapps_commons.config.JerseyController;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Rest Adapter of {@link LtiService}, providing endpoints to allow LTI integration.
 */
@Path("lti/app")
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
@AllArgsConstructor
public class LtiAppController {

    /**
     * The {@link Logger}.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LtiAppController.class);

    /**
     * The {@link LtiService} to which the requests will be delegated.
     */
    private final LtiService ltiService;


    @POST
    @Path(Paths.LOGIN_INITIATION_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginInit(final LoginInitiationRequestDto dto) {
        LOGGER.debug(
                "Login initiation request for issuer {}, with client id {}, and deployment id {}",
                dto.getIssuer(),
                dto.getClientId(),
                dto.getDeploymentId()
        );
        final var authenticationRequest = ltiService.loginInitiation(dto.toModel());
        return Response.ok(AuthenticationRequestDto.fromModel(authenticationRequest)).build();
    }

    @POST
    @Path(Paths.EXAM_SELECTION_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response examSelection(final AuthenticationResponseDto dto) {
        LOGGER.debug("Authentication response to select an exam");
        final var examSelectionResponse = ltiService.examSelection(dto.toModel());
        return Response.ok(ExamSelectionResponseDto.fromModel(examSelectionResponse)).build();
    }

    @POST
    @Path(Paths.EXAM_SELECTED_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response examSelected(final ExamSelectedRequestDto dto) {
        LOGGER.debug("Exam selected request for exam with id {}", dto.getExamId());
        final var response = ltiService.examSelected(dto.toModel());
        return Response.ok(ExamSelectedResponseDto.fromModel(response)).build();
    }

    @POST
    @Path(Paths.EXAM_TAKING_PATH)
    public Response startExam(final AuthenticationResponseDto dto) {
        final var examTakingResponse = ltiService.takeExam(dto.toModel());
        return Response.ok(ExamTakingResponseDto.fromModel(examTakingResponse)).build();
    }

    @PUT
    @Path(Paths.EXAM_SCORING_PATH)
    public Response scoreExam(final ExamScoringRequestDto dto) {
        ltiService.scoreExam(dto.toModel());
        return Response.noContent().build();
    }


}
