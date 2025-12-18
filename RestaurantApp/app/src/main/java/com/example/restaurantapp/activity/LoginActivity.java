package com.example.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.R;
import com.example.restaurantapp.api.ApiClient;
import com.example.restaurantapp.api.ApiService;
import com.example.restaurantapp.model.DatabaseResponse;
import com.example.restaurantapp.model.MessageResponse;
import com.example.restaurantapp.model.User;
import com.example.restaurantapp.model.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    
    private EditText etUsername, etPassword;
    private RadioGroup radioGroup;
    private RadioButton rbStaff, rbGuest;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister;
    
    private static final String STUDENT_ID = "BSCS2509260";
    private ApiService apiService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Initialize API service
        apiService = ApiClient.getApiService();
        
        // Initialize views
        initializeViews();
        
        // Set up click listeners
        setupClickListeners();
    }
    
    private void initializeViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        radioGroup = findViewById(R.id.radio_container);
        rbStaff = findViewById(R.id.rb_staff);
        rbGuest = findViewById(R.id.rb_guest);
        btnLogin = findViewById(R.id.btn_login);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvRegister = findViewById(R.id.tv_register);
    }
    
    private void setupClickListeners() {
        // Login button
        btnLogin.setOnClickListener(v -> performLogin());
        
        // Forgot password (placeholder)
        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot password functionality coming soon", Toast.LENGTH_SHORT).show();
        });
        
        // Register link - show registration dialog
        tvRegister.setOnClickListener(v -> showRegistrationDialog());
    }
    
    private void performLogin() {
        // Get input values
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        // Validation
        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        
        // Show loading indicator (optional - you can add a ProgressDialog here)
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");
        
        // Fetch all users from API
        Call<UserResponse> call = apiService.getAllUsers(STUDENT_ID);
        
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");
                
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    List<User> users = userResponse.getUsers();
                    
                    if (users == null || users.isEmpty()) {
                        Toast.makeText(LoginActivity.this, 
                            "No users found. Please register first.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Loop through users to find matching credentials
                    boolean loginSuccess = false;
                    User loggedInUser = null;
                    
                    for (User user : users) {
                        if (user.getUsername().equals(username) && 
                            user.getPassword().equals(password)) {
                            loginSuccess = true;
                            loggedInUser = user;
                            break;
                        }
                    }
                    
                    if (loginSuccess && loggedInUser != null) {
                        // Check usertype and navigate accordingly
                        String usertype = loggedInUser.getUsertype().toLowerCase();
                        
                        if ("staff".equals(usertype)) {
                            // Navigate to Staff Dashboard
                            Intent intent = new Intent(LoginActivity.this, StaffDashboardActivity.class);
                            startActivity(intent);
                            finish(); // Close login activity
                        } else if ("student".equals(usertype)) {
                            // Navigate to Guest Home
                            Intent intent = new Intent(LoginActivity.this, GuestHomeActivity.class);
                            startActivity(intent);
                            finish(); // Close login activity
                        } else {
                            Toast.makeText(LoginActivity.this, 
                                "Unknown user type: " + usertype, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Credentials don't match
                        Toast.makeText(LoginActivity.this, 
                            "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // API error - check if database doesn't exist
                    if (response.code() == 404) {
                        // Try to create the database automatically
                        createStudentDatabaseAndRetryLogin(username, password);
                    } else {
                        String errorMsg = "Error: " + response.message();
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");
                
                // Network error with helpful message
                String errorMessage;
                if (t instanceof java.net.SocketTimeoutException || 
                    t instanceof java.net.UnknownHostException ||
                    (t.getMessage() != null && t.getMessage().contains("failed to connect"))) {
                    errorMessage = "Cannot connect to server. Please check:\n" +
                                 "1. You are on the university network\n" +
                                 "2. Server is accessible (10.240.72.69)\n" +
                                 "3. Internet connection is active";
                } else {
                    errorMessage = "Network error: " + t.getMessage();
                }
                
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
    
    private void createStudentDatabaseAndRetryLogin(String username, String password) {
        Toast.makeText(this, "Creating student database...", Toast.LENGTH_SHORT).show();
        
        Call<DatabaseResponse> call = apiService.createStudentDatabase(STUDENT_ID);
        call.enqueue(new Callback<DatabaseResponse>() {
            @Override
            public void onResponse(Call<DatabaseResponse> call, Response<DatabaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DatabaseResponse dbResponse = response.body();
                    Toast.makeText(LoginActivity.this, 
                        dbResponse.getMessage() + " Please register to create your account.", 
                        Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, 
                        "Failed to create database. Please contact administrator.", 
                        Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<DatabaseResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, 
                    "Error creating database: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showRegistrationDialog() {
        // Create dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_register, null);
        
        EditText etRegUsername = dialogView.findViewById(R.id.et_reg_username);
        EditText etRegPassword = dialogView.findViewById(R.id.et_reg_password);
        EditText etRegFirstName = dialogView.findViewById(R.id.et_reg_firstname);
        EditText etRegLastName = dialogView.findViewById(R.id.et_reg_lastname);
        EditText etRegEmail = dialogView.findViewById(R.id.et_reg_email);
        EditText etRegContact = dialogView.findViewById(R.id.et_reg_contact);
        RadioGroup rgRegUserType = dialogView.findViewById(R.id.rg_reg_usertype);
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Create Account")
                .setView(dialogView)
                .setPositiveButton("Register", null)
                .setNegativeButton("Cancel", null)
                .create();
        
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                // Get input values
                String username = etRegUsername.getText().toString().trim();
                String password = etRegPassword.getText().toString().trim();
                String firstname = etRegFirstName.getText().toString().trim();
                String lastname = etRegLastName.getText().toString().trim();
                String email = etRegEmail.getText().toString().trim();
                String contact = etRegContact.getText().toString().trim();
                
                // Get selected user type
                int selectedId = rgRegUserType.getCheckedRadioButtonId();
                String usertype = "";
                if (selectedId == R.id.rb_reg_staff) {
                    usertype = "staff";
                } else if (selectedId == R.id.rb_reg_student) {
                    usertype = "student";
                }
                
                // Validation
                if (username.isEmpty()) {
                    etRegUsername.setError("Username is required");
                    return;
                }
                if (password.isEmpty()) {
                    etRegPassword.setError("Password is required");
                    return;
                }
                if (firstname.isEmpty()) {
                    etRegFirstName.setError("First name is required");
                    return;
                }
                if (lastname.isEmpty()) {
                    etRegLastName.setError("Last name is required");
                    return;
                }
                if (email.isEmpty()) {
                    etRegEmail.setError("Email is required");
                    return;
                }
                if (contact.isEmpty()) {
                    etRegContact.setError("Contact is required");
                    return;
                }
                if (usertype.isEmpty()) {
                    Toast.makeText(this, "Please select user type", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Create user object
                User newUser = new User(username, password, usertype, 
                                      firstname, lastname, email, contact);
                
                // Register user via API
                performRegistration(newUser, dialog);
            });
        });
        
        dialog.show();
    }
    
    private void performRegistration(User newUser, AlertDialog dialog) {
        // Disable button during registration
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);
        positiveButton.setText("Registering...");
        
        // Call API to create user
        Call<MessageResponse> call = apiService.createUser(STUDENT_ID, newUser);
        
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                positiveButton.setEnabled(true);
                positiveButton.setText("Register");
                
                if (response.isSuccessful() && response.body() != null) {
                    MessageResponse messageResponse = response.body();
                    Toast.makeText(LoginActivity.this, 
                        messageResponse.getMessage() + " You can now login.", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    
                    // Auto-fill username in login fields
                    etUsername.setText(newUser.getUsername());
                    etPassword.setText("");
                } else {
                    String errorMsg = "Registration failed: " + response.message();
                    if (response.code() == 400) {
                        errorMsg = "Invalid user data. Please check all fields.";
                    } else if (response.code() == 404) {
                        // Database doesn't exist, try to create it
                        createStudentDatabaseAndRetryRegistration(newUser, dialog);
                        return;
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                positiveButton.setEnabled(true);
                positiveButton.setText("Register");
                
                String errorMessage;
                if (t instanceof java.net.SocketTimeoutException || 
                    t instanceof java.net.UnknownHostException ||
                    t.getMessage() != null && t.getMessage().contains("failed to connect")) {
                    errorMessage = "Cannot connect to server. Please check:\n" +
                                 "1. You are on the university network\n" +
                                 "2. Server is accessible (10.240.72.69)\n" +
                                 "3. Internet connection is active";
                } else {
                    errorMessage = "Network error: " + t.getMessage();
                }
                
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
    
    private void createStudentDatabaseAndRetryRegistration(User newUser, AlertDialog dialog) {
        Toast.makeText(this, "Creating student database...", Toast.LENGTH_SHORT).show();
        
        Call<DatabaseResponse> call = apiService.createStudentDatabase(STUDENT_ID);
        call.enqueue(new Callback<DatabaseResponse>() {
            @Override
            public void onResponse(Call<DatabaseResponse> call, Response<DatabaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DatabaseResponse dbResponse = response.body();
                    Toast.makeText(LoginActivity.this, 
                        dbResponse.getMessage() + " Retrying registration...", 
                        Toast.LENGTH_SHORT).show();
                    // Retry registration after database is created
                    performRegistration(newUser, dialog);
                } else {
                    Toast.makeText(LoginActivity.this, 
                        "Failed to create database. Please contact administrator.", 
                        Toast.LENGTH_SHORT).show();
                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setEnabled(true);
                    positiveButton.setText("Register");
                }
            }
            
            @Override
            public void onFailure(Call<DatabaseResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, 
                    "Error creating database: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setEnabled(true);
                positiveButton.setText("Register");
            }
        });
    }
}

