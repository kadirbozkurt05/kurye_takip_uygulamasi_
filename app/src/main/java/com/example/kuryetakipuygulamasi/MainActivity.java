package com.example.kuryetakipuygulamasi;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import com.example.kuryetakipuygulamasi.databinding.ActivityMainBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher<String> permissionLauncher;
    ActivityMainBinding binding;
    FirebaseAuth mAuth;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        super.onCreate(savedInstanceState);
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            Intent intent = new Intent(MainActivity.this,CourierList.class);
            finish();
            startActivity(intent);
        }
        askGps();

        name="";

        binding.textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uriUrl = Uri.parse("https://jpst.it/2HazD");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });


        Snackbar.make(view,"YÖNETİCİ İSENİZ ÜST KISMI, KURYE İSENİZ ALT KISMI DOLDURUN",Snackbar.LENGTH_INDEFINITE).setAction("ANLADIM", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();
        registerLauncher();

        String savedName= getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE).getString("courierName", "");
        String savedEmail =getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE).getString("courierEmail", "");
        if (!savedEmail.matches("")&&!savedName.matches("")){
            binding.courierEmailText.setText(savedName);
            binding.courierPasswordText.setText(savedEmail);
            if (isLocationEnabled(MainActivity.this)==true){
                signInCourier(view);
            }

        }



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {


                Snackbar.make(view, "KONUM ERİŞİM İZNİ GEREKLİ", Snackbar.LENGTH_INDEFINITE).setAction("İZİN VER", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //İZİN İSTE
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).show();


            } else {
                //İZİN İSTE
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        } else {
            // İZİN VERİLDİ
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {


                    Snackbar.make(view, "KONUM ERİŞİM İZNİ GEREKLİ", Snackbar.LENGTH_INDEFINITE).setAction("İZİN VER", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //İZİN İSTE
                            permissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                        }
                    }).show();
                } else {
                    //İZİN İSTE
                    permissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                }
            } else {
                // İZİN VERİLDİ
            }
        }

    }
    public void registerLauncher() {

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    //İZİN VERİLDİ


                } else {
                    //TOST MESAJI
                    Toast.makeText(MainActivity.this, "KONUM ERİŞİM İZNİ GEREKLİ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void signInAdmin(View view){
        if (!binding.adminEmailText.getText().toString().matches("")&&!binding.adminPasswordText.getText().toString().matches("")){
            mAuth.signInWithEmailAndPassword(binding.adminEmailText.getText().toString(),binding.adminPasswordText.getText().toString()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    //Toast.makeText(MainActivity.this, "GİRİŞ BAŞARILI", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,CourierList.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }else{
            Toast.makeText(MainActivity.this, "LÜTFEN YÖNETİCİ GİRİŞ ALANLARININ DOLDURUN!", Toast.LENGTH_SHORT).show();
        }
    }
    public void signInCourier(View view){
        if (!binding.courierEmailText.getText().toString().matches("")&&!binding.courierPasswordText.getText().toString().matches("")){
            name = binding.courierEmailText.getText().toString();
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putString("courierName", name);
            edit.putString("courierEmail",binding.courierPasswordText.getText().toString());
            edit.commit();
            Intent intent = new Intent(MainActivity.this,CourierMapsActivity.class);
            intent.putExtra("name",binding.courierEmailText.getText().toString());
            intent.putExtra("adminEmail",binding.courierPasswordText.getText().toString());
            finish();
            startActivity(intent);
        }else{
            Toast.makeText(this, "LÜTFEN KURYE GİRİŞ ALANLARININ TAMAMINI DOLDURUN!", Toast.LENGTH_SHORT).show();
        }
    }
    public void askGps(){
        if (isLocationEnabled(MainActivity.this)==false){
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Konum İzni")
                    .setCancelable(false)
                    .setMessage("Uygulamayı kullanabilmek için Konum servisini açmanız gereklidir. Konum servisi açılsın mı?")
                    .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this, "Konum Servisi Açılmadığı İçin Uygulama Kapanacak", Toast.LENGTH_SHORT).show();
                            CountDownTimer countDownTimer = new CountDownTimer(3000,1000) {
                                @Override
                                public void onTick(long l) {

                                }

                                @Override
                                public void onFinish() {
                                    finish();
                                }
                            }.start();

                        }
                    })
                    .setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LocationRequest locationRequest = LocationRequest.create();
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                    .addLocationRequest(locationRequest);

                            Task<LocationSettingsResponse> result =
                                    LocationServices.getSettingsClient(MainActivity.this).checkLocationSettings(builder.build());


                            result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                                    try {
                                        LocationSettingsResponse response = task.getResult(ApiException.class);
                                        // All location settings are satisfied. The client can initialize location
                                        // requests here.
                                    } catch (ApiException exception) {
                                        switch (exception.getStatusCode()) {
                                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                                // Location settings are not satisfied. But could be fixed by showing the
                                                // user a dialog.
                                                try {
                                                    // Cast to a resolvable exception.
                                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                                                    // Show the dialog by calling startResolutionForResult(),
                                                    // and check the result in onActivityResult().
                                                    resolvable.startResolutionForResult(
                                                            MainActivity.this,
                                                            LocationRequest.PRIORITY_HIGH_ACCURACY);
                                                } catch (IntentSender.SendIntentException e) {
                                                    // Ignore the error.
                                                } catch (ClassCastException e) {
                                                    // Ignore, should be an impossible error.
                                                }
                                                break;
                                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                                // Location settings are not satisfied. However, we have no way to fix the
                                                // settings so we won't show the dialog.
                                                break;
                                        }
                                    }
                                }
                            });
                        }
                    }).show();
        }




    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(MainActivity.this, "Konum Servisi Açılmadığı İçin Uygulama Kapanacak", Toast.LENGTH_SHORT).show();
                        CountDownTimer countDownTimer = new CountDownTimer(3000,1000) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                finish();
                            }
                        }.start();
                        break;
                    default:
                        break;
                }
                break;
        }
    }
    @SuppressWarnings("deprecation")
    public static Boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is a new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            // This was deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }


}