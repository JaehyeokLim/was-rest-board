package main.java.com.jaehyeoklim.wasrestboard.board.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Post {

    private final UUID id;
    private final String owner;
    private final LocalDateTime createdAt;
    private String title;
    private String content;

    public Post(UUID id, String owner, LocalDateTime createdAt, String title, String content) {
        this.id = id;
        this.owner = owner;
        this.createdAt = createdAt;
        this.title = title;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return String.format("Post[UUID: %s, Owner: %s, Title: %s, Content: %s]", id, owner, title, content);
    }
}
