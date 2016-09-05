package com.bookmantax.airbooks.Screens;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bookmantax.airbooks.Business.PerDiem;
import com.bookmantax.airbooks.R;
import com.bookmantax.airbooks.Utils.Utils;

/**
 * Created by Usman on 06/08/2016.
 */

public class TaxCalculate extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    TextView tvTotal ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tax, container, false);
        tvTotal = (TextView) rootView.findViewById(R.id.tvPerDiemTotal);
        updatePerDiem();
        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updatePerDiem();
    }

    private void updatePerDiem()
    {
        PerDiem perDiem = new PerDiem();
        Utils.perdiemAllowanceToDate = perDiem.getPerDiemAllowanceToDate(this.getContext());
        tvTotal.setText("$"+ Utils.perdiemAllowanceToDate);
    }
}