package com.example.remedy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 5;

    public static final String DB_NAME = "UserDB";
    public static final String TABLE_NAME = "users";
    public static final String TABLE_ADMIN = "admin";
    public static final String TABLE_CUSTOM_REMEDIES = "custom_remedies";
    public static final String TABLE_ADMIN_CATEGORIES = "admin_categories";

    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT," +
                    "email TEXT," +
                    "password TEXT)";

    private static final String CREATE_ADMIN_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ADMIN + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "email TEXT UNIQUE," +
                    "password TEXT)";

    private static final String CREATE_CUSTOM_REMEDIES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOM_REMEDIES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT," +
                    "category TEXT," +
                    "ingredients TEXT," +
                    "description TEXT," +
                    "steps TEXT," +
                    "safe TEXT," +
                    "avoid TEXT," +
                    "benefits TEXT," +
                    "caution TEXT," +
                    "doctor TEXT," +
                    "image TEXT," +
                    "common INTEGER DEFAULT 0)";

    private static final String CREATE_ADMIN_CATEGORIES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ADMIN_CATEGORIES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT UNIQUE NOT NULL)";

    public static final class CustomRemedyWithId {
        public final long id;
        public final Remedy remedy;

        public CustomRemedyWithId(long id, Remedy remedy) {
            this.id = id;
            this.remedy = remedy;
        }
    }

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ensureCoreTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ensureCoreTables(db);
    }

    private void ensureCoreTables(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ADMIN_TABLE);
        db.execSQL(CREATE_CUSTOM_REMEDIES_TABLE);
        db.execSQL(CREATE_ADMIN_CATEGORIES_TABLE);
        db.execSQL("INSERT OR IGNORE INTO " + TABLE_ADMIN +
                " (email, password) VALUES ('admin@gmail.com', 'admin123')");
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static void putText(ContentValues values, String key, String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            values.putNull(key);
        } else {
            values.put(key, trimmed);
        }
    }

    private static String normalizedEqualsClause(String columnName) {
        return "LOWER(TRIM(" + columnName + ")) = LOWER(TRIM(?))";
    }

    public boolean insertUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE email=? AND password=?",
                new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getUsername(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT username FROM " + TABLE_NAME + " WHERE email=?",
                new String[]{email});
        String username = "User";
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                username = cursor.getString(0);
            }
            cursor.close();
        }
        return username;
    }

    public boolean checkAdmin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_ADMIN + " WHERE email=? AND password=?",
                new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean addCustomRemedy(String title, String category, String ingredients,
                                   String description, String steps, String safe,
                                   String avoid, String benefits, String caution,
                                   String doctor, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        putText(values, "title", title);
        putText(values, "category", category);
        putText(values, "ingredients", ingredients);
        putText(values, "description", description);
        putText(values, "steps", steps);
        putText(values, "safe", safe);
        putText(values, "avoid", avoid);
        putText(values, "benefits", benefits);
        putText(values, "caution", caution);
        putText(values, "doctor", doctor);
        putText(values, "image", image);
        values.put("common", 1);
        long result = db.insert(TABLE_CUSTOM_REMEDIES, null, values);
        return result != -1;
    }

    public boolean updateCustomRemedy(long id, String title, String category, String ingredients,
                                      String description, String steps, String safe,
                                      String avoid, String benefits, String caution,
                                      String doctor, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        putText(values, "title", title);
        putText(values, "category", category);
        putText(values, "ingredients", ingredients);
        putText(values, "description", description);
        putText(values, "steps", steps);
        putText(values, "safe", safe);
        putText(values, "avoid", avoid);
        putText(values, "benefits", benefits);
        putText(values, "caution", caution);
        putText(values, "doctor", doctor);
        putText(values, "image", image);
        values.put("common", 1);
        return db.update(TABLE_CUSTOM_REMEDIES, values, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public Remedy getCustomRemedyById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CUSTOM_REMEDIES + " WHERE id=?", new String[]{String.valueOf(id)});
        if (c == null) {
            return null;
        }
        try {
            if (c.moveToFirst()) {
                return remedyFromCursor(c);
            }
            return null;
        } finally {
            c.close();
        }
    }

    public List<CustomRemedyWithId> getAllCustomRemediesWithId() {
        ArrayList<CustomRemedyWithId> out = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CUSTOM_REMEDIES + " ORDER BY title COLLATE NOCASE", null);
        if (c == null) {
            return out;
        }
        try {
            while (c.moveToNext()) {
                long rowId = c.getLong(c.getColumnIndexOrThrow("id"));
                out.add(new CustomRemedyWithId(rowId, remedyFromCursor(c)));
            }
        } finally {
            c.close();
        }
        return out;
    }

    private Remedy remedyFromCursor(Cursor c) {
        return new Remedy(
                c.getString(c.getColumnIndexOrThrow("title")),
                c.getString(c.getColumnIndexOrThrow("category")),
                c.getInt(c.getColumnIndexOrThrow("common")) == 1,
                c.getString(c.getColumnIndexOrThrow("ingredients")),
                c.getString(c.getColumnIndexOrThrow("description")),
                c.getString(c.getColumnIndexOrThrow("steps")),
                c.getString(c.getColumnIndexOrThrow("benefits")),
                c.getString(c.getColumnIndexOrThrow("safe")),
                c.getString(c.getColumnIndexOrThrow("avoid")),
                c.getString(c.getColumnIndexOrThrow("caution")),
                c.getString(c.getColumnIndexOrThrow("doctor")),
                c.getString(c.getColumnIndexOrThrow("image"))
        );
    }

    public boolean deleteCustomRemedyById(long id) {
        return getWritableDatabase().delete(TABLE_CUSTOM_REMEDIES, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public Cursor getAllUsers() {
        return getReadableDatabase().rawQuery("SELECT username, email FROM " + TABLE_NAME + " ORDER BY username COLLATE NOCASE", null);
    }

    public boolean deleteUserByEmail(String email) {
        return getWritableDatabase().delete(TABLE_NAME, "email=?", new String[]{email}) > 0;
    }

    public int getCustomRemediesCount() {
        Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + TABLE_CUSTOM_REMEDIES, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int getUserCount() {
        Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public boolean insertAdminCategory(String name) {
        String normalizedName = trimToNull(name);
        if (normalizedName == null || adminCategoryRowExists(normalizedName)) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", normalizedName);
        try {
            return db.insertOrThrow(TABLE_ADMIN_CATEGORIES, null, values) != -1;
        } catch (Exception e) {
            return false;
        }
    }

    public int deleteAdminCategoryRow(String name) {
        return getWritableDatabase().delete(
                TABLE_ADMIN_CATEGORIES,
                normalizedEqualsClause("name"),
                new String[]{name}
        );
    }

    public boolean adminCategoryRowExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM " + TABLE_ADMIN_CATEGORIES +
                        " WHERE " + normalizedEqualsClause("name") + " LIMIT 1",
                new String[]{name}
        );
        if (c == null) {
            return false;
        }
        try {
            return c.moveToFirst();
        } finally {
            c.close();
        }
    }

    public List<String> getAllAdminCategoryNames() {
        List<String> out = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM " + TABLE_ADMIN_CATEGORIES + " ORDER BY name COLLATE NOCASE", null);
        if (c == null) {
            return out;
        }
        try {
            while (c.moveToNext()) {
                out.add(c.getString(0));
            }
        } finally {
            c.close();
        }
        return out;
    }

    public int getCustomRemedyCountForCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_CUSTOM_REMEDIES +
                        " WHERE " + normalizedEqualsClause("category"),
                new String[]{category}
        );
        if (cursor == null) {
            return 0;
        }
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    public int renameCategoryEverywhere(String oldName, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String normalizedNewName = trimToNull(newName);
            if (normalizedNewName == null) {
                return -1;
            }

            ContentValues cvRemedy = new ContentValues();
            cvRemedy.put("category", normalizedNewName);
            int updated = db.update(
                    TABLE_CUSTOM_REMEDIES,
                    cvRemedy,
                    normalizedEqualsClause("category"),
                    new String[]{oldName}
            );

            int removedAdminRows = db.delete(
                    TABLE_ADMIN_CATEGORIES,
                    normalizedEqualsClause("name"),
                    new String[]{oldName}
            );
            if (updated == 0 && removedAdminRows > 0) {
                ContentValues cvCat = new ContentValues();
                cvCat.put("name", normalizedNewName);
                db.insertWithOnConflict(TABLE_ADMIN_CATEGORIES, null, cvCat, SQLiteDatabase.CONFLICT_IGNORE);
            }

            db.setTransactionSuccessful();
            return updated;
        } catch (Exception e) {
            return -1;
        } finally {
            db.endTransaction();
        }
    }

    public int deleteCustomRemediesByCategory(String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(
                TABLE_CUSTOM_REMEDIES,
                normalizedEqualsClause("category"),
                new String[]{category}
        );
    }

    public Cursor getAllCustomRemedies() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CUSTOM_REMEDIES + " ORDER BY category COLLATE NOCASE, title COLLATE NOCASE", null);
    }
}
