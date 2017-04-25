package com.example.tg71223.roadio;

import android.*;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.text.Text;

import java.text.DecimalFormat;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    PolylineOptions tripOptions;
    Boolean tripStart;
    TextView distanceView;
    TextView timeView;

    //TODO put all these into an object
    double distance;
    long time;

    // ==========================================
    // Activity Lifecycle Callbacks
    // ==========================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Set starting trip state to false
        tripStart = false;

        // Async inflate map with google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Button onClick logic
        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTrip();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    // ==========================================
    // PERMISSIONS
    // ==========================================

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show user explanation of why they should give our app permissions
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted by the user
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, disable the functionality
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // ==========================================
    // Google API Client & Callbacks
    // ==========================================

    // Builds up Google API Client

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission, not already granted
                checkLocationPermission();
            }
        }
        else {
            // Earlier version of Android that doesn't need explicit user validation
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Creates the Location Request and sets the parameters of how long to wait between location
        // requests
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    // ==========================================
    // Trip Logic and Map Manipulation
    // ==========================================

    /************************
     * This fires off all necessary events that must occur when the a user's new current location
     * is discovered
     *
     * @param location : Location object with most current Location data
     */
    @Override
    public void onLocationChanged(Location location)
    {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        // Get current LatLng
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        // Add new distance to current distance and reflect changes
        if(distanceView != null && mLastLocation != null) {
            LatLng prevLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            double recentDistance = calculateDistance(prevLatLng, latLng);
            distance += recentDistance;
            distanceView.setText(String.format("%.2f", distance) + " km");
        }

        if(tripStart) {
            //Add polyline to show trip
            tripOptions.add(latLng);

            //Draw polylines
            mMap.addPolyline(tripOptions);

            mLastLocation = location;
        }
        //Move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

    }

    /************************
     * Draws the current trip on the map and sets the constraints to fit the entirety of the trip
     *
     * @param arr A list of LatLng objects that represent the entire trip taken by the user so far
     */
    private void moveToBounds(List<LatLng> arr)
    {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0; i < arr.size();i++){
            builder.include(arr.get(i));
        }
        LatLngBounds bounds = builder.build();
        int padding = 40; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    public void startTrip() {
        distance = 0;
        time = 0;
        tripStart = true;
        tripOptions = new PolylineOptions();
        distanceView = (TextView) findViewById(R.id.distance_traveled);
        distanceView.setText("0.0 km");
        timeView = (TextView) findViewById(R.id.elapsed_time);
        timeView.setText("WIP");
        findViewById(R.id.start_button).setVisibility(View.GONE);
        findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
        findViewById(R.id.stat_table).setVisibility(View.VISIBLE);
    }

    public void stopTrip() {
        //stop location updates when Activity is no longer active

    }

    public double calculateDistance(LatLng prev, LatLng current) {
        double prevLat = prev.latitude;
        double prevLong = prev.longitude;
        double currentLat = current.latitude;
        double currentLong = current.longitude;
        double R = 6371;
        double latDistance = toRad(currentLat - prevLat);
        double longDistance = toRad(currentLong - prevLong);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(prevLat)) * Math.cos(toRad(currentLat)) *
                        Math.sin(longDistance / 2) * Math.sin(longDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

}
