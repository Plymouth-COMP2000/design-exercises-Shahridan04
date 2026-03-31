package com.example.restaurantapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reservations")
public class Reservation {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String guestName;
    private String guestEmail;
    private String guestContact;
    private String date;
    private String time;
    private int partySize;
    private String specialRequests;
    private String status; // "confirmed", "pending", "cancelled"
    private String tableAssigned;
    
    // Constructor
    public Reservation(String guestName, String guestEmail, String guestContact, 
                      String date, String time, int partySize, 
                      String specialRequests, String status, String tableAssigned) {
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.guestContact = guestContact;
        this.date = date;
        this.time = time;
        this.partySize = partySize;
        this.specialRequests = specialRequests;
        this.status = status;
        this.tableAssigned = tableAssigned;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getGuestName() {
        return guestName;
    }
    
    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }
    
    public String getGuestEmail() {
        return guestEmail;
    }
    
    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }
    
    public String getGuestContact() {
        return guestContact;
    }
    
    public void setGuestContact(String guestContact) {
        this.guestContact = guestContact;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public int getPartySize() {
        return partySize;
    }
    
    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }
    
    public String getSpecialRequests() {
        return specialRequests;
    }
    
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getTableAssigned() {
        return tableAssigned;
    }
    
    public void setTableAssigned(String tableAssigned) {
        this.tableAssigned = tableAssigned;
    }
}

