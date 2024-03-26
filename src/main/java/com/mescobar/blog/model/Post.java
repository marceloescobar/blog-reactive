package com.mescobar.blog.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@MongoEntity(collection="posts")
@Data
@Builder
public class Post {
    private ObjectId id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime creationDate;
    private List<Comment> comments;
}
