package com.example.restaurantapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.restaurantapp.model.MenuItem;

import java.util.List;

@Dao
public interface MenuDao {
    
    @Insert
    void insertMenuItem(MenuItem menuItem);
    
    @Query("SELECT * FROM menu_items")
    List<MenuItem> getAllMenuItems();
    
    @Query("SELECT * FROM menu_items WHERE category = :category")
    List<MenuItem> getMenuItemsByCategory(String category);
    
    @Query("DELETE FROM menu_items WHERE id = :id")
    void deleteMenuItem(int id);
    
    @Query("UPDATE menu_items SET name = :name, price = :price, description = :description, category = :category WHERE id = :id")
    void updateMenuItem(int id, String name, double price, String description, String category);
}

