package com.example.restaurantapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.R;
import com.example.restaurantapp.model.MenuItem;

import java.util.List;
import java.util.Locale;

public class MenuStaffAdapter extends RecyclerView.Adapter<MenuStaffAdapter.MenuViewHolder> {
    
    private List<MenuItem> menuItems;
    private OnEditClickListener editListener;
    private OnDeleteClickListener deleteListener;
    
    public interface OnEditClickListener {
        void onEditClick(MenuItem menuItem);
    }
    
    public interface OnDeleteClickListener {
        void onDeleteClick(MenuItem menuItem);
    }
    
    public MenuStaffAdapter(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
    
    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editListener = listener;
    }
    
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }
    
    public void updateMenuItems(List<MenuItem> newMenuItems) {
        this.menuItems = newMenuItems;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_staff, parent, false);
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
        private TextView tvItemName, tvItemPrice, tvEmoji;
        private Button btnEdit, btnDelete;
        
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_item_price);
            tvEmoji = itemView.findViewById(R.id.tv_emoji);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            
            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && editListener != null) {
                    editListener.onEditClick(menuItems.get(position));
                }
            });
            
            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && deleteListener != null) {
                    deleteListener.onDeleteClick(menuItems.get(position));
                }
            });
        }
        
        public void bind(MenuItem item) {
            tvItemName.setText(item.getName());
            
            // Format price
            String price = String.format(Locale.getDefault(), "RM %.2f", item.getPrice());
            tvItemPrice.setText(price);
            
            // Set emoji based on category
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

