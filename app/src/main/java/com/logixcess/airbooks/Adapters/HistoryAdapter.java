package com.logixcess.airbooks.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.logixcess.airbooks.Business.ArrayAdapterFeeder;
import com.logixcess.airbooks.R;

import java.util.List;

/**
 * Created by Usman on 06/08/2016.
 */

public class HistoryAdapter extends ArrayAdapter<ArrayAdapterFeeder> {
    public HistoryAdapter(Context context, List<ArrayAdapterFeeder> obj) {
        super(context, R.layout.history_list, obj);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.history_list,parent,false);

        ArrayAdapterFeeder object = getItem(position);
        String title = object.getTitle();
        String date = object.getDate();
        int amount = object.getMoney();

        TextView etTitle = (TextView) customView.findViewById(R.id.tvTitle);
        TextView etDate = (TextView) customView.findViewById(R.id.tvDate);
        TextView etMoney = (TextView) customView.findViewById(R.id.tvAmount);

        etTitle.setText(title );
        etDate.setText(date);
        etMoney.setText("$"+amount);
        return customView;
    }
}
