package com.example.rescue_pets.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rescue_pets.ApiService;
import com.example.rescue_pets.R;
import com.example.rescue_pets.RetrofitClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminVolunteersAdapter extends RecyclerView.Adapter<AdminVolunteersAdapter.ViewHolder> {

    Context context;
    List<VolunteerManageModel> list;

    public AdminVolunteersAdapter(Context context, List<VolunteerManageModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VolunteerManageModel volunteer = list.get(position);

        holder.name.setText(volunteer.getName());
        holder.email.setText(volunteer.getEmail());

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Volunteer")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteVolunteer(volunteer.getId(), position))
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        if(volunteer.getStatus().equals("blocked")){
            holder.btnBlock.setText("Activate");
        }else{
            holder.btnBlock.setText("Block");
        }

        holder.btnBlock.setOnClickListener(v -> {

            if(volunteer.getStatus().equals("blocked")){
               // Toast.makeText(context, "blocked", Toast.LENGTH_LONG).show();
                activateVolunteer(volunteer.getId(), position);
            }else{
                blockVolunteer(volunteer.getId(), position);
                //Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
            }

        });
    }

    private void blockVolunteer(String id, int position) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.blockVolunteer(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    list.get(position).setStatus("blocked");
                    notifyItemChanged(position);
                    Toast.makeText(context, "Volunteer blocked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Block failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void activateVolunteer(String id, int position) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.activateVolunteer(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    list.get(position).setStatus("active");
                    notifyItemChanged(position);
                    Toast.makeText(context, "Volunteer activated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Activate failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteVolunteer(String id, int position) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.deleteVolunteerByAdmin(id).enqueue(new Callback<ResponseBody>() {
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
        TextView name, email , btnBlock ;
        Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtName);
            email = itemView.findViewById(R.id.txtEmail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnBlock = itemView.findViewById(R.id.btnBlock);
        }
    }
}