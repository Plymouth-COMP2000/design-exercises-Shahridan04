package com.example.restaurantapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.activity.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Start LoginActivity immediately
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity
    }
}