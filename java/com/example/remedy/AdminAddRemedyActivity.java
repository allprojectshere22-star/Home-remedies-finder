package com.example.remedy;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class AdminAddRemedyActivity extends AppCompatActivity {

    public static final String EXTRA_EDIT_REMEDY_ID = "edit_remedy_id";

    EditText remedyTitle, remedyCategory, remedyIngredients, remedyDescription;
    EditText remedySteps, remedySafe, remedyAvoid, remedyBenefits;
    EditText remedyCaution, remedyDoctor, remedyImage;
    MaterialButton addRemedyBtn;
    DatabaseHelper db;
    private long editingId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_remedy);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        AdminToolbarUi.setupBack(toolbar, this);

        remedyTitle = findViewById(R.id.remedyTitle);
        remedyCategory = findViewById(R.id.remedyCategory);
        remedyIngredients = findViewById(R.id.remedyIngredients);
        remedyDescription = findViewById(R.id.remedyDescription);
        remedySteps = findViewById(R.id.remedySteps);
        remedySafe = findViewById(R.id.remedySafe);
        remedyAvoid = findViewById(R.id.remedyAvoid);
        remedyBenefits = findViewById(R.id.remedyBenefits);
        remedyCaution = findViewById(R.id.remedyCaution);
        remedyDoctor = findViewById(R.id.remedyDoctor);
        remedyImage = findViewById(R.id.remedyImage);
        addRemedyBtn = findViewById(R.id.btnAddRemedy);

        db = new DatabaseHelper(this);

        editingId = getIntent().getLongExtra(EXTRA_EDIT_REMEDY_ID, -1L);
        if (editingId >= 0) {
            Remedy existing = db.getCustomRemedyById(editingId);
            if (existing == null) {
                Toast.makeText(this, R.string.admin_remedy_load_failed, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            toolbar.setTitle(R.string.title_admin_edit_remedy);
            addRemedyBtn.setText(R.string.btn_save);
            populateForm(existing);
        }

        addRemedyBtn.setOnClickListener(v -> saveRemedy());
    }

    private void populateForm(Remedy r) {
        remedyTitle.setText(nullToEmpty(r.getTitle()));
        remedyCategory.setText(nullToEmpty(r.getCategory()));
        remedyIngredients.setText(nullToEmpty(r.getIngredients()));
        remedyDescription.setText(nullToEmpty(r.getDescription()));
        remedySteps.setText(nullToEmpty(r.getSteps()));
        remedySafe.setText(nullToEmpty(r.getSafe()));
        remedyAvoid.setText(nullToEmpty(r.getAvoid()));
        remedyBenefits.setText(nullToEmpty(r.getBenefits()));
        remedyCaution.setText(nullToEmpty(r.getCaution()));
        remedyDoctor.setText(nullToEmpty(r.getDoctor()));
        remedyImage.setText(nullToEmpty(r.getImage()));
    }

    private static String nullToEmpty(String s) {
        return s != null ? s : "";
    }

    private void saveRemedy() {
        String title = remedyTitle.getText().toString().trim();
        String category = RemedyCatalog.resolveCategoryName(this, db, remedyCategory.getText().toString());
        String ingredients = remedyIngredients.getText().toString().trim();
        String description = remedyDescription.getText().toString().trim();
        String steps = remedySteps.getText().toString().trim();
        String safe = remedySafe.getText().toString().trim();
        String avoid = remedyAvoid.getText().toString().trim();
        String benefits = remedyBenefits.getText().toString().trim();
        String caution = remedyCaution.getText().toString().trim();
        String doctor = remedyDoctor.getText().toString().trim();
        String image = remedyImage.getText().toString().trim();

        if (title.isEmpty() || category.isEmpty() || ingredients.isEmpty()
                || description.isEmpty() || steps.isEmpty()
                || safe.isEmpty() || avoid.isEmpty() || benefits.isEmpty()
                || caution.isEmpty() || doctor.isEmpty()) {
            Toast.makeText(this, R.string.admin_all_fields_required, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success;
        if (editingId >= 0) {
            success = db.updateCustomRemedy(editingId, title, category, ingredients, description,
                    steps, safe, avoid, benefits, caution, doctor, image);
            if (success) {
                Toast.makeText(this, R.string.admin_remedy_updated, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update remedy", Toast.LENGTH_SHORT).show();
            }
        } else {
            success = db.addCustomRemedy(title, category, ingredients, description,
                    steps, safe, avoid, benefits, caution, doctor, image);
            if (success) {
                Toast.makeText(this, "Remedy added successfully!", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(this, "Failed to add remedy", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clearFields() {
        remedyTitle.setText("");
        remedyCategory.setText("");
        remedyIngredients.setText("");
        remedyDescription.setText("");
        remedySteps.setText("");
        remedySafe.setText("");
        remedyAvoid.setText("");
        remedyBenefits.setText("");
        remedyCaution.setText("");
        remedyDoctor.setText("");
        remedyImage.setText("");
    }
}
