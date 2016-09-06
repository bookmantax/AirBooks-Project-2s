package com.bookmantax.airbooks.Business;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.bookmantax.airbooks.Utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.bookmantax.airbooks.MainActivity;

import java.util.Calendar;


/**
 * Created by Usman on 12/08/2016.
 */

public class TaskScheduler extends BroadcastReceiver {
    Context context;

    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            // Set the alarm here.
            this.CancelAlarm(context);
            TaskScheduler taskScheduler = new TaskScheduler();
            taskScheduler.SetAlarm(context);
            enableReciever(context);
        }
        checkForAlarmType(intent);
    }

    // check for intent values morning or night
    private void checkForAlarmType(Intent intent){
        int value = -1;
        // checking for 9:00 or 23:55
        Toast.makeText(this.context,"Recieved",Toast.LENGTH_SHORT).show();
        if(intent.hasExtra("Night"))
            if((value = intent.getIntExtra("Night",-1)) != -1)
            {
                if(value == 1){
                    // its night
                    Intent serviceIntent = new Intent(this.context, AutomationFunctions.class);
                    if(!AutomationFunctions.isRunning)
                        this.context.startService(serviceIntent);
                    else{
                        if(!Utils.isAppRunning)
                            this.context.stopService(serviceIntent);
                        this.context.startService(serviceIntent);
                    }
                }else if(value == 0){
                    // its morning
                    AutomationFunctions automationFunctions = new AutomationFunctions(this.context);
                    automationFunctions.alarmAtMorning();
                    // showNotification("New Trip has Started","A trip to " + this.currentTrip.getCity() + " has been started.",1);
                }
            }
        Log.e("Yayyyy!","Yess We are in");
    }




    public void SetAlarm(Context context)
    {
        try{
            this.context = context;
            AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, TaskScheduler.class);
            Intent imorning = new Intent(context, TaskScheduler.class);
            boolean alarmUp = (PendingIntent.getBroadcast(context, 0,
                    i,
                    PendingIntent.FLAG_NO_CREATE) != null);
            boolean alarmUp2 = (PendingIntent.getBroadcast(context, 0,
                    imorning,
                    PendingIntent.FLAG_NO_CREATE) != null);
            if (alarmUp && alarmUp2)
                return;
            i.putExtra("Night",1);
            imorning.putExtra("Night",0);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent piMorning = PendingIntent.getBroadcast(context, 0, imorning, PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY,23);
            c.set(Calendar.MINUTE,55);
            c.set(Calendar.SECOND,00);
            Calendar c2 = Calendar.getInstance();
            c2.set(Calendar.HOUR_OF_DAY,9);
            c2.set(Calendar.MINUTE,00);
            c2.set(Calendar.SECOND,00);
            am.cancel(pi);
            am.cancel(piMorning);
            am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pi); // Millisec * Second * Minute
            am.setRepeating(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(),AlarmManager.INTERVAL_DAY, piMorning); // Millisec * Second * Minute

        }
        catch (Exception e){
            Log.e("Err",e.getMessage());
        }
    }

    public void CancelAlarm(Context context)
    {
        try {
            Intent intent = new Intent(context, TaskScheduler.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(sender);
        }catch (Exception e){Log.e("Err",e.getMessage());}
    }

    public void enableReciever(Context context){
        ComponentName receiver = new ComponentName(context, TaskScheduler.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

}
