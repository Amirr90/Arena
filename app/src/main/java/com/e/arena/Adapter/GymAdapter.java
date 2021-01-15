package com.e.arena.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.arena.BuyCredits;
import com.e.arena.CodeScannerActivity;
import com.e.arena.Model.Banner;
import com.e.arena.Model.GymModel;
import com.e.arena.Model.TransactionModel;
import com.e.arena.PicassoImageLoadingService;
import com.e.arena.R;
import com.e.arena.ShowAllTutor;
import com.e.arena.TutorEnquiry;
import com.e.arena.Utility.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.event.OnSlideClickListener;

public class GymAdapter extends RecyclerView.Adapter<GymAdapter.MyViewHolder> {
    List<GymModel> gymList;
    List<Banner> bannerList;
    Context context;
    private String TAG = "GymAdapter";
    private GymViewAdapter adapter;
    List<TransactionModel> transactionList;
    List<DocumentSnapshot> otherOptionLists;

    private FirebaseAuth mAuth;

    public GymAdapter(List<GymModel> gymList, List<Banner> bannerList, Context context) {
        this.gymList = gymList;
        this.bannerList = bannerList;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public GymAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.home_view1, viewGroup, false );
        return new MyViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull GymAdapter.MyViewHolder myViewHolder, int i) {
        try {
            if (gymList.size() == 1)
                myViewHolder.gym_count.setText( gymList.size() + " Gym found" );
            else {
                myViewHolder.gym_count.setText( gymList.size() + " Gyms found" );
            }
            adapter = new GymViewAdapter( gymList, context );
            myViewHolder.recyclerView.setLayoutManager( new LinearLayoutManager( context ) );
            myViewHolder.recyclerView.setAdapter( adapter );
            //myViewHolder.slider.setSliderAdapter( new SliderAdapter( bannerList ) );
            myViewHolder.slider.setSliderAdapter( new SliderAdapterExample( bannerList,context ) );
            adapter.notifyDataSetChanged();


            transactionList = new ArrayList<>();
            final RecentHomeTransactionAdapter adapter = new RecentHomeTransactionAdapter( transactionList, context );
            GridLayoutManager layoutManager = new GridLayoutManager( context, 4 );
            myViewHolder.recent_recyclerView.setLayoutManager( layoutManager );
            myViewHolder.recent_recyclerView.setAdapter( adapter );
            loadRecentTransactionData( transactionList, adapter, myViewHolder );

            otherOptionLists = new ArrayList<>();
            final OtherOptionAdapter OtherOptionAdapter = new OtherOptionAdapter( otherOptionLists, context );
            GridLayoutManager layoutManager2 = new GridLayoutManager( context, 4 );
            myViewHolder.mOtherOptionRecyclerView.setLayoutManager( layoutManager2 );
            myViewHolder.mOtherOptionRecyclerView.setAdapter( OtherOptionAdapter );
            loadOtherFunctionData( OtherOptionAdapter, myViewHolder );


            /*myViewHolder.slider.setOnSlideClickListener( new OnSlideClickListener() {
                @Override
                public void onSlideClick(int position) {
                    context.startActivity( new Intent( context, BuyCredits.class ) );
                }
            } );*/

        } catch (Exception e) {
            Log.d( TAG, "onBindViewHolder: " + e.getLocalizedMessage() );
        }


    }

    private void loadOtherFunctionData(final OtherOptionAdapter adapter, final MyViewHolder myViewHolder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() != null) {
            db.collection( "OtherService" )
                    .whereEqualTo( "isActive", true )
                    //.whereEqualTo( "type", new Utils().MONEY_PAID )
                    //.orderBy( "timestamp", Query.Direction.DESCENDING ).limit( 4 )
                    .get()
                    .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                otherOptionLists.clear();
                                QuerySnapshot snapshot = task.getResult();
                                if (!snapshot.isEmpty()) {
                                    otherOptionLists.addAll( task.getResult().getDocuments() );
                                    adapter.notifyDataSetChanged();

                                }

                            } else {
                                Toast.makeText( context, "task not completed\n" + task.getException().getMessage(), Toast.LENGTH_SHORT ).show();
                                Log.d( TAG, "onComplete: " + task.getException().getMessage() );

                            }
                        }
                    } );
        } else {
            Toast.makeText( context, "user is null", Toast.LENGTH_SHORT ).show();
        }
    }

    private void loadRecentTransactionData(final List<TransactionModel> transactionList, final RecentHomeTransactionAdapter adapter, final MyViewHolder myViewHolder) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() != null) {
            db.collection( "Transaction" )
                    .whereEqualTo( "from", mAuth.getCurrentUser().getUid() )
                    .whereEqualTo( "type", new Utils().MONEY_PAID )
                    .orderBy( "timestamp", Query.Direction.DESCENDING ).limit( 4 )
                    .get()
                    .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot snapshot = task.getResult();
                                if (!snapshot.isEmpty()) {
                                    for (int a = 0; a < snapshot.size(); a++) {
                                        String transaction_id = (String) snapshot.getDocuments().get( a ).getId();
                                        String gym_id = (String) snapshot.getDocuments().get( a ).get( "to" );
                                        transactionList.add( new TransactionModel( gym_id, transaction_id ) );
                                    }
                                    adapter.notifyDataSetChanged();
                                    myViewHolder.layout.setVisibility( View.VISIBLE );

                                } else {
                                    Toast.makeText( context, "No Transaction found", Toast.LENGTH_SHORT ).show();
                                    myViewHolder.layout.setVisibility( View.GONE );

                                }

                            } else {
                                Toast.makeText( context, "task not completed\n" + task.getException().getMessage(), Toast.LENGTH_SHORT ).show();
                                Log.d( TAG, "onComplete: " + task.getException().getMessage() );
                                myViewHolder.layout.setVisibility( View.GONE );
                            }
                        }
                    } );
        } else {
            Toast.makeText( context, "user is null", Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView, recent_recyclerView, mOtherOptionRecyclerView;
        SliderView slider;
        private TextView gym_count;
        LinearLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super( itemView );
            adapter = new GymViewAdapter( gymList, context );
            recyclerView = (RecyclerView) itemView.findViewById( R.id.rec_home );
            recyclerView.setLayoutManager( new LinearLayoutManager( context ) );
            recyclerView.setAdapter( adapter );
            slider = (SliderView) itemView.findViewById( R.id.banner_slider1 );
            Slider.init( new PicassoImageLoadingService( context ) );
            gym_count = (TextView) itemView.findViewById( R.id.gym_found_count );
            recent_recyclerView = (RecyclerView) itemView.findViewById( R.id.recent_transaction_rec );
            mOtherOptionRecyclerView = (RecyclerView) itemView.findViewById( R.id.app_other_function_rec );
            layout = (LinearLayout) itemView.findViewById( R.id.recent_lay );

            Slider.init( new PicassoImageLoadingService( context ) );


            slider.setIndicatorAnimation( IndicatorAnimations.WORM ); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            // sliderView.setSliderTransformAnimation( SliderAnimations.CUBEINDEPTHTRANSFORMATION );
            slider.setSliderTransformAnimation( SliderAnimations.CUBEINROTATIONTRANSFORMATION );
            slider.setAutoCycleDirection( SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH );
            slider.setIndicatorSelectedColor( context.getResources().getColor( R.color.colorbackground ) );
            slider.setIndicatorUnselectedColor( Color.GRAY );
            slider.setScrollTimeInSec( 4 ); //set scroll delay in seconds :
            slider.startAutoCycle();

        }
    }

    private class RecentHomeTransactionAdapter extends RecyclerView.Adapter<RecentHomeTransactionAdapter.MyViewHolder> {
        List<TransactionModel> list;
        Context context;

        public RecentHomeTransactionAdapter(List<TransactionModel> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public RecentHomeTransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.home_transaction_view, viewGroup, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull RecentHomeTransactionAdapter.MyViewHolder myViewHolder, final int i) {

            try {
                String gym = list.get( i ).getTo();
                setGymDetail( gym, myViewHolder, i );
            } catch (Exception e) {
                Toast.makeText( context, "GymAdapter: " + e.getMessage(), Toast.LENGTH_SHORT ).show();
            }

            myViewHolder.layout.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String gym_id = list.get( i ).getTo();
                    context.startActivity( new Intent( context, CodeScannerActivity.class ).putExtra( "uid", gym_id ) );
                }
            } );
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private CircleImageView gym_icon;
            private TextView GymTitle;
            private RelativeLayout layout;


            public MyViewHolder(@NonNull View itemView) {
                super( itemView );
                gym_icon = (CircleImageView) itemView.findViewById( R.id.imageView5 );
                GymTitle = (TextView) itemView.findViewById( R.id.textView9 );
                layout = (RelativeLayout) itemView.findViewById( R.id.recent_transaction_layout );
            }
        }
    }

    private class OtherOptionAdapter extends RecyclerView.Adapter<OtherOptionAdapter.MyViewHolder> {
        List<DocumentSnapshot> list;
        Context context;

        public OtherOptionAdapter(List<DocumentSnapshot> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public OtherOptionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.home_transaction_view, viewGroup, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull OtherOptionAdapter.MyViewHolder myViewHolder, final int i) {

            try {
                String serviceName = (String) otherOptionLists.get( i ).get( "name" );
                String iconUrl = (String) otherOptionLists.get( i ).get( "icon" );
                myViewHolder.GymTitle.setText( serviceName );
                if (iconUrl != null && !iconUrl.equals( "" )) {
                    Picasso.with( context ).load( iconUrl ).into( myViewHolder.gym_icon );
                }
            } catch (Exception e) {
                Toast.makeText( context, "GymAdapter: " + e.getMessage(), Toast.LENGTH_SHORT ).show();
            }

            myViewHolder.layout.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (i == 0) {
                        String service_id = list.get( i ).getId();
                        context.startActivity( new Intent( context, TutorEnquiry.class ).putExtra( "uid", service_id ) );
                    } else if (i == 1) {
                        String service_id = list.get( i ).getId();
                        context.startActivity( new Intent( context, ShowAllTutor.class ).putExtra( "uid", service_id ) );
                    }
                }
            } );
        }

        @Override
        public int getItemCount() {

            return list.size();

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private CircleImageView gym_icon;
            private TextView GymTitle;
            private RelativeLayout layout;


            public MyViewHolder(@NonNull View itemView) {
                super( itemView );
                gym_icon = (CircleImageView) itemView.findViewById( R.id.imageView5 );
                GymTitle = (TextView) itemView.findViewById( R.id.textView9 );
                layout = (RelativeLayout) itemView.findViewById( R.id.recent_transaction_layout );
            }
        }
    }


    private void setGymDetail(String gym, final RecentHomeTransactionAdapter.MyViewHolder myViewHolder, int i) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection( "GYM LIST" )
                .document( gym )
                .get()
                .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists()) {
                                String image_url = snapshot.getString( "image" );
                                String gym_name = snapshot.getString( "title" );
                                if (!image_url.equals( "" ) && image_url != null)
                                    Picasso.with( context ).load( image_url ).into( myViewHolder.gym_icon );

                                myViewHolder.GymTitle.setText( gym_name );
                            }

                        }
                    }
                } );
    }

}
