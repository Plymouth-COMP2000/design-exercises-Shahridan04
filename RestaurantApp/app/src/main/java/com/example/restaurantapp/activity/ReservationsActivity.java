package com.example.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.R;
import com.example.restaurantapp.adapter.ReservationAdapter;
import com.example.restaurantapp.database.AppDatabase;
import com.example.restaurantapp.model.Reservation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservationsActivity extends AppCompatActivity {
    
    private TextView btnBack, tvDate, tvReservationsTitle;
    private EditText etSearch;
    private RecyclerView rvReservations;
    private TextView navHome, navMenu, navBookings, navSettings;
    
    private ReservationAdapter adapter;
    private AppDatabase database;
    private ExecutorService executorService;
    private String selectedDate;
    private List<Reservation> allReservations = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);
        
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        initializeViews();
        setupRecyclerView();
        setupDateSelector();
        setupSearch();
        setupClickListeners();
        setupBottomNavigation();
        
        // Load reservations for today
        loadReservations(selectedDate);
    }
    
    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        tvDate = findViewById(R.id.tv_date);
        tvReservationsTitle = findViewById(R.id.tv_reservations_title);
        etSearch = findViewById(R.id.et_search);
        rvReservations = findViewById(R.id.rv_reservations);
        
        // Bottom navigation
        navHome = findViewById(R.id.nav_home);
        navMenu = findViewById(R.id.nav_menu);
        navBookings = findViewById(R.id.nav_bookings);
        navSettings = findViewById(R.id.nav_settings);
        
        // Set today's date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date());
        
        // Format date for display
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        tvDate.setText(displayFormat.format(new Date()));
    }
    
    private void setupRecyclerView() {
        adapter = new ReservationAdapter(new ArrayList<>());
        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        rvReservations.setAdapter(adapter);
        
        // Set click listeners
        adapter.setOnViewClickListener(reservation -> {
            Intent intent = new Intent(ReservationsActivity.this, ReservationDetailsActivity.class);
            intent.putExtra("RESERVATION_ID", reservation.getId());
            intent.putExtra("GUEST_NAME", reservation.getGuestName());
            intent.putExtra("GUEST_EMAIL", reservation.getGuestEmail());
            intent.putExtra("GUEST_CONTACT", reservation.getGuestContact());
            intent.putExtra("DATE", reservation.getDate());
            intent.putExtra("TIME", reservation.getTime());
            intent.putExtra("PARTY_SIZE", reservation.getPartySize());
            intent.putExtra("SPECIAL_REQUESTS", reservation.getSpecialRequests());
            intent.putExtra("STATUS", reservation.getStatus());
            intent.putExtra("TABLE_ASSIGNED", reservation.getTableAssigned());
            startActivity(intent);
        });
        
        adapter.setOnCancelClickListener(reservation -> {
            showCancelConfirmation(reservation);
        });
    }
    
    private void setupDateSelector() {
        // Date selector is currently just displaying today's date
        // Can be enhanced later with a date picker
    }
    
    private void setupSearch() {
        if (etSearch == null) return;
        
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAndSearch();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void setupClickListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
    
    private void loadReservations(String date) {
        executorService.execute(() -> {
            List<Reservation> reservations = database.reservationDao().getReservationsByDate(date);
            
            allReservations = reservations;
            
            runOnUiThread(() -> {
                updateReservationsTitle(reservations.size());
                filterAndSearch();
            });
        });
    }
    
    private void filterAndSearch() {
        String searchQuery = "";
        if (etSearch != null) {
            searchQuery = etSearch.getText().toString().toLowerCase().trim();
        }
        
        List<Reservation> filtered = new ArrayList<>();
        
        for (Reservation reservation : allReservations) {
            boolean searchMatch = searchQuery.isEmpty() ||
                                reservation.getGuestName().toLowerCase().contains(searchQuery) ||
                                (reservation.getGuestEmail() != null && reservation.getGuestEmail().toLowerCase().contains(searchQuery));
            
            if (searchMatch) {
                filtered.add(reservation);
            }
        }
        
        adapter.updateReservations(filtered);
    }
    
    private void updateReservationsTitle(int count) {
        if (tvReservationsTitle != null) {
            tvReservationsTitle.setText("Today's Reservations (" + count + ")");
        }
    }
    
    private void showCancelConfirmation(Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel " + reservation.getGuestName() + "'s reservation?")
                .setPositiveButton("Cancel Reservation", (dialog, which) -> cancelReservation(reservation))
                .setNegativeButton("Keep", null)
                .show();
    }
    
    private void cancelReservation(Reservation reservation) {
        executorService.execute(() -> {
            database.reservationDao().updateReservationStatus(reservation.getId(), "cancelled");
            
            runOnUiThread(() -> {
                Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                
                // Send notification to guest about cancellation
                com.example.restaurantapp.utils.NotificationHelper notificationHelper = 
                    new com.example.restaurantapp.utils.NotificationHelper(getApplicationContext());
                String message = "Your reservation for " + reservation.getDate() + " at " + reservation.getTime() + " has been cancelled.";
                notificationHelper.showReservationUpdateNotification(message, "Cancelled");
                
                loadReservations(selectedDate);
            });
        });
    }
    
    private void setupBottomNavigation() {
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, StaffDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }
        
        if (navMenu != null) {
            navMenu.setOnClickListener(v -> {
                Intent intent = new Intent(this, MenuListActivity.class);
                startActivity(intent);
                finish();
            });
        }
        
        if (navBookings != null) {
            navBookings.setOnClickListener(v -> {
                // Already on bookings
            });
        }
        
        if (navSettings != null) {
            navSettings.setOnClickListener(v -> {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload when returning from other activities
        loadReservations(selectedDate);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
