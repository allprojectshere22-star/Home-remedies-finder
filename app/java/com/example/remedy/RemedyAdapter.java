package com.example.remedy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RemedyAdapter extends RecyclerView.Adapter<RemedyAdapter.ViewHolder> {

    private Context context;
    private List<Remedy> remedyList;

    public RemedyAdapter(Context context, List<Remedy> remedyList) {
        this.context = context;
        this.remedyList = remedyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_remedy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Remedy remedy = remedyList.get(position);

        holder.title.setText(remedy.getTitle());
        holder.category.setText(remedy.getCategory());

        SharedPreferences sp = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        Set<String> saved = new HashSet<>(sp.getStringSet("saved_remedies", new HashSet<>()));
        Set<String> fav = new HashSet<>(sp.getStringSet("favorite_remedies", new HashSet<>()));

        String data = (remedy.getCategory() + "|" +
                remedy.getTitle() + "|" +
                remedy.getDescription() + "|" +
                remedy.getIngredients() + "|" +
                remedy.getSteps() + "|" +
                remedy.getBenefits()).trim();

        // 🔖 SET DEFAULT ICON STATE
        if (saved.contains(data)) {
            holder.btnSave.setColorFilter(Color.parseColor("#4CAF50")); // green
        } else {
            holder.btnSave.setColorFilter(Color.GRAY);
        }

        String checkData = (remedy.getCategory() + "|" +
                remedy.getTitle() + "|" +
                remedy.getDescription() + "|" +
                remedy.getIngredients() + "|" +
                remedy.getSteps() + "|" +
                remedy.getBenefits()).trim();

        if (fav.contains(checkData)) {
            holder.btnFav.setColorFilter(Color.RED);
        } else {
            holder.btnFav.setColorFilter(Color.GRAY);
        }

        // 🔖 SAVE TOGGLE
        holder.btnSave.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            SharedPreferences spLocal = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            Set<String> savedLocal = new HashSet<>(spLocal.getStringSet("saved_remedies", new HashSet<>()));

            if (savedLocal.contains(data)) {
                savedLocal.remove(data);
                Toast.makeText(context, "Removed from Saved", Toast.LENGTH_SHORT).show();
            } else {
                savedLocal.add(data);
                Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
            }

            spLocal.edit().putStringSet("saved_remedies", savedLocal).apply();
            notifyItemChanged(pos);
        });

        // ❤️ FAVORITE TOGGLE
        holder.btnFav.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Remedy r = remedyList.get(pos);

            SharedPreferences spLocal = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            Set<String> favLocal = new HashSet<>(spLocal.getStringSet("favorite_remedies", new HashSet<>()));

            if (favLocal.contains(data)) {
                favLocal.remove(data);
                Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            } else {
                favLocal.add(data);
                Toast.makeText(context, "Added to Favorites ❤️", Toast.LENGTH_SHORT).show();
            }

            spLocal.edit().putStringSet("favorite_remedies", favLocal).apply();

            notifyItemChanged(pos);
        });

        // 📄 OPEN DETAILS - View Details Button
        holder.detailButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);

            intent.putExtra("title", remedy.getTitle());
            intent.putExtra("ingredients", remedy.getIngredients());
            intent.putExtra("description", remedy.getDescription());
            intent.putExtra("steps", remedy.getSteps());
            intent.putExtra("benefits", remedy.getBenefits());
            intent.putExtra("safe", remedy.getSafe());
            intent.putExtra("avoid", remedy.getAvoid());
            intent.putExtra("caution", remedy.getCaution());
            intent.putExtra("doctor", remedy.getDoctor());
            intent.putExtra("image", remedy.getImage());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return remedyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, category;
        ImageView btnSave, btnFav;
        android.widget.Button detailButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtTitle);
            category = itemView.findViewById(R.id.txtCategory);
            btnSave = itemView.findViewById(R.id.btnSave);
            btnFav = itemView.findViewById(R.id.btnFav);
            detailButton = itemView.findViewById(R.id.detailButton);
        }
    }
}