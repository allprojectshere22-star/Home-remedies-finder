package com.example.remedy;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_detail);

        TextView title = findViewById(R.id.detailTitle);
        TextView desc = findViewById(R.id.detailDesc);
        TextView ing = findViewById(R.id.detailIngredients);
        TextView steps = findViewById(R.id.detailSteps);
        TextView benefits = findViewById(R.id.detailBenefits);
        TextView safe = findViewById(R.id.detailSafe);
        TextView avoid = findViewById(R.id.detailAvoid);
        TextView caution = findViewById(R.id.detailCaution);
        TextView doctor = findViewById(R.id.detailDoctor);

        ImageView image = findViewById(R.id.detailImage);
        MaterialCardView imageCard = findViewById(R.id.detailImageCard);

        View sectionDesc = findViewById(R.id.sectionDesc);
        View sectionIngredients = findViewById(R.id.sectionIngredients);
        View sectionSteps = findViewById(R.id.sectionSteps);
        View sectionBenefits = findViewById(R.id.sectionBenefits);
        View sectionSafe = findViewById(R.id.sectionSafe);
        View sectionAvoid = findViewById(R.id.sectionAvoid);
        View sectionCaution = findViewById(R.id.sectionCaution);
        View sectionDoctor = findViewById(R.id.sectionDoctor);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        title.setText(extras.getString("title", ""));

        bindSection(sectionDesc, desc, extras.getString("description", ""));
        bindSection(sectionIngredients, ing, extras.getString("ingredients", ""));
        bindSection(sectionSteps, steps, extras.getString("steps", ""));
        bindSection(sectionBenefits, benefits, extras.getString("benefits", ""));
        bindSection(sectionSafe, safe, extras.getString("safe", ""));
        bindSection(sectionAvoid, avoid, extras.getString("avoid", ""));
        bindSection(sectionCaution, caution, extras.getString("caution", ""));
        bindSection(sectionDoctor, doctor, extras.getString("doctor", ""));

        String imgName = extras.getString("image", "");
        if (!imgName.isEmpty()) {
            int imgId = getResources().getIdentifier(imgName, "drawable", getPackageName());
            if (imgId != 0) {
                image.setImageResource(imgId);
                imageCard.setVisibility(View.VISIBLE);
                return;
            }
        }
        imageCard.setVisibility(View.GONE);
    }

    private static void bindSection(View section, TextView body, String value) {
        String text = value != null ? value.trim() : "";
        body.setText(text);
        section.setVisibility(text.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
