package com.bookmantax.airbooks.Business;

import android.content.Context;

import com.bookmantax.airbooks.Adapters.DatabaseAdapter;

import java.util.List;

/**
 * Created by Usman on 14/08/2016.
 */

public class Airports {
    String airportName,address,distance,airportCode,city,country,perDiem;
    Double lat,lng;

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public String getAirportName() {
        return airportName;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public String getPerDiem() {
        return perDiem;
    }

    public void setPerDiem(String perDiem) {
        this.perDiem = perDiem;
    }

    public Airports getAirportByCode(Context context, String code){
        DatabaseAdapter db = DatabaseAdapter.getInstance(context);
        db.open(false);
        Airports obj = db.getAirportByName(code);
        DatabaseAdapter.getInstance(context).close();
        return obj;
    }

    public Airports getAirportUsingWhereClause(Context context,String WHERE){
        DatabaseAdapter db = DatabaseAdapter.getInstance(context);
        db.open(false);
        Airports obj = db.getAirportbyWhereClause(WHERE);
        DatabaseAdapter.getInstance(context).close();
        return obj;
    }

    public List<Airports> getAllAirportsByCountry(Context context,String country){
        DatabaseAdapter db = DatabaseAdapter.getInstance(context);
        db.open(false);
        List<Airports> obj = db.getAirportByCountry(country);
        DatabaseAdapter.getInstance(context).close();
        return obj;
    }
}
