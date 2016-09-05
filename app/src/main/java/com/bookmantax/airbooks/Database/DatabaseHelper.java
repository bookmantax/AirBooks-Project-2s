package com.bookmantax.airbooks.Database;

import android.content.Context;

import com.bookmantax.airbooks.Utils.Utils;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


/**
 * Created by Usman on 11/08/2016.
 */
public class DatabaseHelper extends SQLiteAssetHelper {

    private static final String DB_NAME = "BookmanAirbooks_DB.db";

    private static final int DATABASE_VERSION = 1;



    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        Utils.databaseDone = true;
    }




}
