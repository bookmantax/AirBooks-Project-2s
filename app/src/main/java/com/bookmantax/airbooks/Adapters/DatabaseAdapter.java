package com.bookmantax.airbooks.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import com.bookmantax.airbooks.Database.DatabaseHelper;
import com.bookmantax.airbooks.Business.Airports;
import com.bookmantax.airbooks.Business.Trip;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Usman on 11/08/2016.
 */


public class DatabaseAdapter {


    private static DatabaseAdapter instance;
    private static DatabaseHelper openHelper;
    private  SQLiteDatabase database;



    private static final String AIRPORT_NAME = "airport";
    //private static final String USER_TABLE_NAME = "user_info_table";
    private static final String AIRPORTS_TABLE_NAME = "airportnew";
    private static final String PER_DIEM_TABLE_NAME = "perdiem_table";
    private static final String TRIP_HISTORY_TABLE_NAME = "trip_history_table";
    private static final String TEMP_TRIP_TABLE_NAME = "temp_trip_table";



    //Trip History Variables Declaration
    static final String _ID = "_ID";
    static final String CITY = "city";
    static final String LOCATION = "location";
    static final String DATE_IN = "date_in";
    static final String DATE_OUT = "date_out";
    static final String PER_DIEM = "per_diem";
    static final String LATITUDE = "latitude";
    static final String LONGITUDE = "longitude";


    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAdapter(Context context) {
        this.openHelper = new DatabaseHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAdapter(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open(boolean toWrite) {
        if(toWrite)
            this.database = openHelper.getWritableDatabase();
        else
            this.database = openHelper.getReadableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }



    /**
     * Add trip to the Database
     */
    public boolean insertTempTrip(String location,String city,String lat,String lng, String dateIn,String airport, String perDiem){
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOCATION, location);
        contentValues.put(CITY, city);
        contentValues.put(LATITUDE, lat);
        contentValues.put(LONGITUDE, lng);
        contentValues.put(DATE_IN, dateIn);
        contentValues.put(PER_DIEM, perDiem);
        contentValues.put("AIRPORT",airport);
        long result = -1;
        try {
            result = database.insert(TEMP_TRIP_TABLE_NAME, null, contentValues);
        }catch (Exception e){
            Log.e("ERR",e.getMessage());}
        finally {
        }
        return result != -1;
    }

    public boolean insertTrip(String location,String city,String lat,String lng, String dateIn, String dateOut,String airport, String perDiem){

        ContentValues contentValues = new ContentValues();
        contentValues.put(LOCATION, location);
        contentValues.put(CITY, city);
        contentValues.put(LATITUDE, lat);
        contentValues.put(LONGITUDE, lng);
        contentValues.put(DATE_IN, dateIn);
        contentValues.put(DATE_OUT, dateOut);
        contentValues.put("AIRPORT",airport);
        contentValues.put(PER_DIEM, perDiem);
        long result = -1;
        try {
            result = database.insert(TRIP_HISTORY_TABLE_NAME, null, contentValues);
        }catch (Exception e){
            Log.e("ERR",e.getMessage());}
        finally {
        }
        return result != -1;
    }


    public boolean updateData(String id, String location,String city,String lat,String lng, String dateIn, String dateOut,String airport, String perDiem) {

        String[] args = {String.valueOf(id)};
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOCATION, location);
        contentValues.put(CITY, city);
        contentValues.put(LATITUDE, lat);
        contentValues.put(LONGITUDE, lng);
        contentValues.put(DATE_IN, dateIn);
        contentValues.put(DATE_OUT, dateOut);
        contentValues.put(PER_DIEM, perDiem);
        contentValues.put("AIRPORT",airport);
        long result = -1;
        try {
            result = database.update(TRIP_HISTORY_TABLE_NAME, contentValues, "_ID=?", args);
        }catch (Exception e){
            Log.e("ERR",e.getMessage());}
        finally {
        }
        return result != -1;

    } // end of updatetData

    public boolean deleteTempTrip(int id){
        try {
            database.execSQL("DELETE FROM temp_trip_table");
        }catch (Exception e){}
        finally {
        }
        return true;
    }

    public boolean deleteTrip(int id){
        long result = -1;
        try{
            result = database.delete(TRIP_HISTORY_TABLE_NAME,_ID +" = ?",new String[]{""+id});
        }catch (Exception e){}
        finally {
        }
        return result != -1;
    }

    public boolean runRawQuery(String query){
        database.execSQL(query);
        return true;
    }



    public Trip getTempTripData() {
        Trip obj = null;
        Cursor mCursor = null;
        try {
            mCursor = database.rawQuery("select * FROM " + TEMP_TRIP_TABLE_NAME, null);

            if (mCursor.moveToFirst()) {
                do {
                    obj = new Trip();
                    obj.setID(mCursor.getInt(0));
                    obj.setCity(mCursor.getString(1));
                    obj.setLocation(mCursor.getString(2));
                    obj.setDate_in(mCursor.getString(5));
                    obj.setLat(mCursor.getString(3));
                    obj.setLng(mCursor.getString(4));
                    obj.setAirportCode(mCursor.getString(7));
                    obj.setPer_diem(mCursor.getString(6));
                    if(mCursor.getInt(8) == 1)
                        obj.set_notificationStatus(true);
                    else
                        obj.set_notificationStatus(false);
                } while (mCursor.moveToNext());
            }

        }
        finally {
            if(mCursor != null)
                mCursor.close();
        }
        return obj;
    }

    public List<Trip> getAllTrips() {

        List<Trip> obj = new ArrayList<>();
        Trip tripObj = null;
        Cursor mCursor = null;
        try {
            mCursor = database.rawQuery("select * from " + TRIP_HISTORY_TABLE_NAME, null);
            if(mCursor.moveToFirst()) {
                do{
                    tripObj = new Trip();
                    tripObj.setID(mCursor.getInt(0));
                    tripObj.setCity(mCursor.getString(1));
                    tripObj.setLocation(mCursor.getString(2));
                    tripObj.setDate_in(mCursor.getString(5));
                    tripObj.setDate_out(mCursor.getString(6));
                    tripObj.setLat(mCursor.getString(3));
                    tripObj.setLng(mCursor.getString(4));
                    tripObj.setAirportCode(mCursor.getString(8));
                    tripObj.setPer_diem(mCursor.getString(7));
                    obj.add(tripObj);
                }while (mCursor.moveToNext());
            }
        }
        finally {
            if(mCursor != null)
                mCursor.close();
        }
        return obj;
    } // End of getAllData

    public Trip getSingleTrip() {
        Trip tripObj = null;
        Cursor mCursor = null;
        try {
            mCursor = database.rawQuery("select * from " + TRIP_HISTORY_TABLE_NAME +" ORDER BY _ID DESC LIMIT 1", null);

            if (mCursor.moveToFirst()) {
                do {
                    tripObj = new Trip();
                    tripObj.setID(mCursor.getInt(0));
                    tripObj.setCity(mCursor.getString(1));
                    tripObj.setLocation(mCursor.getString(2));
                    tripObj.setDate_in(mCursor.getString(5));
                    tripObj.setDate_out(mCursor.getString(6));
                    tripObj.setLat(mCursor.getString(3));
                    tripObj.setLng(mCursor.getString(4));
                    tripObj.setAirportCode(mCursor.getString(8));
                    tripObj.setPer_diem(mCursor.getString(7));
                } while (mCursor.moveToNext());
            }

        }
        finally {
            if (mCursor != null)
                mCursor.close();
        }
        return tripObj;
    } // End of getSingleData


    public Trip getSingleTrip(int id) {

        Cursor mCursor = null;
        Trip tripObj = null;
        try {
            mCursor = database.rawQuery("select * from " + TRIP_HISTORY_TABLE_NAME +" WHERE _ID = '"+id+"'", null);
            if (mCursor.moveToFirst()) {
                do {
                    tripObj = new Trip();
                    tripObj.setID(mCursor.getInt(0));
                    tripObj.setCity(mCursor.getString(1));
                    tripObj.setLocation(mCursor.getString(2));
                    tripObj.setDate_in(mCursor.getString(5));
                    tripObj.setDate_out(mCursor.getString(6));
                    tripObj.setLat(mCursor.getString(3));
                    tripObj.setLng(mCursor.getString(4));
                    tripObj.setAirportCode(mCursor.getString(8));
                    tripObj.setPer_diem(mCursor.getString(7));
                } while (mCursor.moveToNext());
            }

        }
        finally {
            if(mCursor != null)
                mCursor.close();
        }
        return tripObj;
    } // End of getSingleData


    public Airports getAirportByName(String code){
        code = code.toUpperCase();
        Cursor mCursor = null;
        try {
            mCursor = database.rawQuery("Select *  from AIRPORTNEW"
                    + " where CODE = '" + code + "'", null);
            if (mCursor.moveToFirst()) {
                do {
                    Airports airports = new Airports();
                    airports.setAirportCode(mCursor.getString(6));
                    airports.setAirportName(mCursor.getString(5));
                    airports.setAddress(mCursor.getString(2) +","+ mCursor.getString(1));
                    airports.setLat(mCursor.getDouble(8));
                    airports.setLng(mCursor.getDouble(9));
                    airports.setCountry(mCursor.getString(1));
                    airports.setCity(mCursor.getString(2));
                    airports.setPerDiem(mCursor.getString(10));
                    mCursor.close();
                    return airports;
                } while (mCursor.moveToNext());
            }
        }
        finally {
            if (mCursor != null)
                mCursor.close();
        }

        return null;
    }


    public List<Airports> getAirportByCountry(String country){
        List<Airports> airports = null;
        String query = "";
        Cursor mCursor = null;

            query = "Select *  from AIRPORTNEW";
                    //+ " where Country = '" + country + "' ";<></<>
        try {
            mCursor = database.rawQuery(query, null);

            if (mCursor.moveToFirst()) {
                airports = new ArrayList<>();
                do {
                    Airports airportsObj = new Airports();
                    airportsObj.setAirportCode(mCursor.getString(4));
                    airportsObj.setAirportName(mCursor.getString(1));
                    airportsObj.setAddress(mCursor.getString(2) +","+ mCursor.getString(3));
                    airportsObj.setLat(mCursor.getDouble(6));
                    airportsObj.setLng(mCursor.getDouble(7));
                    String airportCode = "";
                    if(!(airportCode = mCursor.getString(4)).trim().contentEquals(""))
                        airports.add(airportsObj);
                } while (mCursor.moveToNext());
            }

        }
        finally {
            if (mCursor != null)
                mCursor.close();

        }
        return airports;
    }

    public List<String> getAirportCities(){
        String query = "";
        List<String> data = null;
        Cursor mCursor = null;
        try {
            query = "Select Distinct City,Country from AIRPORTNEW";
            //+ " where Country = '" + country + "' ";
            mCursor = database.rawQuery(query, null);
            if (mCursor.moveToFirst()) {
                data = new ArrayList<>();
                do {
                    data.add(mCursor.getString(0)+","+mCursor.getString(1));
                } while (mCursor.moveToNext());
            }

        }
        finally {
            if(mCursor != null)
                mCursor.close();
        }
        return data;
    }


    public Airports getAirportbyWhereClause(String WHERE){
        String query = "";
        Airports airports = null;
        Cursor mCursor = null;
        try {
            query = "Select *," + WHERE  +" AS DISTANCE  from AIRPORTNEW WHERE CODE != '' AND DISTANCE not null ORDER BY DISTANCE LIMIT 1";
            mCursor = database.rawQuery(query, null);
            if(mCursor.moveToFirst()) {
                do {
                    airports = new Airports();
                    airports.setAirportCode(mCursor.getString(6));
                    airports.setAirportName(mCursor.getString(5));
                    airports.setAddress(mCursor.getString(2) +","+ mCursor.getString(1));
                    airports.setLat(mCursor.getDouble(8));
                    airports.setLng(mCursor.getDouble(9));
                    airports.setCountry(mCursor.getString(1));
                    airports.setCity(mCursor.getString(2));
                    airports.setPerDiem(mCursor.getString(10));
                } while (mCursor.moveToNext());

            }

        }
        finally {
            if(mCursor != null)
                mCursor.close();

        }
        return airports;
    }

//
//
//    public int getPerDiemByCity(String city, String country){
//        country = country.toUpperCase();
//         mCursor = database.rawQuery("Select MealsIncidentals from " + PER_DIEM_TABLE_NAME
//                + " where Location = '" + city + "' AND COUNTRY = '" + country +"'", null);
//        if(mCursor.getCount() > 0){
//            mCursor.moveToFirst();
//            return mCursor.getInt(mCursor.getColumnIndex("MealsIncidentals"));
//        }
//        else
//            return (getPerDiemByOtherCity(country));
//    }

    public int getPerDiemByOtherCountry(String country) {
        Cursor mCursor = null;
        try {
            mCursor = database.rawQuery("Select MealsIncidentals from  AirportNew"
                    + " where CITY LIKE '%Other%' AND COUNTRY = '" + country + "'", null);
            if (mCursor.moveToFirst()) {
                do {
                    
                    return mCursor.getInt(0);
                } while (mCursor.moveToNext());
            }
        }finally {
            if(mCursor != null)
                mCursor.close();

        }
        return 0;

    }

    public double getPerDiemAllowanceToDate(){
        Cursor mCursor = null;
        try {

            mCursor = database.rawQuery("Select SUM(Per_Diem) AS TotalPerDiem from  Trip_History_Table", null);
            if (mCursor.moveToFirst()) {  return mCursor.getInt(0);}
        }finally {
            if(mCursor != null)
                mCursor.close();

        }
        return 0;
    }


    public void setNotificationOn() {
        
        try {

            database.execSQL("UPDATE  "+ TEMP_TRIP_TABLE_NAME + " SET Notification = '1'");
            
        }catch (Exception e){}
        finally {

        }
    }

    public int getPerDiemByCity(String city, String country) {
        Cursor mCursor = null;
        try {
            mCursor = database.rawQuery("Select MealsIncidentals from airportNew"
                    + " where CITY = '"+city+"' AND COUNTRY = '" + country + "'", null);
            if (mCursor.moveToFirst()) do{  return mCursor.getInt(0); }while (mCursor.moveToNext());
        }
        finally {
            if(mCursor != null)
                mCursor.close();

        }
        return 0;
    }
}
