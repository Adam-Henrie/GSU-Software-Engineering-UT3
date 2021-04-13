package com.example.live_courier_ut3;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class DriversList extends AppCompatActivity {


    String store;
    String dropLocation;

    ImageButton driver1;
    ImageButton driver2;
    ImageButton driver3;

    TextView driverName1;
    TextView driverName2;
    TextView driverName3;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drivers_list);
        Intent intent = getIntent();
        dropLocation = intent.getStringExtra("dropLocation");
       store = intent.getStringExtra("store");


        driver1 = findViewById(R.id.iv_driver_1);
        driver2 = findViewById(R.id.iv_driver_2);
        driver3 = findViewById(R.id.iv_driver_3);

        driverName1 = findViewById(R.id.tv_driver_name);
        driverName2 = findViewById(R.id.tv_driver_name_2);
        driverName3 = findViewById(R.id.tv_driver_name_3);


        Picasso.get().load("https://busytape.com/wp-content/uploads/2020/03/Josh-peck-1.jpg").into(driver1);
        Picasso.get().load("https://www.thefamouspeople.com/profiles/thumbs/jan-hooks-1.jpg").into(driver2);
        Picasso.get().load("http://newsradio1620.com/wp-content/uploads/2020/08/sarah-matthews.jpg").into(driver3);

        driver1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DriverDetails.class);
                intent.putExtra("dropLocation", dropLocation);
                intent.putExtra("name", "Josh");
                intent.putExtra("store", store);
                startActivity(intent);
            }
        });

        driver2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DriverDetails.class);
                intent.putExtra("dropLocation", dropLocation);
                intent.putExtra("name", "Jan");
                intent.putExtra("store", store);
                startActivity(intent);
            }
        });

        driver3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DriverDetails.class);
                intent.putExtra("dropLocation", dropLocation);
                intent.putExtra("name", "Sarah");
                intent.putExtra("store", store);
                startActivity(intent);
            }
        });

    }


}
