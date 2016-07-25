package asri_tools.devalkhatech.com.asri.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import asri_tools.devalkhatech.com.asri.R;
import asri_tools.devalkhatech.com.asri.controllers.DBController;

public class BarcodeFragment extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    EditText scan, lat, lang;
    Button submitAbsen;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    private TextView addressText, locationText;

    LatLng latLng;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    Marker currLocationMarker;
    HashMap<String, String> queryValues;
    private Context context;

    public BarcodeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_barcode, container, false);
        mFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mFragment.getMapAsync(this);

        scan = (EditText) rootView.findViewById(R.id.t_result);
        lat = (EditText) rootView.findViewById(R.id.t_latitude);
        lang = (EditText) rootView.findViewById(R.id.t_langitude);
        locationText = (TextView) rootView.findViewById(R.id.location);
        addressText = (TextView) rootView.findViewById(R.id.address);
        submitAbsen = (Button) rootView.findViewById(R.id.btn_submit_attendance);

        Intent intent = getActivity().getIntent();
        String _res = intent.getStringExtra("result_scan");
        final ProgressDialog csprogress = new ProgressDialog(getActivity());

        scan.setText(_res);
        submitAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csprogress.setMessage("Loading...Inserting data to Local DB");
                csprogress.setCancelable(false);
                csprogress.show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        csprogress.dismiss();
                        insertData();
                    }
                }, 1000);//just mention the time when you want to launch your action

            }
        });
        return rootView;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(getActivity(), "onConnected", Toast.LENGTH_SHORT).show();
        // for ActivityCompat#requestPermissions for more details.
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                //place marker at current position
                //mGoogleMap.clear();
                latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());


                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Your Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                currLocationMarker = mGoogleMap.addMarker(markerOptions);
            }
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5000); //5 seconds
            mLocationRequest.setFastestInterval(3000); //3 seconds
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            locationText.setText("You location at [" + longitude + " ; " + latitude + " ]");

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                String alamat = "Street: " + address + "\n" + "City/Province: " + city + "\nCountry: " + country
                        + "\nPostal CODE: " + postalCode + "\n" + "Place Name: " + knownName;
                addressText.setText(alamat);

            } catch (IOException e) {
                e.printStackTrace();
            }
            //new GetAddressTask(SalesAttendance.this).execute(String.valueOf(latitude), String.valueOf(longitude));
            lat.setText(String.valueOf(latitude));
            lang.setText(String.valueOf(longitude));
        } else {
            Toast.makeText(getActivity(), R.string.error_permission_map, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        //place marker at current position
        //mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        //Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();

        //zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(14).build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        locationText.setText("You are at [" + longitude + " ; " + latitude + " ]");

        //new GetAddressTask(SalesAttendance.this).execute(String.valueOf(latitude), String.valueOf(longitude));
        lat.setText(String.valueOf(latitude));
        lang.setText(String.valueOf(longitude));

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        mGoogleMap = gMap;

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            Toast.makeText(getActivity(), R.string.error_permission_map, Toast.LENGTH_LONG).show();
        }


        buildGoogleApiClient();

        mGoogleApiClient.connect();

    }

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(getActivity(), "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void insertData() {
        try {
            context = getActivity(); //use the instance variable
            DBController db = new DBController(context);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTimeString = df.format(new Date());

            // DB QueryValues Object to insert into SQLite
            queryValues = new HashMap<String, String>();
            queryValues.put("customer_code", scan.getText().toString());
            queryValues.put("latitude", lat.getText().toString());
            queryValues.put("langitude", lang.getText().toString());
            queryValues.put("created", currentDateTimeString);

//            queryValues.put("remark", t_remark.getText().toString());
            db.insertAbsen(queryValues);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
