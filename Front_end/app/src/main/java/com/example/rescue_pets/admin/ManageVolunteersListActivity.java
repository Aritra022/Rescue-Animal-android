package com.example.rescue_pets.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rescue_pets.ApiService;
import com.example.rescue_pets.R;
import com.example.rescue_pets.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageVolunteersListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdminVolunteersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_volunteers);

        recyclerView = findViewById(R.id.recyclerVolunteers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadVolunteers();
    }

    private void loadVolunteers() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getAllVolunteers().enqueue(new Callback<List<VolunteerManageModel>>() {
            @Override
            public void onResponse(Call<List<VolunteerManageModel>> call, Response<List<VolunteerManageModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new AdminVolunteersAdapter(ManageVolunteersListActivity.this, response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ManageVolunteersListActivity.this, "Failed to load volunteers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<VolunteerManageModel>> call, Throwable t) {
                Toast.makeText(ManageVolunteersListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}