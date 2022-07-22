package com.iu.open311_klarschiff;


import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Step1Fragment extends AbstractStepFragment {
    private GridView gridView;
    private Step1EntryAdapter entryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_new_issue_1, container, false);
        gridView = view.findViewById(R.id.gridView);

        return view;
    }

    @Override
    public VerificationError verifyStep() {
        if (null == getViewModel().getSelectedServiceCategoryGroup()) {
            return new VerificationError(getResources().getString(R.string.error_step1));
        }

        return null;
    }

    @Override
    public void onSelected() {
        getActivity().setTitle(R.string.new_issue_step1);
        loadServiceCategoryGroups();
        handleGroupClick();
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    private void handleGroupClick() {
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            getViewModel().setSelectedServiceCategoryGroup(entryAdapter.getByPosition(position));
            entryAdapter.notifyDataSetChanged();
        });
    }

    private void loadServiceCategoryGroups() {
        getViewModel().loadServiceCategories();

        getViewModel().getServiceCategories()
                .observe(getViewLifecycleOwner(), serviceCategories -> {
                    if (null == serviceCategories || serviceCategories.isEmpty()) {
                        return;
                    }

                    List<Pair<Integer, String>> groups = new ArrayList<>();
                    for (ServiceCategory serviceCategory : serviceCategories) {
                        if (groups.stream()
                                .noneMatch(pair -> pair.first.equals(
                                        serviceCategory.groupId))) {
                            groups.add(Pair.create(serviceCategory.groupId,
                                    serviceCategory.group
                            ));
                        }
                    }
                    groups.sort(Comparator.comparing(pair -> pair.second));

                    entryAdapter = new Step1EntryAdapter(getContext(), groups, getViewModel(),
                            getResources()
                    );
                    gridView.setAdapter(entryAdapter);
                });
    }
}