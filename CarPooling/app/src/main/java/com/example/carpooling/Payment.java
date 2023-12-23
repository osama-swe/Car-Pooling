package com.example.carpooling;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Payment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        String price = getIntent().getStringExtra("price");


        TextView priceTextView = findViewById(R.id.priceTextView);
        priceTextView.setText(price);


        Button payButton = findViewById(R.id.pay);
        EditText cardEditText = findViewById(R.id.cardNumber);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the entered card is not empty
                String enteredCard = cardEditText.getText().toString().trim();
                if (!enteredCard.isEmpty()) {
                    // The card is not empty, proceed with the payment logic
                    performPayment();
                } else {
                    // Display a toast message indicating that the card is empty
                    Toast.makeText(Payment.this, "Please enter a valid card number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void performPayment() {
        // Add your payment logic here
        // This method is called when the card is not empty, and you can proceed with the payment process
        Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
        // Add any additional logic or API calls for processing the payment
    }
}
