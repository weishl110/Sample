package com.sample.app.adapter;


import android.app.Fragment;
import android.app.FragmentManager;

import com.sample.app.BaseFragment;

import java.util.ArrayList;

/**
 * Created by ${wei} on 2017/5/31.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<BaseFragment> fragments;

    public MainPagerAdapter(FragmentManager fm, ArrayList<BaseFragment> fragments) {
        super(fm);
        if (fragments == null) {
            throw new IllegalArgumentException("fragments不能为空");
        }
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
