package com.example.live_courier_ut3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class DriverDetails extends AppCompatActivity {


    Button grabStars;
    Button grabDistance;
    Button pickThisDriver;
    TextView stars;
    TextView distance;
    TextView driver;

    String store;
    String driverName;
    String dropLocation;

    ImageView star1;
    ImageView star2;
    ImageView star3;
    ImageView star4;
    ImageView star5;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_details);

        Intent intent = getIntent();

        store = intent.getStringExtra("store");
        driverName = intent.getStringExtra("name");
        dropLocation = intent.getStringExtra("dropLocation");

        grabStars = findViewById(R.id.grabStars);
        grabDistance = findViewById(R.id.grabDistance);

        distance = findViewById(R.id.tv_distance);
        pickThisDriver = findViewById(R.id.btn_pick_driver);
        driver = findViewById(R.id.driverProfile);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);




//        Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star1);
//        Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star2);
//        Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star3);
//        Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star4);
//        Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star5);

       // star5.setVisibility(View.INVISIBLE);



        driver.setText(driverName);

        DocumentReference docRef = FirebaseFirestore.getInstance().document("drivers/Josh");

        grabStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String starRatings = documentSnapshot.getString("stars");
                        int id = Integer.parseInt(starRatings);

                        switch (id) {
                            case 1:
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star1);
                                return;
                            case 2:
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star1);
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star2);
                                return;
                            case 3:
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star1);
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star2);
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star3);
                                return;
                            case 4:
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star1);
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star2);
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star3);
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star4);
                                return;
                            case 5:
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star1);
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star2);
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star3);
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star4);
                                Picasso.get().load("http://images.wikia.com/glee/images/a/a0/Gold_Star.png").into(star5);
                                return;
                        }
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


        pickThisDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),ItemTracking.class);
                intent.putExtra("dropLocation",dropLocation);
                intent.putExtra("driver", driverName);
                intent.putExtra("store",store);


                startActivity(intent);
            }
        });


    }


}
