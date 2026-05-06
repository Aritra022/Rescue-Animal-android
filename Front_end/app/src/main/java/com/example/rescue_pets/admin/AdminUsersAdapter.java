package com.example.rescue_pets.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rescue_pets.ApiService;
import com.example.rescue_pets.R;
import com.example.rescue_pets.RetrofitClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUsersAdapter extends RecyclerView.Adapter<AdminUsersAdapter.ViewHolder> {

    Context context;
    List<UserManageModel> list;

    public AdminUsersAdapter(Context context, List<UserManageModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        UserManageModel user = list.get(position);

        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());

        holder.btnDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Delete User")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteUser(user.getId(), position))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void deleteUser(String id, int position) {

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.deleteUserByAdmin(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                list.remove(position);
                notifyItemRemoved(position);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, email;
        Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txtName);
            email = itemView.findViewById(R.id.txtEmail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}