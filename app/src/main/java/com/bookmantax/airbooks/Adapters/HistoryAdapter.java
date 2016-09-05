package com.bookmantax.airbooks.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bookmantax.airbooks.Business.Trip;
import com.bookmantax.airbooks.R;

import java.util.List;

/**
 * Created by Usman on 06/08/2016.
 */

public class HistoryAdapter extends ArrayAdapter<Trip> {
    public HistoryAdapter(Context context, List<Trip> obj) {
        super(context, R.layout.history_list, obj);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.history_list,parent,false);

        Trip object = getItem(position);
        String title = object.getCity();
        String date = object.getDate_in() + "-" + object.getDate_out();
        String amount = object.getPer_diem();
        int id = object.getID();

        TextView etTitle = (TextView) customView.findViewById(R.id.tvTitle);
        TextView etDate = (TextView) customView.findViewById(R.id.tvDate);
        TextView etMoney = (TextView) customView.findViewById(R.id.tvAmount);
        TextView etID = (TextView) customView.findViewById(R.id.tvItemID);
        etTitle.setText(title );
        etDate.setText(date);
        etMoney.setText("$"+amount);
        etID.setText(""+id);
        return customView;
    }
}
