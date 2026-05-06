package com.example.remedy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AdminPanelActivity extends AppCompatActivity {

    MaterialButton btnShowAddRemedy, btnRemoveUser, btnViewRemedies;
    MaterialButton btnManageCategories, btnViewUsers, logoutBtn;
    android.widget.TextView adminWelcome, adminTotalUsers, adminTotalRemedies;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        adminWelcome = findViewById(R.id.adminWelcome);
        btnShowAddRemedy = findViewById(R.id.btnShowAddRemedy);
        btnRemoveUser = findViewById(R.id.btnRemoveUser);
        btnViewRemedies = findViewById(R.id.btnViewRemedies);
        btnManageCategories = findViewById(R.id.btnManageCategories);
        btnViewUsers = findViewById(R.id.btnViewUsers);
        logoutBtn = findViewById(R.id.btnAdminLogout);
        adminTotalUsers = findViewById(R.id.adminTotalUsers);
        adminTotalRemedies = findViewById(R.id.adminTotalRemedies);

        db = new DatabaseHelper(this);

        SharedPreferences sp = getSharedPreferences("AdminSession", MODE_PRIVATE);
        String adminEmail = sp.getString("adminEmail", "Admin");

        adminWelcome.setText(getString(R.string.admin_welcome_format, toAdminLabel(adminEmail)));

        refreshStats();

        btnShowAddRemedy.setOnClickListener(v ->
                startActivity(new Intent(this, AdminAddRemedyActivity.class)));

        btnRemoveUser.setOnClickListener(v ->
                startActivity(new Intent(this, AdminRemoveUserActivity.class)));

        btnViewRemedies.setOnClickListener(v ->
                startActivity(new Intent(this, AdminRemediesListActivity.class)));

        btnManageCategories.setOnClickListener(v ->
                startActivity(new Intent(this, AdminManageCategoriesActivity.class)));

        btnViewUsers.setOnClickListener(v ->
                startActivity(new Intent(this, AdminViewUsersActivity.class)));

        logoutBtn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isAdminLoggedIn", false);
            editor.remove("adminEmail");
            editor.apply();

            startActivity(new Intent(AdminPanelActivity.this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStats();
    }

    private void refreshStats() {
        int users = db.getUserCount();
        int remedies = RemedyCatalog.countAllRemedies(this, db);
        adminTotalUsers.setText(getString(R.string.total_users_format, users));
        adminTotalRemedies.setText(getString(R.string.total_remedies_format, remedies));
    }

    private String toAdminLabel(String adminEmail) {
        if (adminEmail == null || adminEmail.trim().isEmpty()) {
            return "Admin";
        }

        String label = adminEmail.trim();
        int atIndex = label.indexOf('@');
        if (atIndex > 0) {
            label = label.substring(0, atIndex);
        }
        if (label.isEmpty()) {
            return "Admin";
        }

        return Character.toUpperCase(label.charAt(0)) + label.substring(1);
    }
}
