package com.example.remedy;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class AdminRemoveUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_remove_user);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        AdminToolbarUi.setupBack(toolbar, this);

        EditText input = findViewById(R.id.inputUserEmail);
        MaterialButton btn = findViewById(R.id.btnConfirmRemoveUser);
        DatabaseHelper db = new DatabaseHelper(this);

        btn.setOnClickListener(v -> {
            String email = input.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, R.string.email_required, Toast.LENGTH_SHORT).show();
                return;
            }
            boolean deleted = db.deleteUserByEmail(email);
            if (deleted) {
                Toast.makeText(this, R.string.user_removed, Toast.LENGTH_SHORT).show();
                input.setText("");
            } else {
                Toast.makeText(this, R.string.user_not_found, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
