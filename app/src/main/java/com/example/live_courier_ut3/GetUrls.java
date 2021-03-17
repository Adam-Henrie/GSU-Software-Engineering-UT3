package com.example.live_courier_ut3;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GetUrls {


   public ArrayList<String> imageUrls = new ArrayList<String>();

    public GetUrls(ArrayList<String> urls) {
        imageUrls = urls;
    };


    public ArrayList<String> getImageUrls(){

        return imageUrls;
    };


    public void addUrl(ArrayList<String> url){

        imageUrls = url;
        Log.d("url get" , "got the url" + imageUrls.toString()   );
    };



};
