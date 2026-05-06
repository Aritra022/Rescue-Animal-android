package com.example.rescue_pets.Volunteer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rescue_pets.ApiClient;
import com.example.rescue_pets.ApiService;
import com.example.rescue_pets.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    Context context;
    List<RequestModelV> list;

    public RequestAdapter(Context context, List<RequestModelV> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestModelV model = list.get(position);

        holder.petName.setText(model.getPetType());
        holder.description.setText(model.getDescription());

        // If you have a TextView with id txtLocation in XML, keep this line.
        // Otherwise remove both this line and the "location" variable from ViewHolder.
        // holder.location.setText(model.getAddress());

        Glide.with(context)
                .load(model.getImage())
                .into(holder.image);

        holder.btnViewLocation.setOnClickListener(v -> {
            double lat = model.getLatitude();
            double lng = model.getLongitude();

            if (lat == 0.0 && lng == 0.0) {
                Toast.makeText(context, "Location not found for this request", Toast.LENGTH_SHORT).show();
                return;
            }

            String uri = "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(intent);
        });

        holder.btnAccept.setOnClickListener(v -> {
            updateStatus(model.getId(), "Accepted", position);
        });

        holder.btnReject.setOnClickListener(v -> {

            int currentPosition = holder.getBindingAdapterPosition();

            if (currentPosition != RecyclerView.NO_POSITION) {
                list.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyDataSetChanged();

                Toast.makeText(context, "Request skipped", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    private void updateStatus(String id, String status, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        StatusRequest req = new StatusRequest(status);

        apiService.updateStatus(id, req).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call,
                                   Response<okhttp3.ResponseBody> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(context, "Marked as " + status, Toast.LENGTH_SHORT).show();

                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                } else {
                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView petName, description;
        // TextView location; // Uncomment only if your XML has txtLocation
        Button btnAccept, btnReject, btnViewLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imgPet);
            petName = itemView.findViewById(R.id.txtPetName);
            description = itemView.findViewById(R.id.txtDescription);

            // location = itemView.findViewById(R.id.txtLocation); // Uncomment if exists in XML

            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnViewLocation = itemView.findViewById(R.id.btnViewLocation);
        }
    }

    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}