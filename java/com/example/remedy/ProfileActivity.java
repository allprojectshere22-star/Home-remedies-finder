package com.example.remedy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_PROFILE = "UserProfile";
    private static final String KEY_DISPLAY_NAME = "display_name";
    private static final String KEY_AVATAR_URI = "avatar_uri";
    private static final String KEY_PROFILE_EMAIL = "profile_email";

    EditText userName, userEmail;
    View aboutRemedy;

    ImageView profileImage;

    Button logoutBtn, editBtn, editPhotoBtn, saveBtn, cancelBtn;

    LinearLayout editButtons;

    SharedPreferences sp;
    SharedPreferences profilePrefs;
    View savedLayout;
    View favLayout;

    private static final int PICK_IMAGE = 1;

    private final String[] healthTips = {
            "Drink enough water throughout the day 💧",
            "Eat fruits daily for better immunity 🍎",
            "Include vegetables in every meal 🥗",
            "Avoid junk food as much as possible 🚫",
            "Get at least 7-8 hours of sleep 😴",
            "Exercise or walk for 30 minutes daily 🚶",
            "Start your day with a healthy breakfast 🍳",
            "Reduce sugar intake for better health 🍬",
            "Take short breaks from screens 👀",
            "Practice deep breathing to reduce stress 🧘",
            "Maintain good posture while sitting 💺",
            "Wash your hands regularly 🧼",
            "Avoid skipping meals ⏰",
            "Drink warm water in the morning 🌅",
            "Eat home-cooked food more often 🍲"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton btnBack = findViewById(R.id.btnProfileBack);
        btnBack.setOnClickListener(v -> finish());

        sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        profilePrefs = getSharedPreferences(PREFS_PROFILE, MODE_PRIVATE);

        profileImage = findViewById(R.id.profileImage);

        userName = findViewById(R.id.txtUsername);
        userEmail = findViewById(R.id.txtEmail);

        logoutBtn = findViewById(R.id.btnLogout);
        saveBtn = findViewById(R.id.btnSave);
        cancelBtn = findViewById(R.id.btnCancel);
        editBtn = findViewById(R.id.btnEditProfile);
        editPhotoBtn = findViewById(R.id.btnEditPhoto);

        editButtons = findViewById(R.id.editButtons);
        aboutRemedy = findViewById(R.id.aboutRemedy);

        View healthTipsLayout = findViewById(R.id.healthTipsLayout);

        favLayout = findViewById(R.id.favLayout);
        savedLayout = findViewById(R.id.savedLayout);

        migrateLegacyProfileIfNeeded();
        loadProfileFields();

        favLayout.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, FavoritesActivity.class)));

        savedLayout.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, SavedActivity.class)));

        aboutRemedy.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, AboutActivity.class)));

        profileImage.setOnClickListener(v -> openGallery());
        editPhotoBtn.setOnClickListener(v -> openGallery());

        userEmail.setEnabled(false);
        userName.setEnabled(false);

        editButtons.setVisibility(View.GONE);

        editBtn.setOnClickListener(v -> {
            userEmail.setEnabled(true);
            userName.setEnabled(true);

            userEmail.setBackgroundResource(R.drawable.edit_border);
            userName.setBackgroundResource(R.drawable.edit_border);

            editButtons.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.GONE);
        });

        cancelBtn.setOnClickListener(v -> {
            loadProfileFields();
            userEmail.setEnabled(false);
            userName.setEnabled(false);

            userEmail.setBackgroundResource(R.drawable.profile_field_bg);
            userName.setBackgroundResource(R.drawable.profile_field_bg);

            editButtons.setVisibility(View.GONE);
            editBtn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
        });

        saveBtn.setOnClickListener(v -> {
            SharedPreferences.Editor profileEd = profilePrefs.edit();
            profileEd.putString(KEY_DISPLAY_NAME, userName.getText().toString().trim());
            profileEd.putString(KEY_PROFILE_EMAIL, userEmail.getText().toString().trim());
            profileEd.apply();

            if (Boolean.TRUE.equals(sp.getBoolean("isLoggedIn", false))) {
                sp.edit().putString("username", userName.getText().toString().trim()).apply();
            }

            userEmail.setEnabled(false);
            userName.setEnabled(false);

            userEmail.setBackgroundResource(R.drawable.profile_field_bg);
            userName.setBackgroundResource(R.drawable.profile_field_bg);

            editButtons.setVisibility(View.GONE);
            editBtn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
        });

        healthTipsLayout.setOnClickListener(v -> showRandomTip());

        logoutBtn.setOnClickListener(v -> showLogoutDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (editButtons.getVisibility() != View.VISIBLE) {
            loadProfileFields();
        }
    }

    private void migrateLegacyProfileIfNeeded() {
        SharedPreferences.Editor pe = profilePrefs.edit();
        boolean changed = false;
        if (!profilePrefs.contains(KEY_AVATAR_URI)) {
            String legacy = sp.getString("profileImage", null);
            if (legacy != null && !legacy.isEmpty()) {
                pe.putString(KEY_AVATAR_URI, legacy);
                changed = true;
            }
        }
        if (!profilePrefs.contains(KEY_DISPLAY_NAME)) {
            String u = sp.getString("username", null);
            if (u != null && !u.isEmpty()) {
                pe.putString(KEY_DISPLAY_NAME, u);
                changed = true;
            }
        }
        String sessionEmail = sp.getString("email", null);
        if (!profilePrefs.contains(KEY_PROFILE_EMAIL) && sessionEmail != null && !sessionEmail.isEmpty()) {
            pe.putString(KEY_PROFILE_EMAIL, sessionEmail);
            changed = true;
        }
        if (changed) {
            pe.apply();
        }
    }

    private void loadProfileFields() {
        String name = profilePrefs.getString(KEY_DISPLAY_NAME, null);
        if (name == null || name.isEmpty()) {
            name = sp.getString("username", "User");
        }
        userName.setText(name);

        String sessionEmail = sp.getString("email", null);
        if (sessionEmail != null && !sessionEmail.isEmpty()) {
            userEmail.setText(sessionEmail);
        } else {
            String stored = profilePrefs.getString(KEY_PROFILE_EMAIL, "");
            userEmail.setText(stored.isEmpty() ? "Not logged in" : stored);
        }

        String avatar = profilePrefs.getString(KEY_AVATAR_URI, null);
        if (avatar != null && !avatar.isEmpty()) {
            try {
                profileImage.setImageURI(Uri.parse(avatar));
            } catch (Exception e) {
                profileImage.setImageResource(R.drawable.user_profile);
            }
        } else {
            profileImage.setImageResource(R.drawable.user_profile);
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.remove("email");
                    editor.remove("username");
                    editor.remove("profileImage");
                    editor.putBoolean("isLoggedIn", false);
                    editor.apply();

                    Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRandomTip() {
        int index = new java.util.Random().nextInt(healthTips.length);

        new AlertDialog.Builder(this)
                .setTitle("Health Tip 💡")
                .setMessage(healthTips[index])
                .setPositiveButton("OK", null)
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            profileImage.setImageURI(imageUri);

            profilePrefs.edit().putString(KEY_AVATAR_URI, imageUri.toString()).apply();
        }
    }
}
