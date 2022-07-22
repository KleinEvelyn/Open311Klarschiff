package com.iu.open311_klarschiff;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.iu.open311_klarschiff.databinding.FragmentSearchBinding;

public class SearchFragment extends AbstractSearchFragment {
    private FragmentSearchBinding binding;

    private FragmentSearchBinding getBinding(@NonNull LayoutInflater inflater, ViewGroup container
    ) {
        if (null == binding) {

            binding = FragmentSearchBinding.inflate(inflater, container, false);
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
        client.loadRequests();
        observeServiceRequests();

        // search
        handleSearch(binding.inputSearch, binding.searchCount);

        // Sorting
        handleSortButton(binding.btnSort);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void observeServiceRequests() {
        client.getServiceRequests().observe(getViewLifecycleOwner(), serviceRequests -> {
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