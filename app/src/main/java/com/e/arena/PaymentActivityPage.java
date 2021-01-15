package com.e.arena;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.e.arena.Model.TransactionResponse;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PaymentActivityPage extends AppCompatActivity {
    RadioGroup radioGroup;

    private String MONEY_TO_ADD;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_payment_page );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );

        CollapsingToolbarLayout mCollapsingToolbarLayout = findViewById( R.id.toolbar_layout );
        Typeface typeface = ResourcesCompat.getFont( this, R.font.antic );
        mCollapsingToolbarLayout.setCollapsedTitleTypeface( typeface );
        mCollapsingToolbarLayout.setExpandedTitleTypeface( typeface );


        mCollapsingToolbarLayout.setExpandedTitleTextAppearance( R.style.ExpandedAppBar );
        mCollapsingToolbarLayout.setCollapsedTitleTextAppearance( R.style.CollapsedAppBar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setDisplayShowHomeEnabled( true );

        radioGroup = (RadioGroup) findViewById( R.id.radio_grp );
        progressBar = (ProgressBar) findViewById( R.id.progressBar2 );

        if (getIntent().hasExtra( "money" )) {
            MONEY_TO_ADD = getIntent().getStringExtra( "money" );
            mCollapsingToolbarLayout.setTitle( "Pay " + MONEY_TO_ADD );
        } else {
            onBackPressed();
            finish();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void StartTransaction(View view) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton payment_method_button = (RadioButton) findViewById( selectedId );
        if (selectedId == -1) {
            Toast.makeText( PaymentActivityPage.this, "Nothing selected", Toast.LENGTH_SHORT ).show();
        } else {
            Toast.makeText( PaymentActivityPage.this, payment_method_button.getText(), Toast.LENGTH_SHORT ).show();
            String payment_method = payment_method_button.getText().toString();
            doTransaction( payment_method );
        }
    }

    private void doTransaction(String payment_method) {
        progressBar.setVisibility( View.VISIBLE );
        /*final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> map = new HashMap<>();


        map.put( "type", new Utils().MONEY_ADD );
        map.put( "amount", Long.valueOf( MONEY_TO_ADD ) );
        map.put( "status", false );
        map.put( "from", auth.getCurrentUser().getUid() );
        map.put( "to", "" );
        map.put( "merchant_name", payment_method );
        map.put( "timestamp", System.currentTimeMillis() );
        map.put( "failed_msg", "" );

        final WriteBatch batch = db.batch();

        //Creating Transaction
        db.collection( "Transaction" )
                .add( map )
                .addOnCompleteListener( new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            DocumentReference reference = task.getResult();
                            final String payment_id = reference.getId();
                            Map<String, Object> map2 = new HashMap<>();
                            map2.put( "payment_id", payment_id );
                            db.collection( "Transaction" ).document( payment_id ).update( map2 );

                            // batch.set(group1Ref, map);
                            //get UserCurrent Balance
                            db.collection( "users" ).document( auth.getCurrentUser().getUid() )
                                    .get()
                                    .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot snapshot = task.getResult();
                                                if (snapshot.exists()) {
                                                    long current_balance = (Long) snapshot.get( "balance" );

                                                    long update_balance = current_balance + Long.valueOf( MONEY_TO_ADD );
                                                    //Updating Transaction Money
                                                    Map<String, Object> map1 = new HashMap<>();
                                                    map1.put( "balance", update_balance );
                                                    DocumentReference updateRef = db.collection( "users" )
                                                            .document( auth.getCurrentUser().getUid() );
                                                    batch.update( updateRef, map1 );


                                                    batch.commit().addOnCompleteListener( new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText( PaymentActivityPage.this, "transaction Successful", Toast.LENGTH_SHORT ).show();
                                                                Map<String, Object> map1 = new HashMap<>();
                                                                map1.put( "status", true );
                                                                map1.put( "complete_at", System.currentTimeMillis() );
                                                                db.collection( "Transaction" )
                                                                        .document( payment_id )
                                                                        .update( map1 );
                                                                progressBar.setVisibility( View.GONE );

                                                                //Toast.makeText( PaymentActivityPage.this, "sms sent: "+sendSms(), Toast.LENGTH_SHORT ).show();
                                                                showDialog();
                                                            }
                                                        }
                                                    } );
                                                }
                                            }
                                        }
                                    } );


                        }
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( PaymentActivityPage.this, "could'nt add credits", Toast.LENGTH_SHORT ).show();
            }
        } );*/

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( "https://us-central1-phonenumber-4a8b0.cloudfunctions.net/" )
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        RetrofitService uploadInterFace = retrofit.create( RetrofitService.class );

        String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Call<TransactionResponse> call = uploadInterFace.addCreditsToAccount( uID, Long.valueOf( MONEY_TO_ADD ), payment_method );
        call.enqueue( new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                if (response.code() == 200) {
                    TransactionResponse transactionResponse = response.body();
                    if (transactionResponse != null) {
                        if (transactionResponse.isStatus()) {
                            Toast.makeText( PaymentActivityPage.this, "transaction Successful", Toast.LENGTH_SHORT ).show();
                            progressBar.setVisibility( View.GONE );
                            showDialog();
                        } else {
                            Toast.makeText( PaymentActivityPage.this, "transaction failed", Toast.LENGTH_SHORT ).show();
                            progressBar.setVisibility( View.GONE );
                            String msg = transactionResponse.getMsg();
                            showFailedDialog( msg );
                        }
                    } else {
                        Toast.makeText( PaymentActivityPage.this, "transaction failed", Toast.LENGTH_SHORT ).show();
                        progressBar.setVisibility( View.GONE );
                        String msg = "server error";
                        showFailedDialog( msg );
                    }
                }
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {

            }
        } );


    }

    private void showFailedDialog(String msg) {
        new TTFancyGifDialog.Builder( PaymentActivityPage.this )
                .setTitle( "Transaction Failed" )
                .setMessage( msg )
                .setPositiveBtnText( "OK" )
                .setNegativeBtnText( "Cancel" )
                .setPositiveBtnBackground( "#22b573" )
                //.setGifResource( R.drawable.tranasction )      //pass your gif, png or jpg
                .isCancellable( false )
                .OnPositiveClicked( new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        startActivity( new Intent( PaymentActivityPage.this, BuyCredits.class ) );
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

    private void showDialog() {
        new TTFancyGifDialog.Builder( PaymentActivityPage.this )
                .setTitle( "Transaction Successful" )
                .setMessage( "You have successfully Buy " + MONEY_TO_ADD + " credits" )
                .setPositiveBtnText( "OK" )
                .setPositiveBtnBackground( "#22b573" )
                .setGifResource( R.drawable.tranasction )      //pass your gif, png or jpg
                .isCancellable( false )
                .OnPositiveClicked( new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        startActivity( new Intent( PaymentActivityPage.this, MyTransaction.class ) );
                        finish();


                    }
                } )
                .build();


    }


}
