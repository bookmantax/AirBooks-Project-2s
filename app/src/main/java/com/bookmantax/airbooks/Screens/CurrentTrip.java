package com.bookmantax.airbooks.Screens;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bookmantax.airbooks.Business.AutomationFunctions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.bookmantax.airbooks.Business.Airports;
import com.bookmantax.airbooks.Business.PerDiem;
import com.bookmantax.airbooks.Business.TaskScheduler;
import com.bookmantax.airbooks.Business.Trip;
import com.bookmantax.airbooks.R;
import com.bookmantax.airbooks.Utils.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;


/**
 * Created by Usman on 06/08/2016.
 */

public class CurrentTrip extends Fragment {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int DATE_PICKER_ID = 2;
    Switch swIsActiveTrip,swWorkTrip;
    Button btnStart,btnStart2,btnCancel;
    FloatingActionButton fab;
    TextView tvPerDiem,tvDate,tvStatus;
    static EditText etCity,etStartDate,etEndDate;
    static EditText etCity2,etPerDiem2,etStartDate2;
    static String latitude, longitude,location,nonAirportCountry;
    LinearLayout layout,layoutPrimary,layout_active_trip,layout_no_trip,layout_end_date;
    private Trip tripObj;
    private int year,month,day;
    Airports globalAirport;
    boolean NotLocationPlaceHolder = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_current_trip, container, false);
        initializeElem(rootView);
        listeners();
        handleTrip(1);

        getActivity().showDialog(DATE_PICKER_ID);

        return rootView;
    }


    private void initializeElem(View view){

        btnStart = (Button) view.findViewById(R.id.btnStartTrip);
        btnStart2 = (Button) view.findViewById(R.id.btnStartTrip2);
        btnCancel = (Button) view.findViewById(R.id.btnEndTrip);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        tvStatus = (TextView) view.findViewById(R.id.tvTripStatus);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvPerDiem = (TextView) view.findViewById(R.id.tvPerDiem);

        etCity = (EditText) view.findViewById(R.id.etCity);
        etCity.setFocusable(false);
        etStartDate = (EditText) view.findViewById(R.id.etStartDate);
        etEndDate = (EditText) view.findViewById(R.id.etEndDate);
        etCity2 = (EditText) view.findViewById(R.id.etCurrentCity);
        etStartDate2 = (EditText) view.findViewById(R.id.etCurrentStartDate);
        etPerDiem2 = (EditText) view.findViewById(R.id.etCurrentPerDiem);

        swIsActiveTrip = (Switch) view.findViewById(R.id.swActiveTrip);
        swWorkTrip = (Switch) view.findViewById(R.id.swWorkTrip);
        layout = (LinearLayout) view.findViewById(R.id.layout_add_trip);
        layoutPrimary = (LinearLayout) view.findViewById(R.id.layout_primary);
        layout_active_trip = (LinearLayout) view.findViewById(R.id.layout_active_trip);
        layout_no_trip = (LinearLayout) view.findViewById(R.id.layout_no_trip);
        layout_end_date = (LinearLayout) view.findViewById(R.id.layout_end_date);

        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);
    }




    private void listeners(){
        swIsActiveTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swIsActiveTrip.isChecked())
                    layout_end_date.setVisibility(View.GONE);
                else
                    layout_end_date.setVisibility(View.VISIBLE);
            }
        });

        swWorkTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!swWorkTrip.isChecked()){
                    new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Warning")
                            .setMessage("Your trip will be cancelled?")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(tripObj.cancel(getContext()))
                                    {
                                        Toast.makeText(getContext(),"Trip is Cancelled",Toast.LENGTH_SHORT).show();
                                        handleTrip(-1);
                                    }
                                }

                            })
                            .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    swWorkTrip.setChecked(true);
                                }
                            })
                            .show();
                }


            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"Start Clicked",Toast.LENGTH_SHORT).show();
                layoutPrimary.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
            }
        });

        btnStart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start Trip manually
                NotLocationPlaceHolder = true;
                if(globalAirport != null) {
                    PerDiem perDiem = new PerDiem(globalAirport.getCity(),globalAirport.getCountry(),getContext());
                    if(perDiem != null)
                        etPerDiem2.setText(String.valueOf(perDiem.getPerDiem()));
                    Double distance = Double.parseDouble(distance(Double.parseDouble(latitude), Double.parseDouble(longitude), globalAirport.getLat(), globalAirport.getLng()));
                    if(distance <= 120701){
                        //airport found within 75 miles
                        if (globalAirport.getAirportCode() != null) {
                            startTripWithAirport();
                        }
                    }
                    else{
                        // airport is not in 75 miles radius
                        startTripWithoutAirport();
                    }
                }
                else{
                    startTripWithoutAirport();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Warning")
                        .setMessage("Are you sure to cancel?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(tripObj.cancel(getContext())) {
                                    Toast.makeText(getContext(), "Trip is Cancelled", Toast.LENGTH_SHORT).show();
                                    handleTrip(-1);
                                    tripObj = null;
                                }else
                                    Toast.makeText(getContext(), "Trip is not Cancelled", Toast.LENGTH_SHORT).show();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

        etCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.itGooglePlayServicesInstalled) {
                    try {
                        NotLocationPlaceHolder = false;
                        AutocompleteFilter mAutocompleteFilter = new AutocompleteFilter.Builder().
                                setTypeFilter(Place.TYPE_CITY_HALL).build();
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                        .setFilter(mAutocompleteFilter)
                                        .build(getActivity());

                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Google play services are missing", Toast.LENGTH_SHORT).show();
                }
            }
        });

        etStartDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    SelectDateFragment obj = new SelectDateFragment();
                    DialogFragment newFragment = obj;
                    Utils.selector = 0;
                    newFragment.show(getActivity().getFragmentManager(), "DatePicker");

                }
                return true;
            }
        });

        etEndDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    SelectDateFragment obj = new SelectDateFragment();
                    DialogFragment newFragment = obj;
                    Utils.selector = 1;
                    newFragment.show(getActivity().getFragmentManager(), "DatePicker");

                }
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                SearchPerDiem nextFrag= new SearchPerDiem();
                nextFrag.show(manager,"SearchDiem");
            }
        });
    }


    public void startTripWithAirport(){
        tripObj = new Trip();
        tripObj.setCity(etCity.getText().toString());
        tripObj.setLat(latitude);
        tripObj.setLng(longitude);
        tripObj.setLocation(location);
        tripObj.setDate_in(etStartDate.getText().toString());
        tripObj.setDate_out(etEndDate.getText().toString());
        tripObj.setAirportCode(globalAirport.getAirportCode());
        tripObj.setPer_diem(globalAirport.getPerDiem());
        if (swIsActiveTrip.isChecked())
            tripObj.save(0, getContext());
        else
            tripObj.save(1, getContext());
        layout.setVisibility(View.GONE);
        layoutPrimary.setVisibility(View.VISIBLE);
        handleTrip(1);
    }

    public void startTripWithoutAirport(){
        tripObj = new Trip();
        tripObj.setCity(etCity.getText().toString());
        tripObj.setLat(latitude);
        tripObj.setLng(longitude);
        tripObj.setLocation(location);
        tripObj.setDate_in(etStartDate.getText().toString());
        tripObj.setDate_out(etEndDate.getText().toString());
        PerDiem objPerDiem = new PerDiem(tripObj.getCity(),nonAirportCountry , getContext());

        tripObj.setAirportCode("NONE");
        tripObj.setPer_diem("" + objPerDiem.getPerDiem());
        if (swIsActiveTrip.isChecked())
            tripObj.save(0, getContext());
        else
            tripObj.save(1, getContext());
        layout.setVisibility(View.GONE);
        layoutPrimary.setVisibility(View.VISIBLE);
        handleTrip(1);
    }


    public static void setDate(int selector){
        if(selector == 0)
            etStartDate.setText(Utils.date);
        else
            etEndDate.setText(Utils.date);
    }




    /*Handling Trip*/
    private void handleTrip(int check){
        if(Utils.currentTrip == null) {
            this.tripObj = new Trip();
            this.tripObj = this.tripObj.checkForTrip(getContext());
            if(this.tripObj != null)
                Utils.currentTrip = this.tripObj;
        }else
            this.tripObj = Utils.currentTrip;

        if(Utils.currentTrip != null && check == 1){
            // trip is active
            //Snackbar.make(view,this.tripObj.getCity() + " Trip is Active",Snackbar.LENGTH_SHORT).show();
         //   TaskScheduler.currentTrip = tripObj;
            layout.setVisibility(View.GONE);
            layoutPrimary.setVisibility(View.VISIBLE);
            layout_no_trip.setVisibility(View.GONE);
            layout_active_trip.setVisibility(View.VISIBLE);
            tvStatus.setText("A Work Trip is in Progress");
            tvStatus.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreenNotification));
            btnCancel.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.GONE);
            etCity2.setText(this.tripObj.getCity());
            etStartDate2.setText(this.tripObj.getDate_in());
            etPerDiem2.setText(this.tripObj.getPer_diem());
        }else{
            // No Trip
            layout.setVisibility(View.GONE);
            layoutPrimary.setVisibility(View.VISIBLE);
            layout_active_trip.setVisibility(View.GONE);
            layout_no_trip.setVisibility(View.VISIBLE);
            tvStatus.setText("No Work Trip is in Progress");
            tvStatus.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorDelete));
            btnCancel.setVisibility(View.GONE);
            btnStart.setVisibility(View.VISIBLE);
            tvDate.setText("No Trip Found");
            tvDate.setTextColor(ContextCompat.getColor(getContext(), R.color.colorDelete));
            tvPerDiem.setText("No Trip Found");
            tvPerDiem.setTextColor(ContextCompat.getColor(getContext(), R.color.colorDelete));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //autocompleteFragment.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                LatLng latlng = place.getLatLng();
                latitude = "" + latlng.latitude;
                longitude = "" + latlng.longitude;
                location = "" + place.getAddress();

                etStartDate.setText(Utils.getDate());
                etCity.setText(place.getName());
                List<Address> address = getLocationInfo(latlng);
                if(address != null)
                    nonAirportCountry = address.get(0).getCountryName();
                String Where = Utils.buildDistanceWhereClause(latlng.latitude,latlng.longitude);
                globalAirport = new Airports();
                globalAirport = globalAirport.getAirportUsingWhereClause(getContext(),Where);


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                Log.i(TAG, status.getStatusMessage());
            } else if (requestCode == RESULT_CANCELED) {

            }
        }
    }



    // date picker
    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm+1, dd);
        }

        public void populateSetDate(int year, int month, int day) {
            Utils.date = "" + month + "/" +  day + "/" + year;
            Utils.updateDate();
        }
    }




//    public static String buildDistanceWhereClause(double latitude, double longitude) {
//
//        // see: http://stackoverflow.com/questions/3695224/sqlite-getting-nearest-locations-with-latitude-and-longitude
//        final String format = "(%1$s - %2$s) * (%3$s - %4$s) + (%5$s - %6$s) * (%7$s - %8$s)";
//        final String selection = String.format(format,
//                latitude, "LATITUDE",
//                latitude, "LATITUDE",
//                longitude, "LONGITUDE",
//                longitude, "LONGITUDE"
//        );
//        return selection;
//    }


    // Haversine Formula to calculate distance between two locations by coordinates.
    public String distance(double homeLat, double homeLong, double currentLat, double currentLong){

        int R = 6371; // km
        double dLat = toRadians(currentLat - homeLat);

        double dLon = toRadians(currentLong - homeLong);

        homeLat = toRadians(homeLat);
        currentLat = toRadians(currentLat);

        double a = sin(dLat/2) * sin(dLat/2) +
                sin(dLon/2) * sin(dLon/2) * cos(homeLat) * cos(currentLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        BigDecimal bd = new BigDecimal(R * c);
        bd = bd.setScale(6, RoundingMode.HALF_UP);

        return bd.toString();
    }

    public double toRadians(Double deg) {
        return deg * (PI/180);
    }




    @Override
    public void onResume() {
        super.onResume();

        if(NotLocationPlaceHolder)
        handleTrip(1);
    }


    private List<Address> getLocationInfo(LatLng currentLocation) {
        Geocoder gcd = new Geocoder(this.getContext(), Locale.getDefault());
        List<Address> addresses = null;
        if (gcd.isPresent())
            try {
                addresses = gcd.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (addresses.size() > 0)
            return addresses;
        else
            return null;
    }

}
