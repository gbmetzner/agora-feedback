package com.agora.domain.feedback.resource;

import com.agora.domain.feedback.application.FeedbackApplicationService;
import com.agora.domain.feedback.application.dto.CreateFeedbackCommand;
import com.agora.domain.feedback.application.dto.UpdateFeedbackCommand;
import com.agora.domain.feedback.model.dto.FeedbackResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/feedbacks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeedbackResource {
    private final FeedbackApplicationService feedbackApplicationService;

    @Inject
    public FeedbackResource(FeedbackApplicationService feedbackApplicationService) {
        this.feedbackApplicationService = feedbackApplicationService;
    }

    @GET
    public Response listAll() {
        List<FeedbackResponse> responses = feedbackApplicationService.getAllFeedbacks();
        return Response.ok(responses).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        FeedbackResponse response = feedbackApplicationService.getFeedback(id);
        return Response.ok(response).build();
    }

    @POST
    public Response create(CreateFeedbackCommand command) {
        FeedbackResponse response = feedbackApplicationService.createFeedback(command);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, UpdateFeedbackCommand command) {
        FeedbackResponse response = feedbackApplicationService.updateFeedback(id, command);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        feedbackApplicationService.deleteFeedback(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/archive")
    public Response archive(@PathParam("id") Long id) {
        FeedbackResponse response = feedbackApplicationService.archiveFeedback(id);
        return Response.ok(response).build();
    }

    @POST
    @Path("/{id}/reopen")
    public Response reopen(@PathParam("id") Long id) {
        FeedbackResponse response = feedbackApplicationService.reopenFeedback(id);
        return Response.ok(response).build();
    }
}
