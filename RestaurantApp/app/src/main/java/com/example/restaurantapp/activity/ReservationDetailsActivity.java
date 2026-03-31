package com.example.restaurantapp.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.R;
import com.example.restaurantapp.database.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservationDetailsActivity extends AppCompatActivity {
    
    private TextView btnBack, tvGuestName;
    private TextView tvDateTimeValue, tvPartyValue, tvTableValue;
    private TextView tvContactEmail, tvContactPhone;
    private TextView tvRequest1, tvRequest2;
    private Button btnCancelReservation;
    
    private AppDatabase database;
    private ExecutorService executorService;
    private int reservationId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_details);
        
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        // Get data from intent
        reservationId = getIntent().getIntExtra("RESERVATION_ID", -1);
        String guestName = getIntent().getStringExtra("GUEST_NAME");
        String guestEmail = getIntent().getStringExtra("GUEST_EMAIL");
        String guestContact = getIntent().getStringExtra("GUEST_CONTACT");
        String date = getIntent().getStringExtra("DATE");
        String time = getIntent().getStringExtra("TIME");
        int partySize = getIntent().getIntExtra("PARTY_SIZE", 0);
        String specialRequests = getIntent().getStringExtra("SPECIAL_REQUESTS");
        String status = getIntent().getStringExtra("STATUS");
        String tableAssigned = getIntent().getStringExtra("TABLE_ASSIGNED");
        
        initializeViews();
        populateData(guestName, guestEmail, guestContact, date, time, partySize, specialRequests, status, tableAssigned);
        setupClickListeners();
    }
    
    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        tvGuestName = findViewById(R.id.tv_guest_name);
        tvDateTimeValue = findViewById(R.id.tv_datetime_value);
        tvPartyValue = findViewById(R.id.tv_party_value);
        tvTableValue = findViewById(R.id.tv_table_value);
        tvContactEmail = findViewById(R.id.tv_contact_email);
        tvContactPhone = findViewById(R.id.tv_contact_phone);
        tvRequest1 = findViewById(R.id.tv_request_1);
        tvRequest2 = findViewById(R.id.tv_request_2);
        btnCancelReservation = findViewById(R.id.btn_cancel_reservation);
    }
    
    private void populateData(String guestName, String guestEmail, String guestContact,
                              String date, String time, int partySize,
                              String specialRequests, String status, String tableAssigned) {
        if (tvGuestName != null) tvGuestName.setText(guestName != null ? guestName : "N/A");
        
        // Format date and time
        String dateTimeText = "";
        if (date != null && time != null) {
            dateTimeText = date + " at " + time;
        } else if (date != null) {
            dateTimeText = date;
        } else if (time != null) {
            dateTimeText = time;
        } else {
            dateTimeText = "N/A";
        }
        if (tvDateTimeValue != null) tvDateTimeValue.setText(dateTimeText);
        
        if (tvPartyValue != null) tvPartyValue.setText(partySize + " guests");
        if (tvTableValue != null) tvTableValue.setText(tableAssigned != null && !tableAssigned.isEmpty() ? "Table " + tableAssigned : "Not assigned");
        
        if (tvContactEmail != null) {
            String emailText = guestEmail != null ? "📧 " + guestEmail : "📧 N/A";
            tvContactEmail.setText(emailText);
        }
        
        if (tvContactPhone != null) {
            String phoneText = guestContact != null ? "📞 " + guestContact : "📞 N/A";
            tvContactPhone.setText(phoneText);
        }
        
        // Special requests - split into two lines if needed
        if (specialRequests != null && !specialRequests.isEmpty()) {
            String[] requests = specialRequests.split("\n");
            if (tvRequest1 != null) {
                tvRequest1.setText("• " + (requests.length > 0 ? requests[0] : specialRequests));
                tvRequest1.setVisibility(android.view.View.VISIBLE);
            }
            if (tvRequest2 != null) {
                if (requests.length > 1) {
                    tvRequest2.setText("• " + requests[1]);
                    tvRequest2.setVisibility(android.view.View.VISIBLE);
                } else {
                    tvRequest2.setVisibility(android.view.View.GONE);
                }
            }
        } else {
            if (tvRequest1 != null) {
                tvRequest1.setText("• None");
                tvRequest1.setVisibility(android.view.View.VISIBLE);
            }
            if (tvRequest2 != null) {
                tvRequest2.setVisibility(android.view.View.GONE);
            }
        }
    }
    
    private void setupClickListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        
        if (btnCancelReservation != null) {
            btnCancelReservation.setOnClickListener(v -> showCancelConfirmation());
        }
    }
    
    private void showCancelConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?")
                .setPositiveButton("Cancel Reservation", (dialog, which) -> cancelReservation())
                .setNegativeButton("Keep", null)
                .show();
    }
    
    private void cancelReservation() {
        if (reservationId == -1) {
            Toast.makeText(this, "Error: Reservation ID not found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get reservation details for notification
        String date = getIntent().getStringExtra("DATE");
        String time = getIntent().getStringExtra("TIME");
        String guestEmail = getIntent().getStringExtra("GUEST_EMAIL");
        
        executorService.execute(() -> {
            database.reservationDao().updateReservationStatus(reservationId, "cancelled");
            
            runOnUiThread(() -> {
                Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                
                // Send notification to guest about cancellation
                com.example.restaurantapp.utils.NotificationHelper notificationHelper = 
                    new com.example.restaurantapp.utils.NotificationHelper(getApplicationContext());
                String message = "Your reservation for " + date + " at " + time + " has been cancelled.";
                notificationHelper.showReservationUpdateNotification(message, "Cancelled");
                
                finish();
            });
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}

