package com.example.live_courier_ut3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reviews extends AppCompatActivity {

    String driverName;
    TextView review;
    DocumentReference getReviews;
    Button leaveReview;
    Button next;
    Button prev;


    int reviewTracker = 0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_reviews);

        Intent intent = getIntent();
        driverName = intent.getStringExtra("driver");
        review = findViewById(R.id.tv_review_text);

        getReviews = FirebaseFirestore.getInstance().document("drivers/" + driverName + "/userReviews/reviews");

        getReviews.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> reviewText = (List<String>) documentSnapshot.get("reviewsArray");
                review.setText(reviewText.get(reviewTracker));
            }
        });

        leaveReview = findViewById(R.id.btn_leave_review);
        next = findViewById(R.id.btn_next_review);
        prev = findViewById(R.id.btn_last_review);

        leaveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent toLeaveReview = new Intent(v.getContext(),SubmitReview.class);
               toLeaveReview.putExtra("driver",driverName);
               startActivity(toLeaveReview);
            }
        });




        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReviews.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> reviewText = (List<String>) documentSnapshot.get("reviewsArray");
                        if(reviewTracker == reviewText.size() - 1){
                            Toast.makeText(v.getContext(), "This is the last review",Toast.LENGTH_LONG).show();
                        } else {
                            review.setText(reviewText.get(reviewTracker + 1));
                            reviewTracker++;
                        }

                    }
                });
            }
        });


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReviews.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> reviewText = (List<String>) documentSnapshot.get("reviewsArray");
                        if(reviewTracker == 0){
                            Toast.makeText(v.getContext(), "This is the first review",Toast.LENGTH_LONG).show();
                        } else {
                            review.setText(reviewText.get(reviewTracker - 1));
                            reviewTracker--;
                        }

                    }
                });
            }
        });




    }


}
