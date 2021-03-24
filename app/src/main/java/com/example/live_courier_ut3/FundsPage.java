package com.example.live_courier_ut3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FundsPage extends AppCompatActivity {


    Button plusTen;
    Button minusTen;
    TextView currentFunds;
    String fundsKey = "funds";
    String fundsLeft;
    String TAG = "FundsPage";
    Button addFunds;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_funds_page);
        Intent intent = getIntent();
        fundsLeft = intent.getStringExtra(fundsKey);

        plusTen = findViewById(R.id.btn_plus_ten);
        minusTen = findViewById(R.id.btn_minus_ten);
        currentFunds = findViewById(R.id.tv_set_add_funds);
        addFunds = findViewById(R.id.button_add_funds_real);

        currentFunds.setText(fundsLeft);
        Log.d(TAG, "funds left " + fundsLeft);



        plusTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double add =  Double.parseDouble(fundsLeft) + 10;
             fundsLeft = Double.toString(add);
             currentFunds.setText(fundsLeft);
            }
        });

        minusTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double sub =  Double.parseDouble(fundsLeft) - 10;
                fundsLeft = Double.toString(sub);
                currentFunds.setText(fundsLeft);
            }
        });


        addFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/"
                        + FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                        + "/userFunds"
                        + "/funds");
                Map<String, Object> newFunds = new HashMap<>();
                newFunds.put("fundsLeft", fundsLeft);
                Log.d(TAG, "current funds in this page" + newFunds.toString());
                mDocRef.update(newFunds);
                mDocRef.set(newFunds);
            }
        });





    }

}
