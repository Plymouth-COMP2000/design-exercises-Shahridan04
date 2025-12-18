package com.example.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.R;

public class StaffDashboardActivity extends AppCompatActivity {
    
    private Button btnManageMenu;
    private Button btnViewReservations;
    private TextView navHome, navMenu, navBookings, navSettings;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);
        
        // Initialize views
        btnManageMenu = findViewById(R.id.btn_manage_menu);
        btnViewReservations = findViewById(R.id.btn_view_reservations);
        navHome = findViewById(R.id.nav_home);
        navMenu = findViewById(R.id.nav_menu);
        navBookings = findViewById(R.id.nav_bookings);
        navSettings = findViewById(R.id.nav_settings);
        
        // Set up click listeners
        setupClickListeners();
        
        // Set up bottom navigation
        setupBottomNavigation();
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

