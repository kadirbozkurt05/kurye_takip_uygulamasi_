package com.example.kuryetakipuygulamasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.kuryetakipuygulamasi.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends AppCompatActivity {
    private ActivityProfileBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view =binding.getRoot();
        setContentView(view);

        mAuth= FirebaseAuth.getInstance();
    }
    public void changePassword(View view){
        FirebaseUser user = mAuth.getCurrentUser();
        String newPassword = binding.editTextNumberPassword.getText().toString();

        user.updatePassword(newPassword).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this, "Şifre Değiştirmek İçin Çıkış Yapıp Tekrar Girin", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(Profile.this, "ŞİFRE DEĞİŞTİRİLDİ", Toast.LENGTH_SHORT).show();

            }
        });
    }
}