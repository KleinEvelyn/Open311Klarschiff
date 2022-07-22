package com.iu.open311_klarschiff;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.iu.open311_klarschiff.databinding.FragmentHomeBinding;

import java.util.StringJoiner;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private final SharedPreferences.OnSharedPreferenceChangeListener listener =
            (sharedPreferences, s) -> updateUsername(sharedPreferences);

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initShownUsername();
        initButtonAdd();
        initButtonIssues();
        initButtonSearch();

        return root;
    }

    private void initButtonAdd() {
        binding.btnNewIssue.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), NewIssueActivity.class);
            startActivityForResult(intent, 0);
        });
    }

    private void initButtonIssues() {
        binding.btnIssues.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(getActivity(),
                    R.id.nav_host_fragment_content_main
            );
            navController.navigate(R.id.nav_issues);
        });
    }

    private void initButtonSearch() {
        binding.btnSearch.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(getActivity(),
                    R.id.nav_host_fragment_content_main
            );
            navController.navigate(R.id.nav_search);
        });
    }

    private void initShownUsername() {
        if (null != getContext()) {
            updateUsername(PreferenceManager.getDefaultSharedPreferences(getContext()));
            PreferenceManager.getDefaultSharedPreferences(getContext())
                    .registerOnSharedPreferenceChangeListener(listener);
        }
    }

    private void updateUsername(SharedPreferences sharedPreferences) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        stringJoiner.add(sharedPreferences.getString("firstname", ""));
        stringJoiner.add(sharedPreferences.getString("lastname", ""));
        binding.textUsername.setText(stringJoiner.toString());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (null != getContext()) {
            PreferenceManager.getDefaultSharedPreferences(getContext())
                    .registerOnSharedPreferenceChangeListener(listener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        if (null != getContext()) {
            PreferenceManager.getDefaultSharedPreferences(getContext())
                    .unregisterOnSharedPreferenceChangeListener(listener);
        }
    }
}