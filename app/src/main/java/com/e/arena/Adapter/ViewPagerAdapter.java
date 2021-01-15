package com.e.arena.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragments1=new ArrayList<>(  );
    private final List<String> Title=new ArrayList<>(  );
    public ViewPagerAdapter(FragmentManager fm) {
        super( fm );
    }

    @Override
    public Fragment getItem(int position) {
        return fragments1.get( position );
    }

    @Override
    public int getCount() {
        return Title.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return Title.get( position );
    }

    public void Addfragment(android.support.v4.app.Fragment fragment, String string){
        fragments1.add( fragment );
        Title.add( string);
    }
}
