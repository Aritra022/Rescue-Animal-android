package com.example.rescue_pets.user;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rescue_pets.ApiService;
import com.example.rescue_pets.R;
import com.example.rescue_pets.RetrofitClient;
import com.example.rescue_pets.Volunteer.RequestModelV;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestListActivity2 extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestAdapterUser adapter;
    private List<RequestModelV> list;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list2);

        recyclerView = findViewById(R.id.recyclerViewReports);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new RequestAdapterUser(this, list);
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        fetchRequests();
    }

    private void fetchRequests() {
        String userEmail = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getString("userEmail", "");

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "User email not found. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        Call<List<RequestModelV>> call = apiService.getUserRequests(userEmail);

        call.enqueue(new Callback<List<RequestModelV>>() {
            @Override
            public void onResponse(@NonNull Call<List<RequestModelV>> call,
                                   @NonNull Response<List<RequestModelV>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    list.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(RequestListActivity2.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RequestModelV>> call, @NonNull Throwable t) {
                Toast.makeText(RequestListActivity2.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}