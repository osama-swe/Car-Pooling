package com.example.driver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddTrip extends AppCompatActivity {

    private EditText etSource, etDestination, etDate, etTime, etPrice;
    private Button btnAddTrip;

    private DatabaseReference routesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        routesRef = FirebaseDatabase.getInstance().getReference("Routes");

        etSource = findViewById(R.id.etSource);
        etDestination = findViewById(R.id.etDestination);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etPrice = findViewById(R.id.etPrice);
        btnAddTrip = findViewById(R.id.btnAddTrip);

        btnAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTrip();
            }
        });
    }

    private void addTrip() {
        String source = etSource.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String price = etPrice.getText().toString().trim();

        boolean isToCampus;

        if(source.isEmpty() || destination.isEmpty() || date.isEmpty() || time.isEmpty() || price.isEmpty()){
            Toast.makeText(this, "Enter data in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate if at least one of them is gate 3 or gate 4
        if (!source.equalsIgnoreCase("gate 3") && !source.equalsIgnoreCase("gate 4") &&
                !destination.equalsIgnoreCase("gate 3") && !destination.equalsIgnoreCase("gate 4")) {
            Toast.makeText(this, "At least one of Source or Destination must be gate 3 or gate 4", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate if both of them are gate 3 or gate 4
        if ((source.equalsIgnoreCase("gate 3") || source.equalsIgnoreCase("gate 4")) &&
                (destination.equalsIgnoreCase("gate 3") || destination.equalsIgnoreCase("gate 4"))) {
            Toast.makeText(this, "The trip can't be from campus to campus", Toast.LENGTH_SHORT).show();
            return;
        }

        if(source.equalsIgnoreCase("gate 3") || source.equalsIgnoreCase("gate 4")){
            isToCampus = false;
        }
        else{
            isToCampus = true;
        }
        // validate the input date
        if (!isDateValid(date)) {
            Toast.makeText(this, "Invalid Date Format. Use this format: 17 December 2023", Toast.LENGTH_SHORT).show();
            return;
        }
        // Validate the input time
        if (!isValidTime(time)) {
            // Show a toast if the time is invalid
            Toast.makeText(this, "Invalid time. Please enter 7:30 AM or 5:30 PM", Toast.LENGTH_SHORT).show();
            return; // Exit the method if the time is invalid
        }

        if(isToCampus && !time.equals("7:30 AM")){
            Toast.makeText(this, "This is a To Campus Trip. Time must be 7:30 AM", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isToCampus && !time.equals("5:30 PM")){
            Toast.makeText(this, "This is a From Campus Trip. Time must be 5:30 PM", Toast.LENGTH_SHORT).show();
            return;
        }


        // Get the current signed-in Driver's ID
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Route trip = new Route(source, destination, time, date, price, driverId);
        // Add the new route to the database with a generated key
        String newRouteKey = routesRef.push().getKey();
        trip.setRouteId(newRouteKey); // Set the routeId attribute

        if (newRouteKey != null) {
            // Create a Map to store the route details
            Map<String, Object> routeDetails = new HashMap<>();
            routeDetails.put("from", trip.getFrom());
            routeDetails.put("to", trip.getTo());
            routeDetails.put("time", trip.getTime());
            routeDetails.put("date", trip.getDate());
            routeDetails.put("price", trip.getPrice());
            routeDetails.put("routeId", trip.getRouteId());
            routeDetails.put("driverId", driverId);
            routeDetails.put("status", trip.status);
            routesRef.child(newRouteKey).setValue(routeDetails)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddTrip.this, "Trip added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddTrip.this, "Failed to add trip", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean isDateValid(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        try {
            // Parse the entered string as a date
            Date parsedDate = sdf.parse(date);
            // If parsing is successful, the entered string is in the correct format
            return parsedDate != null;
        } catch (ParseException e) {
            // If parsing fails, the entered string is not in the correct format
            return false;
        }
    }
    private boolean isValidTime(String inputTime) {
        // Validate that the time is either "7:30 AM" or "5:30 PM"
        return inputTime.equals("7:30 AM") || inputTime.equals("5:30 PM");
    }
}
