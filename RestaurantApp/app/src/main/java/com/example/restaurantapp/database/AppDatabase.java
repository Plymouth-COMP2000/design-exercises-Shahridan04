package com.example.restaurantapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.restaurantapp.dao.MenuDao;
import com.example.restaurantapp.dao.ReservationDao;
import com.example.restaurantapp.model.MenuItem;
import com.example.restaurantapp.model.Reservation;

@Database(entities = {MenuItem.class, Reservation.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    private static AppDatabase INSTANCE;
    
    public abstract MenuDao menuDao();
    public abstract ReservationDao reservationDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "restaurant_database"
            )
            .fallbackToDestructiveMigration() // For development - allows version changes
            .build();
        }
        return INSTANCE;
    }
}

