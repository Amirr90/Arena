package com.e.arena;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.e.arena.Model.Banner;
import com.e.arena.Model.FaciliyModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class GymDetailActivity extends AppCompatActivity {
    String gym_id, gym_title;
    FirebaseFirestore db;
    RecyclerView recyclerView;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_gym_detail );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        recyclerView = (RecyclerView) findViewById( R.id.gym_rec );
        layout = (LinearLayout) findViewById( R.id.loading_layout );

        db = FirebaseFirestore.getInstance();
        if (getIntent().hasExtra( "gym_id" )) {
            gym_id = getIntent().getStringExtra( "gym_id" );
            gym_title = getIntent().getStringExtra( "gym_title" );
            setToolbar( toolbar, gym_title );


            setGymView( gym_id, gym_title );

        }
    }

    private void setLoadingLayout(boolean status) {
        layout.setVisibility( status ? View.VISIBLE : View.GONE );
        recyclerView.setVisibility( status ? View.GONE : View.VISIBLE );
    }

    private void setGymView(String gym_id, String gym_title) {
        List<Banner> photoList = new ArrayList<>();
        List<FaciliyModel> modelList = new ArrayList<>();


        GymDetailAdapter detailAdapter = new GymDetailAdapter( gym_id, gym_title, photoList, modelList, GymDetailActivity.this );


        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setAdapter( detailAdapter );
        loadPhotoData( photoList, detailAdapter, gym_id, modelList );

    }

    private void loadPhotoData(final List<Banner> photoList, final GymDetailAdapter photosAdapter, String gym_id, final List<FaciliyModel> modelList) {
        db.collection( "GYM LIST" )
                .document( gym_id )
                .collection( "Photos" )
                .limit( 5 )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful())
                            return;
                        QuerySnapshot snapshots = task.getResult();
                        if (!snapshots.isEmpty()) {
                            for (int a = 0; a < snapshots.size(); a++) {
                                String image_url = (String) snapshots.getDocuments().get( a ).getString( "image" );
                                photoList.add( new Banner( image_url ) );
                            }

                            photosAdapter.notifyDataSetChanged();

                        }
                    }
                } );


        db.collection( "GYM LIST" )
                .document( gym_id )
                .collection( "Facilities" )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful())
                            return;
                        QuerySnapshot snapshots = task.getResult();
                        if (!snapshots.isEmpty()) {
                            for (int a = 0; a < snapshots.size(); a++) {
                                String image_url = (String) snapshots.getDocuments().get( a ).getString( "image" );
                                String title = (String) snapshots.getDocuments().get( a ).getString( "title" );
                                modelList.add( new FaciliyModel( image_url, title ) );
                            }

                            photosAdapter.notifyDataSetChanged();
                            setLoadingLayout( false );
                        }
                    }
                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onBackPressed();
                        finish();
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

    private class GymPhotosAdapter extends RecyclerView.Adapter<GymPhotosAdapter.MyViewHolder> {

        List<Banner> photoList;
        Context context;

        public GymPhotosAdapter(List<Banner> photoList, Context context) {
            this.photoList = photoList;
            this.context = context;
        }

        @NonNull
        @Override
        public GymPhotosAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.image_view, viewGroup, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull final GymPhotosAdapter.MyViewHolder myViewHolder, int position) {
            String image_url = photoList.get( position ).getImage();
            if (!image_url.equals( "" ) && image_url != null)
                Picasso.with( context ).load( image_url ).into( myViewHolder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        myViewHolder.imageView.setImageResource( R.drawable.profile );
                    }
                } );

        }

        @Override
        public int getItemCount() {
            return photoList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;

            public MyViewHolder(@NonNull View itemView) {
                super( itemView );
                imageView = (ImageView) itemView.findViewById( R.id.roundedImageView );
            }
        }
    }

    private class GymDetailAdapter extends RecyclerView.Adapter<GymDetailAdapter.MyViewHolder> {

        private String Gym_id;
        private String Gym_title;
        List<Banner> photoList;
        List<FaciliyModel> faciliyList;
        Context context;

        public GymDetailAdapter(String gym_id, String gym_title, List<Banner> photoList, List<FaciliyModel> faciliyList, Context context) {
            Gym_id = gym_id;
            Gym_title = gym_title;
            this.photoList = photoList;
            this.faciliyList = faciliyList;
            this.context = context;
        }

        @NonNull
        @Override
        public GymDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.gym_rec_lay, viewGroup, false );

            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull GymDetailAdapter.MyViewHolder myViewHolder, int i) {


            setGymDetail( myViewHolder );

            //Setting Photos
            GymPhotosAdapter photosAdapter = new GymPhotosAdapter( photoList, context );
            myViewHolder.recyclerView.setLayoutManager( new GridLayoutManager( context, 3 ) );
            myViewHolder.recyclerView.setAdapter( photosAdapter );
            photosAdapter.notifyDataSetChanged();

            //Setting Facilities
            GymFacilityAdapter facilityAdapter = new GymFacilityAdapter( faciliyList, context );
            myViewHolder.recyclerView2.setLayoutManager( new GridLayoutManager( context, 2 ) );
            myViewHolder.recyclerView2.setAdapter( facilityAdapter );
            facilityAdapter.notifyDataSetChanged();


            myViewHolder.payBtn.setText( "PAY AT "+gym_title );
            myViewHolder.payBtn.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity( new Intent( context,CodeScannerActivity.class ).putExtra( "uid",gym_id ) );
                }
            } );

        }

        @Override
        public int getItemCount() {
            return 1;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            RecyclerView recyclerView, recyclerView2;
            private TextView gym_name, gym_address, gym_number, abt_gym_title, abt_gym, abt_faci;
            private TextView weekDaysTiming, weekEndTiming;
            private ImageView gym_icon;
            private Button payBtn;

            public MyViewHolder(@NonNull View itemView) {
                super( itemView );
                recyclerView = (RecyclerView) itemView.findViewById( R.id.image_rec );
                recyclerView2 = (RecyclerView) itemView.findViewById( R.id.facility_rec );
                gym_name = (TextView) itemView.findViewById( R.id.textView3 );
                gym_address = (TextView) itemView.findViewById( R.id.textView4 );
                gym_number = (TextView) itemView.findViewById( R.id.number );
                abt_gym_title = (TextView) itemView.findViewById( R.id.text23 );
                abt_gym = (TextView) itemView.findViewById( R.id.abou_gym );
                abt_faci = (TextView) itemView.findViewById( R.id.textView10 );
                gym_icon = (ImageView) itemView.findViewById( R.id.imageView3 );

                weekDaysTiming = (TextView) itemView.findViewById( R.id.set_weekTiming );
                weekEndTiming = (TextView) itemView.findViewById( R.id.ser_weekend_timing );

                payBtn=(Button)itemView.findViewById( R.id.pay_btn_gym_acivity );


            }


        }
    }

    private void setGymDetail(final GymDetailAdapter.MyViewHolder myViewHolder) {

        myViewHolder.gym_name.setText( gym_title );
        myViewHolder.abt_gym_title.setText( "About " + gym_title );
        myViewHolder.abt_faci.setText( "Facilities at " + gym_title );
        //setting Timing
        db.collection( "GYM LIST" )
                .document( gym_id )
                .collection( "Timing" )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful())
                            return;

                        QuerySnapshot snapshots = task.getResult();
                        if (!snapshots.isEmpty() && snapshots.size() > 1) {
                            String morning = (String) snapshots.getDocuments().get( 0 ).get( "morning" );
                            String evening = (String) snapshots.getDocuments().get( 0 ).get( "evening" );
                            myViewHolder.weekDaysTiming.setText( "Morning: " + morning + "\nEvening: " + evening );

                            String Emorning = (String) snapshots.getDocuments().get( 1 ).get( "morning" );
                            String Eevening = (String) snapshots.getDocuments().get( 1 ).get( "evening" );
                            myViewHolder.weekEndTiming.setText( "Morning: " + morning + "\nEvening: " + evening );
                        }
                    }
                } );


        db.collection( "GYM LIST" )
                .document( gym_id )
                .get()
                .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful())
                            return;

                        DocumentSnapshot snapshot = task.getResult();
                        String address = (String) snapshot.getString( "address" );
                        String image_url = (String) snapshot.getString( "image" );
                        String about = (String) snapshot.getString( "about" );
                        long phone = (Long) snapshot.get( "phone" );

                        if (!image_url.equals( "" ) && image_url != null)
                            Picasso.with( GymDetailActivity.this ).load( image_url ).into( myViewHolder.gym_icon );

                        myViewHolder.gym_address.setText( address );
                        myViewHolder.abt_gym.setText( about );
                        myViewHolder.gym_number.setText( phone + "" );

                    }
                } );

    }

    private class GymFacilityAdapter extends RecyclerView.Adapter<GymFacilityAdapter.MyViewHolder> {

        List<FaciliyModel> facilityModels;
        Context context;

        public GymFacilityAdapter(List<FaciliyModel> faciliyModels, Context context) {
            this.facilityModels = faciliyModels;
            this.context = context;
        }

        @NonNull
        @Override
        public GymFacilityAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.faciliy_view, viewGroup, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull GymFacilityAdapter.MyViewHolder myViewHolder, int i) {
            String image_url = facilityModels.get( i ).getImage();
            myViewHolder.facilityTitle.setText( facilityModels.get( i ).getTitle() );
            if (!image_url.equals( "" ) && image_url != null)
                Picasso.with( context ).load( image_url ).into( myViewHolder.mFacilityIcon, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                } );


        }

        @Override
        public int getItemCount() {
            return facilityModels.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView facilityTitle;
            private ImageView mFacilityIcon;

            public MyViewHolder(@NonNull View itemView) {
                super( itemView );
                facilityTitle = (TextView) itemView.findViewById( R.id.textView2 );
                mFacilityIcon = (ImageView) itemView.findViewById( R.id.imageView4 );
            }
        }
    }
}
