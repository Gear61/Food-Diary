package com.randomappsinc.foodjournal.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.randomappsinc.foodjournal.fragments.PictureFullViewFragment;

import java.util.List;

public class PictureFullViewGalleryAdapter extends FragmentStatePagerAdapter {

    private List<String> mImagePaths;

    public PictureFullViewGalleryAdapter(FragmentManager fragmentManager, List<String> imageUrls) {
        super(fragmentManager);
        mImagePaths = imageUrls;
    }

    public String getImagePath(int position) {
        return mImagePaths.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return PictureFullViewFragment.newInstance(mImagePaths.get(position));
    }

    @Override
    public int getCount() {
        return mImagePaths.size();
    }
}
