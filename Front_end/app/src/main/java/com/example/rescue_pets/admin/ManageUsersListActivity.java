package com.example.rescue_pets.admin;

import android.os.Bundle;

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

public class ManageUsersListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdminUsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        recyclerView = findViewById(R.id.recyclerUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUsers();
    }

    private void loadUsers() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getAllUsers().enqueue(new Callback<List<UserManageModel>>() {
            @Override
            public void onResponse(Call<List<UserManageModel>> call, Response<List<UserManageModel>> response) {
                adapter = new AdminUsersAdapter(ManageUsersListActivity.this, response.body());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<UserManageModel>> call, Throwable t) {

            }
        });
    }
}