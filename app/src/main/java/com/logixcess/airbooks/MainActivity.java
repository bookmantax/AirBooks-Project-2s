package com.logixcess.airbooks;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.logixcess.airbooks.Adapters.FixedTabsPagerAdapter;
import com.logixcess.airbooks.Utils.Utils;

public class MainActivity extends AppCompatActivity{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ViewPager mViewPager;
    TabLayout tabLayout;
    static Toolbar toolbar;
    private static final int ALPHA_SELECTED = 255;
    private static final int ALPHA_UNSELECTED = 128;
    private static final int NUM_TABS = 4;



    /*Location System Starts*/
//    private static final int INITIAL_REQUEST=1337;
//    private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;
//    private static final String[] LOCATION_PERMS={
//            Manifest.permission.ACCESS_FINE_LOCATION
//    };
//    LocationManager locationManager;

    /*Location System Ends*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //final ActionBar actionBar = getActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mViewPager = (ViewPager) findViewById(R.id.container);
        PagerAdapter pagerAdapter = new FixedTabsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.trip);
        tabLayout.getTabAt(1).setIcon(R.drawable.history);
        tabLayout.getTabAt(2).setIcon(R.drawable.tax);
        tabLayout.getTabAt(3).setIcon(R.drawable.settings);
        selectIcon(0);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        Utils.activityTitle = "Current Trip";
                        MainActivity.setActionBarTitle();
                        selectIcon(position);
                        return;
                    case 1:
                        Utils.activityTitle = "Trip History";
                        MainActivity.setActionBarTitle();
                        selectIcon(position);
                        return;
                    case 2:
                        Utils.activityTitle = "Per Diem Tax";
                        MainActivity.setActionBarTitle();
                        selectIcon(position);
                        return;
                    case 3:
                        Utils.activityTitle = "Settings";
                        MainActivity.setActionBarTitle();

                        selectIcon(position);
                        return;
                    default:
                        return;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Will Search Per Diem", Snackbar.LENGTH_LONG)
                        .setAction("Search", null).show();
            }
        });


        // managing Location
//        if(Utils.currentapiVersion != Build.VERSION_CODES.M) {
//            try {
//                locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//            } catch (SecurityException ex) {
//            }
//        }else{
//            checkForPermission(Manifest.permission.ACCESS_FINE_LOCATION);
//        }


    }



    private void selectIcon(int position) {
        for (int i = 0; i < NUM_TABS; i++) {
            if (i == position) {
                tabLayout.getTabAt(i).getIcon().setAlpha(ALPHA_SELECTED);
            } else {
                tabLayout.getTabAt(i).getIcon().setAlpha(ALPHA_UNSELECTED);
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit")
                .setMessage("App will be Exit?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static void setActionBarTitle(){ toolbar.setTitle(Utils.activityTitle);
    }

    /*Location System*/

//    @TargetApi(Build.VERSION_CODES.M)
//    public void initLocation() {
//        try {
//            locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//        } catch (SecurityException ex) {
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.M)
//    public void checkForPermission(String permission){
//        if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(LOCATION_PERMS,LOCATION_REQUEST);
//        }
//    }
//
//
//    @Override
//    public void onLocationChanged(Location location) {
//        Toast.makeText(this,"Location is: "+ location.getLongitude() +","+location.getLatitude(),Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        Toast.makeText(this,provider + " is disabled!" , Toast.LENGTH_SHORT).show();
//    }
}
