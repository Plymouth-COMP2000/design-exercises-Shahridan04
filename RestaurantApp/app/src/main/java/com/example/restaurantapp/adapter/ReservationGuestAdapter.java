package com.example.restaurantapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.R;
import com.example.restaurantapp.model.Reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservationGuestAdapter extends RecyclerView.Adapter<ReservationGuestAdapter.ReservationViewHolder> {
    
    private List<Reservation> reservations;
    private OnReservationClickListener onReservationClickListener;
    
    public ReservationGuestAdapter(List<Reservation> reservations) {
        this.reservations = reservations != null ? reservations : new ArrayList<>();
    }
    
    public void updateReservations(List<Reservation> newReservations) {
        this.reservations = newReservations != null ? newReservations : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void setOnReservationClickListener(OnReservationClickListener listener) {
        this.onReservationClickListener = listener;
    }
    
    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation_guest, parent, false);
        return new ReservationViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        
        // Status
        String status = reservation.getStatus() != null ? reservation.getStatus() : "pending";
        holder.tvStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1));
        
        // Set status color and indicator
        int statusColor;
        int indicatorColor;
        int statusBgColor;
        switch (status.toLowerCase()) {
            case "confirmed":
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.accent);
                indicatorColor = holder.itemView.getContext().getResources().getColor(R.color.accent);
                statusBgColor = holder.itemView.getContext().getResources().getColor(R.color.accent_light);
                break;
            case "pending":
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.warning);
                indicatorColor = holder.itemView.getContext().getResources().getColor(R.color.warning);
                statusBgColor = holder.itemView.getContext().getResources().getColor(R.color.warning_light);
                break;
            case "cancelled":
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.error);
                indicatorColor = holder.itemView.getContext().getResources().getColor(R.color.error);
                statusBgColor = holder.itemView.getContext().getResources().getColor(R.color.error_light);
                break;
            default:
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.text_secondary);
                indicatorColor = holder.itemView.getContext().getResources().getColor(R.color.text_secondary);
                statusBgColor = holder.itemView.getContext().getResources().getColor(R.color.background_secondary);
        }
        holder.tvStatus.setTextColor(statusColor);
        holder.tvStatus.setBackgroundColor(statusBgColor);
        holder.indicatorStatus.setBackgroundColor(indicatorColor);
        
        // Date
        String dateStr = reservation.getDate();
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            
            // Determine date label (Today, Tomorrow, etc.)
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            
            Calendar reservationDate = Calendar.getInstance();
            reservationDate.setTime(date);
            reservationDate.set(Calendar.HOUR_OF_DAY, 0);
            reservationDate.set(Calendar.MINUTE, 0);
            reservationDate.set(Calendar.SECOND, 0);
            reservationDate.set(Calendar.MILLISECOND, 0);
            
            long diffInMillis = reservationDate.getTimeInMillis() - today.getTimeInMillis();
            long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
            
            String dateLabel;
            if (diffInDays == 0) {
                dateLabel = "Today";
            } else if (diffInDays == 1) {
                dateLabel = "Tomorrow";
            } else if (diffInDays > 1 && diffInDays < 7) {
                dateLabel = "In " + diffInDays + " days";
            } else {
                dateLabel = outputFormat.format(date);
            }
            
            holder.tvDateLabel.setText(dateLabel);
            holder.tvDateValue.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.tvDateLabel.setText(dateStr);
            holder.tvDateValue.setText(dateStr);
        }
        
        // Time
        holder.tvTime.setText(reservation.getTime() != null ? reservation.getTime() : "N/A");
        
        // Party size
        holder.tvParty.setText("Party of " + reservation.getPartySize());
        
        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (onReservationClickListener != null) {
                onReservationClickListener.onReservationClick(reservation);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return reservations.size();
    }
    
    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        View indicatorStatus;
        TextView tvStatus, tvDateLabel, tvDateValue, tvTime, tvParty, tvDetailsHint;
        
        ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            indicatorStatus = itemView.findViewById(R.id.indicator_status);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDateLabel = itemView.findViewById(R.id.tv_date_label);
            tvDateValue = itemView.findViewById(R.id.tv_date_value);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvParty = itemView.findViewById(R.id.tv_party);
            tvDetailsHint = itemView.findViewById(R.id.tv_details_hint);
        }
    }
    
    public interface OnReservationClickListener {
        void onReservationClick(Reservation reservation);
    }
}

