package com.example.restaurantapp.api;

import com.example.restaurantapp.model.DatabaseResponse;
import com.example.restaurantapp.model.MessageResponse;
import com.example.restaurantapp.model.User;
import com.example.restaurantapp.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    
    // Create student database endpoint
    // Endpoint: POST /create_student/{studentId}
    // Response: { "message": "Student database created successfully" }
    @POST("create_student/{studentId}")
    Call<DatabaseResponse> createStudentDatabase(@Path("studentId") String studentId);
    
    // Get all users endpoint
    // Endpoint: GET /read_all_users/{studentId}
    // Response: { "users": [...] }
    @GET("read_all_users/{studentId}")
    Call<UserResponse> getAllUsers(@Path("studentId") String studentId);
    
    // Create user endpoint
    // Endpoint: POST /create_user/{studentId}
    // Request Body: User object with: username, password, firstname, lastname, email, contact, usertype
    // Response: { "message": "User created successfully" }
    @POST("create_user/{studentId}")
    Call<MessageResponse> createUser(@Path("studentId") String studentId, @Body User user);
}

