package com.bookmantax.airbooks.Screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.bookmantax.airbooks.Utils.Utils;
import com.bookmantax.airbooks.R;

/**
 * Created by Usman on 06/08/2016.
 */

public class Settings extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    Button btnSave;
    EditText etCode,etPhone;
    Switch swNotifications;
    int keyDel = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        etCode = (EditText) rootView.findViewById(R.id.etAirCode);
        etPhone = (EditText) rootView.findViewById(R.id.etPhone);
        swNotifications = (Switch) rootView.findViewById(R.id.swNoti);
        btnSave = (Button) rootView.findViewById(R.id.btnSave);

        setValues();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etCode.getText().toString().isEmpty() && !etPhone.getText().toString().isEmpty()){
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(
                            "com.bookmantax.airbooks", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("Airport_Code", etCode.getText().toString()).apply();
                    String  phone = etPhone.getText().toString().replace("-","");
                    sharedPreferences.edit().putString("PhoneNo", phone).apply();
                    if(swNotifications.isChecked()) {
                        sharedPreferences.edit().putString("MuteNotifications", "Mute").apply();
                        Utils.muteNotifications = true;
                    }
                    else {
                        sharedPreferences.edit().putString("MuteNotifications", "Not").apply();
                        Utils.muteNotifications = false;
                    }
                    Utils.PHONE_NO = etPhone.getText().toString();
                    Utils.AIRPORT_CODE = etCode.getText().toString();
                    Toast.makeText(getContext(),"Saved",Toast.LENGTH_SHORT).show();
                }
            }
        });

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



        return rootView;

    }

    public void setValues(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(
                "com.bookmantax.airbooks", Context.MODE_PRIVATE);
        Intent main;
        if(sharedPreferences.contains("Airport_Code"))
        {
            Utils.AIRPORT_CODE = sharedPreferences.getString("Airport_Code","");
            Utils.PHONE_NO = sharedPreferences.getString("PhoneNo","");
            if(sharedPreferences.getString("MuteNotifications","").contains("Mute")) {
                Utils.muteNotifications = true;
                swNotifications.setChecked(true);
            }
            else {
                Utils.muteNotifications = false;
                swNotifications.setChecked(false);
            }
            etCode.setText(Utils.AIRPORT_CODE);
            etPhone.setText(Utils.PHONE_NO);
        }


    }
}
