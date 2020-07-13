package com.example.handshop.models;

public class posts {
    private String description;
    private String postId;
    private String postImage;
    private String publisher;

    public posts(String description, String postId, String postImage, String publisher) {
        this.description = description;
        this.postId = postId;
        this.postImage = postImage;
        this.publisher = publisher;
    }
    public posts(){

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
