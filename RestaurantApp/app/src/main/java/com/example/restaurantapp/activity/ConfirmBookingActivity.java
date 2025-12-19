package com.example.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ConfirmBookingActivity extends AppCompatActivity {
    
    private TextView btnBack, tvDateValue, tvTimeValue, tvPartySizeValue;
    private TextView tvSpecialRequests;
    private Button btnViewReservations, btnBackToHome;
    
    private String selectedDate;
    private String selectedTime;
    private int partySize;
    private String specialRequests;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);
        
        // Get data from intent (booking was already saved in BookTableActivity)
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        selectedTime = getIntent().getStringExtra("SELECTED_TIME");
        partySize = getIntent().getIntExtra("PARTY_SIZE", 4);
        specialRequests = getIntent().getStringExtra("SPECIAL_REQUESTS");
        
        initializeViews();
        populateSummary();
        setupClickListeners();
    }
    
    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        tvDateValue = findViewById(R.id.tv_date_value);
        tvTimeValue = findViewById(R.id.tv_time_value);
        tvPartySizeValue = findViewById(R.id.tv_party_value);
        
        // Find TextView in the special requests card
        tvSpecialRequests = findViewById(R.id.tv_special_requests_text);
        
        btnViewReservations = findViewById(R.id.btn_view_reservations);
        btnBackToHome = findViewById(R.id.btn_back_to_home);
    }
    
    private void populateSummary() {
        // Format date for display
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            java.util.Date date = inputFormat.parse(selectedDate);
            tvDateValue.setText(outputFormat.format(date));
        } catch (Exception e) {
            tvDateValue.setText(selectedDate);
        }
        
        tvTimeValue.setText(selectedTime);
        tvPartySizeValue.setText(partySize + " guests");
        
        // Show special requests if available
        if (tvSpecialRequests != null) {
            if (specialRequests != null && !specialRequests.isEmpty()) {
                tvSpecialRequests.setText(specialRequests);
            } else {
                tvSpecialRequests.setText("None");
            }
        }
    }
    
    private void setupClickListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                // Navigate back to home
                Intent intent = new Intent(this, GuestHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }
        
        if (btnViewReservations != null) {
            btnViewReservations.setOnClickListener(v -> {
                Intent intent = new Intent(this, MyReservationsActivity.class);
                startActivity(intent);
                finish();
            });
        }
        
        if (btnBackToHome != null) {
            btnBackToHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, GuestHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}

