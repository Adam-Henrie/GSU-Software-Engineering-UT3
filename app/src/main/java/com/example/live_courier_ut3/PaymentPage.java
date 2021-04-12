package com.example.live_courier_ut3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
    Button agreeAndPay;



  public  DocumentReference cartItemLookup;

  public  DocumentReference removeFromInventory;

  public  DocumentReference mDocRef;

  public  ArrayList<Map<String,String>> itemName;

   public ArrayList<String> numInv;    //new ArrayList<String>(Collections.emptyList());


   public List<String> mNames = new ArrayList<String>();
    public List<Integer> mIndex = new ArrayList<Integer>();
    public String numUpdate;

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
        agreeAndPay = findViewById(R.id.agree_and_pay);







    //    Double storeDoubleTotal = Double.parseDouble(storeTotal);
    //    orderTotal.setText(Double.toString(storeDoubleTotal));
            orderTotal.setText(storeTotal);
     //   DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/" + FirebaseAuth.getInstance().getCurrentUser() + "/userFunds/funds");

        //Adam Henrie changed
         mDocRef = FirebaseFirestore.getInstance().document("users/" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "/userFunds/funds");

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
                    funds = snapshot.getString("fundsLeft");

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

//thinking of grabbing the itemList from the cart. Iterating through list and progressively removing items as the program sees them.
        //this code does not account for item overdraw where the user tries to buy more items than there are currently in the inventory.
         cartItemLookup = FirebaseFirestore.getInstance().document("users/"
                + FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                + "/storeCart/"
                 + store + "Cart"
                );

         removeFromInventory = FirebaseFirestore.getInstance().document("stores/" + store + "/itemInfo/items");


        agreeAndPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DriversList.class);
                //need to send the store location to the item tracking page to start the tracking bard on the item tracking page.
                intent.putExtra("store", store);

                Map<String, Object> subFunds = new HashMap<>();
                Double updatedFunds = Double.parseDouble(funds) - Double.parseDouble(storeTotal);
                Double rounded = Math.round(updatedFunds*100.0)/100.0;
                subFunds.put("fundsLeft", Double.toString(rounded) );
                Log.d(TAG,"funds calculation" + Double.parseDouble(funds) + " " + Double.parseDouble(storeTotal));
                mDocRef.update(subFunds);
                Log.d("Payment Page: ", "order total has be subbed from funds");

                //removing items from the inventory of the store before switching activities


                cartItemLookup.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                         itemName = (ArrayList<Map<String, String>>) documentSnapshot.get("itemList");
                        Log.d(TAG, "here is the itemList from the store" + Arrays.asList(itemName).toString());
                        for (int i = 0; i < itemName.size(); i++) {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap = (HashMap<String, String>) itemName.get(i);
                            Iterator<String> mapItr = hashMap.keySet().iterator();
                            while (mapItr.hasNext()) {
                                String key = mapItr.next();
                                mNames.add(key);
                                mIndex.add(i);

                            }
                            Log.d(TAG, "onSuccess: mIndex " + mIndex);
                        }


                    }
                });


                removeFromInventory.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            numInv = (ArrayList<String>) documentSnapshot.get("itemNumber");
                            assert numInv != null;
                        //    Log.d(TAG, "Checking the value of numInv" + numInv.toString());
                        } else {
                            Log.d(TAG, "this is not working!");
                        }

                    }
                });

               // Log.d(TAG, "checking value of mNames " + mNames.toString());


//                for(int y = 0; y < size; y ++){
//                    Map<String, Object> updateInv = new HashMap<String,Object>();
//                     Log.d(TAG, "Re-checking the value of numInv" + numInv.toString());
//                    int indexOfName = numInv.indexOf(mNames.get(y));
//                    Log.d(TAG, "checking for indexOfName " + indexOfName);
//                    numUpdate = numInv.get(numInv.indexOf(mNames.get(y)));
//                    numUpdate = String.valueOf(Integer.parseInt(numUpdate) - 1);
//                    numInv.set(indexOfName,numUpdate);
//
//                    updateInv.put("itemNumber", numInv);
//                    removeFromInventory.update(updateInv);
//                    removeFromInventory.set(updateInv);
//                }

                Handler h = new Handler();

                h.postDelayed(new Runnable() {
                    public void run() {
                        int size = mNames.size();

                        forLoop(size, mNames, mIndex);
                        startActivity(intent);
                    }
                }, 5000);


            }
        });






    }



    void forLoop(int sizze, List<String> mNamess, List<Integer> index){
        int size = sizze;
        List<String> mNammmessss = mNamess;
        List<Integer> mIndexx = index;
        Log.d(TAG, "is this running!!!!!");
        for(int y = 0; y < size; y ++){
                    Map<String, Object> updateInv = new HashMap<String,Object>();
                     Log.d(TAG, "Re-checking the value of numInv" + numInv.toString());
                     Log.d(TAG, "names passed to method? " + mNammmessss);
                     Log.d(TAG, "first name from names " + mNammmessss.get(y));
                     Log.d(TAG, "index array " + mIndexx);
                    int indexOfName = mIndexx.get(y);
                    Log.d(TAG, "checking for indexOfName " + indexOfName);
                    numUpdate = numInv.get(indexOfName);
                    numUpdate = String.valueOf(Integer.parseInt(numUpdate) - 1);
                    numInv.set(indexOfName,numUpdate);

                    updateInv.put("itemNumber", numInv);
                    removeFromInventory.update(updateInv);
                    //this commented out command underneath can be very evil if you call ".set" it sets only the selected data field and wipes the reset from firestore
                  //  removeFromInventory.set(updateInv);
                }
    }


}