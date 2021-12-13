package com.example.kuryetakipuygulamasi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.kuryetakipuygulamasi.databinding.ActivityAdminMapsBinding;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityAdminMapsBinding binding;
    private FirebaseFirestore db;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String name;
    double latitude;
    double longitude;
    Intent intent;
    LatLng latLng;
    Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        latLng = new LatLng(0,0);
        intent = getIntent();
        if (name!=null){
            name ="";
        }
        name = intent.getStringExtra("name");
        getData();
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMapsActivity.this,CourierList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
    public void getData(){
            if (name!=null){
                db.collection(user.getEmail()).document(name).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Map<String,Object> data = value.getData();
                        latitude = (double) data.get("latitude");
                        longitude = (double) data.get("longitude");

                        latLng = new LatLng(latitude,longitude);
                        if(marker!=null){
                            marker.remove();
                        }
                        marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon)).position(latLng).title(name));

                        if (binding.switch2.isChecked()){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                        }else{
                            binding.switch2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                                }
                            });
                        }
                    }
                });
            }
    }
}