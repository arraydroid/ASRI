package asri_tools.devalkhatech.com.asri.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import asri_tools.devalkhatech.com.asri.R;
import asri_tools.devalkhatech.com.asri.services.GetAddressTask;

public class MapView extends FragmentActivity   {

    EditText scan, lat, lang;
    // Progress Dialog Object
    ProgressDialog prgDialog;

    private TextView locationText;
    private TextView addressText;
    private GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_attendance);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        scan = (EditText) findViewById(R.id.t_result);
        lat = (EditText) findViewById(R.id.t_latitude);
        lang = (EditText) findViewById(R.id.t_langitude);
        locationText = (TextView) findViewById(R.id.location);
        addressText = (TextView) findViewById(R.id.address);

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Getting current your location");
        prgDialog.setCancelable(false);

        Intent intent = getIntent();
        String _res = intent.getStringExtra("result_scan");
        scan.setText(_res);

        //replaceMapFragment();

    }

   /* private void replaceMapFragment() {
       *//* map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
*//*
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        map = fm.getMap();
        // Enable Zoom
        map.getUiSettings().setZoomGesturesEnabled(true);

        //set Map TYPE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //enable Current location Button
        map.setMyLocationEnabled(true);

        //set "listener" for changing my location
        map.setOnMyLocationChangeListener(myLocationChangeListener());
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener() {
        return new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                Marker marker;
                marker = map.addMarker(new MarkerOptions().position(loc));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                locationText.setText("You are at [" + longitude + " ; " + latitude + " ]");

                //get current address by invoke an AsyncTask object
               // new GetAddressTask(SalesAttendance.this).execute(String.valueOf(latitude), String.valueOf(longitude));
                *//*lat.setText(String.valueOf(latitude));
                lang.setText(String.valueOf(longitude));*//*
            }
        };
    }

    public void callBackDataFromAsyncTask(String address) {
        addressText.setText(address);
    }*/
}
