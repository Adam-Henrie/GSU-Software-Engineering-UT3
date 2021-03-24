package com.example.live_courier_ut3;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

public class ItemTracking extends AppCompatActivity {


    private static final String TAG = "ItemTracking" ;


    public void onMapReady(GoogleMap map) {
        googleMap = map;
        //this is my house

        LatLng latLng = new LatLng(34.140980, -84.357679);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("My home");
        markerOptions.position(latLng);


        LatLng walmart = new LatLng(34.149409, -84.249323);
        WAL_MARKER.title("Walmart, Milton");
        WAL_MARKER.position(walmart);

        //markerOptions here is referring to my home
        // TODO: 2/26/2021 change this to my home so that I don't get confused.



        googleMap.addMarker(markerOptions);
        googleMap.addMarker(WAL_MARKER);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        googleMap.animateCamera(cameraUpdate);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        googleMap.setMyLocationEnabled(true);

        //geo api context used to be here

    }


    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey" ;

    //textview for the quote input
    TextView mQuoteTextView;

    //boolean for checking if google play services is enabled
    boolean isPermissionGranted;

    //map object used for maps
    MapView mapView;

    private GoogleMap googleMap;

    //location object based in google maps sdk
    private FusedLocationProviderClient mFusedLocationClient;

    private GeoApiContext mGeoApiContext;

    public MarkerOptions userPosition = new MarkerOptions();

    public GeoPoint geoStart;

    public static final MarkerOptions WAL_MARKER = new MarkerOptions();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);



        checkPermission();
        if (isPermissionGranted) {
            if (checkGooglePlayServices()) {

                mapView.getMapAsync((OnMapReadyCallback) ItemTracking.this);
                mapView.onCreate(savedInstanceState);


                Toast.makeText(this, "Google Play Services available", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Google play Services Not available", Toast.LENGTH_SHORT).show();
            }
        }



    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApiAvailability.isUserResolvableError(result)) {

            Dialog dialog = googleApiAvailability.getErrorDialog(this, result, 20001, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Toast.makeText(ItemTracking.this, "User canceled Dialogue", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();

            return false;
        }
        return false;
    }

    private void checkPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener((new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                isPermissionGranted = true;
                Toast.makeText(ItemTracking.this, "Permission granted", Toast.LENGTH_SHORT);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        })).check();
    }


    private void calculateDirections(MarkerOptions marker, GeoPoint curLoc) {
        Log.d(TAG, "calculateDirections: calculating directions.");

        //google maps api key here
        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build();
        }

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                curLoc.getLatitude(),
                curLoc.getLongitude()
        );


        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.alternatives(false);

        directions.origin(
                new com.google.maps.model.LatLng(
                        // TODO: 3/1/2021  need origin from get last known location call to complete directions api
                        marker.getPosition().latitude,
                        marker.getPosition().longitude
                )
        );

        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        Log.d(TAG, "did this run?");
        directions.destination(destination)
                .setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                        Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                        Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                        Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());


                        addPolylinesToMap(result);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

                    }
                });
    }


    private void addPolylinesToMap(final DirectionsResult result ){


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }

                    Polyline polyline = googleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getApplicationContext(), R.color.teal_200));
                    polyline.setClickable(true);

                }
            }
        });
    }









    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //   mapView.onSaveInstanceState(outState);



        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);




    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }








}
