package com.bookmantax.airbooks;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bookmantax.airbooks.Adapters.DatabaseAdapter;
import com.bookmantax.airbooks.Adapters.FixedTabsPagerAdapter;
import com.bookmantax.airbooks.Business.AutomationFunctions;
import com.bookmantax.airbooks.Business.PerDiem;
import com.bookmantax.airbooks.Business.TaskScheduler;
import com.bookmantax.airbooks.Business.Trip;
import com.bookmantax.airbooks.Utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivity extends AppCompatActivity{

    private ViewPager mViewPager;
    TabLayout tabLayout;
    static Toolbar toolbar;
    private static final int ALPHA_SELECTED = 255;
    private static final int ALPHA_UNSELECTED = 128;
    private static final int NUM_TABS = 4;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES_ERROR = 1900;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            startRegistrationService();
        }
        if(this.getIntent().hasExtra("Value")) {
            if (this.getIntent().getIntExtra("Value",-1) == 1) {
                Trip trip = new Trip();
                trip = trip.checkForTrip(this);
                if (trip != null)
                    if (trip.cancel(this))
                        if (Utils.currentTrip != null) Utils.currentTrip = null;
                Toast.makeText(this, "Trip has been cancelled!", Toast.LENGTH_SHORT).show();

                if (Utils.isAppRunning) {
                    if (Utils.mViewPager != null) {
                        Fragment f = ((FixedTabsPagerAdapter) Utils.mViewPager.getAdapter()).getFragment(0);
                        if (f != null)
                            f.onResume();
                    }
                }
                else return;

            } else if (this.getIntent().getIntExtra("Value",-1) == 0) {
                if (!Utils.isAppRunning) return;
            }
        }
        Utils.isAppRunning = true;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mViewPager = (ViewPager) findViewById(R.id.container);
        PagerAdapter pagerAdapter = new FixedTabsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        Utils.mViewPager = mViewPager;
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.trip);
        tabLayout.getTabAt(1).setIcon(R.drawable.history);
        tabLayout.getTabAt(2).setIcon(R.drawable.tax);
        tabLayout.getTabAt(3).setIcon(R.drawable.settings);
        selectIcon(0);
        //exportDatabse("Airbooks_DB");


        Utils utils = new Utils();
        if(!utils.checkForSettings(this.getApplicationContext())) {
            showSettingsAlert();
            Utils.resultActivity = true;
        }
        else {
            Utils.resultActivity = false;
         //   new TaskSchedulingProcess().execute();
            beginAutomation();
        }
        utils = null;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        Utils.activityTitle = "Current Trip";
                        if (!Utils.resultActivity) {
                            Fragment f = ((FixedTabsPagerAdapter) mViewPager.getAdapter()).getFragment(position);
                            if (f != null)
                                f.onResume();
                        }
                        MainActivity.setActionBarTitle();
                        selectIcon(position);
                        return;
                    case 1:
                        Utils.activityTitle = "Trip History";
                        MainActivity.setActionBarTitle();
                        if (!Utils.resultActivity) {
                            Fragment f2 = ((FixedTabsPagerAdapter) mViewPager.getAdapter()).getFragment(position);
                            if (f2 != null)
                                f2.onResume();
                        }
                        selectIcon(position);
                        return;
                    case 2:
                        Utils.activityTitle = "Per Diem Tax";
                        MainActivity.setActionBarTitle();
                        if (!Utils.resultActivity){
                            Fragment f3 = ((FixedTabsPagerAdapter) mViewPager.getAdapter()).getFragment(position);
                        if (f3 != null)
                            f3.onResume();
                        }
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




        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                        "com.bookmantax.airbooks", Context.MODE_PRIVATE);

                if(!sharedPreferences.contains("DatabaseDone")){
                    DatabaseAdapter db =  DatabaseAdapter.getInstance(getApplicationContext());
                    db.open(false);
                    if(Utils.databaseDone)
                        sharedPreferences.edit().putString("DatabaseDone","Yes").apply();
                    DatabaseAdapter.getInstance(getApplicationContext()).close();
                }
                else
                    Utils.databaseDone =true;

            }
        else
            Toast.makeText(this, "Please grant all Permissions", Toast.LENGTH_SHORT).show();


    }


    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
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





    // checks for google play services

    private void startRegistrationService() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            onActivityResult(REQUEST_GOOGLE_PLAY_SERVICES, Activity.RESULT_OK, null);
            Utils.itGooglePlayServicesInstalled = true;
        } else if (api.isUserResolvableError(code) &&
                api.showErrorDialogFragment(this, code, REQUEST_GOOGLE_PLAY_SERVICES_ERROR)) {
            // wait for onActivityResult call (see below)
            Utils.itGooglePlayServicesInstalled = false;
            Toast.makeText(this,"Not Found",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, api.getErrorString(code), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            Utils.itGooglePlayServicesInstalled = true;
        }
        else
            Utils.itGooglePlayServicesInstalled = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {

                }

                break;
            case REQUEST_GOOGLE_PLAY_SERVICES_ERROR:
                Toast.makeText(this,"Not Found",Toast.LENGTH_SHORT).show();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms&hl=en")));
                }
                finish();
                break;
            case 1002:
                Utils utils = new Utils();
                if(utils.checkForSettings(this.getApplicationContext()))
                        beginAutomation();   //new TaskSchedulingProcess().execute();

                utils = null;
                Utils.resultActivity = false;
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void beginAutomation() {
        PerDiem perDiem = new PerDiem();
        Utils.perdiemAllowanceToDate = perDiem.getPerDiemAllowanceToDate(getApplicationContext());
        if(Utils.itGooglePlayServicesInstalled) {
            if(!AutomationFunctions.isRunning)
                startService(new Intent(getApplicationContext(), AutomationFunctions.class));
            TaskScheduler taskScheduler = new TaskScheduler();
            taskScheduler.SetAlarm(getApplicationContext());
        }else
            Toast.makeText(getApplicationContext(),"Google play services are missing, Features will not work",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        Utils.isAppRunning = false;
    }

    AlertDialog dialog;
    /**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     * */
    public void showSettingsAlert() {

        dialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Location Service")
                .setMessage("Please activate location service")
                .setPositiveButton("Activate", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent,1002);
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (dialog!=null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }


    class TaskSchedulingProcess extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String...params) {
//            PerDiem perDiem = new PerDiem();
//            Utils.perdiemAllowanceToDate = perDiem.getPerDiemAllowanceToDate(getApplicationContext());
//            if(Utils.itGooglePlayServicesInstalled) {
//                if(!AutomationFunctions.isRunning)
//                    startService(new Intent(getApplicationContext(), AutomationFunctions.class));
//                TaskScheduler taskScheduler = new TaskScheduler();
//                taskScheduler.SetAlarm(getApplicationContext());
//            }else
//                Toast.makeText(getApplicationContext(),"Google play services are missing, Features will not work",Toast.LENGTH_SHORT).show();
            return "";
        }


        protected void onPostExecute(String value) {
            // TODO: check this.exception
            // TODO: do something with the feed

        }
    }

}
