package com.example.restaurantapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.R;
import com.example.restaurantapp.model.MenuItem;

import java.util.List;
import java.util.Locale;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    
    private List<MenuItem> menuItems;
    private OnItemClickListener listener;
    
    public interface OnItemClickListener {
        void onItemClick(MenuItem menuItem);
    }
    
    public MenuAdapter(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    
    public void updateMenuItems(List<MenuItem> newMenuItems) {
        this.menuItems = newMenuItems;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_guest, parent, false);
        return new MenuViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.bind(item);
    }
    
    @Override
    public int getItemCount() {
        return menuItems == null ? 0 : menuItems.size();
    }
    
    class MenuViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName, tvItemDesc, tvItemPrice, tvEmoji;
        
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemDesc = itemView.findViewById(R.id.tv_item_desc);
            tvItemPrice = itemView.findViewById(R.id.tv_item_price);
            tvEmoji = itemView.findViewById(R.id.tv_emoji);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(menuItems.get(position));
                }
            });
        }
        
        public void bind(MenuItem item) {
            tvItemName.setText(item.getName());
            
            // Set description (or empty if null)
            String description = item.getDescription();
            if (description == null || description.isEmpty()) {
                tvItemDesc.setText("No description available");
            } else {
                tvItemDesc.setText(description);
            }
            
            // Format price
            String price = String.format(Locale.getDefault(), "RM %.2f", item.getPrice());
            tvItemPrice.setText(price);
            
            // Set emoji based on category (simple mapping)
            String emoji = getEmojiForCategory(item.getCategory());
            tvEmoji.setText(emoji);
        }
        
        private String getEmojiForCategory(String category) {
            if (category == null) return "🍽️";
            
            switch (category.toLowerCase()) {
                case "starters":
                    return "🥗";
                case "mains":
                    return "🍽️";
                case "desserts":
                    return "🍰";
                case "drinks":
                    return "🥤";
                default:
                    return "🍽️";
            }
        }
    }
}

