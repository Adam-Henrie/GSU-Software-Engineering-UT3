package com.example.live_courier_ut3;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ItemTracking extends AppCompatActivity implements OnMapReadyCallback {


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


        LatLng latTarget = new LatLng(34.098322, -84.269331);
        MarkerOptions markerTarget = new MarkerOptions();
        markerTarget.title("Target, Milton");
        markerTarget.position(latTarget);


        LatLng latTaco = new LatLng(34.072233, -84.295421);
        MarkerOptions markerTaco = new MarkerOptions();
        markerTaco.title("Taco Bell, Alpharetta");
        markerTaco.position(latTaco);


        // TODO: 2/26/2021 change this to my home so that I don't get confused.


        //markerOptions here is referring to my home
        googleMap.addMarker(markerOptions);
        googleMap.addMarker(WAL_MARKER);
        googleMap.addMarker(markerTarget);
        googleMap.addMarker(markerTaco);

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

    ProgressBar progressBar;
    int maxProgressBarTime;

    FirebaseUser user;
    GeoPoint curLoc;
    GeoPoint startingPoint;
    String store;
    MarkerOptions directionsMarker = new MarkerOptions();
    //count for the timer for the progress bar
    int count = 0;

    //timer variable for the progress bar. Making it global so that I can close the timer on back button pressed
    Timer t = new Timer();

    public TextView tv_count_down;

    int countdown;

    //variables



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_tracking);
        Intent intent = getIntent();
        store = intent.getStringExtra("store");
        Log.d("name of store passed", store);
        mapView = findViewById(R.id.map_route_to_house);



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




        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);




            //CALL GET LAST KNOWN LOCATION ONLY IF YOU ARE NOT USING THE EMULATOR GIVEN GPS POSITION
            getLastKnownLocation();


            //     Log.d(TAG, "Null object? : " + test.getLatitude() + " " + test.getLongitude());
            //walmart starting point 34.149409, -84.249323
           // LatLng start = new LatLng(34.149409, -84.249323);



            DocumentReference mStoreLocationRef = FirebaseFirestore.getInstance().document("stores/" + store);
            mStoreLocationRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                   startingPoint =  documentSnapshot.getGeoPoint("location");
                    LatLng startingLatLng = new LatLng(startingPoint.getLatitude(),startingPoint.getLongitude());

                    directionsMarker.position(startingLatLng);
                }
            });


            user = FirebaseAuth.getInstance().getCurrentUser();

            DocumentReference directionsRef = FirebaseFirestore.getInstance().document("sampleData/" + user.getDisplayName().toString());

            //calling directions request from within grab of location data


            directionsRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        curLoc = documentSnapshot.getGeoPoint("location") ;
                        Log.d(TAG, "checking to see if location grabbed from firestore " + curLoc.getLongitude());
                        calculateDirections(directionsMarker, curLoc);
                    }
                }
            });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        //THIS LINE FOR OFFICIAL TIME IT WILL TAKE TO GET TO PLACE
       // progressBar.setMax(maxProgressBarTime);
        //THIS LINE FOR DEMO
        progressBar.setMax(30);
        Log.d("Checking max bar time ", Integer.toString(progressBar.getMax()));
        int progress = 0;
        progressBar.setProgress(progress);

        tv_count_down = findViewById(R.id.number_time_left);

        countdown = progressBar.getMax();

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            //this allows the tv_count_down.setText to run on the ui thread since it is technically running inside another non-ui Thread i.e. the timer is a different thread.
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_count_down.setText(Integer.toString(countdown));
                    }
                });


                countdown = countdown - 1;
                count = count + 1;
                progressBar.setProgress(count);
             //   setProgressValue(progress);
                Log.d("Time left: ",  Integer.toString(progressBar.getProgress()));

                // Do stuff
                if (count >= progressBar.getMax()) {
                    t.cancel();
                }
            }
        }, 0, 1000);



//        // This callback will only be called when MyFragment is at least Started.
//        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
//            @Override
//            public void handleOnBackPressed() {
//                // Handle the back button event
//                t.cancel();
//            }
//        };






    }
    //end onCreate------------------------------------------------->
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Back Button is being Pressed!", Toast.LENGTH_SHORT).show();
        t.cancel();
        super.onBackPressed();
    }




    private void setProgressValue(final int progress) {
        // set the progress
        progressBar.setProgress(progress + 1);
        // thread is used to change the progress value
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//        thread.start();
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
                        maxProgressBarTime = (int) result.routes[0].legs[0].duration.inSeconds;
                        Log.d(TAG, "maxProgressBarTime value " + maxProgressBarTime);

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



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GeoPoint getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");


        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Log.d(TAG, "went to firebase login");
            Intent intent = new Intent(this,LoginRegisterActivity.class );
            startActivity(intent);
            this.finish();
        }


// marker spoof location method------------------------------------------------------------------------------------------>
//        LatLng startPos = new LatLng(34.140980,-84.357679);
//        Location mockLocation = new Location(LocationManager.GPS_PROVIDER); // a string
//
//        mockLocation.setLatitude(startPos.latitude);  // double
//        mockLocation.setLongitude(startPos.longitude);
//        mockLocation.setAltitude(100);
//        mockLocation.setTime(System.currentTimeMillis());
//        mockLocation.setAccuracy(1);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
//        }
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            GeoPoint placateReturnGeo = new GeoPoint(23.232323,23.232323);
//            return placateReturnGeo;
//        }
//        LocationServices.getFusedLocationProviderClient(this).setMockMode(true);
//        LocationServices.getFusedLocationProviderClient(this).setMockLocation(mockLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d("Main location thread:", "Setting location through mock location worked?");
//            }
//        });


//end marker geoPoint spoof method------------------------------------------------------------------------------------->



        // geoPoint for this return statement under
        //This should never run. If it does, something went wrong. ---------------------------------------------------------------------->
        GeoPoint placateGeo = new GeoPoint(23.232323,23.232323);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return placateGeo;
        }
        // ------------------------------------------------------------------------------------------------------------------------------------------->


        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());

                    //Manual insertion commented out. Used for testing
                    //Adam's House you should swat him
                    LatLng latLng = new LatLng(34.140980, -84.357679);
                    //  userPosition = new MarkerOptions();

                    //this latLng should be from current user realtime position
                   //  LatLng latLng = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());

                    //   userPosition.position(latLng);
                    geoStart = new GeoPoint(latLng.latitude,latLng.longitude);
                    Log.d("geoStart", geoStart.toString());
                    //  geoStart = new GeoPoint(userPosition.getPosition().latitude,userPosition.getPosition().longitude);
                    Log.d(TAG, "Complete: latitude: current realtime position " + geoStart.getLatitude());
                    Log.d(TAG, "Complete: longitude: current realtime position " + geoStart.getLongitude());


                    // TODO: 2/26/2021 change this into a method and refactor
                    //------------------------------------------------------------------------------>


                    try {
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        Log.d(TAG, "username is " + user.getDisplayName());
                        DocumentReference locations = FirebaseFirestore.getInstance().document("sampleData/" + user.getDisplayName());

                        Map<String, Object> geoLoc = new HashMap<String, Object>();

                        geoLoc.put("location", geoStart );
                        locations.update(geoLoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("User Location", "Location has been saved!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("InspiringQuote", "Document was not saved!", e);
                            }
                        });

                        //inserting current user location and running from within getLastKnownLocation
                        // ".......".set(....) finalizes the setting of the data into firestore
                        locations.set(geoLoc);
                        //---------------------------------------------------------------------------->
                    } catch (NullPointerException e){
                        Log.d(TAG, "user location code skipped");
                    }


                }
            }
        });

        //no functions use this return value it is just here to placate the getLastKnownLocation function
        return geoStart;
    }




}
