package com.example.rescue_pets.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rescue_pets.ApiService;
import com.example.rescue_pets.R;
import com.example.rescue_pets.RetrofitClient;
import com.example.rescue_pets.Volunteer.RequestModelV;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageRequestsListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdminRequestsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_requests);

        recyclerView = findViewById(R.id.recyclerRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadRequests();
    }

    private void loadRequests() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getAllAdminRequests().enqueue(new Callback<List<RequestModelV>>() {
            @Override
            public void onResponse(Call<List<RequestModelV>> call, Response<List<RequestModelV>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new AdminRequestsAdapter(ManageRequestsListActivity.this, response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ManageRequestsListActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RequestModelV>> call, Throwable t) {
                Toast.makeText(ManageRequestsListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}