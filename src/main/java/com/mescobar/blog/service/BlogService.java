package com.mescobar.blog.service;

import com.mescobar.blog.dto.request.CreateCommentRequest;
import com.mescobar.blog.dto.request.CreatePostRequest;
import com.mescobar.blog.dto.request.SearchPostRequest;
import com.mescobar.blog.dto.request.UpdateCommentRequest;
import com.mescobar.blog.dto.request.UpdatePostRequest;
import com.mescobar.blog.model.Comment;
import com.mescobar.blog.model.Post;
import com.mescobar.blog.repository.CommentRepository;
import com.mescobar.blog.repository.PostRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@AllArgsConstructor
public class BlogService {

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    public Uni<Post> createPost(CreatePostRequest createPostRequest){
        var postNew = Post.builder().creationDate(LocalDateTime.now())
                .author(createPostRequest.author())
                .title(createPostRequest.title())
                .content(createPostRequest.content())
                .build();

        return postRepository.persist(postNew);
    }


    public Uni<Post> updatePost(String id, UpdatePostRequest updatePost) {
        Uni<Post> postUni = postRepository .findById(new ObjectId(id));
        return postUni
                .onItem().transform(post -> {
                    post.setContent(updatePost.content());
                    post.setTitle(updatePost.title());
                    return post;
                }).call(postRepository::persistOrUpdate);
    }

    public Uni<Post> findById(String id){
        return postRepository.findById(new ObjectId(id));
    }

    public Uni<Post> addCommentToPost(CreateCommentRequest createCommentRequest, String postId) {
        Uni<Post> postUni = this.findById(postId);
        Comment comment = this.convertToEntity(createCommentRequest, postId);

        return postUni.onItem().transform(post -> {
            if (post.getComments() == null) {
                post.setComments(List.of(comment));
            } else {
                post.getComments().add(comment);
            }

            return post;
        }).call(post -> commentRepository.persist(comment).chain(() -> postRepository.persistOrUpdate(post)));
    }

    private Comment convertToEntity(CreateCommentRequest comment, String postId){
        return Comment.builder()
                .content(comment.content())
                .title(comment.title())
                .creationDate(LocalDateTime.now())
                .postId(postId)
                .build();
    }

    public Uni<List<Post>> searchPosts(SearchPostRequest searchPostRequest){
        if (searchPostRequest.getAuthor() != null) {
            return postRepository.find("{'author': ?1,'title': ?2}", searchPostRequest.getAuthor(), searchPostRequest.getTitle()).list();
        }

        return postRepository
                .find("{'creationDate': {$gte: ?1}, 'creationDate': {$lte: ?2}}",
                        ZonedDateTime.parse(searchPostRequest.getDateFrom()).toLocalDateTime(),
                        ZonedDateTime.parse(searchPostRequest.getDateTo()).toLocalDateTime()).list();
    }

    public Uni<List<Post>> searchPostsByAuthors(List<String> authors){
        return postRepository.find(new Document("author", new Document("$in", authors))).list();
    }

    public Uni<Void> deletePost(String postId) {
        Uni<Post> postUni = this.findById(postId);
        Multi<Comment> commentsUni = this.streamAllCommentsByPostId(postId);

        return postUni.call(post -> commentsUni.onItem()
                .call(commentRepository::delete)
                .collect().asList()).chain(post -> {
            if (post == null) {
                throw new NotFoundException();
            }
            return postRepository.delete(post);
        });
    }

    public Multi<Post> streamAllPosts() {
        return postRepository.streamAll();
    }


    public Uni<Comment> updateComment(String id, UpdateCommentRequest updateCommentRequest) {
        Uni<Comment> commentUni = commentRepository.findById(new ObjectId(id));

        return commentUni.call(comment -> {
            comment.content = updateCommentRequest.content();

            Uni<Post> uni = this.findById(comment.postId);
            return uni.call(posts -> {
                if (posts != null) {
                    Optional<Comment> com = posts.getComments().stream()
                            .filter(comment1 -> comment1.equals(comment)).findFirst();
                    com.ifPresent(value -> value.content = updateCommentRequest.content());
                }
                return Uni.createFrom().item(comment);
            }).chain(postRepository::persistOrUpdate);
        }).chain(comment -> {
            if (comment == null) {
                throw new NotFoundException();
            }
            return commentRepository.persistOrUpdate(comment);
        });
    }

    public Uni<Void> deleteComment(String commentId) {
        Uni<Comment> commentUni = this.findCommentById(commentId);

        return commentUni.call(comment -> {

            Uni<Post> uni = this.findById(comment.postId);
            return uni.call(posts -> {
                if (posts != null) {
                    posts.getComments().remove(comment);
                }
                return Uni.createFrom().item(comment);
            }).chain(postRepository::persistOrUpdate);
        }).chain(comment -> {
            if (comment == null) {
                throw new NotFoundException();
            }
            return commentRepository.delete(comment);
        });
    }

    public Uni<Comment> findCommentById(String id){
        return commentRepository.findById(new ObjectId(id));
    }

    public Multi<Comment> streamAllComments() {
        return commentRepository.streamAll();
    }

    public Multi<Comment> streamAllCommentsByPostId(String postId) {
        return commentRepository.stream("postId", postId);
    }

}
