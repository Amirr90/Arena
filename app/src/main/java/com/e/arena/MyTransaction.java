package com.e.arena;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.arena.Utility.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyTransaction extends AppCompatActivity {

    ProgressBar progressBar;
    RecyclerView recyclerView;
    RelativeLayout mNoInternetLayout;
    List<DocumentSnapshot> myTransactionData;
    MyTransactionAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_my_transaction );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        setToolbar( toolbar, "My Transactions" );

        progressBar = (ProgressBar) findViewById( R.id.progressBar );
        recyclerView = (RecyclerView) findViewById( R.id.my_transaction_rec );
        mNoInternetLayout = (RelativeLayout) findViewById( R.id.no_internet_lay );
        db = FirebaseFirestore.getInstance();



        myTransactionData = new ArrayList<>();
        adapter = new MyTransactionAdapter( myTransactionData, this );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setAdapter( adapter );
        loadData();


    }


    private void loadData() {

        showProgressBar();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection( "Transaction" )
                    .whereEqualTo( "from", uid )
                    .orderBy( "timestamp", Query.Direction.DESCENDING )
                    .get()
                    .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            hideProgressBar();
                            if (task.isSuccessful()) {
                                QuerySnapshot snapshots = task.getResult();
                                if (!snapshots.isEmpty()) {
                                    myTransactionData.addAll( task.getResult().getDocuments() );
                                    adapter.notifyDataSetChanged();
                                    showRecyclerView();
                                    hideProgressBar();

                                }
                                return;
                            }

                            Toast.makeText( MyTransaction.this, "No Data", Toast.LENGTH_SHORT ).show();
                        }
                    } )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressBar();
                            Toast.makeText( MyTransaction.this, "try again", Toast.LENGTH_SHORT ).show();
                        }
                    } );
        }
    }


    public void checkInternet(View view) {
        hideNoInternetLayout();
        showProgressBar();

        if (isInternetAvailable()) {
            hideProgressBar();
            hideNoInternetLayout();
            showRecyclerView();
        } else {
            hideProgressBar();
            showNoInternetLayout();

        }
    }

    private void showNoInternetLayout() {
        mNoInternetLayout.setVisibility( View.VISIBLE );
    }

    private void hideNoInternetLayout() {
        mNoInternetLayout.setVisibility( View.GONE );
    }

    private void showRecyclerView() {
        recyclerView.setVisibility( View.VISIBLE );
    }

    private void hideRecyclerView() {
        recyclerView.setVisibility( View.GONE );
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName( "google.com" );
            //You can replace it with your name
            return !ipAddr.equals( "" );

        } catch (Exception e) {
            return false;
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


    public void hideProgressBar() {
        progressBar.setVisibility( View.GONE );
    }

    public void showProgressBar() {
        progressBar.setVisibility( View.VISIBLE );
    }

    private class MyTransactionAdapter extends RecyclerView.Adapter<MyTransactionAdapter.MyViewHolder> {
        List<DocumentSnapshot> list;
        Context context;

        public MyTransactionAdapter(List<DocumentSnapshot> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public MyTransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.my_transaction_view, viewGroup, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull MyTransactionAdapter.MyViewHolder myViewHolder, int i) {
           try {
               String TYPE = list.get( i ).getString( "type" );
               String merchant_name = list.get( i ).getString( "merchant_name" );
               long timestamp = (Long) list.get( i ).get( "timestamp" );
               long amount = (Long) list.get( i ).get( "amount" );
               String mytime = java.text.DateFormat.getTimeInstance().format( timestamp );
               String date = DateUtils.formatDateTime( context, timestamp, DateUtils.FORMAT_SHOW_DATE );
               myViewHolder.time.setText( mytime + "\n" + date );
               boolean failed_status=list.get( i ).getBoolean( "status" );


               switch (TYPE) {
                   case "PAID":
                       myViewHolder.amount.setText( "-₹" + new DecimalFormat( "#,##0.##" ).format( amount ) );
                       myViewHolder.amount.setTextColor( getResources().getColor( R.color.colorButton ) );
                       myViewHolder.senderName.setText( new Utils().CREDITS_PAID_TO_TEXT + merchant_name );
                       String gymId = myTransactionData.get( i ).getString( "to" );
                       loadImage( myViewHolder, i, gymId );

                       if (failed_status){

                           myViewHolder.failed_msg.setVisibility( View.GONE );
                       }
                       else {
                           myViewHolder.amount.setTextColor( Color.RED );
                           myViewHolder.failed_msg.setVisibility( View.VISIBLE );
                       }
                       break;
                   case "ADD":
                       if (merchant_name.equals( "PayTm" )) {
                           myViewHolder.imageView.setImageResource( R.drawable.paytm_logo );
                       }
                       if (merchant_name.equals( "PhonePay" )) {
                           myViewHolder.imageView.setImageResource( R.drawable.phone_pay_logo );
                       }
                       myViewHolder.amount.setText( "+₹" + new DecimalFormat( "#,##0.##" ).format( amount ) );
                       myViewHolder.amount.setTextColor( getResources().getColor( R.color.green ) );
                       myViewHolder.senderName.setText( new Utils().MONEY_ADDED_TO_WALLET + merchant_name );
                       if (failed_status){

                           myViewHolder.failed_msg.setVisibility( View.GONE );
                       }
                       else {
                           myViewHolder.amount.setTextColor( Color.RED );
                           myViewHolder.failed_msg.setVisibility( View.VISIBLE );
                       }
                       break;

               }
           }
           catch (Exception e){

           }


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView senderName, time, amount, failed_msg;
            private CircleImageView imageView;

            public MyViewHolder(@NonNull View itemView) {
                super( itemView );

                failed_msg = (TextView) itemView.findViewById( R.id.textView21 );
                senderName = (TextView) itemView.findViewById( R.id.textView14 );
                amount = (TextView) itemView.findViewById( R.id.textView18 );
                time = (TextView) itemView.findViewById( R.id.textView15 );
                imageView = (CircleImageView) itemView.findViewById( R.id.image_merchant );
            }
        }
    }

    private void loadImage(final MyTransactionAdapter.MyViewHolder myViewHolder, int i, String gymId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection( "GYM LIST" )
                .document( gymId )
                .get()
                .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText( MyTransaction.this, "load image error", Toast.LENGTH_SHORT ).show();
                            return;
                        }

                        DocumentSnapshot snapshot = task.getResult();
                        String image_url = snapshot.getString( "image" );
                        if (image_url != null && !image_url.equals( "" ))
                            Picasso.with( MyTransaction.this ).load( image_url ).into( myViewHolder.imageView );
                    }
                } );
    }
}
