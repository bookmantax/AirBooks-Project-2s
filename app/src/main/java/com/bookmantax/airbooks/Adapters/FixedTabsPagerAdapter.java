package com.bookmantax.airbooks.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.bookmantax.airbooks.Screens.CurrentTrip;
import com.bookmantax.airbooks.Screens.History;
import com.bookmantax.airbooks.Screens.Settings;
import com.bookmantax.airbooks.Screens.TaxCalculate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Usman on 06/08/2016.
 */

public class FixedTabsPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_TABS = 4;
    private Map<Integer,String> fragmentInfo;
    private FragmentManager fragmentManager;
    public FixedTabsPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentManager = fm;
        fragmentInfo = new HashMap<Integer, String>();
    }






    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new CurrentTrip();
            case 1:
                return new History();
            case 2:
                return new TaxCalculate();
            case 3:
                return new Settings();
            default:
                return  null;
        }
    }

    @Override
    public int getCount() {
        return NUM_TABS;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Object  obj = super.instantiateItem(container,position);
        if(obj instanceof Fragment){
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            fragmentInfo.put(position,tag);
        }
        return obj;
    }

    public Fragment getFragment(int position){
        String tag = fragmentInfo.get(position);
        if(tag == null)
            return null;
        return fragmentManager.findFragmentByTag(tag);
    }
}
