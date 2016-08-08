package com.logixcess.airbooks.Business;

/**
 * Created by Usman on 06/08/2016.
 */

public class ArrayAdapterFeeder {
    String title,date;
    int money;

    public ArrayAdapterFeeder(String title, String date, int money) {
        this.title = title;
        this.date = date;
        this.money = money;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public int getMoney() {
        return money;
    }
}
