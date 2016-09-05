package com.bookmantax.airbooks.Utils;

import android.Manifest;
import android.content.Context;
import android.location.LocationManager;
import android.support.v4.view.ViewPager;

import com.bookmantax.airbooks.Adapters.DatabaseAdapter;
import com.bookmantax.airbooks.Screens.CurrentTrip;
import com.bookmantax.airbooks.Screens.History;
import com.bookmantax.airbooks.Business.Trip;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by Usman on 06/08/2016.
 */

public class Utils {

    public static boolean isAppRunning = false;
    public static Trip currentTrip = null;
    public static boolean muteNotifications = false;
    public static boolean resultActivity = false;
    private LocationManager locationManager;
    public static ViewPager mViewPager;
    /*Permissions v >= 23*/
    public static final String[] PERMISSIONS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static String activityTitle = "";
    public static int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    public static boolean databaseDone = false;
    public static boolean itGooglePlayServicesInstalled = false;

    /*Current Trip*/
    public static boolean isTripActive = false;
    public static String date = "";
    public static int selector = -1;
    public static void updateDate(){
        CurrentTrip.setDate(selector);
    }
    public static void updateDate2(){
        History.setDate(selector);
    }

    public static Date parseDate(String date){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date startDate = null;
        try {
            startDate = df.parse(date);
            String newDateString = df.format(startDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate;
    }

    public static String getDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
    /* Manages Shared Prefs Code + Phone */
    public static String AIRPORT_CODE = "";
    public static String PHONE_NO = "";








    public static int getDaysDifference(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;


        // may be use them later somewhere
//        different = different % daysInMilli;
//        long elapsedHours = different / hoursInMilli;
//        different = different % hoursInMilli;
//
//        long elapsedMinutes = different / minutesInMilli;
//        different = different % minutesInMilli;
//
//        long elapsedSeconds = different / secondsInMilli;
        return Integer.parseInt("" + elapsedDays);
    }




    public static String buildDistanceWhereClause(double latitude, double longitude) {

        // see: // see: http://stackoverflow.com/questions/3695224/sqlite-getting-nearest-locations-with-latitude-and-longitude
        double fudge = Math.pow(Math.cos(Math.toRadians(latitude)),2);
        final String format = "((%1$s - %2$s) * (%3$s - %4$s) + (%5$s - %6$s) * (%7$s - %8$s) * %9$s)";
        final String selection = String.format(format,
                latitude, "LATITUDE",
                latitude, "LATITUDE",
                longitude, "LONGITUDE",
                longitude, "LONGITUDE", fudge
        );
        return selection;
    }


    // Haversine Formula to calculate distance between two locations by coordinates.
    public static String distance(double homeLat, double homeLong, double currentLat, double currentLong){

        int R = 6371; // km
        double dLat = toRadians(currentLat - homeLat);

        double dLon = toRadians(currentLong - homeLong);

        homeLat = toRadians(homeLat);
        currentLat = toRadians(currentLat);

        double a = sin(dLat/2) * sin(dLat/2) +
                sin(dLon/2) * sin(dLon/2) * cos(homeLat) * cos(currentLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        BigDecimal bd = new BigDecimal(R * c);
        bd = bd.setScale(6, RoundingMode.HALF_UP);

        return bd.toString();
    }

    public static double toRadians(Double deg) {
        return deg * (PI/180);
    }






    /*
    * This function will calculate the Per Diem per days
    *
    * */
    public static double perdiemAllowanceToDate = 0.0;
    public static double getTotalPerDiemPerDay(double perdiem, int days){
        return perdiem * days;
    }



    /*
    * This will check location service enabled or  not
    *
    * */

    public  boolean checkForSettings(Context context){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            return true;
        else
            return false;
    }

}
