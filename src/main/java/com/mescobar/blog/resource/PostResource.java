package com.mescobar.blog.resource;

import com.mescobar.blog.dto.request.CreateCommentRequest;
import com.mescobar.blog.dto.request.CreatePostRequest;
import com.mescobar.blog.dto.request.SearchPostRequest;
import com.mescobar.blog.dto.request.UpdatePostRequest;
import com.mescobar.blog.model.Post;
import com.mescobar.blog.service.BlogService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;

import java.net.URI;
import java.util.List;

@Path("/api/posts")
@AllArgsConstructor
public class PostResource {

    private final BlogService blogService;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Post> list() {
        return blogService.streamAllPosts();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> addPost(CreatePostRequest createPostRequest) {

        return blogService.createPost(createPostRequest).map(v ->
                Response.created(URI.create("/posts/" + v.getId().toString()))
                        .entity(v).build());
    }

    @PUT
    @Path("/{id}")
    public Uni<Post> update(@PathParam("id") String id, UpdatePostRequest updatePost) {
        return blogService.updatePost(id, updatePost);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Post> getPost(@PathParam("id") String id) {
        return blogService.findById(id);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deletePost(@PathParam("id") String id) {
        return blogService.deletePost(id);
    }

    @GET
    @Path("/search")
    public Uni<List<Post>> search(@Valid  @BeanParam SearchPostRequest searchPostRequest) {
        return blogService.searchPosts(searchPostRequest);
    }

    @GET
    @Path("/searchByAuthors")
    public Uni<List<Post>> searchByAuthors(@QueryParam("authors") List<String> authors) {
        return blogService.searchPostsByAuthors(authors);
    }

    @PUT
    @Path("/{id}/comment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> addCommentToPost(@PathParam("id") String id, CreateCommentRequest comment) {
        return blogService.addCommentToPost(comment, id).map(v -> Response.accepted(v).build());
    }

}
