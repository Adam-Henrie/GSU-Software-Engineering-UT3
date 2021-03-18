package com.example.live_courier_ut3;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.live_courier_ut3.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginRegisterActivity extends AppCompatActivity {



    private static final String TAG = "LoginRegisterActivity";
    int AUTHUI_REQUEST_CODE = 10001;




    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);



        if (FirebaseAuth.getInstance().getCurrentUser()  != null    ){
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
            return;
        }


    }





    public void handleLoginRegister(View view){

        List<AuthUI.IdpConfig> provider = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .setTosAndPrivacyPolicyUrls("https://example.com", "https://example.com")
                .setLogo(R.drawable.common_google_signin_btn_icon_light_normal_background)
                .setAlwaysShowSignInMethodScreen(true)
                .build();

        startActivityForResult(intent, AUTHUI_REQUEST_CODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AUTHUI_REQUEST_CODE){
            if(resultCode == RESULT_OK) {


                // We have signed in the user or we have a new user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                Log.d(TAG, "is the login activity working?" + user.getDisplayName());

                Log.d(TAG, "onActivityResult: " + user.getEmail());

                //creating document reference because of successful login to add data to the firestore database

                DocumentReference userInfo = FirebaseFirestore.getInstance().document("users/" + user.getDisplayName());


                String email = user.getEmail().toString();
                String userName = user.getDisplayName().toString();


                String emailKey = "email";
                String userNameKey = "username";

                Map<String, Object> dataToSave = new HashMap<String, Object>();
                dataToSave.put(emailKey, email);
                dataToSave.put(userNameKey, userName);


                userInfo.update(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
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


                userInfo.set(dataToSave);



                //this pulls the meta data for the user to check to see if they are a new user or not.
                // we can use this to pass the user name info on to the main activity to make a document reference to make a new Firestore
                // database entry.
                if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()){
                    Toast.makeText(this, "Welcome New User" + user.getDisplayName() + " ", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();

            } else {
                //Signing in failed
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null) {
                    Log.d(TAG, "onActivityResult: the user has cancelled the sign in request ");
                }else{
                    Log.e(TAG, "onActivityResult: " , response.getError());
                }
            }
        }

    }
}