package com.bookmantax.airbooks.Business;

import android.content.Context;

import com.bookmantax.airbooks.Adapters.DatabaseAdapter;
import com.bookmantax.airbooks.Database.DatabaseHelper;

/**
 * Created by Usman on 11/08/2016.
 */

public class PerDiem {
    String city,country;
    Context context;
    public PerDiem(String city,String country, Context context){
        this.city = city;
        this.country = country;
        this.context = context;
    }

    public PerDiem(){}
    public int getPerDiem(){
        DatabaseAdapter db =  DatabaseAdapter.getInstance(context);
        db.open(false);
        int perDiem = db.getPerDiemByOtherCountry(country);
        DatabaseAdapter.getInstance(context).close();
        return  perDiem;
    }

    public int getPerDiemByCity(){
        DatabaseAdapter db =  DatabaseAdapter.getInstance(context);
        db.open(false);
        int perDiem = db.getPerDiemByCity(city,country);
        DatabaseAdapter.getInstance(context).close();
        return  perDiem;
    }

    public double getPerDiemAllowanceToDate(Context context){
        DatabaseAdapter db =  DatabaseAdapter.getInstance(context);
        db.open(false);
        double perDiem = db.getPerDiemAllowanceToDate();
        DatabaseAdapter.getInstance(context).close();
        return  perDiem;
    }




}

