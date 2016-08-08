package com.logixcess.airbooks.Screens;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.logixcess.airbooks.MainActivity;
import com.logixcess.airbooks.R;

/**
 * Created by Usman on 06/08/2016.
 */

public class TaxCalculate extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tax, container, false);

        return rootView;
    }
}
