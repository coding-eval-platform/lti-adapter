package ar.edu.itba.cep.lti_service.rest.controller.endpoints;

import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import ar.edu.itba.cep.lti_service.rest.controller.dtos.ToolDeploymentDto;
import ar.edu.itba.cep.lti_service.services.LtiAdminService;
import com.bellotapps.webapps_commons.config.JerseyController;
import com.bellotapps.webapps_commons.exceptions.IllegalParamValueException;
import com.bellotapps.webapps_commons.exceptions.MissingJsonException;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Rest Adapter of {@link LtiAdminService}, providing endpoints to allow
 * {@link ar.edu.itba.cep.lti_service.models.ToolDeployment} management.
 */
@Path("lti/admin/tool-deployments")
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
@AllArgsConstructor
public class LtiAdminToolDeploymentController {

    /**
     * The {@link Logger}.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LtiAdminToolDeploymentController.class);

    /**
     * The {@link LtiAdminService} to which the requests will be delegated.
     */
    private final LtiAdminService ltiAdminService;


    @GET
    public Response getToolDeployments() {
        LOGGER.debug("Getting all tool deployments");
        return buildToolDeploymentsListResponse(ltiAdminService::getAllToolDeployments);
    }

    @GET
    @Path("{id : .+}")
    public Response getToolDeployment(@PathParam("id") final UUID id) {
        validateParamsNotMissing(ParamAndName.create(id, "id"));
        LOGGER.debug("Searching for ToolDeployment with id {}", id);
        return buildSingleToolDeploymentResponse(() -> ltiAdminService.getToolDeploymentById(id));
    }

    @GET
    @Path("issuer/{issuer : .+}")
    public Response getToolDeployment(@PathParam("issuer") final String issuer) {
        validateParamsNotMissing(ParamAndName.create(issuer, "issuer"));
        LOGGER.debug("Searching for ToolDeployment for issuer {}", issuer);
        return buildToolDeploymentsListResponse(() -> ltiAdminService.find(issuer));
    }

    @GET
    @Path("issuer/{issuer : .+}/client-id/{clientId}")
    public Response getToolDeployment(
            @PathParam("issuer") final String issuer,
            @PathParam("clientId") final String clientId) {
        validateParamsNotMissing(ParamAndName.create(issuer, "issuer"), ParamAndName.create(clientId, "clientId"));
        LOGGER.debug("Searching for ToolDeployment for issuer {} and client-id {}", issuer, clientId);
        return buildToolDeploymentsListResponse(() -> ltiAdminService.find(clientId, issuer));
    }

    @GET
    @Path("issuer/{issuer : .+}/client-id/{clientId : .+}/deployment-id/{deploymentId : .+}")
    public Response getToolDeployment(
            @PathParam("issuer") final String issuer,
            @PathParam("clientId") final String clientId,
            @PathParam("deploymentId") final String deploymentId) {
        validateParamsNotMissing(
                ParamAndName.create(issuer, "issuer"),
                ParamAndName.create(clientId, "clientId"),
                ParamAndName.create(deploymentId, "deploymentId")
        );
        LOGGER.debug("Searching for ToolDeployment for issuer {}, client-id {} and deployment-id {}",
                issuer, clientId, deploymentId
        );
        return buildSingleToolDeploymentResponse(() -> ltiAdminService.find(deploymentId, clientId, issuer));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createToolDeployments(@Context final UriInfo uriInfo, @Valid final ToolDeploymentDto dto) {
        if (dto == null) {
            throw new MissingJsonException();
        }
        LOGGER.debug(
                "Registering Tool Deployment for issuer {}, client-id {}, and deployment-id {}",
                dto.getIssuer(),
                dto.getClientId(),
                dto.getDeploymentId()
        );
        final var toolDeployment = ltiAdminService.registerToolDeployment(
                dto.getDeploymentId(),
                dto.getClientId(),
                dto.getIssuer(),
                dto.getOidcAuthenticationEndpoint(),
                dto.getJwksEndpoint(),
                dto.getPrivateKey(),
                dto.getSignatureAlgorithm(),
                dto.getApplicationKey(),
                dto.getApplicationSecret()
        );
        final var location = uriInfo.getAbsolutePathBuilder().path(toolDeployment.getId().toString()).build();
        return Response.created(location).build();
    }

    @DELETE
    @Path("{id : .+}")
    public Response deleteToolDeployment(@PathParam("id") final UUID id) {
        validateParamsNotMissing(ParamAndName.create(id, "id"));
        LOGGER.debug("Unregistering ToolDeployment with id {}", id);
        ltiAdminService.unregisterToolDeployment(id);
        return Response.noContent().build();
    }


    /**
     * Builds a {@link Response} for a collection of {@link ToolDeployment}s.
     *
     * @param toolDeploymentsListSupplier A {@link Supplier} of {@link List} of {@link ToolDeployment}s.
     * @return The created {@link Response}.
     */
    private static Response buildToolDeploymentsListResponse(
            final Supplier<List<ToolDeployment>> toolDeploymentsListSupplier) {
        final var users = toolDeploymentsListSupplier.get()
                .stream()
                .map(ToolDeploymentDto::fromModel)
                .collect(Collectors.toList());
        return Response.ok(users).build();
    }

    /**
     * Builds a {@link Response} for a single {@link ToolDeployment}.
     *
     * @param toolDeploymentOptionalSupplier A {@link Supplier} of {@link Optional} of {@link ToolDeployment}.
     * @return The created {@link Response}.
     */
    private static Response buildSingleToolDeploymentResponse(
            final Supplier<Optional<ToolDeployment>> toolDeploymentOptionalSupplier) {
        return toolDeploymentOptionalSupplier.get()
                .map(ToolDeploymentDto::fromModel)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND).entity(""))
                .build()
                ;
    }

    /**
     * Performs tha param validation (i.e will throw an {@link IllegalParamValueException} if any is missing).
     *
     * @param paramAndNames The {@link ParamAndName}s to be checked.
     * @throws IllegalParamValueException If any of the {@code paramAndName}s array element
     *                                    contains a {@code null} param.
     */
    private static void validateParamsNotMissing(final ParamAndName<?>... paramAndNames)
            throws IllegalParamValueException {
        final var illegalParams = Arrays.stream(paramAndNames)
                .filter(paramAndName -> Objects.isNull(paramAndName.getParam()))
                .map(ParamAndName::getName)
                .collect(Collectors.toList());
        if (!illegalParams.isEmpty()) {
            throw new IllegalParamValueException(illegalParams);
        }

    }

    /**
     * Container class that holds a parameter together with its name.
     * It is used by the {@link #validateParamsNotMissing(ParamAndName[])} method to perform argument validation.
     *
     * @param <T> The concrete type of the param.
     */
    @Value(staticConstructor = "create")
    private static final class ParamAndName<T> {
        private final T param;
        private final String name;
    }
}
