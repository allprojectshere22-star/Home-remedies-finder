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

public class SignupActivity extends AppCompatActivity {

    EditText username, email, password;
    Button signupBtn;
    TextView gotoLogin, errorText;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.signupUsername);
        email = findViewById(R.id.signupEmail);
        password = findViewById(R.id.signupPassword);
        signupBtn = findViewById(R.id.btnSignup);
        gotoLogin = findViewById(R.id.gotoLogin);
        errorText = findViewById(R.id.errorText);

        db = new DatabaseHelper(this);

        signupBtn.setOnClickListener(v -> {

            String user = username.getText().toString().trim();
            String mail = email.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if(user.isEmpty() || mail.isEmpty() || pass.isEmpty()){

                errorText.setText("Please fill all fields");
                errorText.setVisibility(View.VISIBLE);
                return;
            }

            boolean inserted = db.insertUser(user, mail, pass);

            if(inserted){

                errorText.setVisibility(View.GONE);

                SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString("username", user);
                editor.putString("email", mail);
                editor.putBoolean("isLoggedIn", true);

                editor.commit();

                // SUCCESS POPUP DIALOG
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_login_success, null);

                AlertDialog dialog = new AlertDialog.Builder(SignupActivity.this)
                        .setView(dialogView)
                        .setCancelable(false)
                        .create();

                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                TextView message = dialogView.findViewById(R.id.dialogMessage);
                Button button = dialogView.findViewById(R.id.dialogButton);

                message.setText("Yayyy!! Welcome " + user + "! Your account has been created successfully.");

                button.setOnClickListener(view -> {

                    dialog.dismiss();

                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });

                dialog.show();

            } else {

                errorText.setText("User already exists");
                errorText.setVisibility(View.VISIBLE);
            }

        });

        gotoLogin.setOnClickListener(v ->
                startActivity(new Intent(SignupActivity.this, LoginActivity.class)));
    }
}