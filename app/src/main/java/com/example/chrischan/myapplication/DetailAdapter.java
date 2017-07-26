package com.example.chrischan.myapplication;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by chrischan on 4/18/17.
 */

public class DetailAdapter extends FragmentStatePagerAdapter {
    private Context mContext;
    private final String[] titles = {"Alums", "Posts"};
    private final int PAGE_COUNT = 2;


    public DetailAdapter(Context context, FragmentManager fm){
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position ==0)
            return AlbumsFragment.newInstance(position);
        else
            return PostsFragment.newInstance(position);
    }

    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
