package com.mescobar.blog.resource;

import com.mescobar.blog.dto.request.UpdateCommentRequest;
import com.mescobar.blog.model.Comment;
import com.mescobar.blog.service.BlogService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;

@Path("/api/comments")
@AllArgsConstructor
public class CommentResource {

    private final BlogService blogService;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Comment> list() {
        return blogService.streamAllComments();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Comment> getComment(@PathParam("id") String id) {
        return blogService.findCommentById(id);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deleteComment(@PathParam("id") String id) {
        return blogService.deleteComment(id);
    }

    @PUT
    @Path("/{id}")
    public Uni<Comment> update(@PathParam("id") String id, UpdateCommentRequest updateCommentRequest) {
        return blogService.updateComment(id, updateCommentRequest);
    }
}
