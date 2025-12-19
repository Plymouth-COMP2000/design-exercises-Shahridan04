package com.example.restaurantapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.restaurantapp.R;

public class GuestSettingsActivity extends AppCompatActivity {
    
    private TextView btnBack;
    private Button btnChangePassword, btnLogout;
    private SwitchCompat switchReservationUpdates, switchMenuUpdates;
    private TextView navHome, navMenu, navBookings, navProfile;
    private android.widget.EditText etName, etEmail, etContact;
    private SharedPreferences prefs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_settings);
        
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        
        // Initialize views
        btnBack = findViewById(R.id.btn_back);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnLogout = findViewById(R.id.btn_logout);
        switchReservationUpdates = findViewById(R.id.switch_reservation_updates);
        switchMenuUpdates = findViewById(R.id.switch_menu_updates);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etContact = findViewById(R.id.et_contact);
        navHome = findViewById(R.id.nav_home);
        navMenu = findViewById(R.id.nav_menu);
        navBookings = findViewById(R.id.nav_bookings);
        navProfile = findViewById(R.id.nav_profile);
        
        // Load saved preferences and user info
        loadUserInfo();
        loadPreferences();
        
        // Set up click listeners
        setupClickListeners();
        
        // Set up bottom navigation
        setupBottomNavigation();
    }
    
    private void loadUserInfo() {
        // Load user info from SharedPreferences
        String firstName = prefs.getString("firstname", "");
        String lastName = prefs.getString("lastname", "");
        String email = prefs.getString("email", "");
        String contact = prefs.getString("contact", "");
        
        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            etName.setText(firstName + " " + lastName);
        }
        if (!email.isEmpty()) {
            etEmail.setText(email);
        }
        if (!contact.isEmpty()) {
            etContact.setText(contact);
        }
    }
    
    private void loadPreferences() {
        // Load notification preferences (default to true for reservation updates)
        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", true);
        boolean menuUpdatesEnabled = prefs.getBoolean("menu_updates_enabled", false);
        
        switchReservationUpdates.setChecked(notificationsEnabled);
        switchMenuUpdates.setChecked(menuUpdatesEnabled);
        
        // Save listener for reservation updates (main notification toggle)
        switchReservationUpdates.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
            android.widget.Toast.makeText(this, isChecked ? "Notifications enabled" : "Notifications disabled", android.widget.Toast.LENGTH_SHORT).show();
        });
        
        // Save listener for menu updates
        switchMenuUpdates.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("menu_updates_enabled", isChecked).apply();
        });
    }
    
    private void setupClickListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        
        if (btnChangePassword != null) {
            btnChangePassword.setOnClickListener(v -> {
                android.widget.Toast.makeText(this, "Change password functionality coming soon", android.widget.Toast.LENGTH_SHORT).show();
            });
        }
        
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // Clear preferences on logout
                prefs.edit().clear().apply();
                
                // Navigate back to login
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
    
    private void setupBottomNavigation() {
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, GuestHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }
        
        if (navMenu != null) {
            navMenu.setOnClickListener(v -> {
                Intent intent = new Intent(this, GuestMenuActivity.class);
                startActivity(intent);
                finish();
            });
        }
        
        if (navBookings != null) {
            navBookings.setOnClickListener(v -> {
                Intent intent = new Intent(this, MyReservationsActivity.class);
                startActivity(intent);
                finish();
            });
        }
        
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                // Already on profile/settings
            });
        }
    }
}

