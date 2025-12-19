package com.example.restaurantapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.restaurantapp.model.Reservation;

import java.util.List;

@Dao
public interface ReservationDao {
    
    @Insert
    void insertReservation(Reservation reservation);
    
    @Query("SELECT * FROM reservations ORDER BY date ASC, time ASC")
    List<Reservation> getAllReservations();
    
    @Query("SELECT * FROM reservations WHERE date = :date ORDER BY time ASC")
    List<Reservation> getReservationsByDate(String date);
    
    @Query("SELECT * FROM reservations WHERE status = :status ORDER BY date ASC, time ASC")
    List<Reservation> getReservationsByStatus(String status);
    
    @Query("SELECT * FROM reservations WHERE guestName LIKE '%' || :searchQuery || '%' OR guestEmail LIKE '%' || :searchQuery || '%' ORDER BY date ASC, time ASC")
    List<Reservation> searchReservations(String searchQuery);
    
    @Query("SELECT * FROM reservations WHERE guestEmail = :email ORDER BY date ASC, time ASC")
    List<Reservation> getReservationsByGuestEmail(String email);
    
    @Query("UPDATE reservations SET status = :status WHERE id = :id")
    void updateReservationStatus(int id, String status);
    
    @Query("DELETE FROM reservations WHERE id = :id")
    void deleteReservation(int id);
    
    @Query("SELECT COUNT(*) FROM reservations WHERE date = :date AND status = 'confirmed'")
    int getConfirmedReservationsCountForDate(String date);
}

