package com.example.restaurantapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.R;
import com.example.restaurantapp.model.Reservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {
    
    private List<Reservation> reservations;
    private OnViewClickListener onViewClickListener;
    private OnCancelClickListener onCancelClickListener;
    
    public ReservationAdapter(List<Reservation> reservations) {
        this.reservations = reservations != null ? reservations : new ArrayList<>();
    }
    
    public void updateReservations(List<Reservation> newReservations) {
        this.reservations = newReservations != null ? newReservations : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void setOnViewClickListener(OnViewClickListener listener) {
        this.onViewClickListener = listener;
    }
    
    public void setOnCancelClickListener(OnCancelClickListener listener) {
        this.onCancelClickListener = listener;
    }
    
    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        
        holder.tvName.setText(reservation.getGuestName());
        holder.tvTime.setText(reservation.getTime());
        
        // Format details: Party size and table
        String details = "Party of " + reservation.getPartySize();
        if (reservation.getTableAssigned() != null && !reservation.getTableAssigned().isEmpty()) {
            details += " • Table " + reservation.getTableAssigned();
        }
        holder.tvDetails.setText(details);
        
        // Status with color
        String status = reservation.getStatus();
        if (status == null) status = "pending";
        holder.tvStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1));
        
        // Set status color
        int statusColor;
        switch (status.toLowerCase()) {
            case "confirmed":
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.accent);
                break;
            case "pending":
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.primary);
                break;
            case "cancelled":
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.error);
                break;
            default:
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.text_secondary);
        }
        holder.tvStatus.setTextColor(statusColor);
        
        // Click listeners
        holder.btnView.setOnClickListener(v -> {
            if (onViewClickListener != null) {
                onViewClickListener.onViewClick(reservation);
            }
        });
        
        holder.btnCancel.setOnClickListener(v -> {
            if (onCancelClickListener != null) {
                onCancelClickListener.onCancelClick(reservation);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return reservations.size();
    }
    
    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvDetails, tvStatus;
        Button btnView, btnCancel;
        
        ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_res_name);
            tvTime = itemView.findViewById(R.id.tv_res_time);
            tvDetails = itemView.findViewById(R.id.tv_res_details);
            tvStatus = itemView.findViewById(R.id.tv_res_status);
            btnView = itemView.findViewById(R.id.btn_view);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
        }
    }
    
    public interface OnViewClickListener {
        void onViewClick(Reservation reservation);
    }
    
    public interface OnCancelClickListener {
        void onCancelClick(Reservation reservation);
    }
}

