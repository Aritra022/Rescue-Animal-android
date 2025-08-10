package com.example.rescue_pets.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescue_pets.R;

public class User_Dashboard_Activity extends AppCompatActivity {

    Button btn_view_pets, btn_request_adoption, btn_profile, btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        btn_view_pets = findViewById(R.id.admin_manage);
        btn_request_adoption = findViewById(R.id.admin_view);
        btn_profile = findViewById(R.id.admin_set);
        btn_logout = findViewById(R.id.btnLogout);

        btn_view_pets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(User_Dashboard_Activity.this, "View Available Pets", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(...));
            }
        });

        btn_request_adoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(User_Dashboard_Activity.this, "Request Adoption", Toast.LENGTH_SHORT).show();
            }
        });

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(User_Dashboard_Activity.this, "Profile", Toast.LENGTH_SHORT).show();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(User_Dashboard_Activity.this, "Logout", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
