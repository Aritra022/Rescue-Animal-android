package com.example.rescue_pets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescue_pets.Volunteer.Volunteer_login_activity;
import com.example.rescue_pets.admin.Admin_Login_Activity;
import com.example.rescue_pets.user.User_login_activity;

public class ChooseActivity extends AppCompatActivity {

    Button btnUser, btnAdmin, btnVolunteer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        btnUser = findViewById(R.id.admin_manage);
        btnAdmin = findViewById(R.id.admin_view);
        btnVolunteer = findViewById(R.id.admin_set);

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(ChooseActivity.this, User_login_activity.class);
                startActivity(userIntent);
            }
        });

        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adminIntent = new Intent(ChooseActivity.this, Admin_Login_Activity.class);
                startActivity(adminIntent);
            }
        });

        btnVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent volunteerIntent = new Intent(ChooseActivity.this, Volunteer_login_activity.class);
                startActivity(volunteerIntent);
            }
        });
    }
}
