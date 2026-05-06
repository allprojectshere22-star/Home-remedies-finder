package com.example.remedy;

/**
 * Row in admin "all remedies" list. {@code customId} is null for bundled {@code remedies.json} entries.
 */
public final class AdminRemedyListRow {

    public final Long customId;
    public final Remedy remedy;

    public AdminRemedyListRow(Long customId, Remedy remedy) {
        this.customId = customId;
        this.remedy = remedy;
    }

    public boolean isCustom() {
        return customId != null;
    }
}
