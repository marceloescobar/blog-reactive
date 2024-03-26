package com.mescobar.blog.repository;

import com.mescobar.blog.model.Comment;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CommentRepository implements ReactivePanacheMongoRepository<Comment> {
}
