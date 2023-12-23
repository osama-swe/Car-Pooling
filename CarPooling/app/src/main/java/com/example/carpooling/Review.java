package com.example.carpooling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Review extends AppCompatActivity {
    TextView FromValueText, ToValueText, TimeValueText, DateValueText, PriceValueText;
    String routeID;
    Map<String, Boolean> user_ids;
    Button submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        FromValueText = findViewById(R.id.FromValueText);
        ToValueText = findViewById(R.id.ToValueText);
        TimeValueText = findViewById(R.id.TimeValueText);
        DateValueText = findViewById(R.id.DateValueText);
        PriceValueText = findViewById(R.id.PriceValueText);
        Intent intent = getIntent();
        FromValueText.setText(intent.getStringExtra("from"));
        ToValueText.setText(intent.getStringExtra("to"));
        TimeValueText.setText(intent.getStringExtra("time"));
        DateValueText.setText(intent.getStringExtra("date"));
        PriceValueText.setText(intent.getStringExtra("price"));
        routeID = intent.getStringExtra("route_id");
        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the reference to the "routes" node in your database
                DatabaseReference routesRef = FirebaseDatabase.getInstance().getReference("Routes");

                // Get the UID of the signed-in user
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Check if the user has already booked the trip
                routesRef.child(routeID).child("user_ids").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // User has already booked the trip, display a toast message
                            Toast.makeText(Review.this, "You have already booked this trip.", Toast.LENGTH_SHORT).show();
                        } else {
                            // User has not booked the trip, proceed to set the value to "pending"
                            routesRef.child(routeID).child("user_ids").child(userId).setValue("pending");

                            // Navigate to the desired activity
                            Intent intent = new Intent(Review.this, Welcome.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error if needed
                        Toast.makeText(Review.this, "Database error.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}