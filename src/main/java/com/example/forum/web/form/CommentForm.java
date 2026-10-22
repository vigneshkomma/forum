package com.example.forum.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentForm {

    @NotBlank(message = "Write a comment first")
    @Size(max = 5000, message = "Comment is too long")
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
