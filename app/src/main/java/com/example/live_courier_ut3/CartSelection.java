package com.example.live_courier_ut3;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class CartSelection extends AppCompatActivity {

        Button toWalmart;
        Button toTarget;
        Button toTacoBell;
        Button toCVS;
        ImageButton toAldi;
        ImageButton toCookout;
        ImageButton toGameStop;
        ImageButton toMcDonalds;
        ImageButton toPublix;
        ImageButton toWendys;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_cart_selection);

        toWalmart = findViewById(R.id.btn_Walmart);
        toTarget = findViewById(R.id.btn_Target);
        toTacoBell = findViewById(R.id.btn_Taco_Bell);
        toCVS = findViewById(R.id.btn_CVS);
        toAldi = findViewById(R.id.aldiCart);
        toCookout = findViewById(R.id.cookoutCart);
        toGameStop = findViewById(R.id.gamestopCart);
        toMcDonalds = findViewById(R.id.mcDonaldsCart);
        toPublix = findViewById(R.id.publixCart);
        toWendys = findViewById(R.id.wendysCart);


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

        toCVS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Cart.class);
                intent.putExtra("store","CVS");
                startActivity(intent);
            }
        });

        toAldi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Cart.class);
                intent.putExtra("store","Aldi");
                startActivity(intent);
            }
        });

        toCookout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Cart.class);
                intent.putExtra("store","Cookout");
                startActivity(intent);
            }
        });

        toGameStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Cart.class);
                intent.putExtra("store","GameStop");
                startActivity(intent);
            }
        });

        toMcDonalds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Cart.class);
                intent.putExtra("store","McDonalds");
                startActivity(intent);
            }
        });

        toPublix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Cart.class);
                intent.putExtra("store","Publix");
                startActivity(intent);
            }
        });

        toWendys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Cart.class);
                intent.putExtra("store","Wendy's");
                startActivity(intent);
            }
        });



    }
}
