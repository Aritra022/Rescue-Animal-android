package com.example.rescue_pets.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescue_pets.R;

public class Admin_Dashboard_Activity extends AppCompatActivity {

    TextView admin_manage, admin_view, admin_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        admin_manage = findViewById(R.id.admin_manage);
        admin_view = findViewById(R.id.admin_view);
        admin_set = findViewById(R.id.admin_set);

        admin_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Admin_Dashboard_Activity.this, "Manage Pets/Users", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(...));
            }
        });

        admin_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Admin_Dashboard_Activity.this, "View Reports/Data", Toast.LENGTH_SHORT).show();
            }
        });

        admin_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Admin_Dashboard_Activity.this, "Settings", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
