package com.example.rescue_pets;
import com.example.rescue_pets.Volunteer.VolunteerUpdateProfileRequest;

import com.example.rescue_pets.admin.UserManageModel;
import com.example.rescue_pets.user.UpdateProfileRequest;
import com.example.rescue_pets.admin.VolunteerManageModel;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;

import com.example.rescue_pets.Volunteer.RequestModelV;
import com.example.rescue_pets.Volunteer.StatusRequest;
import com.example.rescue_pets.user.UpdateProfileRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

public interface ApiService {

    // ✅ Upload Pet (with userEmail added)
    @Multipart
    @POST("pets/upload/")
    Call<ResponseBody> uploadPet(
            @Part("pet_Type") RequestBody petType,
            @Part("description") RequestBody description,
            @Part("address") RequestBody address,
            @Part("status") RequestBody status,
            @Part("userEmail") RequestBody userEmail,
            @Part("latitude") RequestBody latitude,     // ✅ NEW
            @Part("longitude") RequestBody longitude,       // ✅ NEW
            @Part MultipartBody.Part image
    );

    // ✅ Get all requests (for user - optional now)
    @GET("pets/get_all_pets")
    Call<List<RequestModelV>> getRequests();

    // ✅ Get only pending (for volunteer)
    @GET("pets/get_pending_pets")
    Call<List<RequestModelV>> getPendingRequests();

    // ✅ Get only user's requests
    @GET("pets/get_user_pets/{email}")
    Call<List<RequestModelV>> getUserRequests(
            @Path("email") String email
    );


    // ✅ Update status (Accept / Reject)
    @PATCH("pets/update_pets/{id}")
    Call<ResponseBody> updateStatus(
            @Path("id") String id,
            @Body StatusRequest statusRequest
    );

    @FormUrlEncoded
    @POST("user/save-fcm-token")
    Call<ResponseBody> saveFcmToken(
            @Field("email") String email,
            @Field("fcmToken") String fcmToken
    );

    @PATCH("vol/update/{id}")
    Call<ResponseBody> updateVolunteerProfile(
            @Path("id") String id,
            @Body VolunteerUpdateProfileRequest request
    );

    @PATCH("user/update/{id}")
    Call<ResponseBody> updateUserProfile(@Path("id") String id,@Body UpdateProfileRequest request);

    // ================= ADMIN =================

    // get all users
    @GET("admin/all-users")
    Call<List<UserManageModel>> getAllUsers();

    // get all volunteers
    @GET("admin/all-volunteers")
    Call<List<VolunteerManageModel>> getAllVolunteers();

    // get all requests
    @GET("admin/all-requests")
    Call<List<RequestModelV>> getAllAdminRequests();

    // delete user
    @retrofit2.http.DELETE("admin/delete-user/{id}")
    Call<ResponseBody> deleteUserByAdmin(@Path("id") String id);

    // delete volunteer
    @retrofit2.http.DELETE("admin/delete-volunteer/{id}")
    Call<ResponseBody> deleteVolunteerByAdmin(@Path("id") String id);

    // delete request
    @retrofit2.http.DELETE("admin/delete-request/{id}")
    Call<ResponseBody> deleteRequestByAdmin(@Path("id") String id);


    @PATCH("admin/block-volunteer/{id}")
    Call<ResponseBody> blockVolunteer(@Path("id") String id);

    @PATCH("admin/activate-volunteer/{id}")
    Call<ResponseBody> activateVolunteer(@Path("id") String id);



    //Call<ResponseBody> saveFcmToken(String email, String token);
}