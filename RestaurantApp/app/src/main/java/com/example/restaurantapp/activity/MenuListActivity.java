package com.example.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.R;
import com.example.restaurantapp.adapter.MenuStaffAdapter;
import com.example.restaurantapp.database.AppDatabase;
import com.example.restaurantapp.model.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MenuListActivity extends AppCompatActivity {
    
    private TextView btnBack;
    private Button btnAdd;
    private EditText etSearch;
    private RecyclerView rvMenuItems;
    private MenuStaffAdapter adapter;
    private AppDatabase database;
    private ExecutorService executorService;
    
    private Button btnFilterAll, btnFilterStarters, btnFilterMains, btnFilterDesserts, btnFilterDrinks;
    private String selectedCategory = "All";
    private List<MenuItem> allMenuItems = new ArrayList<>();
    
    private TextView navHome, navMenu, navBookings, navSettings;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);
        
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
        
        // Set up bottom navigation
        setupBottomNavigation();
        
        // Load menu items
        loadMenuItems("All");
    }
    
    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        btnAdd = findViewById(R.id.btn_add);
        etSearch = findViewById(R.id.et_search);
        rvMenuItems = findViewById(R.id.rv_menu_items);
        btnFilterAll = findViewById(R.id.btn_filter_all);
        btnFilterStarters = findViewById(R.id.btn_filter_starters);
        btnFilterMains = findViewById(R.id.btn_filter_mains);
        btnFilterDesserts = findViewById(R.id.btn_filter_desserts);
        btnFilterDrinks = findViewById(R.id.btn_filter_drinks);
        
        // Bottom navigation
        navHome = findViewById(R.id.nav_home);
        navMenu = findViewById(R.id.nav_menu);
        navBookings = findViewById(R.id.nav_bookings);
        navSettings = findViewById(R.id.nav_settings);
    }
    
    private void setupRecyclerView() {
        adapter = new MenuStaffAdapter(new ArrayList<>());
        rvMenuItems.setLayoutManager(new LinearLayoutManager(this));
        rvMenuItems.setAdapter(adapter);
        
        // Set click listeners
        adapter.setOnEditClickListener(item -> {
            // Navigate to edit (for now, just show toast - can implement EditActivity later)
            Intent intent = new Intent(MenuListActivity.this, AddItemActivity.class);
            intent.putExtra("EDIT_ITEM", true);
            intent.putExtra("ITEM_ID", item.getId());
            intent.putExtra("ITEM_NAME", item.getName());
            intent.putExtra("ITEM_PRICE", item.getPrice());
            intent.putExtra("ITEM_DESCRIPTION", item.getDescription());
            intent.putExtra("ITEM_CATEGORY", item.getCategory());
            startActivity(intent);
        });
        
        adapter.setOnDeleteClickListener(item -> {
            showDeleteConfirmation(item);
        });
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MenuListActivity.this, AddItemActivity.class);
            startActivity(intent);
        });
        
        // Category filters
        btnFilterAll.setOnClickListener(v -> {
            selectedCategory = "All";
            updateFilterButtons();
            filterAndSearch();
        });
        
        btnFilterStarters.setOnClickListener(v -> {
            selectedCategory = "Starters";
            updateFilterButtons();
            filterAndSearch();
        });
        
        btnFilterMains.setOnClickListener(v -> {
            selectedCategory = "Mains";
            updateFilterButtons();
            filterAndSearch();
        });
        
        btnFilterDesserts.setOnClickListener(v -> {
            selectedCategory = "Desserts";
            updateFilterButtons();
            filterAndSearch();
        });
        
        btnFilterDrinks.setOnClickListener(v -> {
            selectedCategory = "Drinks";
            updateFilterButtons();
            filterAndSearch();
        });
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
    
    private void updateFilterButtons() {
        // Reset all buttons
        btnFilterAll.setBackgroundResource(R.drawable.input_background);
        btnFilterAll.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        
        btnFilterStarters.setBackgroundResource(R.drawable.input_background);
        btnFilterStarters.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        
        btnFilterMains.setBackgroundResource(R.drawable.input_background);
        btnFilterMains.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        
        btnFilterDesserts.setBackgroundResource(R.drawable.input_background);
        btnFilterDesserts.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        
        btnFilterDrinks.setBackgroundResource(R.drawable.input_background);
        btnFilterDrinks.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        
        // Highlight selected
        Button selected = null;
        switch (selectedCategory) {
            case "All": selected = btnFilterAll; break;
            case "Starters": selected = btnFilterStarters; break;
            case "Mains": selected = btnFilterMains; break;
            case "Desserts": selected = btnFilterDesserts; break;
            case "Drinks": selected = btnFilterDrinks; break;
        }
        
        if (selected != null) {
            selected.setBackgroundResource(R.drawable.button_accent);
            selected.setTextColor(ContextCompat.getColor(this, R.color.text_on_primary));
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
            
            runOnUiThread(() -> {
                filterAndSearch();
            });
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
        
        adapter.updateMenuItems(filtered);
    }
    
    private void showDeleteConfirmation(MenuItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete " + item.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteMenuItem(item))
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void deleteMenuItem(MenuItem item) {
        executorService.execute(() -> {
            database.menuDao().deleteMenuItem(item.getId());
            
            runOnUiThread(() -> {
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
                loadMenuItems(selectedCategory);
            });
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload when returning from AddItemActivity
        loadMenuItems(selectedCategory);
    }
    
    private void setupBottomNavigation() {
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, StaffDashboardActivity.class);
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
                Intent intent = new Intent(this, ReservationsActivity.class);
                startActivity(intent);
                finish();
            });
        }
        
        if (navSettings != null) {
            navSettings.setOnClickListener(v -> {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
