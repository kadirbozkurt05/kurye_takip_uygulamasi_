package com.example.kuryetakipuygulamasi;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.kuryetakipuygulamasi.databinding.ActivityCourierMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CourierMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityCourierMapsBinding binding;
    private FirebaseFirestore db;
    Intent intent;
    LocationManager locationManager;
    LocationListener locationListener;
    String adminEmail;
    FirebaseAuth mAuth;
    String name;
    double latitude;
    double longitude;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourierMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
            stopLocationService();
        }else{
            startLocationService();
        }

        db = FirebaseFirestore.getInstance();
        intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        adminEmail = intent.getStringExtra("adminEmail");
        name = intent.getStringExtra("name");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        sendDataTo();
    }

    public void sendDataTo() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);

        if( locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager .isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //start listener to requestUpdates ...
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        data.put("latitude", latitude);
                        data.put("longitude", longitude);

                        db.collection(adminEmail).document(name).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println(e.getLocalizedMessage());
                            }
                        });
                        LatLng latLng = new LatLng(latitude, longitude);
                        if (marker != null) {
                            marker.remove();
                        }
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon)).title(name));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    }
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                    if (provider!=null){
                    }

                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    if (provider!=null){
                    }
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    if (provider!=null){
                    }
                }
            };
        }else{
            //show message, no providers enabled.
            Toast.makeText(CourierMapsActivity.this, "KONUMA ERİŞİLEMİYOR", Toast.LENGTH_SHORT).show();
        }

        
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);


    }
    private void startLocationService() {

        Intent intent = new Intent(getApplicationContext(), LocationService.class);
        intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
        startService(intent);
        Toast.makeText(CourierMapsActivity.this, "Konum Paylaşılıyor", Toast.LENGTH_SHORT).show();

    }
    private void stopLocationService() {
        if (isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Konum Paylaşımı Durduruldu", Toast.LENGTH_SHORT).show();
        }

    }
    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
    public void changeLocation(View view){
        if (binding.switch1.isChecked()){
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
                stopLocationService();
            }else{
                startLocationService();
            }


        }else{
            stopLocationService();
        }
    }
}