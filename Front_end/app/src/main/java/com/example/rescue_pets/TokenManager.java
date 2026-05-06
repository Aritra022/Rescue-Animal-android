package com.example.rescue_pets;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokenManager {

    public static void fetchAndSendToken(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("FCM_TOKEN", "Fetching token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d("FCM_TOKEN", token);
                    saveTokenToServer(context, token);
                });
    }

    public static void saveTokenToServer(Context context, String token) {
        String email = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getString("userEmail", "");

        if (email.isEmpty()) {
            Log.e("FCM_TOKEN", "User email not found");
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Call<ResponseBody> call = apiService.saveFcmToken(email, token);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("FCM_TOKEN", "Token saved to backend");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("FCM_TOKEN", "Failed to save token", t);
            }
        });
    }
}