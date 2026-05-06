package com.example.rescue_pets.Volunteer;

public class StatusRequest {

    String status;

    public StatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}