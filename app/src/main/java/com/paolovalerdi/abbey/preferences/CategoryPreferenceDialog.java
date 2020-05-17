package com.paolovalerdi.abbey.preferences;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paolovalerdi.abbey.R;
import com.paolovalerdi.abbey.adapter.CategoryAdapter;
import com.paolovalerdi.abbey.model.CategoryInfo;
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil;
import com.thesurix.gesturerecycler.GestureManager;

import java.util.List;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class CategoryPreferenceDialog extends DialogFragment {

    public static CategoryPreferenceDialog newInstance() {
        return new CategoryPreferenceDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.preference_dialog_library_categories, null);
        CategoryAdapter categoryAdapter = new CategoryAdapter();
        categoryAdapter.setData(PreferenceUtil.INSTANCE.getLibraryCategories());
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(categoryAdapter);
        new GestureManager.Builder(recyclerView)
                .setSwipeEnabled(false)
                .setManualDragEnabled(true)
                .build();


        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.library_categories)
                .setNeutralButton(R.string.reset_action, (dialog, which) -> {
                    categoryAdapter.setData(PreferenceUtil.INSTANCE.getDefaultCategories());
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    updateCategorires(categoryAdapter.getData());
                })
                .setView(view)
                .create();
    }


    private boolean validateData(List<CategoryInfo> data) {
        int count = 0;
        for (CategoryInfo categoryInfo : data) {
            if (categoryInfo.visible) count++;
        }
        if (count < 3) {
            Toast.makeText(requireContext(), "You have to select at leat three categories", Toast.LENGTH_SHORT).show();
        } else if (count > 5) {
            Toast.makeText(requireContext(), "You can only select five categories", Toast.LENGTH_SHORT).show();
        }
        return (count > 2 && count < 6);
    }

    private void updateCategorires(List<CategoryInfo> categoryInfos) {
        PreferenceUtil.INSTANCE.setLibraryCategories(categoryInfos);
    }


}
