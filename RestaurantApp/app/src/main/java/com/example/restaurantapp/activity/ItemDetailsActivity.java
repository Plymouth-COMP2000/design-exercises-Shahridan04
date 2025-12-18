package com.example.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.R;

import java.util.Locale;

public class ItemDetailsActivity extends AppCompatActivity {
    
    private TextView btnBack, tvItemName, tvPrice, tvDescription, tvEmoji;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        
        initializeViews();
        
        // Get menu item data from intent
        String itemName = getIntent().getStringExtra("ITEM_NAME");
        double itemPrice = getIntent().getDoubleExtra("ITEM_PRICE", 0.0);
        String itemDescription = getIntent().getStringExtra("ITEM_DESCRIPTION");
        String itemCategory = getIntent().getStringExtra("ITEM_CATEGORY");
        
        // Populate data
        if (itemName != null) {
            tvItemName.setText(itemName);
        }
        
        String price = String.format(Locale.getDefault(), "RM %.2f", itemPrice);
        tvPrice.setText(price);
        
        if (itemDescription != null && !itemDescription.isEmpty()) {
            tvDescription.setText(itemDescription);
        } else {
            tvDescription.setText("No description available");
        }
        
        // Set emoji based on category
        String emoji = getEmojiForCategory(itemCategory);
        if (tvEmoji != null) {
            tvEmoji.setText(emoji);
        }
        
        // Back button
        btnBack.setOnClickListener(v -> finish());
        
        // Make Reservation button
        Button btnMakeReservation = findViewById(R.id.btn_make_reservation);
        if (btnMakeReservation != null) {
            btnMakeReservation.setOnClickListener(v -> {
                // TODO: Navigate to book table when BookTableActivity is implemented
                // Intent intent = new Intent(ItemDetailsActivity.this, BookTableActivity.class);
                // startActivity(intent);
            });
        }
    }
    
    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        tvItemName = findViewById(R.id.tv_item_name);
        tvPrice = findViewById(R.id.tv_price);
        tvDescription = findViewById(R.id.tv_desc_text);
        tvEmoji = findViewById(R.id.tv_emoji);
    }
    
    private String getEmojiForCategory(String category) {
        if (category == null) return "🍽️";
        
        switch (category.toLowerCase()) {
            case "starters":
                return "🥗";
            case "mains":
                return "🍽️";
            case "desserts":
                return "🍰";
            case "drinks":
                return "🥤";
            default:
                return "🍽️";
        }
    }
}

