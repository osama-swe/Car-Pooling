
package com.example.carpooling;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.TextView;

public class Profile extends AppCompatActivity {

    TextView tvName, tvEmail, tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize TextViews
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);

        // Get the UserDataSource instance from MainActivity or wherever it's instantiated
        UserDataSource userDataSource = ((MyApplication) getApplication()).getUserDataSource();

        // Assuming you have a method to retrieve the logged-in user
        User currentUser = userDataSource.getLoggedInUser();

        if (currentUser != null) {
            Log.d("ProfileActivity", "User retrieved successfully");
            // Display user information
            String name = currentUser.getName();
            String email = currentUser.getEmail();
            String password = currentUser.getPassword();
            System.out.println(name);

            tvName.setText("Name: " + name);
            tvEmail.setText("Email: " + email);
            tvPassword.setText("Password: " + password);
        } else {
            Log.d("ProfileActivity", "User is null");
        }
    }
}
