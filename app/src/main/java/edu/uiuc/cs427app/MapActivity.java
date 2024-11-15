package edu.uiuc.cs427app;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String themePreference = getIntent().getStringExtra("themePreference");
//        String themePreference = "Light";
        if (themePreference != null) {
            switch (themePreference) {
                case "Default":
                    setTheme(R.style.Theme_MyFirstApp_Default);
                    break;
                case "Dark":
                    setTheme(R.style.Theme_MyFirstApp_DarkMode);
                    break;
                case "Light":
                    setTheme(R.style.Theme_MyFirstApp_LightMode);
                    break;
                case "Blue":
                    setTheme(R.style.Theme_MyFirstApp_BlueRidge);
                    break;
                case "Sunshine":
                    setTheme(R.style.Theme_MyFirstApp_SunnyDay);
                    break;
                default:
                    setTheme(R.style.Theme_MyFirstApp_Default);
                    break;
            }
        } else {
            setTheme(R.style.Theme_MyFirstApp_Default); // Fallback to default if themePreference is null
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map); // This layout will contain the map fragment

        cityName = getIntent().getStringExtra("cityName");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Retrieve latitude and longitude from intent extras
//        double latitude = getIntent().getDoubleExtra("latitude", 0);
//        double longitude = getIntent().getDoubleExtra("longitude", 0);
//        String cityName = getIntent().getStringExtra("cityName");
//
//        // Add a marker at the specified location and move the camera
//        LatLng cityLocation = new LatLng(latitude, longitude);
//        mMap.addMarker(new MarkerOptions().position(cityLocation).title(cityName));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLocation, 10));
//    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (cityName != null) {
            // Use Geocoder to get the location by city name
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(cityName, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    double latitude = address.getLatitude();
                    double longitude = address.getLongitude();
                    Toast.makeText(this, "latitude: " + latitude + "longitude: "+ longitude, Toast.LENGTH_SHORT).show();
                    // Display the location on the map
                    LatLng cityLocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(cityLocation).title(cityName));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLocation, 10));
                } else {
                    Toast.makeText(this, "Location not found for: " + cityName, Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error fetching location for: " + cityName, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
