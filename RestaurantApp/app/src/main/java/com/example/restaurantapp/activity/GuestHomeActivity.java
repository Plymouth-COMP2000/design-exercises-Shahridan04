package com.example.restaurantapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.R;
import com.example.restaurantapp.database.AppDatabase;
import com.example.restaurantapp.model.MenuItem;
import com.example.restaurantapp.model.Reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuestHomeActivity extends AppCompatActivity {
    
    private Button btnMakeReservation;
    private Button btnViewFullMenu;
    private TextView navHome, navMenu, navBookings, navProfile;
    
    private AppDatabase database;
    private ExecutorService executorService;
    
    private TextView tvPastaName, tvPastaPrice, tvSteakName, tvSteakPrice;
    private View cardPasta, cardSteak;
    
    private TextView tvWelcome, tvDateLabel, tvDateValue, tvTime, tvPartySize;
    private View cardUpcoming;
    
    private String currentUserEmail;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_home);
        
        // Initialize database
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        // Get current user info
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", "");
        String firstName = prefs.getString("firstname", "Guest");
        
        // Initialize views
        btnMakeReservation = findViewById(R.id.btn_make_reservation);
        btnViewFullMenu = findViewById(R.id.btn_view_full_menu);
        navHome = findViewById(R.id.nav_home);
        navMenu = findViewById(R.id.nav_menu);
        navBookings = findViewById(R.id.nav_bookings);
        navProfile = findViewById(R.id.nav_profile);
        
        // Welcome message
        tvWelcome = findViewById(R.id.tv_welcome);
        tvWelcome.setText("Welcome, " + firstName + "!");
        
        // Upcoming reservation
        cardUpcoming = findViewById(R.id.card_upcoming);
        tvDateLabel = findViewById(R.id.tv_date_label);
        tvDateValue = findViewById(R.id.tv_date_value);
        tvTime = findViewById(R.id.tv_time);
        tvPartySize = findViewById(R.id.tv_party_size);
        
        // Featured dishes
        cardPasta = findViewById(R.id.card_pasta);
        cardSteak = findViewById(R.id.card_steak);
        tvPastaName = findViewById(R.id.tv_pasta_name);
        tvPastaPrice = findViewById(R.id.tv_pasta_price);
        tvSteakName = findViewById(R.id.tv_steak_name);
        tvSteakPrice = findViewById(R.id.tv_steak_price);
        
        // Set up click listeners
        setupClickListeners();
        
        // Set up bottom navigation
        setupBottomNavigation();
        
        // Load data
        loadUpcomingReservation();
        loadFeaturedDishes();
    }
    
    private void loadUpcomingReservation() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            cardUpcoming.setVisibility(View.GONE);
            return;
        }
        
        executorService.execute(() -> {
            List<Reservation> allReservations = database.reservationDao().getReservationsByGuestEmail(currentUserEmail);
            
            // Find the next upcoming reservation
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            
            Reservation nextReservation = null;
            Date earliestDate = null;
            
            for (Reservation reservation : allReservations) {
                try {
                    Date reservationDate = sdf.parse(reservation.getDate());
                    Calendar resCal = Calendar.getInstance();
                    resCal.setTime(reservationDate);
                    resCal.set(Calendar.HOUR_OF_DAY, 0);
                    resCal.set(Calendar.MINUTE, 0);
                    resCal.set(Calendar.SECOND, 0);
                    resCal.set(Calendar.MILLISECOND, 0);
                    
                    // Only consider upcoming reservations (today or future)
                    if (resCal.compareTo(today) >= 0) {
                        if (earliestDate == null || reservationDate.before(earliestDate)) {
                            earliestDate = reservationDate;
                            nextReservation = reservation;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            
            final Reservation finalReservation = nextReservation;
            final SimpleDateFormat sdfFinal = sdf; // Make effectively final for lambda
            runOnUiThread(() -> {
                if (finalReservation != null) {
                    // Show the card and populate data
                    cardUpcoming.setVisibility(View.VISIBLE);
                    
                    // Format date label (Today, Tomorrow, or actual date)
                    SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                    try {
                        Date reservationDate = sdfFinal.parse(finalReservation.getDate());
                        Calendar resCal = Calendar.getInstance();
                        resCal.setTime(reservationDate);
                        resCal.set(Calendar.HOUR_OF_DAY, 0);
                        resCal.set(Calendar.MINUTE, 0);
                        resCal.set(Calendar.SECOND, 0);
                        resCal.set(Calendar.MILLISECOND, 0);
                        
                        Calendar todayCal = Calendar.getInstance();
                        todayCal.set(Calendar.HOUR_OF_DAY, 0);
                        todayCal.set(Calendar.MINUTE, 0);
                        todayCal.set(Calendar.SECOND, 0);
                        todayCal.set(Calendar.MILLISECOND, 0);
                        
                        Calendar tomorrowCal = Calendar.getInstance();
                        tomorrowCal.add(Calendar.DAY_OF_YEAR, 1);
                        tomorrowCal.set(Calendar.HOUR_OF_DAY, 0);
                        tomorrowCal.set(Calendar.MINUTE, 0);
                        tomorrowCal.set(Calendar.SECOND, 0);
                        tomorrowCal.set(Calendar.MILLISECOND, 0);
                        
                        if (resCal.equals(todayCal)) {
                            tvDateLabel.setText("Today");
                            tvDateValue.setText(displayFormat.format(reservationDate));
                        } else if (resCal.equals(tomorrowCal)) {
                            tvDateLabel.setText("Tomorrow");
                            tvDateValue.setText(displayFormat.format(reservationDate));
                        } else {
                            // For dates beyond tomorrow, show the day name in label
                            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                            tvDateLabel.setText(dayFormat.format(reservationDate));
                            tvDateValue.setText(displayFormat.format(reservationDate));
                        }
                    } catch (ParseException e) {
                        tvDateLabel.setText("Upcoming");
                        tvDateValue.setText(finalReservation.getDate());
                    }
                    
                    // Set time
                    tvTime.setText(finalReservation.getTime());
                    
                    // Set party size
                    tvPartySize.setText("Party of " + finalReservation.getPartySize());
                    
                    // Make card clickable
                    cardUpcoming.setOnClickListener(v -> {
                        Intent intent = new Intent(GuestHomeActivity.this, ReservationDetailsActivity.class);
                        intent.putExtra("RESERVATION_ID", finalReservation.getId());
                        intent.putExtra("GUEST_NAME", finalReservation.getGuestName());
                        intent.putExtra("GUEST_EMAIL", finalReservation.getGuestEmail());
                        intent.putExtra("GUEST_CONTACT", finalReservation.getGuestContact());
                        intent.putExtra("DATE", finalReservation.getDate());
                        intent.putExtra("TIME", finalReservation.getTime());
                        intent.putExtra("PARTY_SIZE", finalReservation.getPartySize());
                        intent.putExtra("SPECIAL_REQUESTS", finalReservation.getSpecialRequests());
                        intent.putExtra("STATUS", finalReservation.getStatus());
                        intent.putExtra("TABLE_ASSIGNED", finalReservation.getTableAssigned());
                        startActivity(intent);
                    });
                } else {
                    // Hide the card if no upcoming reservations
                    cardUpcoming.setVisibility(View.GONE);
                }
            });
        });
    }
    
    private void loadFeaturedDishes() {
        executorService.execute(() -> {
            List<MenuItem> items = database.menuDao().getAllMenuItems();
            
            runOnUiThread(() -> {
                if (items.size() >= 1) {
                    MenuItem item1 = items.get(0);
                    tvPastaName.setText(item1.getName());
                    tvPastaPrice.setText(String.format(Locale.getDefault(), "RM %.0f", item1.getPrice()));
                    
                    cardPasta.setOnClickListener(v -> {
                        openItemDetails(item1);
                    });
                } else {
                    cardPasta.setVisibility(View.GONE);
                }
                
                if (items.size() >= 2) {
                    MenuItem item2 = items.get(1);
                    tvSteakName.setText(item2.getName());
                    tvSteakPrice.setText(String.format(Locale.getDefault(), "RM %.0f", item2.getPrice()));
                    
                    cardSteak.setOnClickListener(v -> {
                        openItemDetails(item2);
                    });
                } else {
                    cardSteak.setVisibility(View.GONE);
                }
            });
        });
    }
    
    private void openItemDetails(MenuItem item) {
        Intent intent = new Intent(this, ItemDetailsActivity.class);
        intent.putExtra("ITEM_NAME", item.getName());
        intent.putExtra("ITEM_PRICE", item.getPrice());
        intent.putExtra("ITEM_DESCRIPTION", item.getDescription());
        intent.putExtra("ITEM_CATEGORY", item.getCategory());
        startActivity(intent);
    }
    
    private void setupClickListeners() {
        // Make Reservation button - navigate to Book Table
        btnMakeReservation.setOnClickListener(v -> {
            Intent intent = new Intent(GuestHomeActivity.this, BookTableActivity.class);
            startActivity(intent);
        });
        
        // View Full Menu button - navigate to Guest Menu
        btnViewFullMenu.setOnClickListener(v -> {
            Intent intent = new Intent(GuestHomeActivity.this, GuestMenuActivity.class);
            startActivity(intent);
        });
    }
    
    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            // Already on home
        });
        
        navMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestMenuActivity.class);
            startActivity(intent);
            finish();
        });
        
        navBookings.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyReservationsActivity.class);
            startActivity(intent);
            finish();
        });
        
        navProfile.setOnClickListener(v -> {
            // Navigate to settings/profile page for guest
            Intent intent = new Intent(this, GuestSettingsActivity.class);
            startActivity(intent);
            finish();
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload upcoming reservation when returning to home
        loadUpcomingReservation();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}

