package com.e.arena;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.e.arena.Model.TransactionResponse;
import com.e.arena.Utility.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CodeScannerActivity extends AppCompatActivity {
    private String GYM_ID;
    FirebaseFirestore db;
    private RelativeLayout mLoadingLay, mContentLay, progressBar;
    private CircleImageView mGymIcon;
    private TextView mGymAddress, mGymName, mGymId, mPayingCredits, mAvailableCredits, mInsufficientTv;
    private ImageView mSufficientBalanceStatusIcon;
    private Button mPayBtn;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private String TAG = "CodeScannerActivity";
    long PAYING_CREDITS;
    private long backPressedTime;
    boolean STATUS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_code_scanner );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        mLoadingLay = (RelativeLayout) findViewById( R.id.loading_layout );
        mContentLay = (RelativeLayout) findViewById( R.id.content_lay );

        findViews();
        if (getIntent().hasExtra( "uid" )) {
            GYM_ID = getIntent().getStringExtra( "uid" );
            setToolbar( toolbar, "Pay" );
            getGymDetail( GYM_ID );
        }

    }

    private void findViews() {
        mGymIcon = (CircleImageView) findViewById( R.id.gym_icon );
        mGymAddress = (TextView) findViewById( R.id.gym_address );
        mGymName = (TextView) findViewById( R.id.Pgym_name );
        mGymId = (TextView) findViewById( R.id.gym_uid );
        mPayingCredits = (TextView) findViewById( R.id.paying_credits );
        mAvailableCredits = (TextView) findViewById( R.id.credit_balance );
        mInsufficientTv = (TextView) findViewById( R.id.insufficient_tv );
        mSufficientBalanceStatusIcon = (ImageView) findViewById( R.id.check );
        mPayBtn = (Button) findViewById( R.id.pay_button );

    }

    public void startTransaction(View view) {
        /*final ProgressBar progressBar=(ProgressBar)findViewById( R.id.progressBar );
        progressBar.setVisibility( View.VISIBLE );*/


        new TTFancyGifDialog.Builder( CodeScannerActivity.this )
                .setTitle( "Pay " + PAYING_CREDITS + " credits" )
                .setMessage( "Press Proceed to continue" )
                .setPositiveBtnText( "Proceed" )
                .setNegativeBtnText( "Cancel" )
                .setPositiveBtnBackground( "#22b573" )
                .setGifResource( R.drawable.confirmation )      //pass your gif, png or jpg
                .isCancellable( true )
                .OnPositiveClicked( new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        STATUS = true;
                        progressBar = (RelativeLayout) findViewById( R.id.tranasaction_lay );
                        progressBar.setVisibility( View.VISIBLE );
                        mContentLay.setVisibility( View.GONE );
                        mPayBtn.setEnabled( false );
                        mPayBtn.setBackgroundResource( R.drawable.disable_btn );
                        mPayBtn.setText( "Please wait..." );
                        /*final long getGymsCredit = PAYING_CREDITS;
                        Map<String, Object> map = new HashMap<>();
                        map.put( "type", new Utils().MONEY_PAID );
                        map.put( "amount", getGymsCredit );
                        map.put( "status", false );
                        map.put( "from", currentUser.getUid() );
                        map.put( "failed_msg", "" );
                        map.put( "to", GYM_ID );
                        map.put( "merchant_name", mGymName.getText().toString() );
                        map.put( "timestamp", System.currentTimeMillis() );

                        db.collection( "Transaction" )
                                .add( map )
                                .addOnCompleteListener( new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            DocumentReference reference = task.getResult();
                                            final String Transaction_id = reference.getId();
                                            //checking Users available credits
                                            db.collection( "users" )
                                                    .document( currentUser.getUid() )
                                                    .get()
                                                    .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            if (!documentSnapshot.exists()) {
                                                                progressBar.setVisibility( View.GONE );
                                                                mPayBtn.setText( new Utils().FAILED_MSG );
                                                                updateFailedMsg( "No user Found", Transaction_id );
                                                                return;
                                                            }
                                                            final long AvailableCredits = (Long) documentSnapshot.get( "balance" );
                                                            // Get gyms Credits
                                                            if (AvailableCredits < getGymsCredit) {
                                                                Toast.makeText( CodeScannerActivity.this, "insufficient Credits", Toast.LENGTH_SHORT ).show();
                                                                progressBar.setVisibility( View.GONE );
                                                                mPayBtn.setText( new Utils().TRANSACTION_INSUFFICIENT );
                                                                updateFailedMsg( new Utils().TRANSACTION_INSUFFICIENT, Transaction_id );
                                                                return;
                                                            }
                                                            db.collection( "GYM LIST" )
                                                                    .document( GYM_ID )
                                                                    .get()
                                                                    .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (!task.isSuccessful()) {
                                                                                Toast.makeText( CodeScannerActivity.this, "could't reach gyms Account", Toast.LENGTH_SHORT ).show();
                                                                                progressBar.setVisibility( View.GONE );
                                                                                mPayBtn.setText( "GYM NOT FOUND" );
                                                                                updateFailedMsg( "GYM NOT FOUND", Transaction_id );
                                                                                return;

                                                                            }

                                                                            DocumentSnapshot snapshot = task.getResult();
                                                                            long gyms_current_credits = (Long) snapshot.get( "balance" );

                                                                            WriteBatch batch = db.batch();


                                                                            long Credits_to_update_to_user = AvailableCredits - getGymsCredit;

                                                                            long Credits_to_update_to_gym = gyms_current_credits + getGymsCredit;

                                                                            // Deduct Credits from Users Account
                                                                            DocumentReference sfRef = db.collection( "users" ).document( currentUser.getUid() );
                                                                            batch.update( sfRef, "balance", Credits_to_update_to_user );


                                                                            // Add credits to Gyms Account
                                                                            DocumentReference laRef = db.collection( "GYM LIST" )
                                                                                    .document( GYM_ID );
                                                                            batch.update( laRef, "balance", Credits_to_update_to_gym );

                                                                            // Commit the batch
                                                                            batch.commit().addOnCompleteListener( new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (!task.isSuccessful()) {
                                                                                        Toast.makeText( CodeScannerActivity.this, "updating balance ", Toast.LENGTH_SHORT ).show();
                                                                                        progressBar.setVisibility( View.GONE );
                                                                                        mPayBtn.setText( new Utils().FAILED_MSG );
                                                                                        if (task.getException() != null)
                                                                                            updateFailedMsg( task.getException().getMessage(), Transaction_id );
                                                                                        else {
                                                                                            updateFailedMsg( "error in batch transaction", Transaction_id );
                                                                                        }
                                                                                        return;
                                                                                    }
                                                                                    Map<String, Object> map = new HashMap<>();
                                                                                    map.put( "status", true );
                                                                                    map.put( "complete_at", System.currentTimeMillis() );

                                                                                    db.collection( "Transaction" )
                                                                                            .document( Transaction_id )
                                                                                            .update( map )
                                                                                            .addOnSuccessListener( new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    Toast.makeText( CodeScannerActivity.this, "transaction Successful", Toast.LENGTH_SHORT ).show();
                                                                                                    progressBar.setVisibility( View.GONE );
                                                                                                    mContentLay.setVisibility( View.VISIBLE );
                                                                                                    mPayBtn.setText( "Transaction Successful" );
                                                                                                    STATUS = false;
                                                                                                    showDialog();

                                                                                                }
                                                                                            } ).addOnFailureListener( new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            updateFailedMsg( "error in updating transaction status\n" + e.getMessage(), Transaction_id );
                                                                                        }
                                                                                    } );

                                                                                }
                                                                            } );

                                                                        }
                                                                    } );


                                                        }
                                                    } ).addOnFailureListener( new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText( CodeScannerActivity.this, "try again", Toast.LENGTH_SHORT ).show();
                                                    updateFailedMsg( "error in\n" + e.getMessage(), Transaction_id );
                                                }
                                            } );

                                        } else {
                                            Toast.makeText( CodeScannerActivity.this, "Transaction Failed", Toast.LENGTH_SHORT ).show();
                                            startActivity( new Intent( CodeScannerActivity.this, MyTransaction.class ) );
                                            finish();
                                        }

                                    }
                                } ).addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText( CodeScannerActivity.this, "can not start Transaction", Toast.LENGTH_SHORT ).show();
                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText( CodeScannerActivity.this, "Transaction Failed", Toast.LENGTH_SHORT ).show();
                                startActivity( new Intent( CodeScannerActivity.this, MyTransaction.class ) );
                                finish();
                            }
                        } );*/

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl( "https://us-central1-phonenumber-4a8b0.cloudfunctions.net/" )
                                .addConverterFactory( GsonConverterFactory.create() )
                                .build();

                        RetrofitService uploadInterFace = retrofit.create( RetrofitService.class );

                        String uID = mAuth.getCurrentUser().getUid();
                        Call<TransactionResponse> call = uploadInterFace.getTransaction( uID, GYM_ID, PAYING_CREDITS );
                        call.enqueue( new retrofit2.Callback<TransactionResponse>() {
                            @Override
                            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                                if (response.code()==200){
                                    TransactionResponse transactionResponse=response.body();
                                    if (transactionResponse!=null){

                                        progressBar.setVisibility( View.GONE );
                                        mContentLay.setVisibility( View.VISIBLE );
                                        STATUS = false;
                                        if (transactionResponse.isStatus()){
                                            Toast.makeText( CodeScannerActivity.this, "transaction Successful", Toast.LENGTH_SHORT ).show();
                                            showDialog();
                                            mPayBtn.setText( "Transaction Successful" );

                                        }
                                        else {Toast.makeText( CodeScannerActivity.this, "transaction failed", Toast.LENGTH_SHORT ).show();
                                            mPayBtn.setText( "Transaction Failed" );
                                            showFailedDialog();

                                        }
                                    }

                                }
                                else {
                                    Toast.makeText( CodeScannerActivity.this, "Transaction Failed", Toast.LENGTH_SHORT ).show();
                                    onBackPressed();
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                                Toast.makeText( CodeScannerActivity.this, "Transaction Failed", Toast.LENGTH_SHORT ).show();
                                onBackPressed();
                                finish();
                            }
                        } );


                    }
                } )
                .OnNegativeClicked( new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                    }
                } )
                .build();


    }

    private void showFailedDialog() {
        new TTFancyGifDialog.Builder( CodeScannerActivity.this )
                .setTitle( "Failed" )
                .setMessage( "You do not have sufficient credits in your account" )
                .setPositiveBtnText( "Buy Credits" )
                .setNegativeBtnText( "Cancel" )
                .setPositiveBtnBackground( "#22b573" )
                //.setGifResource( R.drawable.tranasction )      //pass your gif, png or jpg
                .isCancellable( false )
                .OnPositiveClicked( new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        startActivity( new Intent( CodeScannerActivity.this, BuyCredits.class ) );
                        finish();

                    }
                } )
                .OnNegativeClicked( new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        finish();
                    }
                } )
                .build();
    }

    private void updateFailedMsg(String FAILED_MSG, String transaction_id) {
        Map<String, Object> map = new HashMap<>();
        map.put( "failed_msg", FAILED_MSG );
        db.collection( "Transaction" )
                .document( transaction_id )
                .update( map );
        Toast.makeText( CodeScannerActivity.this, "Transaction Failed", Toast.LENGTH_SHORT ).show();
        startActivity( new Intent( CodeScannerActivity.this, MyTransaction.class ) );
        finish();
    }

    private void showDialog() {
        new TTFancyGifDialog.Builder( CodeScannerActivity.this )
                .setTitle( "Transaction successful" )
                .setMessage( "You have successfully paid " + PAYING_CREDITS + " credits" )
                .setPositiveBtnText( "Ok" )
                .setPositiveBtnBackground( "#22b573" )
                .setGifResource( R.drawable.tranasction )      //pass your gif, png or jpg
                .isCancellable( false )
                .OnPositiveClicked( new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        startActivity( new Intent( CodeScannerActivity.this, MyTransaction.class ) );
                        finish();

                    }
                } )
                .build();
    }

    private void getGymDetail(final String GYM_ID) {
        db.collection( "GYM LIST" )
                .document( GYM_ID )
                .get()
                .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()) {
                            onBackPressed();
                            finish();
                            return;
                        }
                        setLoadingLayout( false );
                        DocumentSnapshot snapshot = task.getResult();

                        mGymName.setText( (String) snapshot.get( "title" ) );

                        String locality = (String) snapshot.get( "locality" );
                        String city = (String) snapshot.get( "city" );
                        if (city != null && !city.equals( "" ))
                            mGymAddress.setText( locality + "," + city );
                        else {
                            mGymAddress.setText( locality );
                        }
                        mGymId.setText( GYM_ID );
                        long PayingCredits = (Long) snapshot.get( "per_visit" );
                        PAYING_CREDITS = PayingCredits;
                        mPayingCredits.setText( PayingCredits + " Credits" );
                        String image_url = (String) snapshot.get( "image" );
                        if (!image_url.equals( "" ) && image_url != null)
                            Picasso.with( CodeScannerActivity.this ).load( image_url ).into( mGymIcon, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    mGymIcon.setImageResource( R.drawable.profile );
                                    Log.d( TAG, "picUploadError: " );
                                }
                            } );

                        getUsersCredits( currentUser, PayingCredits );
                        //long AvailableCredits = (Long) snapshot.get( "balance" );
                        // mAvailableCredits.setText( AvailableCredits + " Credits" );


                    }
                } );

    }

    private void getUsersCredits(FirebaseUser currentUser, final long payingCredits) {
        db.collection( "users" )
                .document( currentUser.getUid() )
                .get()
                .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            return;
                        }
                        long AvailableCredits = (Long) documentSnapshot.get( "balance" );
                        mAvailableCredits.setText( new DecimalFormat( "#,##0.##" ).format( AvailableCredits ) + " Credits" );
                        if (AvailableCredits >= payingCredits) {
                            mSufficientBalanceStatusIcon.setImageResource( R.drawable.check );
                            mPayBtn.setEnabled( true );
                            mPayBtn.setBackgroundResource( R.drawable.next_btn );
                            mPayBtn.setTextColor( getResources().getColor( R.color.white ) );
                            mInsufficientTv.setVisibility( View.GONE );
                            mAvailableCredits.setTextColor( getResources().getColor( R.color.green ) );


                        } else {
                            mSufficientBalanceStatusIcon.setImageResource( R.drawable.error );
                            mPayBtn.setEnabled( false );
                            mPayBtn.setBackgroundResource( R.drawable.disable_btn );
                            mPayBtn.setTextColor( getResources().getColor( R.color.textbgcolor ) );
                            mInsufficientTv.setVisibility( View.VISIBLE );
                            mAvailableCredits.setTextColor( getResources().getColor( R.color.backgroundColor ) );
                        }
                    }
                } );
    }


    public void buyMore(View view) {
        startActivity( new Intent( CodeScannerActivity.this, BuyCredits.class ) );
    }

    private void setLoadingLayout(boolean status) {
        mLoadingLay.setVisibility( status ? View.VISIBLE : View.GONE );
        mContentLay.setVisibility( status ? View.GONE : View.VISIBLE );
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


    @Override
    public void onBackPressed() {
        if (STATUS) {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {

                finish();

            } else {
                Toast.makeText( this, "Please wait, Your  Transaction is in Progress", Toast.LENGTH_LONG ).show();
            }
            backPressedTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
