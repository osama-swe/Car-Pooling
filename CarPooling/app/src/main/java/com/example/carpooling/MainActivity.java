package com.example.carpooling;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carpooling.SignUpActivity;
import com.example.carpooling.User;
import com.example.carpooling.UserDataSource;
import com.example.carpooling.Welcome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    EditText editTextUsername;
    EditText editTextPassword;
    Button loginButton;
    TextView textViewSignUp;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    UserDataSource userDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userDataSource = ((MyApplication) getApplication()).getUserDataSource();
        progressBar = findViewById(R.id.progressBar);
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();
        if (isNetworkConnected()) {
            // Online: Check if the user is already signed in
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // User is already signed in, navigate to Welcome activity
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                startActivity(intent);
                finish();
            }
        }
        else{
            User loggedInUser = userDataSource.getLoggedInUser();
            if (loggedInUser!=null){
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                startActivity(intent);
                finish();
            }
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = editTextUsername.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check network connectivity
                if (isNetworkConnected()) {
                    // Online: Authenticate with Firebase
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        UserDataSource userDataSource = ((MyApplication) getApplication()).getUserDataSource();
                                        userDataSource.open();
                                        User loggedInUser = userDataSource.getUserByEmailAndPassword(email, password);

                                        // Set the logged-in user in the UserDataSource
                                        userDataSource.setLoggedInUser(loggedInUser);

                                        userDataSource.close();

                                        Toast.makeText(MainActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), Welcome.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign-in fails, display a message to the user.
                                        Exception exception = task.getException();
                                        Toast.makeText(MainActivity.this, "Authentication failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Offline: Check local database for correct email and password
                    authenticateOffline(email, password);
                }
            }
        });

        textViewSignUp = findViewById(R.id.signupText);
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event, navigate to SignUpActivity
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Method to check network connectivity
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // Method to authenticate offline against local database
    private void authenticateOffline(String email, String password) {
        userDataSource.open();

        User user = userDataSource.getUserByEmailAndPassword(email, password);

        userDataSource.close();

        progressBar.setVisibility(View.GONE);

        if (user != null) {
            // Offline authentication successful
            userDataSource.setLoggedInUser(user);
            Toast.makeText(MainActivity.this, "Offline Login successful.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Welcome.class);
            startActivity(intent);
            finish();
        } else {
            // Offline authentication failed
            Toast.makeText(MainActivity.this, "Incorrect email or password.", Toast.LENGTH_SHORT).show();
        }
    }
}
