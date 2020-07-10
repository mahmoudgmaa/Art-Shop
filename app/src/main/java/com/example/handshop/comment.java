package com.example.handshop;

public class comment {
    private String comment;
    private String publisher;

    public comment(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
    }

    public comment(){

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
