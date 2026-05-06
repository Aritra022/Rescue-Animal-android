package com.example.rescue_pets.user;

public class UpdateProfileRequest {
    private String username;
    private String email;
    private String contact;
    private String location;

    public UpdateProfileRequest(String username, String email, String contact, String location) {
        this.username = username;
        this.email = email;
        this.contact = contact;
        this.location = location;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getLocation() {
        return location;
    }
}