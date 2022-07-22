package com.iu.open311_klarschiff;


import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Step2Fragment extends AbstractStepFragment {
    private ListView listView;
    private Step2EntryAdapter entryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_new_issue_2, container, false);
        listView = view.findViewById(R.id.listView);

        return view;
    }

    @Override
    public VerificationError verifyStep() {
        if (null == getViewModel().getSelectedServiceCategory()) {
            return new VerificationError(getResources().getString(R.string.error_step2));
        }

        return null;
    }

    @Override
    public void onSelected() {
        getActivity().setTitle(R.string.new_issue_step2);
        loadServiceCategories();
        handleCategoryClick();
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    private void handleCategoryClick() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            getViewModel().setSelectedServiceCategory(entryAdapter.getByPosition(position));
            entryAdapter.notifyDataSetChanged();
        });
    }

    private void loadServiceCategories() {
        getViewModel().loadServiceCategories();

        getViewModel().getServiceCategories()
                .observe(getViewLifecycleOwner(), serviceCategories -> {
                    if (null == serviceCategories || serviceCategories.isEmpty()) {
                        return;
                    }

                    List<Pair<Integer, String>> services = new ArrayList<>();
                    Integer selectedGroup =
                            getViewModel().getSelectedServiceCategoryGroup().first;

                    for (ServiceCategory serviceCategory : serviceCategories) {
                        if (!serviceCategory.groupId.equals(selectedGroup)) {
                            continue;
                        }
                        if (services.stream()
                                .noneMatch(pair -> pair.first.equals(
                                        serviceCategory.serviceCode))) {
                            services.add(Pair.create(serviceCategory.serviceCode,
                                    serviceCategory.serviceName
                            ));
                        }
                    }
                    services.sort(Comparator.comparing(pair -> pair.second));

                    entryAdapter =
                            new Step2EntryAdapter(getContext(), services, getViewModel(),
                                    getResources()
                            );
                    listView.setAdapter(entryAdapter);
                });
    }
}
