package asri_tools.devalkhatech.com.asri.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

import asri_tools.devalkhatech.com.asri.R;
import asri_tools.devalkhatech.com.asri.services.GetAddressTask;

public class SalesAttendance extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    EditText scan, lat, lang;
    Button scanAgain;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    private TextView addressText,locationText;

    LatLng latLng;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    Marker currLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFragment.getMapAsync(this);

        scan = (EditText) findViewById(R.id.t_result);
        lat = (EditText) findViewById(R.id.t_latitude);
        lang = (EditText) findViewById(R.id.t_langitude);
        locationText = (TextView) findViewById(R.id.location);
        addressText = (TextView) findViewById(R.id.address);
        scanAgain = (Button) findViewById(R.id.btnScan);

        Intent intent = getIntent();
        String _res = intent.getStringExtra("result_scan");
        scan.setText(_res);
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        mGoogleMap = gMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            Toast.makeText(this, R.string.error_permission_map, Toast.LENGTH_LONG).show();
        }
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
            // for ActivityCompat#requestPermissions for more details.
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
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
                locationText.setText("You are at [" + longitude + " ; " + latitude + " ]");

                new GetAddressTask(SalesAttendance.this).execute(String.valueOf(latitude), String.valueOf(longitude));
                lat.setText(String.valueOf(latitude));
                lang.setText(String.valueOf(longitude));
            } else {
                Toast.makeText(this, R.string.error_permission_map, Toast.LENGTH_LONG).show();
            }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
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

        new GetAddressTask(SalesAttendance.this).execute(String.valueOf(latitude), String.valueOf(longitude));
        lat.setText(String.valueOf(latitude));
        lang.setText(String.valueOf(longitude));
        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
    public void callBackDataFromAsyncTask(String address) {
        addressText.setText(address);
    }
}
