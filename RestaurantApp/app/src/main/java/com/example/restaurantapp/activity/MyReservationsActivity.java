package com.example.restaurantapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.R;
import com.example.restaurantapp.adapter.ReservationGuestAdapter;
import com.example.restaurantapp.database.AppDatabase;
import com.example.restaurantapp.model.Reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyReservationsActivity extends AppCompatActivity {
    
    private TextView tabUpcoming, tabPast;
    private RecyclerView rvReservations;
    private TextView tvEmptyState;
    private TextView navHome, navMenu, navBookings, navProfile;
    
    private ReservationGuestAdapter adapter;
    private AppDatabase database;
    private ExecutorService executorService;
    private String currentUserEmail;
    private boolean showingUpcoming = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);
        
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        // Get current user email from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", "");
        
        initializeViews();
        setupRecyclerView();
        setupTabs();
        setupBottomNavigation();
        
        // Load upcoming reservations by default
        loadReservations(true);
    }
    
    private void initializeViews() {
        tabUpcoming = findViewById(R.id.tab_upcoming);
        tabPast = findViewById(R.id.tab_past);
        rvReservations = findViewById(R.id.rv_reservations);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        
        // Bottom navigation
        navHome = findViewById(R.id.nav_home);
        navMenu = findViewById(R.id.nav_menu);
        navBookings = findViewById(R.id.nav_bookings);
        navProfile = findViewById(R.id.nav_profile);
    }
    
    private void setupRecyclerView() {
        adapter = new ReservationGuestAdapter(new ArrayList<>());
        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        rvReservations.setAdapter(adapter);
        
        // Set click listener
        adapter.setOnReservationClickListener(reservation -> {
            Intent intent = new Intent(MyReservationsActivity.this, ReservationDetailsActivity.class);
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
    }
    
    private void setupTabs() {
        tabUpcoming.setOnClickListener(v -> {
            showingUpcoming = true;
            updateTabSelection();
            loadReservations(true);
        });
        
        tabPast.setOnClickListener(v -> {
            showingUpcoming = false;
            updateTabSelection();
            loadReservations(false);
        });
    }
    
    private void updateTabSelection() {
        if (showingUpcoming) {
            tabUpcoming.setBackgroundColor(getResources().getColor(R.color.primary_alpha));
            tabUpcoming.setTextColor(getResources().getColor(R.color.primary));
            tabUpcoming.setTypeface(null, android.graphics.Typeface.BOLD);
            
            tabPast.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            tabPast.setTextColor(getResources().getColor(R.color.text_secondary));
            tabPast.setTypeface(null, android.graphics.Typeface.NORMAL);
        } else {
            tabPast.setBackgroundColor(getResources().getColor(R.color.primary_alpha));
            tabPast.setTextColor(getResources().getColor(R.color.primary));
            tabPast.setTypeface(null, android.graphics.Typeface.BOLD);
            
            tabUpcoming.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            tabUpcoming.setTextColor(getResources().getColor(R.color.text_secondary));
            tabUpcoming.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }
    
    private void loadReservations(boolean upcoming) {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            // If no email, show empty state
            showEmptyState(true);
            return;
        }
        
        executorService.execute(() -> {
            List<Reservation> allReservations = database.reservationDao().getReservationsByGuestEmail(currentUserEmail);
            
            // Filter by date: upcoming (today or future) or past (before today)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            
            List<Reservation> filtered = new ArrayList<>();
            for (Reservation reservation : allReservations) {
                try {
                    Date reservationDate = sdf.parse(reservation.getDate());
                    Calendar resCal = Calendar.getInstance();
                    resCal.setTime(reservationDate);
                    resCal.set(Calendar.HOUR_OF_DAY, 0);
                    resCal.set(Calendar.MINUTE, 0);
                    resCal.set(Calendar.SECOND, 0);
                    resCal.set(Calendar.MILLISECOND, 0);
                    
                    if (upcoming) {
                        // Show today and future dates
                        if (resCal.compareTo(today) >= 0) {
                            filtered.add(reservation);
                        }
                    } else {
                        // Show past dates
                        if (resCal.compareTo(today) < 0) {
                            filtered.add(reservation);
                        }
                    }
                } catch (ParseException e) {
                    // Skip invalid dates
                    e.printStackTrace();
                }
            }
            
            runOnUiThread(() -> {
                adapter.updateReservations(filtered);
                showEmptyState(filtered.isEmpty());
            });
        });
    }
    
    private void showEmptyState(boolean show) {
        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
        }
        if (rvReservations != null) {
            rvReservations.setVisibility(show ? android.view.View.GONE : android.view.View.VISIBLE);
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
                // Already on bookings
            });
        }
        
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(this, GuestSettingsActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload when returning from other activities
        loadReservations(showingUpcoming);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
