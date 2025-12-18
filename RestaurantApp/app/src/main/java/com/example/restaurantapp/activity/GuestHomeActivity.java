package com.example.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.R;
import com.example.restaurantapp.database.AppDatabase;
import com.example.restaurantapp.model.MenuItem;

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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_home);
        
        // Initialize database
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        // Initialize views
        btnMakeReservation = findViewById(R.id.btn_make_reservation);
        btnViewFullMenu = findViewById(R.id.btn_view_full_menu);
        navHome = findViewById(R.id.nav_home);
        navMenu = findViewById(R.id.nav_menu);
        navBookings = findViewById(R.id.nav_bookings);
        navProfile = findViewById(R.id.nav_profile);
        
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
        
        // Load featured dishes
        loadFeaturedDishes();
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
            // TODO: Navigate to BookTableActivity when created
            // Intent intent = new Intent(GuestHomeActivity.this, BookTableActivity.class);
            // startActivity(intent);
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
            // TODO: Profile page
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

