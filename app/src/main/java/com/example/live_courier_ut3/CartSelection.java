package com.example.live_courier_ut3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CartSelection extends AppCompatActivity {

        Button toWalmart;
        Button toTarget;
        Button toTacoBell;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_cart_selection);

        toWalmart = findViewById(R.id.btn_Walmart);
        toTarget = findViewById(R.id.btn_Target);
        toTacoBell = findViewById(R.id.btn_Taco_Bell);


        toTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Cart.class);
                intent.putExtra("store","Target");
                startActivity(intent);
            }
        });

        toWalmart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Cart.class);
                intent.putExtra("store","Walmart");
                startActivity(intent);
            }
        });

        toTacoBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Cart.class);
                intent.putExtra("store","Taco Bell");
                startActivity(intent);
            }
        });



    }






}
