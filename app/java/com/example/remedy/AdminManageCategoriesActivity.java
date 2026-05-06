package com.example.remedy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AdminManageCategoriesActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private CategoryRowAdapter adapter;
    private ListView listView;
    private TextView emptyHint;

    private static final class CategoryRow {
        final String name;
        final int count;

        CategoryRow(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }

    private final class CategoryRowAdapter extends ArrayAdapter<CategoryRow> {

        CategoryRowAdapter(Context ctx, List<CategoryRow> rows) {
            super(ctx, 0, rows);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                row = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_category, parent, false);
            }
            CategoryRow item = getItem(position);
            TextView nameTv = row.findViewById(R.id.categoryName);
            TextView countTv = row.findViewById(R.id.categoryCount);
            if (item != null) {
                nameTv.setText(item.name);
                if (item.count == 0) {
                    countTv.setText(R.string.remedy_count_none);
                } else {
                    countTv.setText(getContext().getString(R.string.remedy_count_format, item.count));
                }
            }
            return row;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_categories);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        AdminToolbarUi.setupBack(toolbar, this);
        Drawable overflow = toolbar.getOverflowIcon();
        if (overflow != null) {
            Drawable wrapped = DrawableCompat.wrap(overflow.mutate());
            DrawableCompat.setTint(wrapped, ContextCompat.getColor(this, R.color.white));
            toolbar.setOverflowIcon(wrapped);
        }

        listView = findViewById(R.id.listCategories);
        emptyHint = findViewById(R.id.emptyCategoriesHint);
        db = new DatabaseHelper(this);

        adapter = new CategoryRowAdapter(this, new ArrayList<>());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            CategoryRow row = adapter.getItem(position);
            if (row == null) {
                return;
            }
            showCategoryActions(row.name);
        });

        reloadCategories();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_categories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_category) {
            showAddCategoryDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadCategories();
    }

    private void reloadCategories() {
        Map<String, Integer> map = RemedyCatalog.categoryCountsForUi(this, db);
        List<CategoryRow> rows = new ArrayList<>();
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            rows.add(new CategoryRow(e.getKey(), e.getValue()));
        }
        rows.sort(Comparator.comparing(r -> r.name, String.CASE_INSENSITIVE_ORDER));

        adapter.clear();
        for (CategoryRow r : rows) {
            adapter.add(r);
        }
        adapter.notifyDataSetChanged();

        boolean isEmpty = rows.isEmpty();
        emptyHint.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        listView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showAddCategoryDialog() {
        final EditText input = new EditText(this);
        input.setHint(R.string.hint_category_name);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        input.setPadding(pad, pad, pad, pad / 2);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.add_category)
                .setView(input)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, R.string.invalid_category_name, Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, Integer> map = RemedyCatalog.categoryCountsForUi(this, db);
            if (RemedyCatalog.containsCategoryName(map, name)) {
                Toast.makeText(this, R.string.category_exists, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!db.insertAdminCategory(name)) {
                Toast.makeText(this, R.string.category_exists, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, R.string.category_added, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            reloadCategories();
        }));

        dialog.show();
    }

    private void showCategoryActions(String categoryName) {
        new AlertDialog.Builder(this)
                .setTitle(categoryName)
                .setItems(new CharSequence[]{
                        getString(R.string.action_edit),
                        getString(R.string.action_delete)
                }, (d, which) -> {
                    if (which == 0) {
                        showRenameCategoryDialog(categoryName);
                    } else {
                        showDeleteCategoryDialog(categoryName);
                    }
                })
                .show();
    }

    private void showRenameCategoryDialog(final String oldName) {
        int customN = db.getCustomRemedyCountForCategory(oldName);
        boolean adminPlace = db.adminCategoryRowExists(oldName);
        if (customN == 0 && !adminPlace) {
            Toast.makeText(this, R.string.cannot_rename_built_in_category, Toast.LENGTH_LONG).show();
            return;
        }

        final EditText input = new EditText(this);
        input.setText(oldName);
        input.setHint(R.string.hint_category_name);
        int pad = (int)(20 * getResources().getDisplayMetrics().density);
        input.setPadding(pad, pad, pad, pad / 2);
        input.setSelectAllOnFocus(true);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.edit_category)
                .setView(input)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        Context context = this;
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newName = input.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(context, R.string.invalid_category_name, Toast.LENGTH_SHORT).show();
                return;
            }
            if (oldName.equals(newName)) {
                dialog.dismiss();
                return;
            }
            Map<String, Integer> map = RemedyCatalog.categoryCountsForUi(context, db);
            String existingCategory = RemedyCatalog.findCategoryDisplayName(map, newName);
            if (existingCategory != null && !existingCategory.equalsIgnoreCase(oldName)) {
                Toast.makeText(context, R.string.category_merge_blocked, Toast.LENGTH_SHORT).show();
                return;
            }
            int updated = db.renameCategoryEverywhere(oldName, newName);
            if (updated < 0) {
                Toast.makeText(context, R.string.rename_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            if (updated > 0) {
                Toast.makeText(context, context.getString(R.string.category_renamed_format, updated), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, R.string.category_renamed_empty_slot, Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            reloadCategories();
        }));

        dialog.show();
    }

    private void showDeleteCategoryDialog(String categoryName) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_category_title)
                .setMessage(getString(R.string.delete_category_admin_msg, categoryName))
                .setPositiveButton(R.string.btn_remove, (d, w) -> {
                    int removed = db.deleteCustomRemediesByCategory(categoryName);
                    db.deleteAdminCategoryRow(categoryName);
                    Toast.makeText(this, getString(R.string.remedies_removed_format, removed), Toast.LENGTH_SHORT).show();
                    reloadCategories();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
