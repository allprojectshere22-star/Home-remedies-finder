package com.example.remedy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.HashSet;
import java.util.Set;

public class SavedActivity extends AppCompatActivity {

    LinearLayout container;
    TextView emptyView;
    SharedPreferences sp;
    Set<String> savedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        container = findViewById(R.id.container);
        emptyView = findViewById(R.id.emptyView);

        sp = getSharedPreferences("UserSession", MODE_PRIVATE);

        loadItems();
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private void loadItems() {
        container.removeAllViews();

        savedItems = new HashSet<>(sp.getStringSet("saved_remedies", new HashSet<>()));

        if (savedItems.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        int green = ContextCompat.getColor(this, R.color.green_primary);
        int primaryLight = ContextCompat.getColor(this, R.color.primaryLight);

        for (String item : savedItems) {
            String[] parts = item.split("\\|", -1);

            String category = parts.length > 0 ? parts[0] : "General";
            String name = parts.length > 1 ? parts[1] : "Unknown";
            String description = parts.length > 2 ? parts[2] : "";
            String ingredients = parts.length > 3 ? parts[3] : "";
            String steps = parts.length > 4 ? parts[4] : "";
            String benefits = parts.length > 5 ? parts[5] : "";

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            int pad = dp(16);
            card.setPadding(pad, pad, pad, pad);
            card.setBackgroundResource(R.drawable.fav_saved_card_bg);
            card.setElevation(dp(2));

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.bottomMargin = dp(12);
            card.setLayoutParams(cardParams);

            TextView nameTv = new TextView(this);
            nameTv.setText(name);
            nameTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            nameTv.setTextColor(green);
            nameTv.setTypeface(null, Typeface.BOLD);
            nameTv.setMaxLines(2);
            nameTv.setEllipsize(TextUtils.TruncateAt.END);

            TextView catTv = new TextView(this);
            catTv.setText(category);
            catTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            catTv.setTextColor(green);
            catTv.setTypeface(null, Typeface.BOLD);
            catTv.setBackgroundResource(R.drawable.remedy_category_chip_bg);
            int cph = dp(10);
            int cpv = dp(4);
            catTv.setPadding(cph, cpv, cph, cpv);
            LinearLayout.LayoutParams chipLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            chipLp.topMargin = dp(8);
            catTv.setLayoutParams(chipLp);

            View divider = new View(this);
            divider.setBackgroundColor(primaryLight);
            LinearLayout.LayoutParams divLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(1)
            );
            divLp.topMargin = dp(14);
            divLp.bottomMargin = dp(12);
            divider.setLayoutParams(divLp);

            LinearLayout buttonRow = new LinearLayout(this);
            buttonRow.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                    0,
                    dp(48),
                    1f
            );
            btnLp.setMarginEnd(dp(6));

            Button detailsBtn = new Button(this);
            detailsBtn.setText(R.string.view_details);
            detailsBtn.setTextColor(0xFFFFFFFF);
            detailsBtn.setBackgroundResource(R.drawable.btn_filled_green);
            detailsBtn.setAllCaps(false);
            detailsBtn.setLayoutParams(btnLp);
            detailsBtn.setOnClickListener(v -> {
                Intent intent = new Intent(SavedActivity.this, DetailActivity.class);
                intent.putExtra("title", name);
                intent.putExtra("description", description);
                intent.putExtra("ingredients", ingredients);
                intent.putExtra("steps", steps);
                intent.putExtra("benefits", benefits);
                intent.putExtra("safe", "");
                intent.putExtra("avoid", "");
                intent.putExtra("caution", "");
                intent.putExtra("doctor", "");
                intent.putExtra("image", "");
                startActivity(intent);
            });

            LinearLayout.LayoutParams btnLp2 = new LinearLayout.LayoutParams(
                    0,
                    dp(48),
                    1f
            );
            btnLp2.setMarginStart(dp(6));

            Button removeBtn = new Button(this);
            removeBtn.setText(R.string.btn_remove);
            removeBtn.setTextColor(green);
            removeBtn.setBackgroundResource(R.drawable.btn_outline_green);
            removeBtn.setAllCaps(false);
            removeBtn.setLayoutParams(btnLp2);
            removeBtn.setOnClickListener(v -> {
                savedItems.remove(item);
                sp.edit().putStringSet("saved_remedies", savedItems).apply();
                loadItems();
            });

            buttonRow.addView(detailsBtn);
            buttonRow.addView(removeBtn);

            card.addView(nameTv);
            card.addView(catTv);
            card.addView(divider);
            card.addView(buttonRow);
            container.addView(card);
        }
    }
}
