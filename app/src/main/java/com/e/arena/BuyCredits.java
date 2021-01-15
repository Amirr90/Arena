package com.e.arena;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

public class BuyCredits extends AppCompatActivity {

    Button paymentBtn;
    EditText editText;
    TextView mAvailableCredits;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_buy_credits );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setDisplayShowHomeEnabled( true );
        setToolbar( toolbar, "" );
        paymentBtn = (Button) findViewById( R.id.button );
        progressBar = (ProgressBar) findViewById( R.id.progressBar3 );

        editText = (EditText) findViewById( R.id.editText );
        editText.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i == 0) {
                    paymentBtn.setBackgroundResource( R.drawable.disable_btn );
                    paymentBtn.setEnabled( false );
                } else {
                    paymentBtn.setBackgroundResource( R.drawable.next_btn );
                    paymentBtn.setEnabled( true );
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        } );

        setAvailableCredits();

    }

    private void setAvailableCredits() {
        mAvailableCredits = (TextView) findViewById( R.id.textView12 );
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            db.collection( "users" )
                    .document( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                    .get()
                    .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            progressBar.setVisibility( View.GONE );
                            if (task.getResult() != null) {
                                DocumentSnapshot snapshot = task.getResult();
                                try {
                                    long balance = (Long) snapshot.get( "balance" );
                                    mAvailableCredits.setText( new DecimalFormat( "#,##0.##" ).format( balance ) );
                                } catch (Exception e) {
                                    Toast.makeText( BuyCredits.this, "can't update balance", Toast.LENGTH_SHORT ).show();
                                }
                                return;
                            }
                            Toast.makeText( BuyCredits.this, "can't update balance", Toast.LENGTH_SHORT ).show();
                        }
                    } )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mAvailableCredits.setText( "" );
                            progressBar.setVisibility( View.GONE );
                            Toast.makeText( BuyCredits.this, "can't update balance", Toast.LENGTH_SHORT ).show();
                        }
                    } );
    }

    public void gotoPaymentPage(View view) {
        if (editText.getText() != null && !editText.getText().toString().equals( "" )) {
            startActivity( new Intent( this, PaymentActivityPage.class ).putExtra( "money", editText.getText().toString() ) );
        } else {
            Toast.makeText( this, "Add Amount first", Toast.LENGTH_SHORT ).show();
        }
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

}
