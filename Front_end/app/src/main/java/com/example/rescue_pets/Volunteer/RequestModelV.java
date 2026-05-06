package com.example.rescue_pets.Volunteer;

import com.google.gson.annotations.SerializedName;

public class RequestModelV {

    @SerializedName("_id")
    private String id;

    @SerializedName("pet_Type")
    private String petType;

    @SerializedName("address")
    private String address;

    @SerializedName("description")
    private String description;

    @SerializedName("status")
    private String status;

    @SerializedName("image")
    private String image;

    public String getId() {
        return id;
    }

    public String getPetType() {
        return petType;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }

    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}