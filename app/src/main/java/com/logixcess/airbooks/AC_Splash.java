package com.logixcess.airbooks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.logixcess.airbooks.Utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Usman on 06/08/2016.
 */

public class AC_Splash extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_splash);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                        "com.logixcess.airbooks", Context.MODE_PRIVATE);
                Intent main;
                if(sharedPreferences.contains("AirportCode"))
                {
                    Utils.AIRPORT_CODE = sharedPreferences.getString("AirportCode","");
                    Utils.PHONE_NO = sharedPreferences.getString("PhoneNo","");

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
