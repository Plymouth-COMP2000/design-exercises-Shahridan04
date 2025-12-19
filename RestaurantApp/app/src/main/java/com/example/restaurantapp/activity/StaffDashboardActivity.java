package com.example.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.R;
import com.example.restaurantapp.database.AppDatabase;
import com.example.restaurantapp.model.Reservation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StaffDashboardActivity extends AppCompatActivity {
    
    private Button btnManageMenu;
    private Button btnViewReservations;
    private TextView navHome, navMenu, navBookings, navSettings;
    private TextView tvReservationCount;
    private View cardActivity1, cardActivity2;
    
    private AppDatabase database;
    private ExecutorService executorService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);
        
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        // Initialize views
        btnManageMenu = findViewById(R.id.btn_manage_menu);
        btnViewReservations = findViewById(R.id.btn_view_reservations);
        navHome = findViewById(R.id.nav_home);
        navMenu = findViewById(R.id.nav_menu);
        navBookings = findViewById(R.id.nav_bookings);
        navSettings = findViewById(R.id.nav_settings);
        tvReservationCount = findViewById(R.id.tv_reservation_count);
        cardActivity1 = findViewById(R.id.card_activity_1);
        cardActivity2 = findViewById(R.id.card_activity_2);
        
        // Set up click listeners
        setupClickListeners();
        
        // Set up bottom navigation
        setupBottomNavigation();
        
        // Load real data
        loadReservationCount();
        hideMockActivityItems();
    }
    
    private void loadReservationCount() {
        executorService.execute(() -> {
            // Get today's date in yyyy-MM-dd format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String today = sdf.format(new Date());
            
            // Count ALL reservations for today (including pending, confirmed, etc.)
            List<com.example.restaurantapp.model.Reservation> todayReservations = 
                database.reservationDao().getReservationsByDate(today);
            int count = todayReservations != null ? todayReservations.size() : 0;
            
            runOnUiThread(() -> {
                if (tvReservationCount != null) {
                    tvReservationCount.setText(String.valueOf(count));
                }
            });
        });
    }
    
    private void hideMockActivityItems() {
        // Hide mock activity items since we're not implementing real-time activity feed
        if (cardActivity1 != null) {
            cardActivity1.setVisibility(View.GONE);
        }
        if (cardActivity2 != null) {
            cardActivity2.setVisibility(View.GONE);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload count when returning to dashboard
        loadReservationCount();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
    
    private void setupClickListeners() {
        // Manage Menu button - navigate to Menu List
        btnManageMenu.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, MenuListActivity.class);
            startActivity(intent);
        });
        
        // View Reservations button - navigate to Reservations
        btnViewReservations.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, ReservationsActivity.class);
            startActivity(intent);
        });
    }
    
    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            // Already on home
        });
        
        navMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuListActivity.class);
            startActivity(intent);
        });
        
        navBookings.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReservationsActivity.class);
            startActivity(intent);
        });
        
        navSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}

