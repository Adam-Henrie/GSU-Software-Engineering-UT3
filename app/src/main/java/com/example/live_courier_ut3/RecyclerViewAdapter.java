package com.example.live_courier_ut3;


import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.*;
import androidx.recyclerview.widget.RecyclerView;

import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

        private static final String TAG = "RecyclerViewAdapter";

        private ArrayList<String> mImageNames = new ArrayList<>();
        private ArrayList<String> mImages = new ArrayList<>();
        private Context mContext;
        private ArrayList<String> mImagePrices = new ArrayList<>();
        private ArrayList<String> mItemQuantity = new ArrayList<>();
        private String mStoreName;


        public RecyclerViewAdapter(Context context, ArrayList<String> imageNames, ArrayList<String> images, ArrayList<String> imagePrices, ArrayList<String> itemQuantity, String store ) {
            mStoreName = store;
            mImageNames = imageNames;
            mImages = images;
            mContext = context;
            mImagePrices = imagePrices;
            mItemQuantity = itemQuantity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            Log.d(TAG, "onBindViewHolder: called.");

            Glide.with(mContext)
                    .asBitmap()
                    .load(mImages.get(position))
                    .into(holder.image);


            holder.imageName.setText(mImageNames.get(position));
            holder.imagePrice.setText(mImagePrices.get(position));
            holder.itemQuantity.setText(mItemQuantity.get(position));

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));

                    DocumentReference mCartRef = FirebaseFirestore.getInstance().document("users/" +
                            "Adam Henrie" +
                                    "/storeCart/" +
                                    mStoreName +"Cart"
                            );
                    DocumentReference mDocRef = FirebaseFirestore.getInstance().document("stores/" + mStoreName + "/itemInfo/items");

            mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String itemBuy = (mImageNames.get(position));
                        ArrayList<Map<String,String>> itemArrayMapList = (ArrayList<Map<String, String>>) documentSnapshot.get("itemList");
                        Log.d(TAG, "checking name info " + itemBuy);
                        Log.d(TAG, "checking itemArrayMapList " + itemArrayMapList.toString() );
                          assert itemArrayMapList != null;
                       // int loc = itemArrayMapList.indexOf(itemBuy) + 1; //used to have a plus 1
                        Log.d(TAG, "checking size of itemArrayMapList" + itemArrayMapList.size());
                    //    Log.d(TAG,"checking to see if we get the location of first item  " + (itemArrayMapList.indexOf(itemBuy) + 1));
                     //   Map<String,Object> itemToSave = new HashMap<>();

                    //    itemToSave.put("itemList",itemArrayMapList.get(loc).keySet());
                     //   Log.d(TAG, "checking to see if it is a array with a map in it" + itemToSave );

                        mCartRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

//                                try {
//                                    Thread.sleep(1000);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }

                                ArrayList<Map<String,String>> overwrite = new ArrayList<Map<String,String>>();
                                        overwrite = (ArrayList<Map<String,String>>) documentSnapshot.get("itemList");
                                        List<Map<String,String>> list = new ArrayList<>();
                                        list.addAll(overwrite);
                                Log.d(TAG, "checking to see if list gets the items" + list.toString());

                                       // String output = Arrays.toString(itemArrayMapList.get(loc).keySet().toArray()).replace("[", "").replace("]", "");
                                        //list.add(itemArrayMapList.get(loc).keySet().toString());

                                        Map<String,String> output = new HashMap<>();
                                        output = itemArrayMapList.get(itemArrayMapList.indexOf(itemBuy) + 1);
                                Log.d(TAG, "checking to see if list get items output" + output);
                                        list.add(output);
                                        Log.d(TAG, "checking to see if list get items" + list.toString());

                                      //  String[] array = overwrite.toArray(new String[overwrite.size()] );
                                    //    Log.d(TAG, "checking to see what the array looks like" + overwrite.toString() );

                                            mCartRef.update("itemList", list  ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Item cart", "Item Added to cart!");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("Item failure", "Item was not saved", e);
                                                }
                                            });


                                         //   mCartRef.set(array);
                            }
                        });

                    }
                }
            });
                    Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();

                   // toCartActivity(mStoreName);
                }
            });
        }


        public void toCartActivity(String mStoreName) {
            Intent intent = new Intent(mContext, Cart.class);
            intent.putExtra("storeLookup", mStoreName);

            mContext.startActivity(intent);

        }

        @Override
        public int getItemCount() {
            return mImageNames.size() ;
        }


        public class ViewHolder extends RecyclerView.ViewHolder{

            CircleImageView image;
            TextView imageName;
            RelativeLayout parentLayout;
            TextView imagePrice;
            TextView itemQuantity;

            public ViewHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
                imageName = itemView.findViewById(R.id.image_name);
                parentLayout = itemView.findViewById(R.id.parent_layout);
                imagePrice = itemView.findViewById(R.id.imagePrice);
                itemQuantity = itemView.findViewById(R.id.itemQuantity);
            }
        }
    }


