package com.e.arena.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.arena.BuyCredits;
import com.e.arena.Model.Banner;
import com.e.arena.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

    List<Banner> bannerList;
    private Context context;

    public SliderAdapterExample(List<Banner> bannerList, Context context) {
        this.bannerList = bannerList;
        this.context = context;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        viewHolder.textViewDescription.setText("This is slider item " + position);
        String image_url=bannerList.get( position ).getImage();
        if (!image_url.equals( "" ) && image_url!=null)
            Picasso.with( context ).load( image_url ).into( viewHolder.imageViewBackground );
        viewHolder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity( new Intent( context, BuyCredits.class ) );
            }
        } );
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return bannerList.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }
}