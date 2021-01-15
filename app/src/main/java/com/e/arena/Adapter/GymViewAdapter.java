package com.e.arena.Adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.e.arena.GymDetailActivity;
import com.e.arena.HomeActivity;
import com.e.arena.Model.GymModel;
import com.e.arena.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GymViewAdapter extends RecyclerView.Adapter<GymViewAdapter.MyViewHolder> {
    List<GymModel> gymList;
    Context context;

    public GymViewAdapter(List<GymModel> gymList, Context context) {
        this.gymList = gymList;
        this.context = context;
    }

    @NonNull
    @Override
    public GymViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.home_view2, viewGroup, false );
        return new MyViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull GymViewAdapter.MyViewHolder myViewHolder, final int position) {


        myViewHolder.title.setText( gymList.get( position ).getTitle() );
        myViewHolder.location.setText( gymList.get( position ).getLocation() );
        myViewHolder.price.setText( "₹" + gymList.get( position ).getPrice() );

        String image_url = gymList.get( position ).getImage();
        if (!image_url.equals( "" ))
            Picasso.with( context ).load( image_url ).into( myViewHolder.gymImageView );

        myViewHolder.cardView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gym_id = gymList.get( position ).getId();
                String gym_title = gymList.get( position ).getTitle();

                context.startActivity( new Intent( context, GymDetailActivity.class )
                        .putExtra( "gym_id", gym_id )
                        .putExtra( "gym_title", gym_title ) );
            }
        } );


        float dis = getGymDistance( gymList, position );
        myViewHolder.distance.setText( dis + " Km" );

    }

    private float getGymDistance(List<GymModel> gymList, int position) {

        try {
            double user_lat = HomeActivity.user_lat;
            double user_lang = HomeActivity.user_long;
            double merchant_lat = Double.parseDouble( gymList.get( position ).getLat() );
            double merchant_long = Double.parseDouble( gymList.get( position ).getLon() );//gymList.get( position ).getLon();
            float[] results = new float[1];
            Location.distanceBetween( user_lat, user_lang,
                    merchant_lat, merchant_long, results );
            float distance = results[0];
            return distance;
        } catch (Exception e) {
            return 0;
        }
       /* try {
            double user_lat = 26.8444334;//HomeActivity.user_lat;
            double user_lang = 80.8680633;//HomeActivity.user_long;
            double merchant_lat = 26.8444100;//gymList.get( position ).getLat();
            double merchant_long = 80.8680633;//gymList.get( position ).getLon();
            double earthRadius = 6371000; //meters
            double dLat = Math.toRadians( merchant_lat - user_lat );
            double dLng = Math.toRadians( merchant_long - user_lang );
            double a = Math.sin( dLat / 2 ) * Math.sin( dLat / 2 ) +
                    Math.cos( Math.toRadians( user_lat ) ) * Math.cos( Math.toRadians( merchant_lat ) ) *
                            Math.sin( dLng / 2 ) * Math.sin( dLng / 2 );
            double c = 2 * Math.atan2( Math.sqrt( a ), Math.sqrt( 1 - a ) );
            float dist = (float) (earthRadius * c);

            return dist;
        } catch (Exception e) {
            return 0;
        }*/
    }

    private void setPhotos(String gym_id) {
        Map<String, Object> map = new HashMap<>();
        map.put( "image", "" );


        for (int a = 0; a < 10; a++) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection( "GYM LIST" )
                    .document( gym_id )
                    .collection( "Photos" )
                    .add( map );
        }
    }

    private void setFacilities(String gym_id) {

        List<String> list = new ArrayList<>();
        list.add( "Cardio Theatre" );
        list.add( "Changing Room" );
        list.add( "Free Parking" );
        list.add( "Lockers" );
        list.add( "Personal Training" );
        list.add( "Pilates" );

        for (int a = 0; a < list.size(); a++) {
            Map<String, Object> map = new HashMap<>();
            map.put( "isActive", true );
            map.put( "image", "" );
            map.put( "title", list.get( a ) );

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection( "GYM LIST" )
                    .document( gym_id )
                    .collection( "Facilities" )
                    .document( list.get( a ) )
                    .set( map );
        }
    }

    private void setTiming(String gym_id) {
        List<String> timing = new ArrayList<>();
        timing.add( "weekdays" );
        timing.add( "weekends" );
        for (int a = 0; a < timing.size(); a++) {
            Map<String, Object> map = new HashMap<>();
            map.put( "morning", "6:00 AM - 12:00 PM" );
            map.put( "evening", "2:00 PM - 10:00 PM" );
            map.put( "leave", "sunday evening" );
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection( "GYM LIST" )
                    .document( gym_id )
                    .collection( "Timing" )
                    .document( timing.get( a ) )
                    .set( map );
        }

    }

    private void setActivities(String gym_id) {
        List<String> list = new ArrayList<>();

        list.add( "Dance Aerobics" );
        list.add( "Aerobics Jazzercise" );
        list.add( "Step Aerobics" );
        list.add( "Power Yoga" );
        list.add( "Tai Chi" );
        list.add( "Dance Aerobics" );
        list.add( "Weight Gain" );
        list.add( "Spinning" );
        list.add( "Les Mills" );
        list.add( "Kettle bell Training" );
        list.add( "Certified Trainers" );
        list.add( "Stretching" );


        List<String> healtList = new ArrayList<>();
        healtList.add( "Benefits all age groups." );
        healtList.add( "Works your entire body." );
        healtList.add( "Improves cardiovascular health." );
        healtList.add( "Helps in weight loss." );
        Map<String, Object> map = new HashMap<>();
        map.put( "health_Benefits", healtList );
        map.put( "results", "Benefits all age groups." );
        map.put( "average_Calorie_Burn", "Benefits all age groups." );

        for (int a = 0; a < list.size(); a++) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection( "GYM LIST" )
                    .document( gym_id )
                    .collection( "activities" )
                    .document( list.get( a ) )
                    .set( map )
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText( context, "updated", Toast.LENGTH_SHORT ).show();
                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( context, "Can't updated " + e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
                }
            } );
        }


    }

    @Override
    public int getItemCount() {
        return gymList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, location, price, rating_count, distance;
        private RatingBar ratingBar;
        private ImageView gymImageView;
        private CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super( itemView );

            distance = (TextView) itemView.findViewById( R.id.textView22 );
            title = (TextView) itemView.findViewById( R.id.tutor_name );
            location = (TextView) itemView.findViewById( R.id.gym_location );
            price = (TextView) itemView.findViewById( R.id.gym_price );
            gymImageView = (ImageView) itemView.findViewById( R.id.tutor_image );
            rating_count = (TextView) itemView.findViewById( R.id.rating_count );
            cardView = (CardView) itemView.findViewById( R.id.home_cardView );
        }
    }
}
