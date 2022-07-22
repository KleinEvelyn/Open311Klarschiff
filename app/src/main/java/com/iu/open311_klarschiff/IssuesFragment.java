package com.iu.open311_klarschiff;


import static android.app.Activity.RESULT_OK;
import static com.iu.open311_klarschiff.MainActivity.INTENT_EXTRA_SHOW_MY_REQUESTS;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.iu.open311_klarschiff.databinding.FragmentIssuesBinding;

public class IssuesFragment extends AbstractSearchFragment {

    private FragmentIssuesBinding binding;

    private FragmentIssuesBinding getBinding(@NonNull LayoutInflater inflater, ViewGroup container
    ) {
        if (null == binding) {

            binding = FragmentIssuesBinding.inflate(inflater, container, false);
        }
        return binding;
    }

    @Override
    protected View getViewRoot(@NonNull LayoutInflater inflater, ViewGroup container) {
        return getBinding(inflater, container).getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = getBinding(inflater, container);

        // loading results
        binding.recyclerList.loading.setVisibility(View.VISIBLE);
        client.loadMyRequests(Database.getInstance(getContext()));
        observeMyServiceRequests();

        // Sorting
        handleSortButton(binding.btnSort);

        // New Issue Button
        handleNewIssueButton();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data.hasExtra(INTENT_EXTRA_SHOW_MY_REQUESTS) && Boolean.TRUE.equals(
                    data.getBooleanExtra(INTENT_EXTRA_SHOW_MY_REQUESTS, false))) {
                client.loadMyRequests(Database.getInstance(getContext()));
            }
        }
    }

    private void handleNewIssueButton() {
        binding.btnNewIssue.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), NewIssueActivity.class);
            startActivityForResult(intent, 0);
        });
    }

    private void observeMyServiceRequests() {
        client.getMyServiceRequests().observe(getViewLifecycleOwner(), serviceRequests -> {
            if (null == serviceRequests) {
                return;
            }
            binding.recyclerList.loading.setVisibility(View.GONE);
            binding.searchCountWrapper.setVisibility(View.VISIBLE);
            binding.filterWrapper.setVisibility(View.VISIBLE);
            binding.searchCount.setText(String.valueOf(serviceRequests.size()));
            entryAdapter.setServiceRequests(serviceRequests);
        });
    }
}