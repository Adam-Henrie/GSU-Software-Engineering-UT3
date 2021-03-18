package com.example.live_courier_ut3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemPage extends AppCompatActivity {

    String itemName;
    String itemLookup = "itemLookup";
    String store;
    String storeLookup = "store";

    private static final String TAG = "ITEM_PAGE" ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_page);
        Intent intent = getIntent();
        store = intent.getStringExtra(storeLookup);
        itemName = intent.getStringExtra(itemLookup);
        //put in another extra bit with getIntent. put extra that pass the item name to look up and fill the ItemPage fields

        Log.d(TAG, "onCreate: sdsdsds " + store);

        ImageView image =(ImageView) findViewById(R.id.itemImageDetails);
     //   Picasso.get().load("https://www.theanimalspot.com/wp-content/uploads/2019/01/brownbearsmall.jpg").into(image);

        DocumentReference docDetails = FirebaseFirestore.getInstance().document("stores/" + store + "/itemInfo/items");

        docDetails.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    ArrayList<Map<String, String>> itemArrayMapList = (ArrayList<Map<String, String>>) documentSnapshot.get("itemList");
                    String url = "" + itemArrayMapList.get(itemArrayMapList.indexOf(itemName) + 1).values() + "";

                    Handler h = new Handler();

                    h.postDelayed(new Runnable() {
                        public void run() {
                            Picasso.get().load("" + url + "").into(image);
                        }
                    }, 1000);


                    Log.d(TAG," url? " + "" + itemArrayMapList.get(itemArrayMapList.indexOf(itemName) + 1).values().toString() + "");
                }
            }


        });
    }
}