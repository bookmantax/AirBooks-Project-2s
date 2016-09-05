package com.bookmantax.airbooks.Screens;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.bookmantax.airbooks.Adapters.HistoryAdapter;
import com.bookmantax.airbooks.Business.Airports;
import com.bookmantax.airbooks.Business.PerDiem;
import com.bookmantax.airbooks.Business.Trip;
import com.bookmantax.airbooks.R;
import com.bookmantax.airbooks.Utils.Utils;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * Created by Usman on 06/08/2016.
 */

public class History extends Fragment {
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int DATE_PICKER_ID = 2;
    Switch swIsActiveTrip;
    static EditText etCity,etPerDiem,etStartDate,etEndDate;
    Button btnEdit;
    Trip obj;
    LinearLayout layout,layoutEmpty;
    ListView listView;
    String location,latitude,longitude;
    List<Trip> objList;
    HistoryAdapter historyAdapter;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        initialize(rootView);
        setAll();
        return rootView;
    }

    private void setAll(){
        obj = new Trip();
        objList = obj.getAll(getContext());
        obj = null;
        if(objList == null) {
            layoutEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }else if(objList.size() < 1) {
            layoutEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }else if( objList.size()  > 0)
        {
            layoutEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        historyAdapter = new HistoryAdapter(getContext(),objList);
        listView.setAdapter(historyAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        setAll();
    }

    private void initialize(View view) {
        btnEdit = (Button) view.findViewById(R.id.btnUpdateTrip);

        etCity = (EditText) view.findViewById(R.id.etCity);
        etCity.setFocusable(false);
        etStartDate = (EditText) view.findViewById(R.id.etStartDate);
        etEndDate = (EditText) view.findViewById(R.id.etEndDate);
        etPerDiem = (EditText) view.findViewById(R.id.etPerDiem);
        layout = (LinearLayout) view.findViewById(R.id.layout_edit_trip);
        listView = (ListView) view.findViewById(R.id.history_item);
        layoutEmpty = (LinearLayout) view.findViewById(R.id.nothing);
        swIsActiveTrip = (Switch) view.findViewById(R.id.swActiveTrip);

        etCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.itGooglePlayServicesInstalled) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
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
                else
                    Toast.makeText(getContext(),"Google play services are missing",Toast.LENGTH_SHORT).show();
            }
        });

        etStartDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    History.SelectDateFragment obj = new History.SelectDateFragment();
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
                    History.SelectDateFragment obj = new History.SelectDateFragment();
                    DialogFragment newFragment = obj;
                    Utils.selector = 1;
                    newFragment.show(getActivity().getFragmentManager(), "DatePicker");
                }
                return true;
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTrip();
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Warning")
                        .setMessage("Are you sure to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                obj = (Trip) listView.getItemAtPosition(position);
                                if(obj.deleteTrip(getContext(),obj)) {
                                    Toast.makeText(getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
                                    objList.clear();
                                    historyAdapter.notifyDataSetChanged();
                                    objList = null;
                                    objList = obj.getAll(getContext());
                                    obj = null;
                                    historyAdapter = null;
                                    historyAdapter = new HistoryAdapter(getContext(),objList);
                                    listView.setAdapter(historyAdapter);


                                }
                                else
                                    Toast.makeText(getContext(),"There was a problem in deleting item",Toast.LENGTH_SHORT).show();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Trip obj2 = (Trip) listView.getItemAtPosition(position);
                obj = obj2.getSingle(obj2.getID(),getContext());
                etCity.setText(obj.getCity());
                location = obj.getLocation();
                latitude = obj.getLat();
                longitude = obj.getLng();
                etStartDate.setText(obj.getDate_in());
                etEndDate.setText(obj.getDate_out());


                etPerDiem.setText(obj.getPer_diem());
                layout.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                obj2 = null;
                objList.clear();
            }
        });

    }

    public static void setDate(int selector){
        if(selector == 0)
            etStartDate.setText(Utils.date);
        else
            etEndDate.setText(Utils.date);
    }
    public void updateTrip() {
        Airports airports = null;
        if(obj != null){
            Trip objNew = new Trip();
            if(obj.getAirportCode() != null) {
                objNew.setAirportCode(obj.getAirportCode());
                airports = new Airports();

                airports = airports.getAirportByCode(getContext(),obj.getAirportCode());
            }

            if(airports != null){
                objNew.setPer_diem(airports.getPerDiem());
            }else{
                LatLng latLng = new LatLng(Double.parseDouble(obj.getLat()),Double.parseDouble(obj.getLng()));
                List<Address> address = getLocationInfo(latLng);
                if(address != null) {
                    PerDiem perDiem = new PerDiem(obj.getCity(), address.get(0).getCountryName(), getContext());
                    objNew.setPer_diem(""+ perDiem.getPerDiem());
                }
            }

            objNew.setID(obj.getID());
            objNew.setCity(etCity.getText().toString());
            objNew.setLocation(location);
            objNew.setLat(latitude);
            objNew.setLng(longitude);

            objNew.setDate_in(etStartDate.getText().toString());
            objNew.setDate_out(etEndDate.getText().toString());
            if(swIsActiveTrip.isChecked()) {

                if(!Utils.isTripActive) {
                    if(obj.deleteTrip(this.getContext(),obj))
                        objNew.save(0, getContext());
                    Toast.makeText(getContext(), "New Trip Added", Toast.LENGTH_SHORT).show();
                    Utils.currentTrip = objNew;
                    objNew = null;
                    objList.clear();
                    historyAdapter.notifyDataSetChanged();
                    objList = null;
                    objList = obj.getAll(getContext());
                    obj = null;
                    historyAdapter = null;
                    historyAdapter = new HistoryAdapter(getContext(), objList);
                    listView.setAdapter(historyAdapter);
                    layout.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
                else
                    Toast.makeText(getContext(), "A trip is already active", Toast.LENGTH_SHORT).show();
            }
            else {
                if (objNew.update(getContext(), objNew)) {
                    Toast.makeText(getContext(), "Item Updated", Toast.LENGTH_SHORT).show();
                    objList.clear();
                    historyAdapter.notifyDataSetChanged();
                    objList = null;
                    objList = obj.getAll(getContext());
                    obj = null;
                    historyAdapter = null;
                    historyAdapter = new HistoryAdapter(getContext(), objList);
                    listView.setAdapter(historyAdapter);
                    layout.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }


                objNew = null;
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
                etCity.setText(place.getName());

                Toast.makeText(getContext(), ""+ latlng.toString(), Toast.LENGTH_SHORT).show();
                place = null;
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
            Utils.updateDate2();
        }

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
