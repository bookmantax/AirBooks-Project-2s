package com.logixcess.airbooks.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.logixcess.airbooks.MainActivity;
import com.logixcess.airbooks.Screens.CurrentTrip;
import com.logixcess.airbooks.Screens.History;
import com.logixcess.airbooks.Screens.Settings;
import com.logixcess.airbooks.Screens.TaxCalculate;
import com.logixcess.airbooks.Utils.Utils;

/**
 * Created by Usman on 06/08/2016.
 */

public class FixedTabsPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_TABS = 4;
    public FixedTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Utils.activityTitle = "Current Trip";
                MainActivity.setActionBarTitle();
                return new CurrentTrip();
            case 1:
                Utils.activityTitle = "Trip History";
                MainActivity.setActionBarTitle();
                return new History();
            case 2:
                Utils.activityTitle = "Per Diem Tax";
                MainActivity.setActionBarTitle();
                return new TaxCalculate();
            case 3:
                MainActivity.setActionBarTitle();
                Utils.activityTitle = "Settings";
                return new Settings();
            default:
                return  null;
        }
    }

    @Override
    public int getCount() {
        return NUM_TABS;
    }

}
