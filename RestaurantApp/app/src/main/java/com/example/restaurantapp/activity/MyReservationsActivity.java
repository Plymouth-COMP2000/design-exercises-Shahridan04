package com.example.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.R;

public class MyReservationsActivity extends AppCompatActivity {
    
    private TextView navHome, navMenu, navBookings, navProfile;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);
        
        // Initialize navigation
        navHome = findViewById(R.id.nav_home);
        navMenu = findViewById(R.id.nav_menu);
        navBookings = findViewById(R.id.nav_bookings);
        navProfile = findViewById(R.id.nav_profile);
        
        // Set up bottom navigation
        setupBottomNavigation();
    }
    
    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        
        navMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestMenuActivity.class);
            startActivity(intent);
            finish();
        });
        
        navBookings.setOnClickListener(v -> {
            // Already on bookings
        });
        
        navProfile.setOnClickListener(v -> {
            // TODO: Profile page
        });
    }
}

