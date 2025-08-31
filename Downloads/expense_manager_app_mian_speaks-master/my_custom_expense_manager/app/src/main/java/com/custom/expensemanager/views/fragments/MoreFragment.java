package com.custom.expensemanager.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.custom.expensemanager.R;
import com.custom.expensemanager.databinding.FragmentMoreBinding;

public class MoreFragment extends Fragment {

    private FragmentMoreBinding binding;
    private SharedPreferences sharedPreferences;

    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater, container, false);

        setupThemeSelection();

        return binding.getRoot();
    }

    private void setupThemeSelection() {
        // Get current theme mode
        int currentNightMode = AppCompatDelegate.getDefaultNightMode();
        RadioGroup themeGroup = binding.themeRadioGroup;

        // Set current selection based on theme
        if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.darkThemeRadio.setChecked(true);
        } else if (currentNightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            binding.lightThemeRadio.setChecked(true);
        } else {
            binding.systemThemeRadio.setChecked(true);
        }

        // Handle theme selection changes
        themeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            String message = "Theme updated";

            if (checkedId == R.id.lightThemeRadio) {
                selectedMode = AppCompatDelegate.MODE_NIGHT_NO;
                message = "Light theme applied";
            } else if (checkedId == R.id.darkThemeRadio) {
                selectedMode = AppCompatDelegate.MODE_NIGHT_YES;
                message = "Dark theme applied";
            } else if (checkedId == R.id.systemThemeRadio) {
                selectedMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                message = "System theme applied";
            }

            // Apply the theme
            AppCompatDelegate.setDefaultNightMode(selectedMode);

            // Save preference
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("theme_mode", selectedMode);
            editor.apply();

            // Show confirmation
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

            // Note: Theme change will take effect when activity is recreated
            // This is normal behavior for DayNight themes
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
