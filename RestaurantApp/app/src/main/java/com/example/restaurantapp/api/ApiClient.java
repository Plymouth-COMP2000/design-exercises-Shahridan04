package com.example.restaurantapp.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    
    private static final String BASE_URL = "http://10.240.72.69/comp2000/coursework/";
    private static ApiService apiService;
    
    public static ApiService getApiService() {
        if (apiService == null) {
            // Configure OkHttpClient with increased timeouts
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)      // Connection timeout: 30 seconds
                    .readTimeout(30, TimeUnit.SECONDS)         // Read timeout: 30 seconds
                    .writeTimeout(30, TimeUnit.SECONDS)        // Write timeout: 30 seconds
                    .build();
            
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}

