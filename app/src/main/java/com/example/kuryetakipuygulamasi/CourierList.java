package com.example.kuryetakipuygulamasi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kuryetakipuygulamasi.databinding.ActivityCourierListBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CourierList extends AppCompatActivity {
    private ActivityCourierListBinding binding;
    ArrayList<String> courierList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    String name;
    FirebaseUser user;
    RecyclerAdapter recyclerAdapter;
    ArrayList<String> phoneList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourierListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        courierList = new ArrayList<>();
        phoneList = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new RecyclerAdapter(courierList);
        binding.recyclerView.setAdapter(recyclerAdapter);

        getList();
    }
    public void getList(){


        db.collection(user.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                courierList.clear();
                if (error!=null){
                    Toast.makeText(CourierList.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }if (value!=null){

                    for (DocumentSnapshot documentSnapshot:value.getDocuments()){
                        Map<String,Object> data = documentSnapshot.getData();
                            name = (String) data.get("name");
                            courierList.add(name);
                    }

                }recyclerAdapter.notifyDataSetChanged();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.admin_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.logout){
            mAuth.signOut();
            Intent intent = new Intent(CourierList.this,MainActivity.class);
            finish();
            startActivity(intent);
        }else if (item.getItemId()==R.id.clearList){
            Intent intent = new Intent(CourierList.this,Profile.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}