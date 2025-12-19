package com.example.restaurantapp.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookTableActivity extends AppCompatActivity {
    
    private TextView btnBack, tvDateValue, tvGuestCount;
    private Button btnTime5pm, btnTime530pm, btnTime6pm, btnTime630pm, btnTime7pm, btnTime730pm;
    private View partySizeSelector;
    private Button btnContinue;
    
    private String selectedDate;
    private String selectedTime;
    private int selectedPartySize = 4; // Default to 4 guests
    
    private Button selectedTimeButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_table);
        
        initializeViews();
        setupDate();
        setupTimeButtons();
        setupPartySizeSelector();
        setupClickListeners();
    }
    
    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        tvDateValue = findViewById(R.id.tv_date_value);
        tvGuestCount = findViewById(R.id.tv_guest_count);
        partySizeSelector = findViewById(R.id.party_size_selector);
        
        btnTime5pm = findViewById(R.id.btn_time_5pm);
        btnTime530pm = findViewById(R.id.btn_time_530pm);
        btnTime6pm = findViewById(R.id.btn_time_6pm);
        btnTime630pm = findViewById(R.id.btn_time_630pm);
        btnTime7pm = findViewById(R.id.btn_time_7pm);
        btnTime730pm = findViewById(R.id.btn_time_730pm);
        
        btnContinue = findViewById(R.id.btn_continue);
    }
    
    private void setupDate() {
        // Get date from intent if coming from a previous step, otherwise use today
        Calendar calendar = Calendar.getInstance();
        if (getIntent().hasExtra("SELECTED_DATE")) {
            selectedDate = getIntent().getStringExtra("SELECTED_DATE");
            // Parse and display the date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                calendar.setTime(sdf.parse(selectedDate));
            } catch (Exception e) {
                // If parsing fails, use today
                calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDate = sdf.format(calendar.getTime());
            }
        } else {
            // Default to today
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            selectedDate = sdf.format(calendar.getTime());
        }
        
        // Format for display
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        tvDateValue.setText(displayFormat.format(calendar.getTime()));
        
        // Allow user to click date to change it
        findViewById(R.id.card_date_selected).setOnClickListener(v -> showDatePicker());
    }
    
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            calendar.setTime(sdf.parse(selectedDate));
        } catch (Exception e) {
            calendar = Calendar.getInstance();
        }
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDate = sdf.format(selected.getTime());
                
                SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                tvDateValue.setText(displayFormat.format(selected.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    
    private void setupTimeButtons() {
        // Set up all time buttons
        Button[] timeButtons = {btnTime5pm, btnTime530pm, btnTime6pm, btnTime630pm, btnTime7pm, btnTime730pm};
        String[] times = {"5:00 PM", "5:30 PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM"};
        
        for (int i = 0; i < timeButtons.length; i++) {
            final Button button = timeButtons[i];
            final String time = times[i];
            
            button.setOnClickListener(v -> {
                // Reset all buttons
                for (Button btn : timeButtons) {
                    // Remove checkmark from text
                    String btnText = btn.getText().toString().replace(" ✓", "");
                    btn.setText(btnText);
                    btn.setBackgroundResource(R.drawable.input_background);
                    btn.setTextColor(android.graphics.Color.parseColor("#212121")); // Dark text for visibility
                    btn.setTypeface(null, android.graphics.Typeface.NORMAL);
                    btn.invalidate(); // Force redraw
                }
                
                // Highlight selected button with green background and checkmark
                button.setBackgroundResource(R.drawable.button_time_selected);
                button.setTextColor(android.graphics.Color.WHITE); // White text on green
                button.setTypeface(null, android.graphics.Typeface.BOLD);
                button.setText(time + " ✓");
                button.invalidate(); // Force redraw
                button.postInvalidate(); // Ensure UI update
                
                selectedTimeButton = button;
                selectedTime = time;
            });
        }
        
        // Default selection: 6:30 PM
        selectedTime = "6:30 PM";
        selectedTimeButton = btnTime630pm;
        btnTime630pm.setBackgroundResource(R.drawable.button_time_selected);
        btnTime630pm.setTextColor(android.graphics.Color.WHITE);
        btnTime630pm.setTypeface(null, android.graphics.Typeface.BOLD);
        btnTime630pm.setText("6:30 PM ✓");
    }
    
    private void setupPartySizeSelector() {
        partySizeSelector.setOnClickListener(v -> showPartySizeDialog());
        tvGuestCount.setText(String.valueOf(selectedPartySize));
    }
    
    private void showPartySizeDialog() {
        String[] sizes = new String[10];
        for (int i = 0; i < 10; i++) {
            sizes[i] = String.valueOf(i + 1);
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Select Party Size")
                .setItems(sizes, (dialog, which) -> {
                    selectedPartySize = which + 1;
                    tvGuestCount.setText(String.valueOf(selectedPartySize));
                })
                .show();
    }
    
    private void setupClickListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        
        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                // Validate inputs
                if (selectedDate == null || selectedDate.isEmpty()) {
                    Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (selectedTime == null || selectedTime.isEmpty()) {
                    Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (selectedPartySize < 1) {
                    Toast.makeText(this, "Please select party size", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Show dialog to collect guest info
                showGuestInfoDialog();
            });
        }
    }
    
    private void showGuestInfoDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Guest Information");
        
        // Create layout for dialog
        android.view.LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.dialog_guest_info, null);
        builder.setView(dialogView);
        
        android.widget.EditText etName = dialogView.findViewById(R.id.et_guest_name);
        android.widget.EditText etEmail = dialogView.findViewById(R.id.et_guest_email);
        android.widget.EditText etContact = dialogView.findViewById(R.id.et_guest_contact);
        android.widget.EditText etSpecialRequests = dialogView.findViewById(R.id.et_special_requests);
        
        // Load saved user info if available
        android.content.SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String firstName = prefs.getString("firstname", "");
        String lastName = prefs.getString("lastname", "");
        String email = prefs.getString("email", "");
        String contact = prefs.getString("contact", "");
        
        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            etName.setText(firstName + " " + lastName);
        }
        if (!email.isEmpty()) {
            etEmail.setText(email);
        }
        if (!contact.isEmpty()) {
            etContact.setText(contact);
        }
        
        builder.setPositiveButton("Confirm Booking", (dialog, which) -> {
            String guestName = etName.getText().toString().trim();
            String guestEmail = etEmail.getText().toString().trim();
            String guestContact = etContact.getText().toString().trim();
            String specialRequests = etSpecialRequests.getText().toString().trim();
            
            // Validate
            if (guestName.isEmpty()) {
                Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (guestEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(guestEmail).matches()) {
                Toast.makeText(this, "Valid email is required", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (guestContact.isEmpty()) {
                Toast.makeText(this, "Contact number is required", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Save reservation and navigate to confirmation
            saveReservationAndConfirm(guestName, guestEmail, guestContact, specialRequests);
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void saveReservationAndConfirm(String guestName, String guestEmail, String guestContact, String specialRequests) {
        com.example.restaurantapp.database.AppDatabase database = com.example.restaurantapp.database.AppDatabase.getInstance(this);
        java.util.concurrent.ExecutorService executorService = java.util.concurrent.Executors.newSingleThreadExecutor();
        
        // Create reservation
        com.example.restaurantapp.model.Reservation reservation = new com.example.restaurantapp.model.Reservation(
                guestName,
                guestEmail,
                guestContact,
                selectedDate,
                selectedTime,
                selectedPartySize,
                specialRequests.isEmpty() ? null : specialRequests,
                "pending",
                null
        );
        
        // Save to database
        executorService.execute(() -> {
            database.reservationDao().insertReservation(reservation);
            
            runOnUiThread(() -> {
                // Send notification to staff about new reservation
                com.example.restaurantapp.utils.NotificationHelper notificationHelper = 
                    new com.example.restaurantapp.utils.NotificationHelper(getApplicationContext());
                
                // Format date for notification
                java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault());
                String displayDate = selectedDate;
                try {
                    java.util.Date date = inputFormat.parse(selectedDate);
                    displayDate = displayFormat.format(date);
                } catch (Exception e) {
                    // Use original date if parsing fails
                }
                
                notificationHelper.showNewReservationNotification(guestName, displayDate, selectedTime);
                
                // Navigate to confirmation screen
                Intent intent = new Intent(BookTableActivity.this, ConfirmBookingActivity.class);
                intent.putExtra("SELECTED_DATE", selectedDate);
                intent.putExtra("SELECTED_TIME", selectedTime);
                intent.putExtra("PARTY_SIZE", selectedPartySize);
                intent.putExtra("SPECIAL_REQUESTS", specialRequests);
                startActivity(intent);
                finish();
            });
        });
    }
}

