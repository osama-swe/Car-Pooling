package com.example.carpooling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    EditText editTextName;
    EditText editTextUsername;
    EditText editTextPassword;
    EditText editTextConfirmPassword;
    Button signupButton;
    TextView textViewLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Welcome.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        editTextName = findViewById(R.id.name);
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.confirmPassword);
        signupButton = findViewById(R.id.signupButton);
        progressBar = findViewById(R.id.progressBar);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String name, email, password, confirmPassword;
                name = editTextName.getText().toString();
                email = editTextUsername.getText().toString();
                password = editTextPassword.getText().toString();
                confirmPassword = editTextConfirmPassword.getText().toString();
                if(TextUtils.isEmpty(email)){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!email.endsWith("@eng.asu.edu.eg")) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "Invalid email domain", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length() < 6){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "Password must be >= 6 Characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "Password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(SignUpActivity.this, "Account created.", Toast.LENGTH_SHORT).show();
                                    // Save user data locally
                                    saveUserDataLocally(name, email, password);
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            // Method to save user data to SQLite database
                            private void saveUserDataLocally(String name, String email, String password) {
                                UserDataSource userDataSource = new UserDataSource(SignUpActivity.this);
                                userDataSource.open();

                                // Check if the user already exists in the local database
                                User existingUser = userDataSource.getUserByEmail(email);
                                if (existingUser == null) {
                                    // User does not exist, save to the local database
                                    User newUser = new User();
                                    newUser.setName(name);
                                    newUser.setEmail(email);
                                    newUser.setPassword(password);

                                    long userId = userDataSource.insertUser(newUser);
                                    Log.d("SignUpActivity", "User saved locally with ID: " + userId);

                                    // Set the logged-in user in the UserDataSource
                                    userDataSource.setLoggedInUser(newUser);
                                }

                                userDataSource.close();
                            }
                        });
            }
        });
        textViewLogin = findViewById(R.id.loginText);
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event, navigate to SignUpActivity
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}