package com.example.live_courier_ut3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmitReview extends AppCompatActivity {


    Button submitReview;
    EditText reviewInput;
    DocumentReference getReviews;
    String driverName;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_input);

        Intent intent = getIntent();
        driverName = intent.getStringExtra("driver");

        submitReview = findViewById(R.id.btn_submit_review);
        reviewInput = findViewById(R.id.et_leave_review);
        getReviews = FirebaseFirestore.getInstance().document("drivers/" + driverName + "/userReviews/reviews");


        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getReviews.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> listOfReviews = (List<String>) documentSnapshot.get("reviewsArray");

                        String textToUpload = reviewInput.getText().toString();
                        Map<String,Object> mapOfReview = new HashMap<String,Object>();
                        listOfReviews.add(textToUpload);
                        mapOfReview.put("reviewsArray",listOfReviews);
                        getReviews.update(mapOfReview).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(v.getContext(),"Review Upload Complete",Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });






            }
        });


    }
}
