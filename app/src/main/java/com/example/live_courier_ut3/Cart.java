package com.example.live_courier_ut3;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.math.BigDecimal;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Cart extends AppCompatActivity {


    private static final String TAG = "CartActivity";
    LocationManager locationManager;
    LocationListener locationListener;


    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    public static final ArrayList<String> mImageUrls = new ArrayList<>();


    public ArrayList<String> mImagePrices = new ArrayList<>();
    private ArrayList<String> mItemQuantity = new ArrayList<>();
    public String storeLookup = "store";
    public Button payNow;
    private TextView orderTotal;
    public CartRecyclerViewAdapter adapter;
    Double priceTotal = 0.00;
    private String total = "total";

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_item_list);
        Intent intent = getIntent();
        String store;
        store = intent.getStringExtra(storeLookup);
        // store = "Taco Bell";
        Log.d(TAG, "onCreate: " + store);


        initImageBitmaps();


        payNow = findViewById(R.id.pay_button);

        //this is the class that will handle the launch to the payment page activity.

        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String storeLookup = "store";
                Intent payPage = new Intent(v.getContext(), PaymentPage.class);
                payPage.putExtra(storeLookup, "Target");
                //casting to a string so that it will pass through the putExtra function. I will recast it to double if need be from within PaymentPage.java
                payPage.putExtra("total", Double.toString(priceTotal));
                startActivity(payPage);

            }
        });


    }



    private void initImageBitmaps(){
     //   String stored = store;
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

//Adam Henrie changed
        DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "/storeCart"  + "/TargetCart");
        DocumentReference mDocR = FirebaseFirestore.getInstance().document("users/" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "/storeCart"  + "/TargetCart");
        DocumentReference mDocRe = FirebaseFirestore.getInstance().document("users/" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "/storeCart"  + "/TargetCart");
   //     Log.d(TAG, "initImageBitmaps:          " + stored);



        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    ArrayList<Map<String, String>> itemMap = new ArrayList<>();
                    itemMap = (ArrayList<Map<String, String>>) documentSnapshot.get("itemList");
                    for(int i = 0; i < itemMap.size(); i++){
                        HashMap<String,String> hashMap = new HashMap<String, String>();
                        hashMap = (HashMap<String, String>) itemMap.get(i);
                        Iterator<String> mapItr = hashMap.keySet().iterator();
                        while(mapItr.hasNext()){
                            String key = mapItr.next();
                            mNames.add(key);
                            mImageUrls.add(hashMap.get(key) );
                        }
                        Log.d(TAG, "onSuccess: mNames " + mNames.toString() );
                        Log.d(TAG, "onSuccess:  " + mImageUrls.toString() );

                    }


                }

                // Log.d(TAG, "initImageBitmaps:          " + stored);
                mDocR.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            ArrayList<String> transfer = (ArrayList<String>) documentSnapshot.get("prices");
                            assert transfer != null;
                            mImagePrices.addAll(transfer);

                            // Log.d(TAG, "prices " +  transfer.toString());
                            //  Log.d(TAG, "image urls inside getting prices" + transfer.toString());
                        }



                        //     Log.d(TAG, "initImageBitmaps:          " + stored);
                        mDocRe.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    ArrayList<String> trans = (ArrayList<String>) documentSnapshot.get("itemNumber");
                                    assert trans != null;
                                    mItemQuantity.addAll(trans);
                                    Log.d(TAG, "number of items " +  trans.toString());
                                    Log.d(TAG, "prices array test " + mImagePrices);
                                    Log.d(TAG, "current image urlsssss" +  mImageUrls.toString() )   ;
                                    Log.d(TAG, "onSuccess: mNames " + mNames.toString() );
                                    orderTotal = findViewById(R.id.insert_cart_order_total);

                                //    NumberFormat nf = NumberFormat.getCurrencyInstance();

                                    for(int i = 0; i < mImagePrices.size(); i++){
                                        priceTotal = priceTotal +  Double.parseDouble(mImagePrices.get(i));

                                    }

                                    orderTotal.setText(Double.toString(priceTotal));


 //recyclerView call here --------->
                                    initRecyclerView();

                                }

                            }
                        });




                    }
                });




            }

        });






// TODO: 3/11/2021  this needs to have a DocumentReference to the user cart with the number of Items that are intended to be ordered. 
        
        





//        mImageUrls.add("https://d2cdo4blch85n8.cloudfront.net/wp-content/uploads/2020/06/Sony-PlayStation-5-Officially-Announced-image-2.jpg");
//        mNames.add("PS5");
//        mImagePrices.add("$5.00");
//        mItemQuantity.add("20");












    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = new RecyclerView(this);
       recyclerView = findViewById(R.id.cart_recycler_view);
       // recyclerView.getPreserveFocusAfterLayout();
         adapter = new CartRecyclerViewAdapter(this, mNames, mImageUrls,mImagePrices, mItemQuantity);

      //  adapter.setHasStableIds(true);
      //  adapter.notifyDataSetChanged();
   //     recyclerView.refreshDrawableState();
     //   recyclerView.invalidate();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



       // recyclerView.setHasFixedSize(false);

    }



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startLoginActivity();
                                } else {
                                    System.out.println("Failed");
                                }
                            }
                        });
                return true;

            case R.id.cart:
            Toast.makeText(this, "Cart",Toast.LENGTH_SHORT).show();
            String storeLookup = "store";
            Intent toCart = new Intent(this.getApplicationContext(), Cart.class);
            toCart.putExtra(storeLookup, "Target");
            startActivity(toCart);


            return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
        this.finish();
    }


}