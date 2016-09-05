package com.bookmantax.airbooks;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bookmantax.airbooks.R;
import com.bookmantax.airbooks.Utils.Utils;

public class AC_Signup extends AppCompatActivity {

    Button btnSave;
    EditText etAirportCode,etPhone;
    String phone;
    int keyDel = 0;
    /*Location System Starts*/
    private static final int INITIAL_REQUEST=1337;
    private static final int[] requests = new int[]{INITIAL_REQUEST+3,INITIAL_REQUEST+4,INITIAL_REQUEST+5,INITIAL_REQUEST+6};
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;
    private static final int INTERNET_REQUEST=INITIAL_REQUEST+4;
    private static final int READ_REQUEST=INITIAL_REQUEST+5;
    private static final int WRITE_REQUEST=INITIAL_REQUEST+6;
    /*Location System Ends*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // managing Location Permission


        btnSave = (Button) findViewById(R.id.btnSave);
        etAirportCode = (EditText)findViewById(R.id.etAirCode);
        etPhone = (EditText) findViewById(R.id.etPhone);

        phone = etPhone.getText().toString();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etAirportCode.getText().toString().isEmpty() && !etPhone.getText().toString().isEmpty()){
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                            "com.bookmantax.airbooks", Context.MODE_PRIVATE);
                    phone = etPhone.getText().toString();
                    sharedPreferences.edit().putString("Airport_Code", etAirportCode.getText().toString()).apply();
                    sharedPreferences.edit().putString("PhoneNo", phone.toString()).apply();
                    sharedPreferences.edit().putString("MuteNotifications", "Not").apply();
                    Utils.muteNotifications = false;
                    Utils.AIRPORT_CODE = etAirportCode.getText().toString();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkForPermission();


        etPhone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etPhone.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_DEL)
                            keyDel = 1;
                        return false;
                    }
                });

                if (keyDel == 0) {
                    int len = etPhone.getText().length();
                    if(len == 3) {
                        etPhone.setText(etPhone.getText() + "-");
                        etPhone.setSelection(etPhone.getText().length());
                    }
                    if(len == 7) {
                        etPhone.setText(etPhone.getText() + "-");
                        etPhone.setSelection(etPhone.getText().length());
                    }

                } else {
                    keyDel = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }
        });
       // throw new NullPointerException();
    }



@TargetApi(Build.VERSION_CODES.M )
    public  void checkForPermission(){
        int i = 0;
        for(String strPerm : Utils.PERMISSIONS)
        {
            if(ContextCompat.checkSelfPermission(this,strPerm) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{strPerm},requests[i]);
            }
            i++;
        }
    }



}
