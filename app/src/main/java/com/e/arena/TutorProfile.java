package com.e.arena;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.e.arena.Model.DemoClassrequestModel;
import com.example.squircleview.SquircleView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Currency;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TutorProfile extends AppCompatActivity {
    String TUTOR_ID, TUTOR_NAME;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseUser user;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    NumberFormat format;
    Button mRequestForDemoClassBtn;
    boolean isRequested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tutor_profile );


        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById( R.id.toolbar_layout );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );


        mRequestForDemoClassBtn = (Button) findViewById( R.id.requestbtn );
        format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits( 0 );
        format.setCurrency( Currency.getInstance( "inr" ) );

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (getIntent().hasExtra( "id" )) {
            TUTOR_ID = getIntent().getStringExtra( "id" );
            setToolbar( toolbar );


            mCollapsingToolbarLayout.setExpandedTitleTextAppearance( R.style.ExpandedAppBar );
            mCollapsingToolbarLayout.setCollapsedTitleTextAppearance( R.style.CollapsedAppBar );

            setTutorDetail( TUTOR_ID );

            checkForRequestDemoClass( TUTOR_ID );
        } else {
            finish();
        }

        mRequestForDemoClassBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (auth.getCurrentUser() != null) {
                    mRequestForDemoClassBtn.setText( "Requesting" );
                    mRequestForDemoClassBtn.setEnabled( false );
                    mRequestForDemoClassBtn.setBackgroundResource( R.drawable.disable_btn );
                    updateDemoClassRequest( TUTOR_ID, auth.getCurrentUser().getUid() );
                } else {
                    Snackbar.make( view, "No user found", Snackbar.LENGTH_SHORT ).show();
                }
            }
        } );

    }

    private void updateDemoClassRequest(String tutorId, String userId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( "https://us-central1-phonenumber-4a8b0.cloudfunctions.net/" )
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        RetrofitService uploadInterFace = retrofit.create( RetrofitService.class );
        Call<DemoClassrequestModel> call = uploadInterFace.setRequestForDemoClass( userId, tutorId );
        call.enqueue( new retrofit2.Callback<DemoClassrequestModel>() {
            @Override
            public void onResponse(Call<DemoClassrequestModel> call, Response<DemoClassrequestModel> response) {
                if (!response.isSuccessful()){
                    Toast.makeText( TutorProfile.this, "could't request, try again", Toast.LENGTH_SHORT ).show();
                    mRequestForDemoClassBtn.setText( "Request For Demo Class" );
                    mRequestForDemoClassBtn.setEnabled( true );
                    mRequestForDemoClassBtn.setBackgroundResource( R.drawable.next_btn );
                    return;
                }
                DemoClassrequestModel requestStatus=response.body();

                if (requestStatus!=null){
                    if (requestStatus.isStatus()){
                        mRequestForDemoClassBtn.setEnabled( false );
                        mRequestForDemoClassBtn.setText( "Requested For Demo Class" );
                        mRequestForDemoClassBtn.setBackgroundResource( R.drawable.disable_btn );
                    }
                }

            }

            @Override
            public void onFailure(Call<DemoClassrequestModel> call, Throwable t) {
                Toast.makeText( TutorProfile.this, "could't request, try again", Toast.LENGTH_SHORT ).show();
                mRequestForDemoClassBtn.setText( "Request For Demo Class" );
                mRequestForDemoClassBtn.setEnabled( true );
                mRequestForDemoClassBtn.setBackgroundResource( R.drawable.next_btn );
            }
        } );
    }

    private void checkForRequestDemoClass(String tutor_id) {
        firestore.collection( "HiredForDemoClass" )
                .whereEqualTo( "tutorId", tutor_id )
                .whereEqualTo( "userId", auth.getCurrentUser().getUid() )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e == null) {
                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                isRequested = true;
                                mRequestForDemoClassBtn.setEnabled( false );
                                mRequestForDemoClassBtn.setText( "Requested For Demo Class" );
                                mRequestForDemoClassBtn.setBackgroundResource( R.drawable.disable_btn );
                            } else {
                                isRequested = false;
                                mRequestForDemoClassBtn.setEnabled( true );
                                mRequestForDemoClassBtn.setText( "Request For Demo Class" );
                                mRequestForDemoClassBtn.setBackgroundResource( R.drawable.next_btn );
                            }
                        }
                    }
                } );

    }

    private void setTutorDetail(final String tutor_id) {
        final TextView name = (TextView) findViewById( R.id.tvName );
        final TextView monthly = (TextView) findViewById( R.id.tv1 );
        final TextView perVisit = (TextView) findViewById( R.id.tv2 );

        final TextView about = (TextView) findViewById( R.id.tvDescription );
        final SquircleView profileImage = (SquircleView) findViewById( R.id.sqProfile );


        firestore.collection( "MerchantList" )
                .document( tutor_id )
                .get()
                .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                DocumentSnapshot tutor = task.getResult();
                                try {
                                    TUTOR_NAME = tutor.getString( "name" );
                                    name.setText( tutor.getString( "name" ) );
                                    monthly.setText( format.format( tutor.getLong( "monthlyfee" ) ) );
                                    perVisit.setText( format.format( tutor.getLong( "pervisitFee" ) ) );
                                    mCollapsingToolbarLayout.setTitle( name.getText().toString() );
                                    String ImageUrl = tutor.getString( "icon" );
                                    if (ImageUrl != null && !ImageUrl.equals( "" ))
                                        Picasso.with( TutorProfile.this ).load( ImageUrl ).placeholder( R.drawable.profile ).into( profileImage, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError() {
                                                profileImage.setImageResource( R.drawable.profile );
                                            }
                                        } );
                                    about.setText( tutor.getString( "about" ) );
                                } catch (Exception e) {
                                }

                            }
                        }

                    }
                } );
    }

    private void setToolbar(Toolbar toolbar) {
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setDisplayShowHomeEnabled( true );
        //getSupportActionBar().setTitle( id );
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void HireTutor(View view) {

    }

    public void feePlan(View view) {
        if (TUTOR_ID != null && TUTOR_NAME != null) {
            startActivity( new Intent( this, FeePlanActivity.class ).putExtra( "id", TUTOR_ID )
                    .putExtra( "name", TUTOR_NAME ) );
        } else {
            Snackbar.make( view, "Please wait", Snackbar.LENGTH_SHORT ).show();
        }
    }


}
