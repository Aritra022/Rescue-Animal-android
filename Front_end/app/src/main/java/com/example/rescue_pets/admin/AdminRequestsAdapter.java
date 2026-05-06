package com.example.rescue_pets.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rescue_pets.ApiService;
import com.example.rescue_pets.R;
import com.example.rescue_pets.RetrofitClient;
import com.example.rescue_pets.Volunteer.RequestModelV;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRequestsAdapter extends RecyclerView.Adapter<AdminRequestsAdapter.ViewHolder> {

    Context context;
    List<RequestModelV> list;

    public AdminRequestsAdapter(Context context, List<RequestModelV> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RequestModelV request = list.get(position);

        holder.txtPetType.setText(request.getPetType());
        holder.txtAddress.setText(request.getAddress());
        holder.txtDescription.setText(request.getDescription());
        holder.txtStatus.setText(request.getStatus());

        Glide.with(context)
                .load(request.getImage())
                .placeholder(R.drawable.dog_removebg_preview)
                .error(R.drawable.dog_removebg_preview)
                .into(holder.imgPet);

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Request")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteRequest(request.getId(), position))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void deleteRequest(String id, int position) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.deleteRequestByAdmin(id).enqueue(new Callback<ResponseBody>() {
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
        ImageView imgPet;
        TextView txtPetType, txtAddress, txtDescription, txtStatus;
        Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            imgPet = itemView.findViewById(R.id.imgPet);
            txtPetType = itemView.findViewById(R.id.txtPetType);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}