package com.example.carpooling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Welcome extends AppCompatActivity {
    TextView welcomeTextView;
    Button routesButton;
    Button tripsButton;
    Button historyButton;
    Button profileButton;
    Button logOutButton;
    FirebaseAuth auth;
    FirebaseUser user;
    UserDataSource userDataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        welcomeTextView = findViewById(R.id.welcomeText);
        profileButton = findViewById(R.id.profileButton);
        logOutButton = findViewById(R.id.logoutButton);
        historyButton = findViewById(R.id.historyButton);
        routesButton = findViewById(R.id.routesButton);
        tripsButton = findViewById(R.id.tripsButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userDataSource = ((MyApplication) getApplication()).getUserDataSource();

        // Check network connectivity
        if (isNetworkConnected()) {
            // User is logged in online or offline
            if (user == null) {
                // Redirect to MainActivity if user is null (logged out)
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // User is logged in, show welcome message
                welcomeTextView.setText("Welcome, " + user.getEmail());
            }
        } else {
            // No network connection, check logged in user from local database
            User loggedInUser = userDataSource.getLoggedInUser();
            System.out.println("loggedin user" + loggedInUser);
            if (loggedInUser != null) {
                System.out.println("loggedin user success offline");
                // User is logged in offline, show welcome message
                welcomeTextView.setText("Welcome, " + loggedInUser.getEmail());
            } else {
                // No network and not logged in offline, redirect to MainActivity
                System.out.println("loggedin user failed offline");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

        routesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this, AvailableRoutes.class);
                startActivity(intent);
            }
        });

        tripsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this, Trips.class);
                startActivity(intent);
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this, History.class);
                startActivity(intent);
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this, Profile.class);
                startActivity(intent);
            }
        });
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDataSource.open();
                userDataSource.setLoggedInUser(null);
                Log.d("UserData", "Logged-out user: " + userDataSource.getLoggedInUser());
                userDataSource.close();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        Log.d("WelcomeActivity", "Network connected: " + isConnected);
        return isConnected;
    }
}