package com.example.rescue_pets.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescue_pets.R;

public class Admin_Forgot_Password_Activity extends AppCompatActivity {

    EditText emailInput;
    Button resetBtn;
    TextView signInText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_forgot_password);

        emailInput = findViewById(R.id.admin_email);
        resetBtn = findViewById(R.id.btn_reset_password);
        signInText = findViewById(R.id.text_signin);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(Admin_Forgot_Password_Activity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Admin_Forgot_Password_Activity.this, "Password reset link sent to " + email, Toast.LENGTH_LONG).show();
                }
            }
        });

        // 🔗 Navigate back to Admin Login
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Forgot_Password_Activity.this, Admin_Login_Activity.class);
                startActivity(intent);
                finish(); // Optional: prevent user from coming back to forgot page on back press
            }
        });
    }
}
