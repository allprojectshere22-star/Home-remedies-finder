package com.example.remedy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class AdminRemediesListActivity extends AppCompatActivity implements AdminRemedyListAdapter.Listener {

    private RecyclerView recyclerView;
    private TextView emptyHint;
    private DatabaseHelper db;
    private AdminRemedyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_remedies_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        AdminToolbarUi.setupBack(toolbar, this);

        recyclerView = findViewById(R.id.recyclerCustomRemedies);
        emptyHint = findViewById(R.id.emptyRemediesHint);
        db = new DatabaseHelper(this);

        adapter = new AdminRemedyListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        List<AdminRemedyListRow> rows = new ArrayList<>();
        for (Remedy r : RemedyCatalog.loadBundledRemedies(this)) {
            rows.add(new AdminRemedyListRow(null, r));
        }
        for (DatabaseHelper.CustomRemedyWithId c : db.getAllCustomRemediesWithId()) {
            rows.add(new AdminRemedyListRow(c.id, c.remedy));
        }
        adapter.setItems(rows);
        boolean isEmpty = rows.isEmpty();
        emptyHint.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDetails(AdminRemedyListRow row) {
        Remedy r = row.remedy;
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("title", safe(r.getTitle()));
        intent.putExtra("description", safe(r.getDescription()));
        intent.putExtra("ingredients", safe(r.getIngredients()));
        intent.putExtra("steps", safe(r.getSteps()));
        intent.putExtra("benefits", safe(r.getBenefits()));
        intent.putExtra("safe", safe(r.getSafe()));
        intent.putExtra("avoid", safe(r.getAvoid()));
        intent.putExtra("caution", safe(r.getCaution()));
        intent.putExtra("doctor", safe(r.getDoctor()));
        intent.putExtra("image", safe(r.getImage()));
        startActivity(intent);
    }

    @Override
    public void onEdit(AdminRemedyListRow row) {
        if (!row.isCustom()) {
            Toast.makeText(this, R.string.admin_remedy_catalog_readonly, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, AdminAddRemedyActivity.class);
        intent.putExtra(AdminAddRemedyActivity.EXTRA_EDIT_REMEDY_ID, row.customId);
        startActivity(intent);
    }

    @Override
    public void onDelete(AdminRemedyListRow row) {
        if (!row.isCustom()) {
            Toast.makeText(this, R.string.admin_remedy_catalog_readonly, Toast.LENGTH_SHORT).show();
            return;
        }
        Remedy r = row.remedy;
        new AlertDialog.Builder(this)
                .setTitle(R.string.action_delete)
                .setMessage(getString(R.string.admin_delete_remedy_msg, r.getTitle()))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.action_delete, (d, w) -> {
                    if (db.deleteCustomRemedyById(row.customId)) {
                        Toast.makeText(this, R.string.admin_remedy_deleted, Toast.LENGTH_SHORT).show();
                        refreshList();
                    } else {
                        Toast.makeText(this, R.string.admin_remedy_delete_failed, Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private static String safe(String s) {
        return s != null ? s : "";
    }
}
