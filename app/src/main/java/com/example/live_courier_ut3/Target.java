package com.example.live_courier_ut3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Target extends AppCompatActivity {


    private static final String TAG = "TargetActivity";
    LocationManager locationManager;
    LocationListener locationListener;


    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);

        initImageBitmaps();


    }

    private void initImageBitmaps(){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        mImageUrls.add("https://d2cdo4blch85n8.cloudfront.net/wp-content/uploads/2020/06/Sony-PlayStation-5-Officially-Announced-image-2.jpg");
        mNames.add("PS5");

        mImageUrls.add("http://1.bp.blogspot.com/-SJk0Cmq3eSg/VS2G9haeJFI/AAAAAAAADRs/ankyII85fmw/s1600/Target-dog.jpg");
        mNames.add("Delux Target Puppo");

        mImageUrls.add("https://www.joehxblog.com/images/certs/wright-state-university-bachelor-of-science-in-computer-science.png");
        mNames.add("Bachelor of Science Computer Science");

        mImageUrls.add("https://shop.jtglobal.com/wp-content/uploads/2020/10/iphone-12-red.jpg");
        mNames.add("iPhone 12");


        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        mNames.add("Mahahual");

        mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg");
        mNames.add("Frozen Lake");


        mImageUrls.add("https://i.redd.it/glin0nwndo501.jpg");
        mNames.add("White Sands Desert");

        mImageUrls.add("https://i.redd.it/obx4zydshg601.jpg");
        mNames.add("Austrailia");

        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");
        mNames.add("Washington");

        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames, mImageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



}