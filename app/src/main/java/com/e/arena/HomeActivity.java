package com.e.arena;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.e.arena.Adapter.GymAdapter;
import com.e.arena.Model.Banner;
import com.e.arena.Model.GymModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class

HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    GymAdapter adapter;
    LinearLayout layout;
    List<GymModel> gymList;
    List<Banner> bannerList;
    FirebaseFirestore db;
    static String TAG2 = "Location";
    public String TAG = "HomeActivity";
    AppLocationService appLocationService;
    FusedLocationProviderClient mFusedLocationClient;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private String UID = "uid";
    private CircleImageView mProfileImage;
    Toolbar toolbar;
    private FirebaseAuth mAuth;
    private long backPressedTime;
    private int PERMISSION_ID = 101;
    public static double user_lat;
    public static double user_long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        toolbar = (Toolbar) findViewById( R.id.tool_bar_home );
        setSupportActionBar( toolbar );

        mAuth = FirebaseAuth.getInstance();
        appLocationService = new AppLocationService(
                HomeActivity.this );
        db = FirebaseFirestore.getInstance();
        findViewById();

        //setData();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient( this );

        getLastLocation();



    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        if (isLocationEnabled()) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(
                    new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                user_lat = location.getLatitude();
                                user_long = location.getLongitude();
                                updateLatLangToDatabase( user_lat, user_long );
                                getAddress( HomeActivity.this, location.getLatitude(), location.getLongitude() );

                            }
                        }
                    }
            );
        } else {
            Toast.makeText( this, "Turn on location", Toast.LENGTH_LONG ).show();
            Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
            startActivity( intent );
        }
    }

    private void updateLatLangToDatabase(double user_lat, double user_long) {
        if (mAuth.getCurrentUser() != null) {
            Map<String, Object> map = new HashMap<>();
            map.put( "lat", user_lat );
            map.put( "long", user_long );
            db.collection( "users" )
                    .document( mAuth.getCurrentUser().getUid() )
                    .update( map );
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        mLocationRequest.setInterval( 0 );
        mLocationRequest.setFastestInterval( 0 );
        mLocationRequest.setNumUpdates( 1 );

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient( this );
        ((FusedLocationProviderClient) mFusedLocationClient).requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            user_lat = mLastLocation.getLatitude();
            user_long = mLastLocation.getLongitude();
            updateLatLangToDatabase( user_lat, user_long );
            getAddress( HomeActivity.this, mLastLocation.getLatitude(), mLastLocation.getLongitude() );


        }
    };


    public void getAddress(Context context, double LATITUDE, double LONGITUDE) {

        //Set Address
        try {
            Geocoder geocoder = new Geocoder( context, Locale.getDefault() );
            List<Address> addresses = geocoder.getFromLocation( LATITUDE, LONGITUDE, 1 );
            if (addresses != null && addresses.size() > 0) {

                String address = addresses.get( 0 ).getAddressLine( 0 ); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get( 0 ).getLocality();
                String state = addresses.get( 0 ).getAdminArea();
                String country = addresses.get( 0 ).getCountryName();
                String area = addresses.get( 0 ).getAdminArea();
                String sub_area = addresses.get( 0 ).getSubAdminArea();
                String locality = addresses.get( 0 ).getLocality();
                String sub_locality = addresses.get( 0 ).getSubLocality();
                String postalCode = addresses.get( 0 ).getPostalCode();
                String admin_area = addresses.get( 0 ).getAddressLine( 1 );
                String sub_admin_area = addresses.get( 0 ).getSubAdminArea();
                String knownName = addresses.get( 0 ).getFeatureName(); // Only if available else return NULL

                setGymView( sub_locality );

                if (mAuth.getCurrentUser() != null)
                    setProfileInformation( mAuth.getCurrentUser() );

                Log.d( TAG2, "getAddress:  address " + address );
                Log.d( TAG2, "getAddress:  area " + area );
                Log.d( TAG2, "getAddress:  sub_area " + sub_area );
                Log.d( TAG2, "getAddress:  locality " + locality );
                Log.d( TAG2, "getAddress:  sub_locality " + sub_locality );
                Log.d( TAG2, "getAddress:  city " + city );
                Log.d( TAG2, "getAddress:  state " + state );
                Log.d( TAG2, "getAddress:  postalCode " + postalCode );
                Log.d( TAG2, "getAddress:  address 1 " + admin_area );
                Log.d( TAG2, "getAddress:  sub_admin_area " + sub_admin_area );
                Log.d( TAG2, "getAddress:  knownName " + knownName );

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    private void setProfileInformation(FirebaseUser currentUser) {
        db.collection( "users" )
                .document( mAuth.getCurrentUser().getUid() )
                .get()
                .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            //Toast.makeText( getApplicationContext(), "user not exist", Toast.LENGTH_SHORT ).show();
                            addNewUser( mAuth.getCurrentUser().getUid() );
                        } else {
                            setUserDetail( mAuth.getCurrentUser().getUid() );
                        }

                    }
                } );

    }

    private void addNewUser(final String uid) {
        Map<String, Object> map = new HashMap<>();
        map.put( "uid", uid );
        map.put( "phone", mAuth.getCurrentUser().getPhoneNumber() );
        map.put( "image", "" );
        map.put( "balance", 0 );
        map.put( "isActive", true );

        db.collection( "users" )
                .document( uid )
                .set( map )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setUserDetail( uid );
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( getApplicationContext(), "can get user detail", Toast.LENGTH_SHORT ).show();
            }
        } );

    }

    private void setUserDetail(String uid) {

        db.collection( "users" )
                .document( uid )
                .get()
                .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful())
                            return;
                        DocumentSnapshot snapshot = task.getResult();
                        String image_url = (String) snapshot.get( "image" );
                        if (!image_url.equals( "" ) && image_url != null)
                            Picasso.with( getApplicationContext() ).load( image_url ).into( mProfileImage );

                    }
                } );
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLastLocation();
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        return locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void setData() {


        for (int a = 0; a < 20; a++) {
            Map<String, Object> map = new HashMap<>();
            map.put( "isActive", true );
            map.put( "image", "" );
            map.put( "title", "" );
            map.put( "rating", new Random().nextInt( 6 ) );
            map.put( "price", new Random().nextInt( 500 ) );
            map.put( "location", "" );
            map.put( "state", "" );
            map.put( "city", "" );
            map.put( "locality", "" );
            map.put( "sub_locality", "" );
            map.put( "pin_code", new Random().nextInt( 6 ) );
            map.put( "address", "" );
            map.put( "lat", new Random().nextInt( 100 ) );
            map.put( "long", new Random().nextInt( 100 ) );
            db.collection( "GYM LIST" )
                    .add( map )
                    .addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String gymId = documentReference.getId();
                            Map<String, Object> map = new HashMap<>();
                            map.put( "gymId", gymId );
                            db.collection( "GYM LIST" )
                                    .document( gymId )
                                    .update( map );
                        }
                    } );
        }

       /* for (int a = 0; a < 10; a++) {
            db.collection( "Banner" )
                    .add( map )
                    .addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String gymId = documentReference.getId();
                            Map<String, Object> map = new HashMap<>();
                            map.put( "bannerId", gymId );
                            db.collection( "Banner" )
                                    .document( gymId )
                                    .update( map );
                        }
                    } );
        }*/

    }

    private void setGymView(String sub_locality) {
        bannerList = new ArrayList<>();
        gymList = new ArrayList<>();
        adapter = new GymAdapter( gymList, bannerList, this );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setAdapter( adapter );
        loadData( adapter, sub_locality );
    }

    private void loadData(final GymAdapter adapter, String sub_locality) {

        //get Gym List
        db.collection( "GYM LIST" )
                .whereEqualTo( "isActive", true )
                //
                .whereEqualTo( "locality", sub_locality )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w( TAG, "GYM Listen failed.", e );
                            Toast.makeText( HomeActivity.this, "something went wrong", Toast.LENGTH_SHORT ).show();
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            gymList.clear();
                            for (int a = 0; a < queryDocumentSnapshots.size(); a++) {
                                try {
                                    String id = (String) queryDocumentSnapshots.getDocuments().get( a ).getId();
                                    String image_url = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "image" );
                                    String title = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "title" );
                                    long price = (Long) queryDocumentSnapshots.getDocuments().get( a ).get( "per_visit" );
                                    long rating = (Long) queryDocumentSnapshots.getDocuments().get( a ).get( "rating" );
                                    String location = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "locality" );
                                    String lat = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "lat" );
                                    String lan = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "long" );
                                    Log.d( TAG, " data: " + queryDocumentSnapshots.getDocuments() );
                                    gymList.add( new GymModel( title, image_url, id, location, rating, price, lat, lan ) );
                                } catch (Exception e1) {
                                    Log.d( TAG, e1.getLocalizedMessage() );
                                }
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d( TAG, "data: null" );
                        }
                    }
                } );


        //get Banner List
        db.collection( "Banner" )
                .whereEqualTo( "isActive", true )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w( TAG, "GYM Listen failed.", e );
                            Toast.makeText( HomeActivity.this, "something went wrong", Toast.LENGTH_SHORT ).show();
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            bannerList.clear();
                            for (int a = 0; a < queryDocumentSnapshots.size(); a++) {
                                try {
                                    String id = (String) queryDocumentSnapshots.getDocuments().get( a ).getId();
                                    String image_url = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "image" );
                                    String title = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "title" );
                                    Log.d( TAG, " data: " + queryDocumentSnapshots.getDocuments() );
                                    bannerList.add( new Banner( id, image_url, title ) );
                                } catch (Exception e1) {
                                    Log.d( TAG, e1.getLocalizedMessage() );
                                }
                            }
                            adapter.notifyDataSetChanged();
                            setLoadingLayout( false );

                        } else {
                            Log.d( TAG, "data: null" );
                            Toast.makeText( HomeActivity.this, "something went wrong", Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );


    }

    private void setLoadingLayout(boolean status) {
        layout.setVisibility( status ? View.VISIBLE : View.GONE );
        recyclerView.setVisibility( status ? View.GONE : View.VISIBLE );
    }

    private void findViewById() {
        mProfileImage = (CircleImageView) toolbar.findViewById( R.id.tool_profile_image );
        recyclerView = (RecyclerView) findViewById( R.id.recycler_view );
        layout = (LinearLayout) findViewById( R.id.loading_layout );
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu_home, menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Toast.makeText( getApplicationContext(), "setting Selected", Toast.LENGTH_LONG ).show();
                return true;
            case R.id.action_scan_code:
                checkCameraPermission();
                return true;
            case R.id.action_buy_credits:
                startActivity( new Intent( HomeActivity.this, BuyCredits.class ) );
                return true;

            case R.id.action_my_transaction:
                startActivity( new Intent( HomeActivity.this, MyTransaction.class ) );
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    private void checkCameraPermission() {
        if (checkPermissions()) {
            Intent i = new Intent( HomeActivity.this, QrCodeActivity.class );
            startActivityForResult( i, REQUEST_CODE_QR_SCAN );
        } else {
            requestPermissions();
        }


    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission( this, Manifest.permission.VIBRATE ) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.VIBRATE},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent( HomeActivity.this, QrCodeActivity.class );
                startActivityForResult( i, REQUEST_CODE_QR_SCAN );
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @android.support.annotation.Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (resultCode != Activity.RESULT_OK) {
            Log.d( TAG, "COULD NOT GET A GOOD RESULT." );
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra( "com.blikoon.qrcodescanner.got_qr_scan_relult" );
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder( HomeActivity.this ).create();
                alertDialog.setTitle( "Scan Error" );
                alertDialog.setMessage( "QR Code could not be scanned" );
                alertDialog.setButton( AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        } );
                alertDialog.show();
            }
            return;

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null) {
                Toast.makeText( appLocationService, "null data", Toast.LENGTH_SHORT ).show();
                return;
            }
            //Getting the passed result.....
            String result = data.getStringExtra( "com.blikoon.qrcodescanner.got_qr_scan_relult" );
            startActivity( new Intent( HomeActivity.this, CodeScannerActivity.class ).putExtra( UID, result ) );
        }
    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {

            finish();

        } else {
            Toast.makeText( this, "PressBack again to exit", Toast.LENGTH_LONG ).show();
        }
        backPressedTime = System.currentTimeMillis();
    }


}
