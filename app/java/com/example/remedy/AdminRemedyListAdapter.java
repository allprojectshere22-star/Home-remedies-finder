package com.example.remedy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AdminRemedyListAdapter extends RecyclerView.Adapter<AdminRemedyListAdapter.VH> {

    public interface Listener {
        void onDetails(AdminRemedyListRow row);

        void onEdit(AdminRemedyListRow row);

        void onDelete(AdminRemedyListRow row);
    }

    private final List<AdminRemedyListRow> items = new ArrayList<>();
    private final Listener listener;

    public AdminRemedyListAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<AdminRemedyListRow> rows) {
        items.clear();
        if (rows != null) {
            items.addAll(rows);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_remedy_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        AdminRemedyListRow row = items.get(position);
        Remedy r = row.remedy;
        h.title.setText(r.getTitle());
        h.category.setText(r.getCategory());

        if (row.isCustom()) {
            h.badge.setText(R.string.admin_remedy_badge_custom);
            h.badge.setBackgroundResource(R.drawable.admin_remedy_badge_custom_bg);
            h.badge.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.white));
            h.btnEdit.setVisibility(View.VISIBLE);
            h.btnDelete.setVisibility(View.VISIBLE);
        } else {
            h.badge.setText(R.string.admin_remedy_badge_catalog);
            h.badge.setBackgroundResource(R.drawable.admin_remedy_badge_catalog_bg);
            h.badge.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.green_primary));
            h.btnEdit.setVisibility(View.GONE);
            h.btnDelete.setVisibility(View.GONE);
        }

        h.btnDetails.setOnClickListener(v -> listener.onDetails(row));
        h.btnEdit.setOnClickListener(v -> listener.onEdit(row));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(row));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static final class VH extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView category;
        final TextView badge;
        final MaterialButton btnDetails;
        final MaterialButton btnEdit;
        final MaterialButton btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.adminRemedyTitle);
            category = itemView.findViewById(R.id.adminRemedyCategory);
            badge = itemView.findViewById(R.id.adminRemedyBadge);
            btnDetails = itemView.findViewById(R.id.btnAdminDetails);
            btnEdit = itemView.findViewById(R.id.btnAdminEdit);
            btnDelete = itemView.findViewById(R.id.btnAdminDelete);
        }
    }
}
