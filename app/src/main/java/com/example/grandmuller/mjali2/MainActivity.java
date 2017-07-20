package com.example.grandmuller.mjali2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    String provider;

    TextView latTV;
    TextView lngTV;
    TextView altTV;
    TextView speedTV;
    TextView bearTV;
    TextView accTV;
    TextView addressTV;

    Double lat;
    Double lng;
    Double alt;
    float speed;
    float bear;
    float acc;
    List<Address> listAddresses;
    String addressHolder;
    private Firebase firebase;
    Button btnsubmit;
    DatabaseReference databaseReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();



        latTV = (TextView) findViewById(R.id.lat);
        lngTV = (TextView) findViewById(R.id.lng);
        altTV = (TextView) findViewById(R.id.alt);
        //speedTV = (TextView) findViewById(R.id.speed);
        //bearTV = (TextView) findViewById(R.id.bear);
        accTV = (TextView) findViewById(R.id.acc);
        addressTV = (TextView) findViewById(R.id.address);
        btnsubmit=(Button)findViewById(R.id.submit);


        firebase = new Firebase("https://mefriend-ff835.firebaseio.com/");



        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat=latTV.getText().toString();
                String lon=lngTV.getText().toString();
                String alt=altTV.getText().toString();
                String acc=accTV.getText().toString();
                String address=addressTV.getText().toString();


                LocationData locationData = new LocationData();
                locationData.setLatitude(lat);
                locationData.setLongitude(lon);
                locationData.setAltitude(alt);
                locationData.setAccuracy(acc);
                locationData.setAddress(address);



                 firebase.child("Mjali2").push().setValue(locationData);


                Toast.makeText(MainActivity.this, "Data sent", Toast.LENGTH_SHORT).show();
                finish();


            }
        });



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        onLocationChanged(location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 1, 1, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            alt = location.getAltitude();
            speed = location.getSpeed();
            bear = location.getBearing();
            acc = location.getAccuracy();

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            try {
                listAddresses = geocoder.getFromLocation(lat, lng, 1);

                if (listAddresses != null&& listAddresses.size() >0 ) {

                    Log.i("PlaceInfo", listAddresses.get(0).toString());

                    addressHolder = "";

                    for (int i = 0; i <= listAddresses.get(0).getMaxAddressLineIndex(); i++) {

                        addressHolder += listAddresses.get(0).getAddressLine(i)+ "\n";
                    }
                    addressTV.setText(addressHolder);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            //round double to 5 decimal place
            double lat5 = Math.round(lat * 100000);
            lat5 = lat5/100000;
            //round double to 5 decimal place
            double lng5 = Math.round(lat * 100000);
            lng5 = lng5/100000;
            //round double to 2 decimal place
            double alt2 = Math.round(alt * 100);
            alt2 = alt2/100;
            //round double to 2 decimal place and convert into kph
            double speed2 = Math.round(speed * 3.6 * 100);
            speed2 = speed2/100;
            //round double to 2 decimal place
            double bear2 = Math.round(bear * 100);
            bear2 = bear2/100;




            latTV.setText( String.valueOf(lat));
            lngTV.setText(String.valueOf(lng));
            altTV.setText(String.valueOf(alt2));
            //speedTV.setText("Speed: "+ speed2 + "kph");
            //bearTV.setText("Bearing: "+ bear2 + "");
            accTV.setText(String.valueOf(acc));

            Log.i("Latitude", String.valueOf(lat));
            Log.i("Longitude", String.valueOf(lng));
            Log.i("Altitude", String.valueOf(alt));
            Log.i("Speed", String.valueOf(speed));
            Log.i("Bearing", String.valueOf(bear));
            Log.i("Accuracy", String.valueOf(acc));
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}