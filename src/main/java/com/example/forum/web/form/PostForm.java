package com.example.forum.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostForm {

    @NotBlank(message = "Give your post a title")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    @NotBlank(message = "Write something first")
    @Size(max = 20000, message = "Post is too long")
    private String body;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
