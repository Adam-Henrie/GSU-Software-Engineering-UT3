package com.example.live_courier_ut3;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;



import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    //declaring and setting map variable before on create inside the onMapReady lifecycle method
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        //this is my house

        //markerOptions here is referring to my home
        LatLng latLng = new LatLng(34.140980, -84.357679);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("My home");
        markerOptions.position(latLng);


        LatLng walmart = new LatLng(34.149409, -84.249323);
        WAL_MARKER.title("Walmart, Milton");
        WAL_MARKER.position(walmart);


        // TODO: 2/26/2021 change this to my home so that I don't get confused.

        googleMap.addMarker(markerOptions);
        googleMap.addMarker(WAL_MARKER);
        //beginning map zoom level for this latlng you pass to it
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





    // Strings used to capture text input for quote section of app
    public static final String Quote_Key = "quote";
    public static final String Author_Key = "author";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey" ;

    //textview for the quote input
    TextView mQuoteTextView;

    //log data for checking if google play services is updated
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    //firestore firebase data and document references to access information
    private final DocumentReference mDocRef = FirebaseFirestore.getInstance().document("sampleData/inspiration");
    private final DocumentReference rDocRef = FirebaseFirestore.getInstance().document("sampleData/with_more_inspiration");

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    //main button name "click me" to access information form the server
    Button btn_button;

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

    GeoPoint curLoc;

    FirebaseUser user;
    FirebaseUser useer;

    //creating image buttons for the transfer to each new store page
    //public ImageButton target = new ImageButton(this);
    public ImageButton target;
    public ImageButton walmart;
    public ImageButton taco_bell;

    public static final MarkerOptions WAL_MARKER = new MarkerOptions();



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try{
            FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        } catch  (NullPointerException e){
            Log.d(TAG, " user was null");

            startLoginActivity();
        }


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



       // mQuoteTextView = findViewById(R.id.quote_display);
       // btn_button = findViewById(R.id.btn_button);
      //  EditText tv_quote = findViewById(R.id.et_quote);
       // EditText tv_author = findViewById(R.id.et_author);


        initGoogleMap(savedInstanceState);

        //if user hasn't logged in before create an intent which transfers you to the LoginRegisterActivity
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
           Log.d(TAG, "what?");
        } else {
            startLoginActivity();
          //  this.finish();
        }


        target = findViewById(R.id.target);

        walmart = findViewById(R.id.walmart);

        taco_bell = findViewById(R.id.taco_bell);


        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String storeLookup = "store";
                //Target.class refers to the class that handles the scroll view of the items in ANY STORE.
                Intent toTarget = new Intent(v.getContext(), StoreItems.class);
                toTarget.putExtra(storeLookup, "Target");
                startActivity(toTarget);

            }
        });
        walmart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String storeLookup = "store";
                Intent toWalmart = new Intent(v.getContext(), StoreItems.class);
                getIntent().putExtra(storeLookup,"Walmart");

                startActivity(toWalmart);
            }
        });
        taco_bell.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String storeLookup = "store";
                Intent toTaco_bell = new Intent(v.getContext(), StoreItems.class);
                toTaco_bell.putExtra(storeLookup, "Taco Bell");
               // getIntent().putExtra(storeLookup, "Taco Bell");
                startActivity(toTaco_bell);
            }
        });


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


//        if(FirebaseAuth.getInstance().getCurrentUser() == null){
//            Log.d(TAG, "went to firebase login");
//            Intent intent = new Intent(this,LoginRegisterActivity.class );
//            startActivity(intent);
//            this.finish();
//        }




        try{

           // if(user.getDisplayName() != null) {
                getLastKnownLocation();
                //     Log.d(TAG, "Null object? : " + test.getLatitude() + " " + test.getLongitude());
                //walmart starting point 34.149409, -84.249323
                LatLng start = new LatLng(34.149409, -84.249323);
                MarkerOptions directionsMarker = new MarkerOptions();
                directionsMarker.position(start);
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
          //  }

        }catch (NullPointerException e){
            Log.d(TAG, "flow of onCreate continued past initial call of loginRegisterActivity");
        }


    }   //end onCreate

    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build();
        }
    } //end initGoogleMap


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GeoPoint getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");


        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Log.d(TAG, "went to firebase login");
            Intent intent = new Intent(this,LoginRegisterActivity.class );
            startActivity(intent);
            this.finish();
        }


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
        //This should never run. If it does, something went wrong. ---------------------------------------------------------------------->
        GeoPoint placateGeo = new GeoPoint(23.232323,23.232323);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return placateGeo;
        }
       // ------------------------------------------------------------------------------------------------------------------------------------------->

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<android.location.Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());

                    //Manual insertion commented out. Used for testing
                    //Adam's House you should swat him
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


    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
        this.finish();
    }

    //target activity
    public void toTargetActivity() {
        Intent intent = new Intent(this, StoreItems.class);
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

            case R.id.cart:

             Toast.makeText(this, "Cart",Toast.LENGTH_SHORT).show();
                      //  String storeLookup = "store";
                        Intent toCart = new Intent(this, CartSelection.class);
                       // toCart.putExtra(storeLookup, "Target");
                        startActivity(toCart);


                return true;


            case R.id.driver_details:
                Toast.makeText(this, "Driver Details",Toast.LENGTH_SHORT).show();
                Intent toDriverDetails = new Intent(this, DriverDetails.class);
                startActivity(toDriverDetails);



            default:
                return super.onOptionsItemSelected(item);


               //Intent to go to AddFunds page goes here as another case in the switch statement



        }


    }





//    public void fetchQuote(View view) {
//        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    String quoteText = documentSnapshot.getString(Quote_Key);
//                    String authorText = documentSnapshot.getString(Author_Key);
//                    mQuoteTextView.setText("\"" + quoteText + "\" -- " + authorText);
//                }
//            }
//        });
//
//    }


//    public void saveQuote(View view) {
//        EditText quoteView = findViewById(R.id.et_quote);
//        EditText authorView = findViewById(R.id.et_author);
//        String quoteText = quoteView.getText().toString();
//        String authorText = authorView.getText().toString();
//
//
//        if (quoteText.isEmpty() || authorText.isEmpty()) {
//            return;
//        }
//        Map<String, Object> dataToSave = new HashMap<String, Object>();
//        dataToSave.put(Quote_Key, quoteText);
//        dataToSave.put(Author_Key, authorText);
//        Map<String, Object> dataToAdd = new HashMap<String, Object>();
//        dataToAdd.put(Quote_Key, quoteText);
//        dataToAdd.put(Author_Key, authorText);
//
//        mDocRef.update(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d("InspiringQuote", "Document has been saved!");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.w("InspiringQuote", "Document was not saved!", e);
//            }
//        });
//
//
//        mDocRef.set(dataToSave);
//
//    }


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