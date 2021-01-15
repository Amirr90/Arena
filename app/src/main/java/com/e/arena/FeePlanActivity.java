package com.e.arena;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.e.arena.Model.FeePlanModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FeePlanActivity extends AppCompatActivity {
    String TUTOR_ID, TUTOR_NAME;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseUser user;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    FeePlanModel data;
    TextView mTotalSaving;
    NumberFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_fee_plan );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits( 2 );
        format.setCurrency( Currency.getInstance( "inr" ) );

        progressBar = (ProgressBar) findViewById( R.id.progressBar6 );
        recyclerView = (RecyclerView) findViewById( R.id.rec_plan );
        mTotalSaving = (TextView) findViewById( R.id.textView24 );

        if (getIntent().hasExtra( "id" )) {
            TUTOR_NAME = getIntent().getStringExtra( "name" );
            TUTOR_ID = getIntent().getStringExtra( "id" );
            setToolbar( toolbar );


            recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
            loadData();
        }


    }

    private void loadData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( "https://us-central1-phonenumber-4a8b0.cloudfunctions.net/" )
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        RetrofitService uploadInterFace = retrofit.create( RetrofitService.class );
        Call<FeePlanModel> call = uploadInterFace.feePlan( TUTOR_ID );
        call.enqueue( new Callback<FeePlanModel>() {
            @Override
            public void onResponse(Call<FeePlanModel> call, Response<FeePlanModel> response) {
                progressBar.setVisibility( View.GONE );
                if (!response.isSuccessful()) {
                    Toast.makeText( FeePlanActivity.this, "could't complete request " + response.code(), Toast.LENGTH_SHORT ).show();
                    return;
                }
                data = response.body();
                FeePlanAdapter adapter = new FeePlanAdapter( data, FeePlanActivity.this );
                recyclerView.setAdapter( adapter );

                try {
                    double totalSaving = data.getTotalSaving();
                    mTotalSaving.setText( format.format( totalSaving ) );
                } catch (Exception e) {

                }


            }

            @Override
            public void onFailure(Call<FeePlanModel> call, Throwable t) {
                progressBar.setVisibility( View.GONE );
                Toast.makeText( FeePlanActivity.this, "error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );

    }

    private void setToolbar(Toolbar toolbar) {
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setDisplayShowHomeEnabled( true );
        getSupportActionBar().setTitle( TUTOR_NAME );
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class FeePlanAdapter extends RecyclerView.Adapter<FeePlanActivity.MyViewHolder> {
        FeePlanModel list;
        Context context;
        public String CURRENCY = "inr";
        NumberFormat format;

        public FeePlanAdapter(FeePlanModel list, Context context) {
            this.list = list;
            this.context = context;
            format = NumberFormat.getCurrencyInstance();
            format.setMaximumFractionDigits( 2 );
            format.setCurrency( Currency.getInstance( CURRENCY ) );

        }

        @NonNull
        @Override
        public FeePlanActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.fee_plan_view2, viewGroup, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull FeePlanActivity.MyViewHolder myViewHolder, int i) {


            try {
                double fee = list.getFeeData().get( i ).getFeeToPay();
                double saving = list.getFeeData().get( i ).getSaving();
                double percent = list.getFeeData().get( i ).getSavingPercent();

                myViewHolder.fee.setText( format.format( fee ) );
                myViewHolder.saving.setText( format.format( saving ) );
                if (i == 0) {
                    myViewHolder.percent.setText( "0.0%" );
                } else {
                    myViewHolder.percent.setText( new DecimalFormat( ".0" ).format( percent ) + "%" );
                }
                myViewHolder.priceHeading.setText( getCount( i ) );
            } catch (Exception e) {

            }
        }

        private String getCount(int i) {
            if (i == 0) {
                return "Fee for 1st Month";
            } else if (i == 1) {
                return "Fee for 2nd Month";
            } else if (i == 2) {
                return "Fee for 3rd Month";
            } else {
                int count = i + 1;
                return "Fee for " + count + "th Month";
            }
        }

        @Override
        public int getItemCount() {
            return list.getFeeData().size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView fee, saving, percent, priceHeading;

        public MyViewHolder(@NonNull View itemView) {
            super( itemView );

            fee = (TextView) itemView.findViewById( R.id.textView31 );
            saving = (TextView) itemView.findViewById( R.id.textView28 );
            percent = (TextView) itemView.findViewById( R.id.textView26 );
            priceHeading = (TextView) itemView.findViewById( R.id.textView30 );


        }
    }
}
