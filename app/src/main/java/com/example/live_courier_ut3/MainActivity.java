package com.example.live_courier_ut3;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firebase.ui.auth.AuthUI;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    // Strings used to capture text input for quote section of app
    public static final String Quote_Key = "quote";
    public static final String Author_Key = "author";

    //textview for the quote input
    TextView mQuoteTextView;

    //log data for checking if google play services is updated
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    //firestore firebase data and document references to access information
    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("sampleData/inspiration");
    private DocumentReference rDocRef = FirebaseFirestore.getInstance().document("sampleData/with_more_inspiration");

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    //main button name "click me" to access information form the server
    Button btn_button;

    //boolean for checking if google play services is enabled
    boolean isPermissionGranted;

    //map object used for maps
    MapView mapView;

    GoogleMap googleMap;
    //location object based in google maps sdk
    private FusedLocationProviderClient mFusedLocationClient;

    private GeoApiContext mGeoApiContext;

    public MarkerOptions userPosition = new MarkerOptions();

    public GeoPoint geoStart;

    GeoPoint curLoc;



    //creating image buttons for the transfer to each new store page
    //public ImageButton target = new ImageButton(this);
    public ImageButton target;
    public static final MarkerOptions WAL_MARKER = new MarkerOptions();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mQuoteTextView = (TextView) findViewById(R.id.quote_display);
        btn_button = findViewById(R.id.btn_button);
        EditText tv_quote = findViewById(R.id.et_quote);
        EditText tv_author = findViewById(R.id.et_author);


        //if user hasn't logged in before create an intent which transfers you to the LoginRegisterActivity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startLoginActivity();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mapView = findViewById(R.id.mapView);

        checkPermission();
        if (isPermissionGranted) {
            if (checkGooglePlayServices()) {

                mapView.getMapAsync(this);
                mapView.onCreate(savedInstanceState);


                Toast.makeText(this, "Google Play Services available", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Google play Services Not available", Toast.LENGTH_SHORT).show();
            }
        }

        target = findViewById(R.id.target);

        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toTarget = new Intent(v.getContext(), Target.class);
                startActivity(toTarget);

            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        getLastKnownLocation();


        DocumentReference directionsRef;



   //     Log.d(TAG, "Null object? : " + test.getLatitude() + " " + test.getLongitude());
        //walmart starting point 34.149409, -84.249323
        LatLng start = new LatLng(34.149409, -84.249323);
        MarkerOptions directionsMarker = new MarkerOptions();
        directionsMarker.position(start);


        directionsRef = FirebaseFirestore.getInstance().document("sampleData/" + user.getDisplayName().toString());


        //calling directions request from within grab of location data
        directionsRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                curLoc = value.getGeoPoint("location") ;
                Log.d(TAG, "checking to see if location grabbed from firestore " + curLoc.getLongitude());
                calculateDirections(directionsMarker, curLoc);
            }
        });





    }


    public GeoPoint getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");
// marker spoof location method
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
//            return;
//        }
//        LocationServices.getFusedLocationProviderClient(this).setMockMode(true);
//        LocationServices.getFusedLocationProviderClient(this).setMockLocation(mockLocation);

        // geoPoint for this return statement under
        GeoPoint placateGeo = new GeoPoint(23.232323,23.232323);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return placateGeo;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<android.location.Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());

                    //Manual insertion commented out. Used for testing
                    //LatLng latLng = new LatLng(34.140980, -84.357679);
                   //  userPosition = new MarkerOptions();

                    //this latLng should be from current user realtime position
                    LatLng latLng = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());

                    userPosition.position(latLng);
                    geoStart = new GeoPoint(latLng.latitude,latLng.longitude);
                    Log.d("geoStart", geoStart.toString());
                  //  geoStart = new GeoPoint(userPosition.getPosition().latitude,userPosition.getPosition().longitude);
                    Log.d(TAG, "Complete: latitude: current realtime position " + geoStart.getLatitude());
                    Log.d(TAG, "Complete: longitude: current realtime position " + geoStart.getLongitude());


                    // TODO: 2/26/2021 change this into a method and refactor
                //------------------------------------------------------------------------------>
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    DocumentReference locations = FirebaseFirestore.getInstance().document("sampleData/" + user.getDisplayName().toString());

                    Map<String, Object> geoLoc = new HashMap<String, Object>();

                    geoLoc.put("location", geoStart );
                    locations.update(geoLoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("InspiringQuote", "Document has been saved!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("InspiringQuote", "Document was not saved!", e);
                        }
                    });

                    //inserting current user location and running from within getLastKnownLocation
                    locations.set(geoLoc);
                 //---------------------------------------------------------------------------->

                }
            }
        });

        return geoStart;
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
                    Toast.makeText(MainActivity.this, "User canceled Dialogue", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT);
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


    //checking google play services ok
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everthing is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;

        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    ;


    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
        this.finish();
    }

    //target activity
    public void toTargetActivity() {
        Intent intent = new Intent(this, Target.class);
        startActivity(intent);
        this.finish();
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
        }


        return super.onOptionsItemSelected(item);
    }


    public void fetchQuote(View view) {
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String quoteText = documentSnapshot.getString(Quote_Key);
                    String authorText = documentSnapshot.getString(Author_Key);
                    mQuoteTextView.setText("\"" + quoteText + "\" -- " + authorText);
                }
            }
        });

    }


    public void saveQuote(View view) {
        EditText quoteView = findViewById(R.id.et_quote);
        EditText authorView = findViewById(R.id.et_author);
        String quoteText = quoteView.getText().toString();
        String authorText = authorView.getText().toString();


        if (quoteText.isEmpty() || authorText.isEmpty()) {
            return;
        }
        Map<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put(Quote_Key, quoteText);
        dataToSave.put(Author_Key, authorText);
        Map<String, Object> dataToAdd = new HashMap<String, Object>();
        dataToAdd.put(Quote_Key, quoteText);
        dataToAdd.put(Author_Key, authorText);

        mDocRef.update(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("InspiringQuote", "Document has been saved!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("InspiringQuote", "Document was not saved!", e);
            }
        });


        mDocRef.set(dataToSave);

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
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap = googleMap;
        //this is my house
        LatLng latLng = new LatLng(34.140980, -84.357679);
        LatLng walmart = new LatLng(34.149409, -84.249323);
        WAL_MARKER.title("Walmart, Milton");


        //markerOptions here is referring to my home
        // TODO: 2/26/2021 change this to my home so that I don't get confused.
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("My home");
        //LatLng latLng;
        WAL_MARKER.position(walmart);
        markerOptions.position(latLng);
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
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }





}