package com.jameschamberlain.footballteamtracker.stats;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.jameschamberlain.footballteamtracker.R;

public class TabAdapter extends FragmentPagerAdapter {

    private final int PAGE_COUNT = 2;
    private Context context;

    /**
     * Create a new {@link TabAdapter} object.
     *
     * @param fm is the fragment manager that will keep each fragment's state in the adapter
     *           across swipes.
     */
    TabAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new GoalsFragment();
        } else {
            return new AssistsFragment();
        }
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.goals);
        } else {
            return context.getString(R.string.assists);
        }
    }


    /**
     * Return the total number of pages.
     */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
