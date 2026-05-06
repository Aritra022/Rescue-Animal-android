package com.example.rescue_pets.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescue_pets.ApiService;
import com.example.rescue_pets.R;
import com.example.rescue_pets.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    EditText etName, etEmail, etPhone, etLocation;
    Button btnUpdateProfile;
    ApiService apiService;
    ProgressDialog progressDialog;

    String id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etLocation = findViewById(R.id.etLocation);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        loadSavedUserData();

        btnUpdateProfile.setOnClickListener(v -> updateProfile());
    }

    private void loadSavedUserData() {
        Context context = getApplicationContext();
        id = context.getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("_id", "");
        String name = context.getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("username", "");
        String email = context.getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("userEmail", "");
        String phone = context.getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("userPhone", "");
        String location = context.getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("userLocation", "");

        etName.setText(name);
        etEmail.setText(email);
        etPhone.setText(phone);
        etLocation.setText(location);
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        UpdateProfileRequest request = new UpdateProfileRequest(name, email, phone, location);

        apiService.updateUserProfile(id, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            .edit()
                            .putString("_id", id)
                            .putString("userEmail", email)
                            .putString("userPhone", phone)
                            .putString("userLocation", location)
                            .putString("username", name)
                            .apply();

                    Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}