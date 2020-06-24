package com.example.limbitlesssummerproject19.Album;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

/*
    This class is is the adapter for the Table Layout on top of the screen! (Where you see "All" and "Starred")
 */
public class GalleryPagerAdapter extends FragmentStateAdapter {



    public GalleryPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return new FragmentAllAlbums();
            case 1:
                return new FragmentStarredAlbums();
            default:
                return new FragmentAllAlbums();
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
