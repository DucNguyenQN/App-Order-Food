package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.databinding.ItemRatingDetailsBinding;
import com.example.foodorderingapp.model.CartItems;
import com.example.foodorderingapp.model.RatingData;
import com.example.foodorderingapp.model.RatingItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RatingDetailsAdapter extends RecyclerView.Adapter<RatingDetailsAdapter.RatingDetailsViewHolder>{
    private Context context;
    private List<RatingItem> ratingItems;

    public RatingDetailsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RatingDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRatingDetailsBinding binding = ItemRatingDetailsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new RatingDetailsViewHolder(binding);
    }

    public void setData(List<RatingItem> ratingItems){
        this.ratingItems = ratingItems;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RatingDetailsViewHolder holder, int position) {
        RatingItem rating =  ratingItems.get(position);
        holder.bind(rating);
    }

    @Override
    public int getItemCount() {
        if (ratingItems != null){
            return ratingItems.size();
        }
        return 0;
    }

    public class RatingDetailsViewHolder extends RecyclerView.ViewHolder {
        private final ItemRatingDetailsBinding binding;
        public RatingDetailsViewHolder(ItemRatingDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(RatingItem rating) {
            //binding.ratingUserName.setText(rating.);
            getUserName(rating.getUserId(), this);
            binding.ratingComment.setText(rating.getComment());
            binding.ratingBar.setRating(rating.getRatingValue());
        }
    }

    private void getUserName(String id, RatingDetailsViewHolder holder){
        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference("user").child(id).child("userName");
        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.getValue(String.class);
                    holder.binding.ratingUserName.setText(userName);
                } else {
                    holder.binding.ratingUserName.setText("Người dùng");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.binding.ratingUserName.setText("Người dùng");
            }
        });
    }
}
