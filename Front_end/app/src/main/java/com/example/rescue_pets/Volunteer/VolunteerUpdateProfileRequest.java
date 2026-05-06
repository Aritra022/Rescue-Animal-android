package com.example.rescue_pets.Volunteer;
import com.example.rescue_pets.Volunteer.VolunteerUpdateProfileRequest;

public class VolunteerUpdateProfileRequest {
    private String name;
    private String email;
    private String contact;
    private String location;

    public VolunteerUpdateProfileRequest(String name, String email, String contact, String location) {
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.location = location;
    }

    public String getName() {
        return name;
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