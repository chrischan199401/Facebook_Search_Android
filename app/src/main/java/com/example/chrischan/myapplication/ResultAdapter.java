package com.example.chrischan.myapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chrischan on 4/16/17.
 */

public class ResultAdapter extends FragmentStatePagerAdapter {

    private static final int PAGE_COUNT = 5;
    private String[] titles = new String[]{"Users", "Pages", "Events", "Places", "Groups"};
    private Context mContext;
    private boolean isFavs;

    public ResultAdapter(Context context, FragmentManager fm, boolean isFavs){
        super(fm);
        mContext = context;

        this.isFavs = isFavs;
    }

    @Override
    public Fragment getItem(int position) {

        return ResultFragment.newInstance(position, isFavs);



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
