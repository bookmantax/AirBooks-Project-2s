package com.logixcess.airbooks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AC_Signup extends AppCompatActivity {

    Button btnSave;
    EditText etAirportCode,etPhone;
    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btnSave = (Button) findViewById(R.id.btnSave);
        etAirportCode = (EditText)findViewById(R.id.etAirCode);
        etPhone = (EditText) findViewById(R.id.etPhone);
        phone = etPhone.getText().toString();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etAirportCode.getText().toString().isEmpty() && !etPhone.getText().toString().isEmpty()){
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                            "com.logixcess.airbooks", Context.MODE_PRIVATE);

                    sharedPreferences.edit().putString("AirportCode", etAirportCode.getText().toString()).apply();
                    sharedPreferences.edit().putString("PhoneNo", phone.toString()).apply();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
