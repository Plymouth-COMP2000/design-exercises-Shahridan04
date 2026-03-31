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
    private boolean isEditMode = false;
    private int editingItemId = -1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        
        // Initialize database and executor
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        // Check if editing
        isEditMode = getIntent().getBooleanExtra("EDIT_ITEM", false);
        if (isEditMode) {
            editingItemId = getIntent().getIntExtra("ITEM_ID", -1);
        }
        
        // Initialize views
        initializeViews();
        
        // Set up click listeners
        setupClickListeners();
        
        // Load data if editing
        if (isEditMode) {
            loadItemData();
        }
    }
    
    private void loadItemData() {
        // Pre-fill fields from intent
        etItemName.setText(getIntent().getStringExtra("ITEM_NAME"));
        etPrice.setText(String.valueOf(getIntent().getDoubleExtra("ITEM_PRICE", 0.0)));
        etDescription.setText(getIntent().getStringExtra("ITEM_DESCRIPTION"));
        selectedCategory = getIntent().getStringExtra("ITEM_CATEGORY");
        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            tvCategory.setText(selectedCategory);
            tvCategory.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        }
        
        // Update title and button text
        TextView tvTitle = findViewById(R.id.tv_title);
        if (tvTitle != null) {
            tvTitle.setText("Edit Item");
        }
        btnSaveItem.setText("Update Item");
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
        
        // Insert or Update into database on background thread
        executorService.execute(() -> {
            try {
                if (isEditMode && editingItemId != -1) {
                    // Update existing item
                    database.menuDao().updateMenuItem(editingItemId, name, price, 
                                                     description.isEmpty() ? "" : description, 
                                                     selectedCategory);
                    
                    runOnUiThread(() -> {
                        Toast.makeText(AddItemActivity.this, "Item Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    // Create new item
                    MenuItem menuItem = new MenuItem(
                        name,
                        price,
                        description.isEmpty() ? "" : description,
                        "", // imagePath - can be implemented later
                        selectedCategory
                    );
                    
                    database.menuDao().insertMenuItem(menuItem);
                    
                    runOnUiThread(() -> {
                        Toast.makeText(AddItemActivity.this, "Item Added", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    String message = isEditMode ? "Error updating item: " : "Error adding item: ";
                    Toast.makeText(AddItemActivity.this, message + e.getMessage(), Toast.LENGTH_LONG).show();
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

