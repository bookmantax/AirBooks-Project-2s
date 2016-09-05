package com.bookmantax.airbooks.Screens;


import android.support.v4.app.DialogFragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.bookmantax.airbooks.Adapters.DatabaseAdapter;
import com.bookmantax.airbooks.Business.PerDiem;
import com.bookmantax.airbooks.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Usman on 25/08/2016.
 */

public class SearchPerDiem extends DialogFragment implements AdapterView.OnItemClickListener {

    AutoCompleteTextView etCity;
    TextView tvTotal;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_perdiem, container, false);
        etCity = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView);
        tvTotal = (TextView) rootView.findViewById(R.id.tvPerDiemTotal);
        getDataSource();
        etCity.setOnItemClickListener(this);
        return rootView;
    }


    public void getDataSource(){
        List<String> data = new ArrayList<>();
        DatabaseAdapter db = DatabaseAdapter.getInstance(this.getActivity());
        db.open(false);
        data = db.getAirportCities();
        DatabaseAdapter.getInstance(getContext()).close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.select_dialog_item, data);
        etCity.setAdapter(adapter);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String data = (String) etCity.getAdapter().getItem(position);
        String[] newData = data.toString().split(",");
        if(newData.length > 0){
            PerDiem perDiem = new PerDiem(newData[0].toString(),newData[1].toString(),view.getContext());
            String diem = "" + perDiem.getPerDiemByCity();
            tvTotal.setText("$" + diem +" /day");
        }

    }
}
