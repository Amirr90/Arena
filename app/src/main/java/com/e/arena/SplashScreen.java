package com.e.arena;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private int PERMISSION_ID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        mAuth = FirebaseAuth.getInstance();
        if (checkPermissions()){
            FirebaseUser user=mAuth.getCurrentUser();
            updateUI( user );
        }
        else {
            requestPermissions();
        }

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FirebaseUser user=mAuth.getCurrentUser();
                updateUI( user );
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (checkPermissions()){
            FirebaseUser user=mAuth.getCurrentUser();
            updateUI( user );
        }
        else {
            requestPermissions();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissions()){
            FirebaseUser user=mAuth.getCurrentUser();
            updateUI( user );
        }
        else {
            requestPermissions();
        }
    }


    private void updateUI(FirebaseUser currentUser) {
        if (currentUser!=null){
           showHomeScreen();
        }
        else {
            startLogin();
        }
    }

    private void startLogin() {

        ProgressDialog dialog = new ProgressDialog( this );
        dialog.setTitle( "Loading" );
        dialog.setMessage( "Please wait" );
        dialog.setCancelable( false );
        List<AuthUI.IdpConfig> providers = Arrays.asList(
               // new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build()
               // new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders( providers )
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html" )
                        //.setLogo(R.drawable.logoo)      // Set logo drawable
                        .setTheme( R.style.AppTheme_NoActionBar )
                        .build(),
                10 );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == 10) {
            IdpResponse response = IdpResponse.fromResultIntent( data );

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Toast.makeText( this, "sign in successfully", Toast.LENGTH_SHORT ).show();
                    showHomeScreen();

                }

            } else {
                Toast.makeText( this, "Sign in failed", Toast.LENGTH_SHORT ).show();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                updateUI(currentUser);
            }
        }
    }

    private void showHomeScreen() {
        startActivity( new Intent( this,HomeActivity.class ) );
        finish();
    }
}

