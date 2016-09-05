package com.bookmantax.airbooks.Utils;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Usman on 13/08/2016.
 */

public class GoogleApiSystem implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {


    private GoogleApiClient mGoogleApiClient;
    LocationManager locationManager;
    public void connect(Context context){

        buildGoogleApiClient(context);
        mGoogleApiClient.connect();
    }

    public  boolean checkForSettings(){
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            return true;
        else
            return false;
    }




    public void disconnect(){
        if(mGoogleApiClient != null)
            if(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())
                mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Utils.itGooglePlayServicesInstalled = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Utils.itGooglePlayServicesInstalled = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Utils.itGooglePlayServicesInstalled = false;
    }


    public  synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
}
