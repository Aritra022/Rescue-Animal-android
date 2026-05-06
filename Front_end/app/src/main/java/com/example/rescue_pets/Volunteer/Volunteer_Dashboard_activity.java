package com.example.rescue_pets.Volunteer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescue_pets.R;
import com.example.rescue_pets.user.Rescue_Field_Activity;

public class Volunteer_Dashboard_activity extends AppCompatActivity {

    Button btnView, btnRescue, btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunterr_dashboard);

        // Match IDs with XML
        btnView = findViewById(R.id.admin_manage);
        btnRescue = findViewById(R.id.admin_view);
        btnEdit = findViewById(R.id.admin_set);

        // 🔹 Open Request List
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Volunteer_Dashboard_activity.this, Request_List.class);
                startActivity(intent);

            }
        });



        // 🔹 Edit Profile
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Volunteer_Dashboard_activity.this, VolunteerProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}