package com.example.live_courier_ut3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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
    Button addFunds;
    Button addFundsReal;
    String fundsKey = "funds";
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
        addFunds = findViewById(R.id.button_add_funds);
        addFundsReal = findViewById(R.id.button_add_funds_real);







    //    Double storeDoubleTotal = Double.parseDouble(storeTotal);
    //    orderTotal.setText(Double.toString(storeDoubleTotal));
            orderTotal.setText(storeTotal);
     //   DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/" + FirebaseAuth.getInstance().getCurrentUser() + "/userFunds/funds");

        //Adam Henrie changed
        DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "/userFunds/funds");

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



         DocumentReference docRef = FirebaseFirestore.getInstance().document("users/" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "/userFunds/funds");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);


                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    fundsLeft.setText(snapshot.getString("fundsLeft"));

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });





        // I intend to set this within an if statement that will re-run the fetch for the funds available to make sure to
        //re-display it on the payment page. 


        addFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FundsPage.class);
                intent.putExtra("funds", funds );
                startActivity(intent);
                // setContentView(R.layout.add_funds_page);
            }
        });







    }






}