package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.R;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.PopularViewHolder>{
    private Context context;

    public PopularAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_item,parent, false);
        return new PopularViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class PopularViewHolder extends RecyclerView.ViewHolder{

        public PopularViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
