package com.bookmantax.airbooks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bookmantax.airbooks.Utils.Utils;
import com.crashlytics.android.Crashlytics;
import com.bookmantax.airbooks.R;

import io.fabric.sdk.android.Fabric;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Usman on 06/08/2016.
 */

public class AC_Splash extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.fragment_splash);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                        "com.bookmantax.airbooks", Context.MODE_PRIVATE);
                Intent main;
                if(sharedPreferences.contains("Airport_Code"))
                {
                    Utils.AIRPORT_CODE = sharedPreferences.getString("Airport_Code","");
                    Utils.PHONE_NO = sharedPreferences.getString("PhoneNo","");
                    if(sharedPreferences.getString("MuteNotifications","").contains("Mute")) {
                        Utils.muteNotifications = true;
                    }
                    else {
                        Utils.muteNotifications = false;
                    }
                    main = new Intent(getApplicationContext(),MainActivity.class);
                }
                else
                    main = new Intent(getApplicationContext(),AC_Signup.class);
                startActivity(main);
                finish();
            }
        },3000);












    }



}
