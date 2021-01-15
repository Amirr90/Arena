package com.e.arena;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

public class ShowAllTutor extends AppCompatActivity {
    String SERVICE_ID;
    RecyclerView mAllTutorRecyclerView;
    List<DocumentSnapshot> allTutorList;
    FirebaseFirestore firestore;
    String Locality, Address, City, State;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_show_all_tutor );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );


        firestore = FirebaseFirestore.getInstance();
        if (getIntent().hasExtra( "uid" )) {
            SERVICE_ID = getIntent().getStringExtra( "uid" );
            setToolbar( toolbar, "All Tutors" );

            setAllTutorData();

            getAddress( this, HomeActivity.user_lat, HomeActivity.user_long );

        }

    }

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

                Locality = sub_locality;
                Address = address;
                City = city;
                State = state;


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    private void generateDemoData() {
        Map<String, Object> map = new HashMap<>();
        map.put( "isActive", true );
        map.put( "isVerified", false );
        map.put( "icon", "" );
        map.put( "name", "" );
        map.put( "address", Address );
        map.put( "locality", Locality );
        map.put( "phoneNumber", 1236564789 );
        map.put( "city", City );
        map.put( "state", State );
        map.put( "rating", 123 );
        map.put( "uid", "" );


        firestore.collection( "Tutor List" )
                .document()
                .set( map );
    }

    private void setAllTutorData() {
        mAllTutorRecyclerView = (RecyclerView) findViewById( R.id.all_tutor_rec );
        mAllTutorRecyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        allTutorList = new ArrayList<>();
        AllTutorAdapter allTutorAdapter = new AllTutorAdapter( allTutorList, this );
        mAllTutorRecyclerView.setAdapter( allTutorAdapter );
        loadTutorData( allTutorAdapter );
    }

    private void loadTutorData(final AllTutorAdapter allTutorAdapter) {
        firestore.collection( "MerchantList" )
                .whereEqualTo( "merchantType", "tutor" )
                .whereEqualTo( "isActive", true )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshots = task.getResult();
                            if (task.getResult() != null) {
                                allTutorList.addAll( snapshots.getDocuments() );
                                allTutorAdapter.notifyDataSetChanged();
                            }


                        }
                    }
                } );
    }

    private void setToolbar(Toolbar toolbar, String id) {
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setDisplayShowHomeEnabled( true );
        getSupportActionBar().setTitle( id );
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class AllTutorAdapter extends RecyclerView.Adapter<AllTutorAdapter.MyViewHolder> {
        List<DocumentSnapshot> list;
        Context context;

        public AllTutorAdapter(List<DocumentSnapshot> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public AllTutorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if (list.isEmpty()) {
                View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.loading_layout, viewGroup, false );
                return new MyViewHolder( view );
            } else {
                View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.all_tutor_view, viewGroup, false );
                return new MyViewHolder( view );
            }
        }

        @Override
        public void onBindViewHolder(@NonNull AllTutorAdapter.MyViewHolder myViewHolder, final int i) {

            if (!list.isEmpty()) {

                myViewHolder.mTutorName.setText( list.get( i ).getString( "name" ) );
                String ImageUrl = list.get( i ).getString( "icon" );
                if (ImageUrl != null && !ImageUrl.equals( "" )) {
                    Picasso.with( context ).load( ImageUrl ).into( myViewHolder.mTutorImage );
                }

                myViewHolder.layout.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id = list.get( i ).getId();
                        startActivity( new Intent( ShowAllTutor.this, TutorProfile.class ).putExtra( "id", id ) );
                        //updateData( id );
                    }
                } );


            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView mTutorName;
            private ImageView mTutorImage;
            private RelativeLayout layout;

            public MyViewHolder(@NonNull View itemView) {
                super( itemView );
                mTutorImage = (ImageView) itemView.findViewById( R.id.tutor_image );
                mTutorName = (TextView) itemView.findViewById( R.id.tutor_name );
                layout = (RelativeLayout) itemView.findViewById( R.id.layout_allTutor );
            }
        }
    }

    private void updateData(String id) {
        Map<String, Object> map = new HashMap<>();
        map.put( "feeType", 0 );
        map.put( "fee", 100 );
        firestore.collection( "Tutor List" )
                .document( id )
                .update( map );
    }
}
