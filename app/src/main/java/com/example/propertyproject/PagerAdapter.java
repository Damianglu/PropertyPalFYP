package com.example.propertyproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.propertyproject.databinding.ChatfragmentBinding;

public class PagerAdapter extends FragmentPagerAdapter {

    int tabcount = 3;


    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 1:
                return new statusFragment();
            case 2:
                return new callFragment();
            default:
                return new chatFragment();
        }
    }

    @Override
    public int getCount() {
        return tabcount;
    }
}
