package com.example.restaurantapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.restaurantapp.R;

public class SettingsActivity extends AppCompatActivity {
    
    private Button btnChangePassword, btnLogout;
    private TextView navHome, navMenu, navBookings, navSettings;
    private SwitchCompat switchReservationUpdates, switchMenuUpdates;
    private android.widget.EditText etName, etEmail, etContact;
    private SharedPreferences prefs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        
        // Initialize views
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
        navSettings = findViewById(R.id.nav_settings);
        
        // Load user info and saved preferences
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
        
        if (etName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            etName.setText(firstName + " " + lastName);
        }
        if (etEmail != null && !email.isEmpty()) {
            etEmail.setText(email);
        }
        if (etContact != null && !contact.isEmpty()) {
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
            Toast.makeText(this, isChecked ? "Notifications enabled" : "Notifications disabled", Toast.LENGTH_SHORT).show();
        });
        
        // Save listener for menu updates
        switchMenuUpdates.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("menu_updates_enabled", isChecked).apply();
        });
    }
    
    private void setupClickListeners() {
        btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "Change password functionality coming soon", Toast.LENGTH_SHORT).show();
        });
        
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

