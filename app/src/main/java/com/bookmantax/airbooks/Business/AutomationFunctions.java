package com.bookmantax.airbooks.Business;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bookmantax.airbooks.Adapters.FixedTabsPagerAdapter;
import com.bookmantax.airbooks.MainActivity;
import com.bookmantax.airbooks.R;
import com.bookmantax.airbooks.Utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Usman on 19/08/2016.
 */

public class AutomationFunctions extends Service implements LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    Context context;
    Trip currentTrip;
    Airports airportObj;

    private static Intent SERVICE_INTENT;

    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;
    public static boolean isRunning;
    Location location;


    /* For Notifications */
    Location mcurrentLocation;
    NotificationCompat.Builder mBuilder;
    private static int notificationID = 101;
    private Trip tempCurrentTrip;
    private double currentPerDiem = 0.0;
    public AutomationFunctions(Context context) {
        this.context = context;
        if (this.currentTrip == null)
            initializeCurrentTrip();
    }
    public AutomationFunctions(){super();}


    public boolean MainAutomationFunction(Location currentLocation) {
        mcurrentLocation = currentLocation;
        String where = Utils.buildDistanceWhereClause(currentLocation.getLatitude(), currentLocation.getLongitude());
        Airports airport = new Airports();
        airport = airport.getAirportUsingWhereClause(this.context, where);
        initializeCurrentTrip();

        if (airport != null) {
            // airport found
            airportObj = airport;
            //currentPerDiem = Double.parseDouble(airportObj.getPerDiem());
            if (checkForDistance(airport.getLat(),airport.getLng(),currentLocation) <= 120700.5) {
                // checking if distance is greater than 75 miles
                // if user is at home
                if(airport.getAirportCode().equalsIgnoreCase(Utils.AIRPORT_CODE)) {
                    if(this.currentTrip != null) {
                        int days = this.currentTrip.checkForTripRules(this.currentTrip, context);
                        makeDecisionOnTripDays(days, 0);
                    }
                    // user is at home therefore no need to start a trip
                    return false;
                }
                    if (this.currentTrip != null) {
                        if(currentTrip.getAirportCode() != null && !currentTrip.getAirportCode().equals("NONE")) {
                            if (!airport.getAirportCode().equalsIgnoreCase(this.currentTrip.getAirportCode())) {
                                this.tempCurrentTrip = checkForSameDayTravel();
                                if(this.tempCurrentTrip != null){
                                    // Example 3 fulfills
                                    moveToCurrentTrip(this.tempCurrentTrip);
                                    return true;
                                }
                                //airport locations are different
                                // EXAMPLE - 2 SATISFIED HERE --- total days of trip should be (total - 1)
                                Calendar c = Calendar.getInstance();
                                c.add(Calendar.DAY_OF_MONTH, -1);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                dateFormat.setTimeZone(c.getTimeZone());
                                this.currentTrip.setDate_out(dateFormat.format(c.getTime())); // assigning end date
                                int days = this.currentTrip.checkForTripRules(this.currentTrip, context);
                                makeDecisionOnTripDays(days, 1);
                                // current trip is ended here
                                // start new trip
                                beginNewTrip(currentLocation);
                            }
                            else{return false; }// user is on same trip }
                        }else{
                            // previous trip was without airport
//                            if (!airport.getAirportCode().equalsIgnoreCase(Utils.AIRPORT_CODE)) {
                                // EXAMPLE - 2 SATISFIED HERE---total days of trip should be (total - 1)
                                Calendar c = Calendar.getInstance();
                                c.add(Calendar.DAY_OF_MONTH, -1);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                dateFormat.setTimeZone(c.getTimeZone());
                                this.currentTrip.setDate_out(dateFormat.format(c.getTime())); // assigning end date
                                int days = this.currentTrip.checkForTripRules(this.currentTrip, context);
                                makeDecisionOnTripDays(days, 1);
                                // current trip is ended here
                                // start new trip
                                beginNewTrip(currentLocation);
//                            } else {
//                                int days = this.currentTrip.checkForTripRules(this.currentTrip, context);
//                                makeDecisionOnTripDays(days, 0); //
//                                // user is at home therefore no need to start a trip
//                            }
                        }
                    } else {
                        // current trip is not in progress
                        // record new trip as per user location and airport
                        beginNewTrip(currentLocation);
                    }

            }else{
                // airport found but distance is greater than 75 miles
                // so add the trip based on locality of that specific area
                List<Address> addresses = getLocationInfo(currentLocation);
                if(currentTrip == null){
                    // start trip without airport

                    if (addresses != null)
                        beginNewTripWithoutAirport(currentLocation, addresses);
                    else {
                        // nothing to record a trip
                    }
                }else{
                    if(checkForDistance(Double.parseDouble(this.currentTrip.getLat())
                            ,Double.parseDouble(this.currentTrip.getLng()),currentLocation)
                            > 120700.5) {
                        //PerDiem objPerDiem = new PerDiem(addresses.get(0).getLocality(), addresses.get(0).getCountryName(), this.context);
                        //currentPerDiem = Double.parseDouble("" + objPerDiem.getPerDiem());
                        int days = this.currentTrip.checkForTripRules(this.currentTrip, context);
                        makeDecisionOnTripDays(days, 0);
                        beginNewTripWithoutAirport(currentLocation, addresses);
                    }
                }

            }
        } else {
            // no airport found
            // use current locality to log into the database and save it without airport code
            List<Address> addresses = getLocationInfo(currentLocation);
            if (this.currentTrip != null) {
                    if(addresses != null){
                        if(checkForDistance(Double.parseDouble(this.currentTrip.getLat())
                                ,Double.parseDouble(this.currentTrip.getLng()),currentLocation)
                                > 120700.5) {

                            PerDiem objPerDiem = new PerDiem(addresses.get(0).getLocality(), addresses.get(0).getCountryName(), this.context);
                            //currentPerDiem = Double.parseDouble("" + objPerDiem.getPerDiem());
                            int days = this.currentTrip.checkForTripRules(this.currentTrip, context);
                            makeDecisionOnTripDays(days, 0);
                            beginNewTripWithoutAirport(currentLocation, addresses);
                        }
                    }else{
                        // nothing found to begin a trip
                    }
                } else {
                    // current trip is not in progress
                    // record new trip as per user location and locality
                    if(addresses != null){
                        PerDiem objPerDiem = new PerDiem(addresses.get(0).getLocality(), addresses.get(0).getCountryName(), this.context);
                        //currentPerDiem = Double.parseDouble("" + objPerDiem.getPerDiem());
                        beginNewTripWithoutAirport(currentLocation,addresses);
                    }
                    else{
                        // nothing found to begin a trip
                    }
                }
            }
        return false;
    }

    private void moveToCurrentTrip(Trip tempCurrentTrip) {
        this.currentTrip.cancel(this.context);
        tempCurrentTrip.save(0,this.context);
    }

    private Float checkForDistance(Double lat,Double lng,Location currentLocation){
        Location airportLocation = new Location("");
        airportLocation.setLatitude(lat);
        airportLocation.setLongitude(lng);
        Float distanceInMetres = airportLocation.distanceTo(currentLocation);
        return distanceInMetres;
    }

    private Trip checkForSameDayTravel(){
        if(this.currentTrip.getDate_in().equals(Utils.getDate())){
            if(airportObj != null){
                if(!this.currentTrip.getAirportCode().equals(airportObj.getAirportCode())) {
                    return checkForCurrentDayHistory();
                }
            }
        }
        return null;
    }

    public Trip checkForCurrentDayHistory(){
        Trip fromHistory = this.currentTrip.getSingle(this.context);
        if(fromHistory != null){
            if(!airportObj.getAirportCode().equals(Utils.AIRPORT_CODE)) {
                Calendar c = Calendar.getInstance();
                c.setTime(Utils.parseDate(fromHistory.getDate_out()));
                if(c.equals(Utils.getDate()) && fromHistory.getAirportCode().equalsIgnoreCase(airportObj.getAirportCode())){
                    // the user is back to previous city trip, get that and set as current trip.
                    return fromHistory;
                }
                else {
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    if (c.equals(Utils.getDate()) && fromHistory.getAirportCode().equalsIgnoreCase(airportObj.getAirportCode())) {
                        // the user is back to previous city trip, get that and set as current trip.
                        return fromHistory;
                    }
                }
            }
        }
        return null;
    }



    private List<Address> getLocationInfo(Location currentLocation) {
        Geocoder gcd = new Geocoder(this.context, Locale.getDefault());
        List<Address> addresses = null;
        if (gcd.isPresent())
            try {
                addresses = gcd.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (addresses.size() > 0)
            return addresses;
        else
            return null;
    }

    private boolean beginNewTrip(Location currentLocation) {
        Trip newTrip = new Trip();
        if(airportObj != null) {
            // All done Now begin new trip
            newTrip.setLocation(airportObj.getAddress());
            newTrip.setLat("" + currentLocation.getLatitude());
            newTrip.setLng("" + currentLocation.getLongitude());
            newTrip.setDate_in(Utils.getDate());
            newTrip.setAirportCode(airportObj.getAirportCode());
            // getting Per diem
            //PerDiem objPerDiem = new PerDiem(airportObj.getCity(), airportObj.getCountry(), this.context);
            newTrip.setPer_diem("" + airportObj.getPerDiem());
            newTrip.setCity(airportObj.getCity());
            if (newTrip.save(0, this.context)) {
                //Toast.makeText(this.context, "New "+ newTrip.getCity() +" Trip has been started", Toast.LENGTH_SHORT).show();
                this.currentTrip = newTrip;
                newTrip = null;
                return true;
            }
        }
        return false;
    }

    private boolean beginNewTripWithoutAirport(Location currentLocation,List<Address> addresses) {
        Trip newTrip = new Trip();
        if(airportObj != null) {
            // All done Now begin new trip
            newTrip.setLocation(addresses.get(0).getAddressLine(1));
            newTrip.setLat("" + currentLocation.getLatitude());
            newTrip.setLng("" + currentLocation.getLongitude());
            newTrip.setDate_in(Utils.getDate());

            // getting Per diem
            PerDiem objPerDiem = new PerDiem(addresses.get(0).getLocality(), addresses.get(0).getCountryName(), this.context);
            currentPerDiem = Double.parseDouble("" + objPerDiem.getPerDiem());
            if(objPerDiem != null) {
                newTrip.setPer_diem("" + objPerDiem.getPerDiem());
            }
            newTrip.setCity(addresses.get(0).getLocality());
            if (newTrip.save(0, this.context)) {
               // Toast.makeText(this.context, "New "+ newTrip.getCity() +" Trip has been started", Toast.LENGTH_SHORT).show();
                this.currentTrip = newTrip;
                newTrip = null;
                return true;
            }
        }
        return false;
    }


    public boolean makeDecisionOnTripDays(int days,int selector){
        if (days > 0)
        {
            // empty temp trip table
            // if that was >= one day then log it into database (TRIP HISTORY)
            currentPerDiem = getPerDiem();
            this.currentTrip.setPer_diem(String.valueOf(Utils.getTotalPerDiemPerDay(currentPerDiem,days)));
            if(selector == 0)
                this.currentTrip.setDate_out(Utils.getDate());
            if(this.currentTrip.save(1,context))
            if(this.currentTrip.cancel(context)){
                Utils.currentTrip = null;
                if(Utils.isAppRunning)
                {
                    if(Utils.mViewPager != null) {
                        Fragment f = ((FixedTabsPagerAdapter) Utils.mViewPager.getAdapter()).getFragment(0);
                        if (f != null)
                            f.onResume();
                    }
                }
            }
            return true;
        }
        else
        {
            // empty temp trip table
            this.currentTrip.cancel(context);
        }
        return false;
    }



    public void alarmAtMorning(){
        // check for
        // pending notifications of current trip
        if(this.currentTrip  == null){
            if(initializeCurrentTrip())
                morningNotification();
        }
        else
            morningNotification();

    }

    public void alarmAtNight(Location location){
        if(!location.equals(null))
            MainAutomationFunction(location);
    }


    // check for notifications status
    public void morningNotification(){
        if(!this.currentTrip.is_notificationStatus()){
            // notification is not showed yet
            showNotification("New Trip","New "+this.currentTrip.getCity()+" Trip has been started",1);
            this.currentTrip.setNotificationOff(this.context);
        }
    }

    // manage notifications
    private void showNotification(String title,String content,int selector){
        if(!Utils.muteNotifications) {
            NotificationCompat.Action action;
            NotificationCompat.Action action2;
            Intent resultIntent = new Intent(this.context, MainActivity.class);
            Intent tripTypeIntent = new Intent(this.context, MainActivity.class);
            Intent tripTypeIntent2 = new Intent(this.context, MainActivity.class);
            tripTypeIntent.putExtra("Value", 1);
            tripTypeIntent2.putExtra("Value", 0);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this.context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            PendingIntent actionOkPendingIntent =
                    PendingIntent.getActivity(
                            this.context,
                            0,
                            tripTypeIntent2,
                            PendingIntent.FLAG_ONE_SHOT
                    );
            PendingIntent actionCancelPendingIntent =
                    PendingIntent.getActivity(
                            this.context,
                            0,
                            tripTypeIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );

            if (selector == -1) {
                action = new NotificationCompat.Action(R.drawable.trip, "Turn Location on", resultPendingIntent);
                mBuilder =
                        new NotificationCompat.Builder(this.context)
                                .setSmallIcon(R.drawable.location)
                                .setContentTitle(title)
                                .addAction(action)
                                .setContentText(content);
            } else {
                action = new NotificationCompat.Action(R.drawable.trip, "Work Trip", actionOkPendingIntent);
                action2 = new NotificationCompat.Action(R.drawable.delete, "Personal", actionCancelPendingIntent);

                mBuilder = new NotificationCompat.Builder(this.context)
                        .setSmallIcon(R.drawable.location)
                        .setContentTitle(title)
                        .addAction(action)
                        .addAction(action2)
                        .setContentText(content);
            }

            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotifyMgr =
                    (NotificationManager) this.context.getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(notificationID, mBuilder.build());

        }
    }

    public boolean initializeCurrentTrip(){
        if(Utils.currentTrip != null) {
            this.currentTrip = Utils.currentTrip;
            return true;
        }
        this.currentTrip = new Trip();
        if ((this.currentTrip = this.currentTrip.checkForTrip(this.context)) != null) {
            // current trip is in progress
            return true;
        }
        else return false;
    }

    private double getPerDiem(){
        if(currentTrip.getAirportCode() != null) {
            Airports airports = new Airports();
            airports = airports.getAirportByCode(this.context,currentTrip.getAirportCode());
            if(airports != null) {
                currentTrip.setPer_diem(airports.getPerDiem());
                return Double.parseDouble(airports.getPerDiem());
            }
        }else{
            List<Address> address = getLocationInfo(mcurrentLocation);
            if(address != null) {
                PerDiem perDiem = new PerDiem(currentTrip.getCity(), address.get(0).getCountryName(), this.context);
                return perDiem.getPerDiem();
            }
        }
        return 0;
    }


    public void initialize() {

        if (Utils.itGooglePlayServicesInstalled) {
            if (isLocationServiceAvaiable()) {
                buildGoogleApiClient();
                mGoogleApiClient.connect();
                isRunning = true;
            }
        }
    }

    private boolean isLocationServiceAvaiable() {
        // create result Acitivity to check for location service availability
        Utils utils = new Utils();
        if(utils.checkForSettings(this.context))
            return true;
        else if(utils.isAppRunning){
            MainActivity mainActivity = new MainActivity();
            mainActivity.showSettingsAlert();
        }
        return false;
    }


    public  void closeAll(){
        isRunning = false;
        mLocationRequest = null;
        mLastLocation = null;
        mGoogleApiClient.disconnect();
        if(SERVICE_INTENT != null)
            this.context.stopService(SERVICE_INTENT);

    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);

        this.context = getApplicationContext();
        SERVICE_INTENT = intent;
        initialize();
        return START_STICKY;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // no permission granted
            return;
        }
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                lat = String.valueOf(mLastLocation.getLatitude());
                lon = String.valueOf(mLastLocation.getLongitude());

            }else if(Utils.isAppRunning){
                //showSettingsAlert();
            }
        }
        else{}
        //showNotification("Location Service","Location is is Turned off",-1);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
        this.location = location;
        if(MainAutomationFunction(location))
            this.closeAll();
        else
            this.closeAll();
    }


    synchronized void buildGoogleApiClient() {
        if(this.context != null)
            mGoogleApiClient = new GoogleApiClient.Builder(this.context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
    }




}
