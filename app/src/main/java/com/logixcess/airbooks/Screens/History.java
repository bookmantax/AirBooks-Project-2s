package com.logixcess.airbooks.Screens;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.logixcess.airbooks.Adapters.HistoryAdapter;
import com.logixcess.airbooks.Business.ArrayAdapterFeeder;
import com.logixcess.airbooks.MainActivity;
import com.logixcess.airbooks.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Usman on 06/08/2016.
 */

public class History extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setActionBarTitle();
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        ArrayAdapterFeeder obj;
        List<ArrayAdapterFeeder> objList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            obj = new ArrayAdapterFeeder("London Trip","2/08/2016-4/08/2016",640);
            objList.add(obj);
        }
        obj = null;
        ListAdapter historyAdapter = new HistoryAdapter(getContext(),objList);
        ListView listView = (ListView) rootView.findViewById(R.id.history_item);
        listView.setAdapter(historyAdapter);

        return rootView;
    }
}
