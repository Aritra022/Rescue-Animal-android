package com.example.rescue_pets.user;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.rescue_pets.ApiService;
import com.example.rescue_pets.MyIP;
import com.example.rescue_pets.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Rescue_Field_Activity extends AppCompatActivity {

    private TextInputEditText PetType, PetDescription, PetAddress;
    private Spinner PetStatus;
    private ImageView ivPetImage;
    private Button btnUploadPet, btnSubmitPet;

    private Uri imageUri;
    private ApiService apiService;

    // 🔥 Location
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude = 0.0;
    private double longitude = 0.0;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;
                    ivPetImage.setImageURI(uri);
                } else {
                    Toast.makeText(Rescue_Field_Activity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fine = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarse = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                if (Boolean.TRUE.equals(fine) || Boolean.TRUE.equals(coarse)) {
                    fetchCurrentLocation();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue_field);

        PetType = findViewById(R.id.PetType);
        PetDescription = findViewById(R.id.PetDescription);
        PetAddress = findViewById(R.id.PetAddress);
        PetStatus = findViewById(R.id.PetStatus);
        ivPetImage = findViewById(R.id.iv_pet_image);
        btnUploadPet = findViewById(R.id.btnUploadPet);
        btnSubmitPet = findViewById(R.id.btnSubmitPet);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermissionAndFetch();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyIP.IP_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.status_type,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PetStatus.setAdapter(adapter);

        btnUploadPet.setOnClickListener(v -> openGallery());
        btnSubmitPet.setOnClickListener(v -> uploadPet());
    }

    private void checkLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void fetchCurrentLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            Log.d("Rescue_Field", "Location fetched: " + latitude + ", " + longitude);
                        } else {
                            Toast.makeText(this, "Unable to fetch current location", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
                        Log.e("Rescue_Field", "Location error", e);
                    });
        } catch (Exception e) {
            Log.e("Rescue_Field", "Location fetch exception", e);
        }
    }

    private void openGallery() {
        imagePickerLauncher.launch("image/*");
    }

    private void uploadPet() {
        String petType = getText(PetType);
        String description = getText(PetDescription);
        String address = getText(PetAddress);
        String status = PetStatus.getSelectedItem() != null ? PetStatus.getSelectedItem().toString().trim() : "";

        if (petType.isEmpty()) {
            PetType.setError("Enter pet type");
            PetType.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            PetDescription.setError("Enter description");
            PetDescription.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            PetAddress.setError("Enter address");
            PetAddress.requestFocus();
            return;
        }

        if (status.isEmpty() || status.equalsIgnoreCase("choose")) {
            Toast.makeText(this, "Please select status", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (latitude == 0.0 && longitude == 0.0) {
            Toast.makeText(this, "Location not available yet. Please wait and try again.", Toast.LENGTH_SHORT).show();
            checkLocationPermissionAndFetch();
            return;
        }

        String userEmail = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getString("userEmail", "");

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "User email not found. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        File file;
        try {
            file = FileUtil.from(this, imageUri);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to read image", Toast.LENGTH_SHORT).show();
            Log.e("Rescue_Field", "File read error", e);
            return;
        }

        String mimeType = getContentResolver().getType(imageUri);
        if (mimeType == null || mimeType.isEmpty()) {
            mimeType = "image/jpeg";
        }

        RequestBody petTypeBody = RequestBody.create(petType, MediaType.parse("text/plain"));
        RequestBody descriptionBody = RequestBody.create(description, MediaType.parse("text/plain"));
        RequestBody addressBody = RequestBody.create(address, MediaType.parse("text/plain"));
        RequestBody statusBody = RequestBody.create(status, MediaType.parse("text/plain"));
        RequestBody emailBody = RequestBody.create(userEmail, MediaType.parse("text/plain"));
        RequestBody latitudeBody = RequestBody.create(String.valueOf(latitude), MediaType.parse("text/plain"));
        RequestBody longitudeBody = RequestBody.create(String.valueOf(longitude), MediaType.parse("text/plain"));
        RequestBody imageBody = RequestBody.create(file, MediaType.parse(mimeType));

        MultipartBody.Part imagePart =
                MultipartBody.Part.createFormData("image", file.getName(), imageBody);

        btnSubmitPet.setEnabled(false);

        Call<ResponseBody> call = apiService.uploadPet(
                petTypeBody,
                descriptionBody,
                addressBody,
                statusBody,
                emailBody,
                latitudeBody,
                longitudeBody,
                imagePart
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                btnSubmitPet.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(Rescue_Field_Activity.this, "Pet uploaded successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    String errorMessage = "Upload failed: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage += " " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("Rescue_Field", "Error reading server error", e);
                    }

                    Toast.makeText(Rescue_Field_Activity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("Rescue_Field", errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                btnSubmitPet.setEnabled(true);
                Toast.makeText(Rescue_Field_Activity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Rescue_Field", "Upload request failed", t);
            }
        });
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void clearFields() {
        PetType.setText("");
        PetDescription.setText("");
        PetAddress.setText("");
        PetStatus.setSelection(0);
        ivPetImage.setImageDrawable(null);
        imageUri = null;
    }
}