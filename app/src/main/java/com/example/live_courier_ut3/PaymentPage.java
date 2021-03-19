package com.example.live_courier_ut3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PaymentPage extends AppCompatActivity {


    private String storeLookup = "store";
    private String total = "total";
    private String TAG = "Payment_Page";
    String store;
    String storeTotal;
    TextView orderTotal;
    TextView fundsLeft;
    String funds;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_page);
        Intent intent = getIntent();

        store = intent.getStringExtra(storeLookup);
        storeTotal = intent.getStringExtra(total);
        Log.d(TAG, "Store Name " + store);
        Log.d(TAG, "Store total " + storeTotal);

        orderTotal = findViewById(R.id.insert_order_total);
        fundsLeft = findViewById(R.id.insert_funds_left);

    //    Double storeDoubleTotal = Double.parseDouble(storeTotal);
    //    orderTotal.setText(Double.toString(storeDoubleTotal));
            orderTotal.setText(storeTotal);
     //   DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/" + FirebaseAuth.getInstance().getCurrentUser() + "/userFunds/funds");

        DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/" + "Adam Henrie" + "/userFunds/funds");

        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

             funds = (String) documentSnapshot.get("fundsLeft");
            Log.d(TAG, "funds Left " + funds);
                Double fundsDouble = 0.00;
                fundsDouble = fundsDouble + Double.parseDouble(funds);
                fundsLeft.setText( Double.toString(fundsDouble));

            }
        });



    }
}