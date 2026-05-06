package com.example.rescue_pets.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescue_pets.R;

public class User_Dashboard_Activity extends AppCompatActivity {

    Button btn_view_pets, btnRescue, btn_profile, btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);



        btn_view_pets = findViewById(R.id.admin_manage);
        btnRescue = findViewById(R.id.admin_view);
        btn_profile = findViewById(R.id.admin_set);
        btn_logout = findViewById(R.id.btnLogout);



        btn_view_pets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(User_Dashboard_Activity.this, RequestListActivity2.class);
                startActivity(intent);

            }
        });

        btnRescue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_Dashboard_Activity.this, Rescue_Field_Activity.class);
                startActivity(intent);
            }
        });

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(User_Dashboard_Activity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_Dashboard_Activity.this, User_login_activity.class);
                startActivity(intent);
            }
        });




    }
}