package com.example.live_courier_ut3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class DriverDetails extends AppCompatActivity {


    Button grabStars;
    Button grabDistance;
    TextView stars;
    TextView distance;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_details);

        grabStars = findViewById(R.id.grabStars);
        grabDistance = findViewById(R.id.grabDistance);
        stars = findViewById(R.id.tv_grab_stars);
        distance = findViewById(R.id.tv_distance);

        DocumentReference docRef = FirebaseFirestore.getInstance().document("drivers/Josh");

        grabStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                       String starRatings = documentSnapshot.getString("stars");


                       stars.setText(starRatings);


                    }
                });

            }
        });


        grabDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String distanceInfo = documentSnapshot.getString("distance");
                        distance.setText(distanceInfo);


                    }
                });
            }
        });



    }


}
