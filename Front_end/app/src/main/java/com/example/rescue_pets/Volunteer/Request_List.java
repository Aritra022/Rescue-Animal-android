package com.example.rescue_pets.Volunteer;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rescue_pets.ApiClient;
import com.example.rescue_pets.ApiService;
import com.example.rescue_pets.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Request_List extends AppCompatActivity {

    RecyclerView recyclerView;
    RequestAdapter adapter;
    List<RequestModelV> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ApiService apiService = ApiClient
                .getClient()
                .create(ApiService.class);

        Call<List<RequestModelV>> call = apiService.getPendingRequests();

        call.enqueue(new Callback<List<RequestModelV>>() {
            @Override
            public void onResponse(Call<List<RequestModelV>> call, Response<List<RequestModelV>> response) {

                list = response.body();

                adapter = new RequestAdapter(Request_List.this, list);

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<RequestModelV>> call, Throwable t) {

                Toast.makeText(Request_List.this,
                        "API Failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}