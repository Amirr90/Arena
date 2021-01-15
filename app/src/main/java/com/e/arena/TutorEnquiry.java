package com.e.arena;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TutorEnquiry extends AppCompatActivity {
    String SERVICE_ID;
    private EditText mName, mEmail, mPhoneNumber, mStudentName, mClass, mSubject, mGender, mAddress, mMonthlyFee;
    private Button mSubmitEnquiryBtn;
    ArrayList mSelectedItems;
    ProgressBar progressBar;
    String mEnquiryCity, mEnquiryState, mLocality, mSubLocality;
    ImageView mHireTutorOfferImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tutor_enquiry );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        findViewById();

        if (getIntent().hasExtra( "uid" )) {
            SERVICE_ID = getIntent().getStringExtra( "uid" );
            setToolbar( toolbar, "Tutor Enquiry" );

        }
    }

    private void findViewById() {
        mName = (EditText) findViewById( R.id.editText1 );
        mEmail = (EditText) findViewById( R.id.editTextEmail );
        mPhoneNumber = (EditText) findViewById( R.id.editTextNumber );
        mStudentName = (EditText) findViewById( R.id.editTextStudentName );
        mClass = (EditText) findViewById( R.id.editTextClass );
        mSubject = (EditText) findViewById( R.id.editTextSubject );
        mGender = (EditText) findViewById( R.id.editTextGender );
        mAddress = (EditText) findViewById( R.id.editTextAddress );
        mSubmitEnquiryBtn = (Button) findViewById( R.id.button4 );
        mMonthlyFee = (EditText) findViewById( R.id.editTextFee );
        progressBar = (ProgressBar) findViewById( R.id.progressBar4 );
        mHireTutorOfferImage = (ImageView) findViewById( R.id.imageView7 );

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mPhoneNumber.setText( FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() );
        }

        getAddress( TutorEnquiry.this, HomeActivity.user_lat, HomeActivity.user_long );

        mAddress.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddress.setText( "" );
                getAddress( TutorEnquiry.this, HomeActivity.user_lat, HomeActivity.user_long );

            }
        } );

        setTutorOfferImage();
    }

    private void setTutorOfferImage() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection( "Offer Banner Image" )
                .document( "HireTutor Offer" )
                .get()
                .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot != null) {
                                if (snapshot.getBoolean( "isActive" )) {
                                    mHireTutorOfferImage.setVisibility( View.VISIBLE );
                                    String iconUrl = snapshot.getString( "icon" );
                                    if (iconUrl != null && !iconUrl.equals( "" ))
                                        Picasso.with( TutorEnquiry.this ).load( iconUrl ).into( mHireTutorOfferImage );
                                } else {
                                    mHireTutorOfferImage.setVisibility( View.GONE );
                                }
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

    public void selectClassDialog(View view) {
        final CharSequence[] items = {"Class I", "Class II", "Class III", "Class IV",
                "Class V", "Class VI", "Class VII", "Class VIII",
                "Class IX", "Class X", "Class XI", "Class XII"};

        AlertDialog.Builder builder = new AlertDialog.Builder( TutorEnquiry.this );
        builder.setTitle( "Make your selection" );
        builder.setItems( items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                mClass.setText( items[item] );
                dialog.dismiss();

            }
        } ).show();
    }

    public void selectMonthlyFee(View view) {
        final CharSequence[] items = {"₹2,000(for 60 minutes)", "₹2,500(for 60 minutes)", "₹3,000(for 60 minutes)", "₹3,500(for 60 minutes)",
                "₹4,000(for 60 minutes)", "₹4,500(for 60 minutes)", "₹5,000(for 60 minutes)"};

        AlertDialog.Builder builder = new AlertDialog.Builder( TutorEnquiry.this );
        builder.setTitle( "Make your selection" );
        builder.setItems( items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                mMonthlyFee.setText( items[item] );
                dialog.dismiss();

            }
        } ).show();
    }

    public void selectSubject(View view) {

        // where we will store or remove selected items
        mSelectedItems = new ArrayList<Integer>();
        final String[] choiceArray = getResources().getStringArray( R.array.choices );

        final AlertDialog.Builder builder = new AlertDialog.Builder( TutorEnquiry.this );

        // set the dialog title
        builder.setTitle( "Choose One or More" )
                .setMultiChoiceItems( R.array.choices, null, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        if (isChecked) {
                            // if the user checked the item, add it to the selected items
                            mSelectedItems.add( which );
                        } else if (mSelectedItems.contains( which )) {
                            // else if the item is already in the array, remove it
                            mSelectedItems.remove( Integer.valueOf( which ) );
                        }

                    }

                } )

                // Set the action buttons
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        StringBuilder selectedIndex = new StringBuilder();
                        for (int a = 0; a < mSelectedItems.size(); a++) {
                            if (mSelectedItems.size() == 1) {
                                selectedIndex.append( choiceArray[(Integer) mSelectedItems.get( a )] );
                            } else {
                                selectedIndex.append( choiceArray[(Integer) mSelectedItems.get( a )] + "," );
                            }
                        }
                        mSubject.setText( selectedIndex.toString() );

                    }
                } )

                .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the AlertDialog in the screen
                    }
                } )

                .show();

    }

    public void selectGender(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder( TutorEnquiry.this );

        final String[] gender = getResources().getStringArray( R.array.choices_gender );
        // Set the dialog title
        builder.setTitle( "Choose One" )

                .setSingleChoiceItems( R.array.choices_gender, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //showToast("Some actions maybe? Selected index: " + arg1);
                    }

                } )

                // Set the action buttons
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // user clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog

                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        mGender.setText( gender[selectedPosition] );

                    }
                } )

                .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the dialog from the screen

                    }
                } )

                .show();

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
                String locality = addresses.get( 0 ).getLocality();
                String sub_locality = addresses.get( 0 ).getSubLocality();
                mAddress.setText( address );
                mEnquiryCity = city;
                mEnquiryState = state;
                mLocality = locality;
                mSubLocality = sub_locality;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public void submitEnquiry(final View view) {

        //Hiding KeyBoard
        hideKeyboard();


        //Checking null or Empty Value
        if (!mName.getText().toString().isEmpty() &&
                !mEmail.getText().toString().isEmpty() &&
                !mPhoneNumber.getText().toString().isEmpty() &&
                !mStudentName.getText().toString().isEmpty() &&
                !mClass.getText().toString().isEmpty() &&
                !mSubject.getText().toString().isEmpty() &&
                !mAddress.getText().toString().isEmpty() &&
                !mMonthlyFee.getText().toString().isEmpty() &&
                !mGender.getText().toString().isEmpty() &&
                FirebaseAuth.getInstance().getCurrentUser() != null) {


            //Disable Button
            mSubmitEnquiryBtn.setEnabled( false );

            //Show Progressbar
            progressBar.setVisibility( View.VISIBLE );

            //setValue To Pojo
            com.e.arena.Model.TutorEnquiry enquiry = new com.e.arena.Model.TutorEnquiry(
                    mName.getText().toString(),
                    mEmail.getText().toString(),
                    mPhoneNumber.getText().toString(),
                    mStudentName.getText().toString(),
                    mClass.getText().toString(),
                    mSubject.getText().toString(),
                    mGender.getText().toString(),
                    mAddress.getText().toString(),
                    mMonthlyFee.getText().toString(),
                    System.currentTimeMillis(),
                    mEnquiryCity,
                    mEnquiryState,
                    mLocality,
                    mSubLocality,
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    "new"
            );

            //trigger FirestoreDatabase
            final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection( "Tutor Enquiry" )
                    .add( enquiry )
                    .addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String id = documentReference.getId();
                            Toast.makeText( TutorEnquiry.this, "Enquiry Submitted", Toast.LENGTH_SHORT ).show();
                            Map<String, Object> data = new HashMap<>();
                            data.put( "id", id );
                            firestore.collection( "Tutor Enquiry" )
                                    .document( id )
                                    .update( data );
                            finish();

                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( TutorEnquiry.this, "Could't Submitted Enquiry ", Toast.LENGTH_SHORT ).show();
                    mSubmitEnquiryBtn.setEnabled( true );
                }
            } );

        } else {
            Snackbar.make( view, "please fill all the fields", Snackbar.LENGTH_SHORT ).show();
        }
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService( Context.INPUT_METHOD_SERVICE );
            inputManager.hideSoftInputFromWindow( view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
        }
    }
}
