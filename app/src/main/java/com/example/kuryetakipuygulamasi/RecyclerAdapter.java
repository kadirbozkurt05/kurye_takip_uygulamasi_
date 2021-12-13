package com.example.kuryetakipuygulamasi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kuryetakipuygulamasi.databinding.RecyclerRowBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.AdapterHolder> {
    ArrayList<String> courierList;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth =FirebaseAuth.getInstance();

    public RecyclerAdapter(ArrayList<String> courierList) {
        this.courierList = courierList;
    }

    @NonNull
    @Override
    public AdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new AdapterHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.AdapterHolder holder, int position) {
                String name=courierList.get(position);
                holder.binding.recyclerViewTextView.setText(name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(),AdminMapsActivity.class);
                intent.putExtra("name",name);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courierList.size();
    }

    public class AdapterHolder extends RecyclerView.ViewHolder {

        private RecyclerRowBinding binding;

        public AdapterHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}