package com.e.arena;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class BuyCreditsFragment extends Fragment {


    public BuyCreditsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LayoutInflater inflater1=inflater.from(getContext());
        View view=inflater1.inflate(R.layout.fragment_buy_credits, container, false);

        return view;
    }

}
