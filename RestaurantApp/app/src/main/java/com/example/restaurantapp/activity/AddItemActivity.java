package com.example.restaurantapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.restaurantapp.R;
import com.example.restaurantapp.database.AppDatabase;
import com.example.restaurantapp.model.MenuItem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddItemActivity extends AppCompatActivity {
    
    private EditText etItemName, etPrice, etDescription;
    private TextView tvCategory, btnBack;
    private View categorySelector;
    private Button btnSaveItem, btnCancel;
    
    private AppDatabase database;
    private ExecutorService executorService;
    
    private String selectedCategory = "";
    private static final String[] CATEGORIES = {"Starters", "Mains", "Desserts", "Drinks"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        
        // Initialize database and executor
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        // Initialize views
        initializeViews();
        
        // Set up click listeners
        setupClickListeners();
    }
    
    private void initializeViews() {
        etItemName = findViewById(R.id.et_item_name);
        etPrice = findViewById(R.id.et_price);
        etDescription = findViewById(R.id.et_description);
        tvCategory = findViewById(R.id.tv_category);
        categorySelector = findViewById(R.id.category_selector);
        btnSaveItem = findViewById(R.id.btn_save_item);
        btnCancel = findViewById(R.id.btn_cancel);
        btnBack = findViewById(R.id.btn_back);
    }
    
    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());
        
        // Cancel button
        btnCancel.setOnClickListener(v -> finish());
        
        // Category selector
        categorySelector.setOnClickListener(v -> showCategoryDialog());
        
        // Save button
        btnSaveItem.setOnClickListener(v -> saveMenuItem());
    }
    
    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category");
        builder.setItems(CATEGORIES, (dialog, which) -> {
            selectedCategory = CATEGORIES[which];
            tvCategory.setText(selectedCategory);
            tvCategory.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        });
        builder.show();
    }
    
    private void saveMenuItem() {
        // Get input values
        String name = etItemName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        
        // Validation
        if (name.isEmpty()) {
            etItemName.setError("Item name is required");
            etItemName.requestFocus();
            return;
        }
        
        if (priceStr.isEmpty()) {
            etPrice.setError("Price is required");
            etPrice.requestFocus();
            return;
        }
        
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                etPrice.setError("Price must be greater than 0");
                etPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etPrice.setError("Invalid price format");
            etPrice.requestFocus();
            return;
        }
        
        if (selectedCategory.isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            showCategoryDialog();
            return;
        }
        
        // Create MenuItem object
        MenuItem menuItem = new MenuItem(
            name,
            price,
            description.isEmpty() ? "" : description,
            "", // imagePath - can be implemented later
            selectedCategory
        );
        
        // Insert into database on background thread
        executorService.execute(() -> {
            try {
                database.menuDao().insertMenuItem(menuItem);
                
                // Show toast on main thread
                runOnUiThread(() -> {
                    Toast.makeText(AddItemActivity.this, "Item Added", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                // Handle error on main thread
                runOnUiThread(() -> {
                    Toast.makeText(AddItemActivity.this, "Error adding item: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
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

