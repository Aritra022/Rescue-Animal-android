package com.example.rescue_pets.user;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rescue_pets.R;
import com.example.rescue_pets.Volunteer.RequestModelV;

import java.util.List;

public class RequestAdapterUser extends RecyclerView.Adapter<RequestAdapterUser.ViewHolder> {

    private Context context;
    private List<RequestModelV> list;

    public RequestAdapterUser(Context context, List<RequestModelV> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestModelV model = list.get(position);

        holder.petType.setText(model.getPetType());
        holder.address.setText(model.getAddress());
        holder.description.setText(model.getDescription());

        Glide.with(context)
                .load(model.getImage())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.image);

        String status = model.getStatus();
        holder.status.setText(status);

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(50f);

        if ("Accepted".equalsIgnoreCase(status)) {
            holder.status.setTextColor(Color.parseColor("#15803D"));
            bg.setColor(Color.parseColor("#DCFCE7"));
        } else if ("Rejected".equalsIgnoreCase(status)) {
            holder.status.setTextColor(Color.parseColor("#B91C1C"));
            bg.setColor(Color.parseColor("#FEE2E2"));
        } else {
            holder.status.setTextColor(Color.parseColor("#A16207"));
            bg.setColor(Color.parseColor("#FEF3C7"));
        }

        holder.status.setBackground(bg);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView petType, address, description, status;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imgPet);
            petType = itemView.findViewById(R.id.tvPetType);
            address = itemView.findViewById(R.id.tvAddress);
            description = itemView.findViewById(R.id.tvDescription);
            status = itemView.findViewById(R.id.tvStatus);
        }
    }
}