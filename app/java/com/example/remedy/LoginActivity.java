package com.example.remedy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button login;
    private TextView signup, errorText;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        login = findViewById(R.id.btnLogin);
        signup = findViewById(R.id.gotoSignup);
        errorText = findViewById(R.id.errorText);

        db = new DatabaseHelper(this);

        login.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPass = password.getText().toString().trim();

            if (userEmail.isEmpty() || userPass.isEmpty()) {
                errorText.setText(R.string.enter_email_password);
                errorText.setVisibility(View.VISIBLE);
                return;
            }

            // 1. Check for Admin Credentials (admin@gmail.com / admin123)
            if (db.checkAdmin(userEmail, userPass)) {
                SharedPreferences adminSp = getSharedPreferences("AdminSession", MODE_PRIVATE);
                adminSp.edit()
                        .putBoolean("isAdminLoggedIn", true)
                        .putString("adminEmail", userEmail)
                        .apply();

                Intent intent = new Intent(LoginActivity.this, AdminPanelActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            // 2. Check for normal User Credentials
            if (db.checkUser(userEmail, userPass)) {
                errorText.setVisibility(View.GONE);

                SharedPreferences userSp = getSharedPreferences("UserSession", MODE_PRIVATE);
                userSp.edit()
                        .putString("email", userEmail)
                        .putString("username", db.getUsername(userEmail))
                        .putBoolean("isLoggedIn", true)
                        .apply();

                showLoginSuccessDialog(db.getUsername(userEmail));
            } else {
                errorText.setText(R.string.invalid_email_password);
                errorText.setVisibility(View.VISIBLE);
            }
        });

        signup.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    private void showLoginSuccessDialog(String username) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_login_success, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView message = dialogView.findViewById(R.id.dialogMessage);
        Button button = dialogView.findViewById(R.id.dialogButton);

        message.setText(getString(R.string.welcome_back_format, username));

        button.setOnClickListener(view -> {
            dialog.dismiss();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });

        dialog.show();
    }
}
