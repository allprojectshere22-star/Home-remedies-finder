package com.example.remedy;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdminViewUsersActivity extends AppCompatActivity {

    private static class UserItem {
        boolean isHeader;
        String text;

        UserItem(boolean isHeader, String text) {
            this.isHeader = isHeader;
            this.text = text;
        }
    }

    private static class UserAdapter extends ArrayAdapter<UserItem> {

        UserAdapter(AdminViewUsersActivity context, List<UserItem> items) {
            super(context, 0, items);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).isHeader ? 0 : 1;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            UserItem item = getItem(position);

            if (item.isHeader) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_user_header, parent, false);

                TextView header = convertView.findViewById(R.id.headerText);
                header.setText(item.text);

            } else {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_user_row, parent, false);

                TextView name = convertView.findViewById(R.id.userName);
                name.setText(item.text);
            }

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_users);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        AdminToolbarUi.setupBack(toolbar, this);

        ListView listView = findViewById(R.id.listUsers);
        TextView emptyHint = findViewById(R.id.emptyUsersHint);

        DatabaseHelper db = new DatabaseHelper(this);
        Cursor cursor = db.getAllUsers();

        List<String> users = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                users.add(name + " — " + email);
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Sort alphabetically
        Collections.sort(users, String.CASE_INSENSITIVE_ORDER);

        // Create list with headers
        List<UserItem> items = new ArrayList<>();
        char lastChar = 0;

        for (String user : users) {
            char firstChar = Character.toUpperCase(user.charAt(0));

            if (firstChar != lastChar) {
                items.add(new UserItem(true, String.valueOf(firstChar)));
                lastChar = firstChar;
            }

            items.add(new UserItem(false, user));
        }

        UserAdapter adapter = new UserAdapter(this, items);
        listView.setAdapter(adapter);

        boolean isEmpty = items.isEmpty();
        emptyHint.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        listView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}