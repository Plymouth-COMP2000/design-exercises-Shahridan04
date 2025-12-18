package com.example.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.R;

public class SettingsActivity extends AppCompatActivity {
    
    private Button btnChangePassword, btnLogout;
    private TextView navHome, navMenu, navBookings, navSettings;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Initialize views
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnLogout = findViewById(R.id.btn_logout);
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
        btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "Change password functionality coming soon", Toast.LENGTH_SHORT).show();
        });
        
        btnLogout.setOnClickListener(v -> {
            // Navigate back to login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    
    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, StaffDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        
        navMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuListActivity.class);
            startActivity(intent);
            finish();
        });
        
        navBookings.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReservationsActivity.class);
            startActivity(intent);
            finish();
        });
        
        navSettings.setOnClickListener(v -> {
            // Already on settings
        });
    }
}

