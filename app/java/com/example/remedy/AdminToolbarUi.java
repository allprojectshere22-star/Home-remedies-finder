package com.example.remedy;

import android.app.Activity;

import com.google.android.material.appbar.MaterialToolbar;

/**
 * Shared green admin toolbar: explicit back arrow + finish on navigation.
 */
public final class AdminToolbarUi {

    private AdminToolbarUi() {
    }

    public static void setupBack(MaterialToolbar toolbar, Activity activity) {
        toolbar.setNavigationIcon(R.drawable.ic_nav_back_white);
        toolbar.setNavigationOnClickListener(v -> activity.finish());
    }
}
