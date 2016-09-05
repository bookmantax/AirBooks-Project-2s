package com.bookmantax.airbooks.Business;

import android.content.Context;

import com.bookmantax.airbooks.Adapters.DatabaseAdapter;
import com.bookmantax.airbooks.Utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * Created by Usman on 08/08/2016.
 */

public class Trip {
    int ID;
    String  location,lat,lng,city,date_in,date_out,per_diem;
    String airportCode;
    static boolean _isActive,_notificationStatus;

    public Trip() {

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String location) {
        this.city = location;
    }

    public String getDate_in() {
        return date_in;
    }

    public void setDate_in(String date_in) {
        this.date_in = date_in;
    }

    public String getDate_out() {
        return date_out;
    }

    public void setDate_out(String date_out) {
        this.date_out = date_out;
    }

    public String getPer_diem() {
        return per_diem;
    }

    public void setPer_diem(String per_diem) {
        this.per_diem = per_diem;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public static boolean is_isActive() {
        return _isActive;
    }

    public static void set_isActive(boolean _isActive) {
        Trip._isActive = _isActive;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public static boolean is_notificationStatus() {
        return _notificationStatus;
    }

    public static void set_notificationStatus(boolean _notificationStatus) {
        Trip._notificationStatus = _notificationStatus;
    }

    // Start New Trip
    public Trip start(String location,String city,String lat,String lng, String date_in, String date_out, String per_diem,boolean isActive,String airport,Context context){
        this.city = city;
        this.location = location;
        this.lat = lat;
        this.lng = lng;
        this.date_in = date_in;
        this.date_out = date_out;
        this.per_diem = per_diem;
        this.airportCode = airport;
        this._isActive = isActive;
        save(0,context);
        return this;
    }

    // Start New Trip
    public boolean save(int selector,Context context){
        if(selector == 0)
        {
            // temp trip
            DatabaseAdapter db =  DatabaseAdapter.getInstance(context);
            db.open(true);
            Utils.isTripActive = true;
            this._isActive = false;
            boolean response = db.insertTempTrip(this.location,this.city,this.lat,this.lng,this.date_in,this.airportCode,this.per_diem);
            if(response)
                Utils.currentTrip = this;
            DatabaseAdapter.getInstance(context).close();
            return response;

        }
        else if(selector == 1)
        {
            // Save Permanently
            DatabaseAdapter db =  DatabaseAdapter.getInstance(context);
            db.open(true);
            boolean response = db.insertTrip(this.location,this.city,this.lat,this.lng,this.date_in,this.date_out,this.airportCode,this.per_diem);
            DatabaseAdapter.getInstance(context).close();
            return response;
        }
        return false;
    }

    public boolean update(Context context,Trip obj){
        DatabaseAdapter db =  DatabaseAdapter.getInstance(context);
        db.open(true);
        boolean response = db.updateData(""+obj.ID,obj.location,obj.city,obj.lat,obj.lng,obj.date_in,obj.date_out,this.airportCode,obj.per_diem);
        DatabaseAdapter.getInstance(context).close();
        obj=null;
        return response;
    }

    // Cancel Current Trip
    public boolean cancel(Context context){
        boolean response = false;
        if(Utils.isTripActive) {
            this._isActive = false;
            Utils.isTripActive = false;
            DatabaseAdapter db = DatabaseAdapter.getInstance(context);
            db.open(true);
            response = db.deleteTempTrip(this.getID());
            DatabaseAdapter.getInstance(context).close();
            return response;
        }
        return response;
    }

    public boolean deleteTrip(Context context,Trip obj){

        DatabaseAdapter db =  DatabaseAdapter.getInstance(context);
        db.open(true);

        boolean response = db.deleteTrip(obj.getID());
        DatabaseAdapter.getInstance(context).close();
        return response;
    }

    public Trip checkForTrip(Context context){
        DatabaseAdapter db =  DatabaseAdapter.getInstance(context);
        db.open(false);
        Trip obj = db.getTempTripData();
        if(obj != null){
            this._isActive = true;
            Utils.isTripActive = true;
            Utils.currentTrip = obj;
        }
        DatabaseAdapter.getInstance(context).close();
        return obj;
    }

    public int checkForTripRules(Trip obj,Context context){
        Date startDate = Utils.parseDate(obj.getDate_in());
        int days = 0;
        if(startDate != null)
            days = Utils.getDaysDifference(startDate,Utils.parseDate(Utils.getDate()));
        return days;
    }

    public List<Trip> getAll(Context context){
        DatabaseAdapter db =  DatabaseAdapter.getInstance(context);
        db.open(false);
        List<Trip> objList = db.getAllTrips();
        if(objList != null){
            DatabaseAdapter.getInstance(context).close();
            return objList;
        }
        else {
            DatabaseAdapter.getInstance(context).close();
            return null;
        }
    }

    public Trip getSingle(Context context){
        DatabaseAdapter db =  DatabaseAdapter.getInstance(context);
        db.open(false);
        Trip obj = db.getSingleTrip();
        DatabaseAdapter.getInstance(context).close();
        return obj;
    }
    public Trip getSingle(int id,Context context){
        DatabaseAdapter db =  DatabaseAdapter.getInstance(context);
        db.open(false);
        Trip obj = db.getSingleTrip(id);
        DatabaseAdapter.getInstance(context).close();
        return obj;
    }

    public void setNotificationOff(Context context){
        DatabaseAdapter db =  DatabaseAdapter.getInstance(context);
        db.open(true);
        db.setNotificationOn();
        DatabaseAdapter.getInstance(context).close();

    }


}
