package com.example.rescue_pets.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescue_pets.R;
import android.content.Intent;


import android.content.Intent;

public class Admin_Dashboard_Activity extends AppCompatActivity {

    TextView admin_manage, admin_view, admin_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        admin_manage = findViewById(R.id.admin_manage);
        admin_view = findViewById(R.id.admin_view);
        admin_set = findViewById(R.id.admin_set);

        admin_manage.setOnClickListener(v -> {
            startActivity(new Intent(
                    Admin_Dashboard_Activity.this,
                    ManageUsersListActivity.class
            ));
        });
        admin_view.setOnClickListener(v -> {
            startActivity(new Intent(
                    Admin_Dashboard_Activity.this,
                    ManageRequestsListActivity.class
            ));
        });
        admin_set.setOnClickListener(v -> {
            startActivity(new Intent(
                    Admin_Dashboard_Activity.this,
                    ManageVolunteersListActivity.class
            ));
        });
    }
}
