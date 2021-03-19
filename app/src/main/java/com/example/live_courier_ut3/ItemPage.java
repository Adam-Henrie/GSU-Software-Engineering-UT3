package com.example.live_courier_ut3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemPage extends AppCompatActivity {

    String itemName;
    String itemLookup = "itemLookup";
    String store;
    String storeLookup = "store";
    int position;
    String positionLookup = "positionLookup";
    TextView itemTextDetails;
    TextView itemNumberLeft;
    TextView itemPrice;
    private static final String TAG = "ITEM_PAGE" ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_page);
        Intent intent = getIntent();
        store = intent.getStringExtra(storeLookup);
        itemName = intent.getStringExtra(itemLookup);
        position = intent.getIntExtra(positionLookup, 1);
        Log.d(TAG, "did the itemName go through?" + itemName);
        Log.d(TAG, "did the position come through?" + position);
        //put in another extra bit with getIntent. put extra that pass the item name to look up and fill the ItemPage fields

        Log.d(TAG, "onCreate: sdsdsds " + store);

        ImageView image =(ImageView) findViewById(R.id.itemImageDetails);
        itemTextDetails = findViewById(R.id.itemDetailText);
        itemNumberLeft = findViewById(R.id.insert_number);
        itemPrice = findViewById(R.id.insert_price);
     //   Picasso.get().load("https://www.theanimalspot.com/wp-content/uploads/2019/01/brownbearsmall.jpg").into(image);
     //   Picasso.get().load(  "https://cdn.vox-cdn.com/thumbor/2w5q--npZhcbqc4ssQdTluNFfRg=/0x60:1920x1140/1600x900/cdn.vox-cdn.com/uploads/chorus_image/image/56173531/Starkiller_Wallpapers___Wallpaper_Cave.0.jpg").into(image);

        DocumentReference docDetails = FirebaseFirestore.getInstance().document("stores/" + store + "/itemInfo/items");



        docDetails.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    ArrayList<Map<String, String>> itemArrayMapList = (ArrayList<Map<String, String>>) documentSnapshot.get("itemList");
                    String url = Arrays.toString(itemArrayMapList.get(itemArrayMapList.indexOf(itemName)+ position + 1).values().toArray()).replace("[", "").replace("]","");

                //   String output = Arrays.toString(itemArrayMapList.get(loc).keySet().toArray()).replace("[", "").replace("]", "");
                    Handler h = new Handler();

                    h.postDelayed(new Runnable() {
                        public void run() {
                            Picasso.get().load("" + url + "").into(image);

                          //  Picasso.get().load("https://cdn.vox-cdn.com/thumbor/2w5q--npZhcbqc4ssQdTluNFfRg=/0x60:1920x1140/1600x900/cdn.vox-cdn.com/uploads/chorus_image/image/56173531/Starkiller_Wallpapers___Wallpaper_Cave.0.jpg").into(image);

                        }
                    }, 1500);


                    Log.d(TAG," url? " + "" + itemArrayMapList.get(itemArrayMapList.indexOf(itemName) + position + 1).values().toString() + "");

                    List<String> itemDetails = (List<String>) documentSnapshot.get("itemDetails");
                    Log.d(TAG, "did this grab the item details? " + itemDetails.toString());
                    itemTextDetails.setText(itemDetails.get(position).toString());
                    List<String> itemInventory = (List<String>) documentSnapshot.get("itemNumber");
                    itemNumberLeft.setText(itemInventory.get(position).toString());
                    List<String> itemPrices = (List<String>) documentSnapshot.get("prices");
                    itemPrice.setText(itemPrices.get(position).toString());

                }
            }


         });
    }
}