package com.example.live_courier_ut3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FundsPage extends AppCompatActivity {


    Button plusTen;
    Button minusTen;
    TextView currentFunds;
    String fundsKey = "funds";
    String fundsLeft;
    String TAG = "FundsPage";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_funds_page);
        Intent intent = getIntent();
        fundsLeft = intent.getStringExtra(fundsKey);

        plusTen = findViewById(R.id.btn_plus_ten);
        minusTen = findViewById(R.id.btn_minus_ten);
        currentFunds = findViewById(R.id.tv_set_add_funds);

        currentFunds.setText(fundsLeft);
        Log.d(TAG, "funds left " + fundsLeft);

        DocumentReference mDocRef = FirebaseFirestore.getInstance().document("");






    }

}
