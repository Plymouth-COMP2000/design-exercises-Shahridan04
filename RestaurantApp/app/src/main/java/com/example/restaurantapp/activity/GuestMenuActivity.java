package com.example.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.R;
import com.example.restaurantapp.adapter.MenuAdapter;
import com.example.restaurantapp.database.AppDatabase;
import com.example.restaurantapp.model.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuestMenuActivity extends AppCompatActivity {
    
    private RecyclerView rvMenuItems;
    private MenuAdapter menuAdapter;
    private AppDatabase database;
    private ExecutorService executorService;
    
    private EditText etSearch;
    private Button btnFilterAll, btnFilterStarters, btnFilterMains, btnFilterDrinks;
    private String selectedCategory = "All";
    private TextView navHome, navMenu, navBookings, navProfile;
    private List<MenuItem> allMenuItems = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_menu);
        
        // Initialize database and executor
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        // Initialize views
        initializeViews();
        
        // Set up RecyclerView
        setupRecyclerView();
        
        // Set up click listeners
        setupClickListeners();
        
        // Set up search
        setupSearch();
        
        // Set initial filter button state
        updateFilterButtons();
        
        // Set up bottom navigation
        setupBottomNavigation();
        
        // Load menu items
        loadMenuItems("All");
    }
    
    private void setupSearch() {
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
                // Already on menu
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
                Toast.makeText(this, "Profile page coming soon", Toast.LENGTH_SHORT).show();
            });
        }
    }
    
    private void initializeViews() {
        rvMenuItems = findViewById(R.id.rv_menu_items);
        etSearch = findViewById(R.id.et_search);
        btnFilterAll = findViewById(R.id.btn_filter_all);
        btnFilterStarters = findViewById(R.id.btn_filter_starters);
        btnFilterMains = findViewById(R.id.btn_filter_mains);
        btnFilterDrinks = findViewById(R.id.btn_filter_drinks);
        
        // Bottom navigation
        navHome = findViewById(R.id.nav_home);
        navMenu = findViewById(R.id.nav_menu);
        navBookings = findViewById(R.id.nav_bookings);
        navProfile = findViewById(R.id.nav_profile);
    }
    
    private void setupRecyclerView() {
        menuAdapter = new MenuAdapter(new ArrayList<>());
        rvMenuItems.setLayoutManager(new LinearLayoutManager(this));
        rvMenuItems.setAdapter(menuAdapter);
        
        // Set item click listener
        menuAdapter.setOnItemClickListener(item -> {
            // Navigate to item details page
            Intent intent = new Intent(GuestMenuActivity.this, ItemDetailsActivity.class);
            intent.putExtra("ITEM_NAME", item.getName());
            intent.putExtra("ITEM_PRICE", item.getPrice());
            intent.putExtra("ITEM_DESCRIPTION", item.getDescription());
            intent.putExtra("ITEM_CATEGORY", item.getCategory());
            startActivity(intent);
        });
    }
    
    private void setupClickListeners() {
        // Category filter buttons
        btnFilterAll.setOnClickListener(v -> {
            selectedCategory = "All";
            updateFilterButtons();
            loadMenuItems("All");
        });
        
        btnFilterStarters.setOnClickListener(v -> {
            selectedCategory = "Starters";
            updateFilterButtons();
            loadMenuItems("Starters");
        });
        
        btnFilterMains.setOnClickListener(v -> {
            selectedCategory = "Mains";
            updateFilterButtons();
            loadMenuItems("Mains");
        });
        
        btnFilterDrinks.setOnClickListener(v -> {
            selectedCategory = "Drinks";
            updateFilterButtons();
            loadMenuItems("Drinks");
        });
    }
    
    private void filterAndSearch() {
        String searchQuery = etSearch.getText().toString().toLowerCase().trim();
        List<MenuItem> filtered = new ArrayList<>();
        
        for (MenuItem item : allMenuItems) {
            // Category filter
            boolean categoryMatch = "All".equals(selectedCategory) || 
                                   selectedCategory.equalsIgnoreCase(item.getCategory());
            
            // Search filter
            boolean searchMatch = searchQuery.isEmpty() ||
                                item.getName().toLowerCase().contains(searchQuery) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(searchQuery));
            
            if (categoryMatch && searchMatch) {
                filtered.add(item);
            }
        }
        
        menuAdapter.updateMenuItems(filtered);
    }
    
    private void updateFilterButtons() {
        // Reset all buttons to default style
        btnFilterAll.setBackgroundResource(R.drawable.input_background);
        btnFilterAll.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        
        btnFilterStarters.setBackgroundResource(R.drawable.input_background);
        btnFilterStarters.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        
        btnFilterMains.setBackgroundResource(R.drawable.input_background);
        btnFilterMains.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        
        btnFilterDrinks.setBackgroundResource(R.drawable.input_background);
        btnFilterDrinks.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        
        // Highlight selected button
        Button selectedButton = null;
        switch (selectedCategory) {
            case "All":
                selectedButton = btnFilterAll;
                break;
            case "Starters":
                selectedButton = btnFilterStarters;
                break;
            case "Mains":
                selectedButton = btnFilterMains;
                break;
            case "Drinks":
                selectedButton = btnFilterDrinks;
                break;
        }
        
        if (selectedButton != null) {
            selectedButton.setBackgroundResource(R.drawable.button_accent);
            selectedButton.setTextColor(ContextCompat.getColor(this, R.color.text_on_primary));
        }
    }
    
    private void loadMenuItems(String category) {
        executorService.execute(() -> {
            List<MenuItem> items;
            
            if ("All".equals(category)) {
                items = database.menuDao().getAllMenuItems();
            } else {
                items = database.menuDao().getMenuItemsByCategory(category);
            }
            
            allMenuItems = items;
            
            // Update UI on main thread
            runOnUiThread(() -> {
                filterAndSearch();
            });
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload menu items when activity resumes (in case items were added/deleted)
        loadMenuItems(selectedCategory);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
