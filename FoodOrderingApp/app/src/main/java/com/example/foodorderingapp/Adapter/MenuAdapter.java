package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.foodorderingapp.DetailsActivity;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder>{
    private List<MenuItem> menuItems;
    private List<MenuItem> menuItemsFilter;
    private Context context;

    public MenuAdapter(Context context) {
        this.context = context;
    }
    public void setData(List<com.example.foodorderingapp.model.MenuItem> menuItems){
        this.menuItems = new ArrayList<>(menuItems);
        this.menuItemsFilter = new ArrayList<>(menuItems);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        return new MenuViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem menuItem = menuItemsFilter.get(position);
        Glide.with(context).load(menuItem.getFoodImage()).into(holder.menuImage);
        holder.txtMenuFoodName.setText(menuItem.getFoodName());
        holder.txtMenuPrice.setText(menuItem.getFoodPrice());
        holder.menu_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("menuitem", menuItem);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(menuItemsFilter != null){
            return menuItemsFilter.size();
        }
        return 0;
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private ImageView menuImage;
        private TextView txtMenuFoodName, txtMenuPrice;
        private CardView menu_item;
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            menuImage = itemView.findViewById(R.id.menuImage);
            txtMenuFoodName = itemView.findViewById(R.id.txtMenuFoodName);
            txtMenuPrice = itemView.findViewById(R.id.txtMenuPrice);
            menu_item = itemView.findViewById(R.id.menu_item);
        }
    }
    public void filter(String text) {
        Log.d("text", text+"");
        menuItemsFilter.clear();
        if (text.isEmpty()) {
            menuItemsFilter.addAll(menuItems);
        }
        else {
            for (MenuItem menuItem : menuItems) {
                if (menuItem.getFoodName().toLowerCase().contains(text.toLowerCase())) {
                    menuItemsFilter.add(menuItem);
                }
            }
        }
        notifyDataSetChanged();
    }
}
