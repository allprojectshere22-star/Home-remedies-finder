package com.example.remedy;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoriesActivity extends AppCompatActivity {

    private RecyclerView categoriesRecycler;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        db = new DatabaseHelper(this);
        categoriesRecycler = findViewById(R.id.categoriesRecycler);
        categoryList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        categoriesRecycler.setLayoutManager(gridLayoutManager);

        categoryAdapter = new CategoryAdapter(this, categoryList);
        categoriesRecycler.setAdapter(categoryAdapter);

        loadAllCategories();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_categories);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            }
            if (id == R.id.nav_categories) {
                return true;
            } else if (id == R.id.nav_me) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllCategories();
    }

    private void loadAllCategories() {
        categoryList.clear();
        Map<String, Integer> categoryCount = RemedyCatalog.categoryCountsForUi(this, db);
        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            categoryList.add(new Category(entry.getKey(), entry.getValue()));
        }
        categoryList.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
        categoryAdapter.notifyDataSetChanged();
    }
}
