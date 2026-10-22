package com.example.forum.web.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterForm {

    @NotBlank(message = "Pick a username")
    @Size(min = 3, max = 30, message = "Username must be 3-30 characters")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Use letters, numbers and underscores only")
    private String username;

    @NotBlank(message = "Enter your email")
    @Email(message = "Enter a valid email address")
    @Size(max = 255, message = "Email is too long")
    private String email;

    @NotBlank(message = "Choose a password")
    @Size(min = 8, max = 72, message = "Password must be 8-72 characters")
    private String password;

    @Size(max = 50, message = "Display name must be at most 50 characters")
    private String displayName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
