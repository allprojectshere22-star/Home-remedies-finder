package com.example.remedy;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RemedyAdapter adapter;

    ArrayList<Remedy> remedyList = new ArrayList<>();
    ArrayList<Remedy> filteredList = new ArrayList<>();

    SearchView searchView;
    ChipGroup chipGroup;
    DatabaseHelper db;

    String selectedCategory = "All";

    private ContextThemeWrapper chipContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_main);

        chipContext = new ContextThemeWrapper(this, R.style.ThemeOverlay_Remedy_HomeChips);

        recyclerView = findViewById(R.id.remedyRecycler);
        searchView = findViewById(R.id.searchView);
        chipGroup = findViewById(R.id.chipGroup);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        db = new DatabaseHelper(this);

        // Check if category is passed from CategoriesActivity
        String categoryFromIntent = getIntent().getStringExtra("category");
        if (categoryFromIntent != null) {
            selectedCategory = categoryFromIntent;
        }

        refreshData();

        adapter = new RemedyAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

        applyFilters("");

        setupSearch();

        searchView.setIconifiedByDefault(false);
        searchView.clearFocus();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_categories) {
                startActivity(new Intent(MainActivity.this, CategoriesActivity.class));
                return true;
            } else if (id == R.id.nav_me) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void refreshData() {
        remedyList.clear();
        remedyList.addAll(RemedyCatalog.loadAllRemedies(this, db));
        syncSelectedCategory();
        setupDynamicChips();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh in case admin added new remedies
        refreshData();
        applyFilters(searchView.getQuery().toString());
    }

    private void setupDynamicChips() {
        chipGroup.removeAllViews();

        LinkedHashSet<String> chipNames = new LinkedHashSet<>();
        chipNames.add("All");
        if (!"All".equalsIgnoreCase(selectedCategory)) {
            chipNames.add(selectedCategory);
        }

        // Exactly 5 chips total: "All" + four categories (always include the currently selected one)
        int maxChipCount = 5;
        java.util.List<String> top = RemedyCatalog.getTopCategories(this, db, 4);
        for (String cat : top) {
            if (chipNames.size() >= maxChipCount) break;
            if (cat == null || cat.equalsIgnoreCase("All")) continue;
            chipNames.add(cat);
        }

        for (String chipName : chipNames) {
            addChip(chipName);
        }

        // Set checked state
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(selectedCategory)) {
                chip.setChecked(true);
            }
        }
    }

    private void addChip(String categoryName) {
        Chip chip = new Chip(chipContext, null, com.google.android.material.R.attr.chipStyle);
        chip.setText(categoryName);
        chip.setCheckable(true);
        chip.setClickable(true);
        chip.setSingleLine(true);
        chip.setMaxLines(1);
        chip.setEllipsize(android.text.TextUtils.TruncateAt.END);

        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedCategory = categoryName;
                applyFilters(searchView.getQuery().toString());
            }
        });

        chipGroup.addView(chip);
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters(query);
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters(newText);
                return true;
            }
        });
    }

    private void syncSelectedCategory() {
        if ("All".equalsIgnoreCase(selectedCategory)) {
            selectedCategory = "All";
            return;
        }

        if (RemedyCatalog.hasCategory(this, db, selectedCategory)) {
            selectedCategory = RemedyCatalog.resolveCategoryName(this, db, selectedCategory);
        } else {
            selectedCategory = "All";
        }
    }

    private void applyFilters(String text) {
        filteredList.clear();
        String query = text.toLowerCase().trim();

        for (Remedy r : remedyList) {
            boolean matchesText = query.isEmpty() ||
                    r.getTitle().toLowerCase().contains(query) ||
                    r.getCategory().toLowerCase().contains(query);

            boolean matchesCategory = selectedCategory.equalsIgnoreCase("All") || 
                                     r.getCategory().equalsIgnoreCase(selectedCategory);

            if (matchesText && matchesCategory) {
                filteredList.add(r);
            }
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }
}
