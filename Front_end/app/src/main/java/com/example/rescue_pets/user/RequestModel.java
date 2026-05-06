package com.example.rescue_pets.user;

import com.google.gson.annotations.SerializedName;

public class RequestModel {

    @SerializedName("pet_Type")
    private String petType;

    @SerializedName("description")
    private String description;

    @SerializedName("address")
    private String address;

    @SerializedName("status")
    private String status;

    @SerializedName("image")
    private String image;

    // ✅ Correct getters
    public String getPetType() {
        return petType;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }
}