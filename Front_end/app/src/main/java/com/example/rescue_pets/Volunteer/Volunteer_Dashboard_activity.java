package com.example.rescue_pets.Volunteer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescue_pets.R;

public class Volunteer_Dashboard_activity extends AppCompatActivity {

    Button btnView, btnRescue, btnEdit, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunterr_dashboard);

        // Match IDs exactly with XML
        btnView = findViewById(R.id.admin_manage);
        btnRescue = findViewById(R.id.admin_view);
        btnEdit = findViewById(R.id.admin_set);


        // View Requests
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Volunteer_Dashboard_activity.this, "View Requests", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(Volunteer_Dashboard_activity.this, ViewRequestsActivity.class));
            }
        });

        // Upload Pet (Rescue Field)
        btnRescue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Volunteer_Dashboard_activity.this, Rescue_Field_Activity.class);
                startActivity(intent);
            }
        });

        // Edit Profile
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Volunteer_Dashboard_activity.this, "Edit Profile", Toast.LENGTH_SHORT).show();
            }
        });

        // Logout

    }
}