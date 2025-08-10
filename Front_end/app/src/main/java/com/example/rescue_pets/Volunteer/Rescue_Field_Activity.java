package com.example.rescue_pets.Volunteer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rescue_pets.R;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Rescue_Field_Activity extends AppCompatActivity {

    EditText etPetType, etDescription, etAddress;
    Spinner spinnerStatus;
    ImageView ivPetImage;
    Button btnChooseImage, btnUploadPet;
    Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    // Change to your backend API
    private static final String UPLOAD_URL = "http://192.168.0.119:4000/pets/upload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue_field);

        etPetType = findViewById(R.id.et_pet_type);
        etDescription = findViewById(R.id.et_description);
        etAddress = findViewById(R.id.et_address);
        spinnerStatus = findViewById(R.id.spinner_status);
        ivPetImage = findViewById(R.id.iv_pet_image);
        btnChooseImage = findViewById(R.id.btn_choose_image);
        btnUploadPet = findViewById(R.id.btn_upload_pet);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Available", "Adopted", "Pending"}
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Pet Image"), PICK_IMAGE_REQUEST);
        });

        btnUploadPet.setOnClickListener(v -> uploadPetData());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK &&
                data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ivPetImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadPetData() {
        String petType = etPetType.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();

        if (petType.isEmpty() || description.isEmpty() || address.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and choose an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert Uri to File without FileUtils
        File file = null;
        String filePath = getRealPathFromURI(imageUri);
        if (filePath != null) {
            file = new File(filePath);
        }

        if (file == null || !file.exists()) {
            Toast.makeText(this, "Failed to get image file", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/*"));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("pet_Type", petType)
                .addFormDataPart("description", description)
                .addFormDataPart("address", address)
                .addFormDataPart("status", status)
                .addFormDataPart("image", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(Rescue_Field_Activity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(Rescue_Field_Activity.this, "Pet uploaded successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(Rescue_Field_Activity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Helper method to get file path from Uri
    private String getRealPathFromURI(Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                result = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return result;
    }
}
