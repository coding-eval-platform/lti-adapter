package ar.edu.itba.cep.lti_service.rest.controller.endpoints;

import com.bellotapps.webapps_commons.config.JerseyController;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Controller in charge of providing endpoints to allow LTI integration.
 */
@Path("lti")
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
public class LtiController {

    @GET
    @Path("oidc")
    public Response login() {
        return Response.noContent().build();
    }

    @POST
    @Path("start-exam")
    public Response startExam() {
        return Response.noContent().build();
    }

    @POST
    @Path("create-exam")
    public Response createExam() {
        return Response.noContent().build();
    }
}
