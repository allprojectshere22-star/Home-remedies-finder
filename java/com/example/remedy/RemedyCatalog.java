package com.example.remedy;

import android.content.Context;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Single source for remedy data: {@code assets/remedies.json} plus custom rows in SQLite.
 */
public final class RemedyCatalog {

    public static final String ASSET_REMEDIES = "remedies.json";

    private RemedyCatalog() {
    }

    public static ArrayList<Remedy> loadBundledRemedies(Context context) {
        ArrayList<Remedy> list = new ArrayList<>();
        try (InputStream is = context.getAssets().open(ASSET_REMEDIES);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }

            JSONArray array = new JSONArray(new String(os.toByteArray(), StandardCharsets.UTF_8));
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                list.add(new Remedy(
                        o.getString("title"),
                        o.getString("category"),
                        o.getBoolean("common"),
                        o.getString("ingredients"),
                        o.getString("description"),
                        o.getString("steps"),
                        o.getString("benefits"),
                        o.getString("safe"),
                        o.getString("avoid"),
                        o.getString("caution"),
                        o.getString("doctor"),
                        o.getString("image")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void appendCustomRemedies(DatabaseHelper db, ArrayList<Remedy> dest) {
        Cursor cursor = null;
        try {
            cursor = db.getAllCustomRemedies();
            if (cursor == null) {
                return;
            }
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                boolean common = cursor.getInt(cursor.getColumnIndexOrThrow("common")) == 1;
                String ingredients = cursor.getString(cursor.getColumnIndexOrThrow("ingredients"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String steps = cursor.getString(cursor.getColumnIndexOrThrow("steps"));
                String benefits = cursor.getString(cursor.getColumnIndexOrThrow("benefits"));
                String safe = cursor.getString(cursor.getColumnIndexOrThrow("safe"));
                String avoid = cursor.getString(cursor.getColumnIndexOrThrow("avoid"));
                String caution = cursor.getString(cursor.getColumnIndexOrThrow("caution"));
                String doctor = cursor.getString(cursor.getColumnIndexOrThrow("doctor"));
                String image = cursor.getString(cursor.getColumnIndexOrThrow("image"));

                dest.add(new Remedy(title, category, common, ingredients, description,
                        steps, benefits, safe, avoid, caution, doctor, image));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static ArrayList<Remedy> loadAllRemedies(Context context, DatabaseHelper db) {
        ArrayList<Remedy> all = loadBundledRemedies(context);
        appendCustomRemedies(db, all);
        return all;
    }

    public static int countBundled(Context context) {
        return loadBundledRemedies(context).size();
    }

    public static int countAllRemedies(Context context, DatabaseHelper db) {
        return countBundled(context) + db.getCustomRemediesCount();
    }

    public static String cleanCategoryName(String category) {
        return category == null ? "" : category.trim();
    }

    public static String normalizeCategoryKey(String category) {
        return cleanCategoryName(category).toLowerCase(Locale.ROOT);
    }

    public static String findCategoryDisplayName(Map<String, Integer> categories, String categoryName) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }

        String requestedKey = normalizeCategoryKey(categoryName);
        if (requestedKey.isEmpty()) {
            return null;
        }

        for (String existingName : categories.keySet()) {
            if (requestedKey.equals(normalizeCategoryKey(existingName))) {
                return existingName;
            }
        }
        return null;
    }

    public static boolean containsCategoryName(Map<String, Integer> categories, String categoryName) {
        return findCategoryDisplayName(categories, categoryName) != null;
    }

    public static boolean hasCategory(Context context, DatabaseHelper db, String categoryName) {
        return containsCategoryName(categoryCountsForUi(context, db), categoryName);
    }

    public static String resolveCategoryName(Context context, DatabaseHelper db, String categoryName) {
        String existingName = findCategoryDisplayName(categoryCountsForUi(context, db), categoryName);
        return existingName != null ? existingName : cleanCategoryName(categoryName);
    }

    public static Map<String, Integer> categoryCounts(Context context, DatabaseHelper db) {
        LinkedHashMap<String, String> displayNamesByKey = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> countsByKey = new LinkedHashMap<>();

        for (Remedy r : loadAllRemedies(context, db)) {
            accumulateCategory(displayNamesByKey, countsByKey, r.getCategory(), 1);
        }

        return toDisplayCountMap(displayNamesByKey, countsByKey);
    }

    /**
     * Same categories as the Categories screen: catalog + DB remedies, plus admin-created empty categories.
     */
    public static Map<String, Integer> categoryCountsForUi(Context context, DatabaseHelper db) {
        LinkedHashMap<String, String> displayNamesByKey = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> countsByKey = new LinkedHashMap<>();

        for (Remedy r : loadAllRemedies(context, db)) {
            accumulateCategory(displayNamesByKey, countsByKey, r.getCategory(), 1);
        }
        for (String name : db.getAllAdminCategoryNames()) {
            accumulateCategory(displayNamesByKey, countsByKey, name, 0);
        }

        return toDisplayCountMap(displayNamesByKey, countsByKey);
    }

    /**
     * Return the top N categories by count (descending). If counts are equal, sort by name.
     */
    public static java.util.List<String> getTopCategories(Context context, DatabaseHelper db, int limit) {
        Map<String, Integer> counts = categoryCountsForUi(context, db);
        java.util.List<java.util.Map.Entry<String, Integer>> entries = new java.util.ArrayList<>(counts.entrySet());
        entries.sort((e1, e2) -> {
            int cmp = Integer.compare(e2.getValue(), e1.getValue());
            if (cmp != 0) return cmp;
            return e1.getKey().compareToIgnoreCase(e2.getKey());
        });
        java.util.List<String> out = new java.util.ArrayList<>();
        for (int i = 0; i < entries.size() && out.size() < limit; i++) {
            out.add(entries.get(i).getKey());
        }
        return out;
    }

    private static void accumulateCategory(Map<String, String> displayNamesByKey,
                                           Map<String, Integer> countsByKey,
                                           String categoryName,
                                           int delta) {
        String cleanedName = cleanCategoryName(categoryName);
        if (cleanedName.isEmpty()) {
            return;
        }

        String categoryKey = normalizeCategoryKey(cleanedName);
        displayNamesByKey.putIfAbsent(categoryKey, cleanedName);
        countsByKey.put(categoryKey, countsByKey.getOrDefault(categoryKey, 0) + delta);
    }

    private static Map<String, Integer> toDisplayCountMap(Map<String, String> displayNamesByKey,
                                                          Map<String, Integer> countsByKey) {
        LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : displayNamesByKey.entrySet()) {
            counts.put(entry.getValue(), countsByKey.getOrDefault(entry.getKey(), 0));
        }
        return counts;
    }
}
