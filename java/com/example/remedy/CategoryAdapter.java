package com.example.remedy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<Category> categoryList;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());
        holder.categoryCount.setText(category.getCount() + " remedies");
        
        holder.categoryContainer.setOnClickListener(v -> openCategory(category.getName()));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    private void openCategory(String category) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("category", category);
        context.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        public TextView categoryCount;
        public View categoryContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryCount = itemView.findViewById(R.id.categoryCount);
            categoryContainer = itemView.findViewById(R.id.categoryContainer);
        }
    }
}

