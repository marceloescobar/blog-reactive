package com.mescobar.blog.repository;

import com.mescobar.blog.model.Post;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostRepository implements ReactivePanacheMongoRepository<Post> {
}
