package com.iu.open311_klarschiff;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stepstone.stepper.VerificationError;

import java.util.StringJoiner;

public class Step7Fragment extends AbstractStepFragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        view = inflater.inflate(R.layout.fragment_new_issue_7, container, false);

        return view;
    }

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {
        getActivity().setTitle(R.string.new_issue_step7);
        initSummary();
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    private void initSummary() {
        NewIssueViewModel viewModel = getViewModel();

        ((TextView) view.findViewById(R.id.summaryGroup)).setText(
                viewModel.getSelectedServiceCategoryGroup().second);
        ((TextView) view.findViewById(R.id.summaryCategory)).setText(
                viewModel.getSelectedServiceCategory().second);
        ((TextView) view.findViewById(R.id.summaryAddress)).setText(getAddress());
        ((TextView) view.findViewById(R.id.summaryDescription)).setText(viewModel.getDescription());
        ((TextView) view.findViewById(R.id.summaryContact)).setText(getContact());

        if (null == viewModel.getPhoto()) {
            view.findViewById(R.id.summaryPhoto).setVisibility(View.VISIBLE);
            view.findViewById(R.id.imagePreview).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.summaryPhoto)).setText(getPhoto());

        } else {
            view.findViewById(R.id.imagePreview).setVisibility(View.VISIBLE);
            view.findViewById(R.id.summaryPhoto).setVisibility(View.GONE);
            ((ImageView) view.findViewById(R.id.imagePreview)).setImageBitmap(viewModel.getPhoto());
        }
    }

    private String getAddress() {
        if (!getViewModel().getAddress().isEmpty()) {
            return getViewModel().getAddress().replace(", ", "\n");
        }

        return getResources().getString(R.string.address_by_position);
    }

    private String getPhoto() {
        return getResources().getString(R.string.no_photo);
    }

    private String getContact() {
        StringJoiner stringJoiner = new StringJoiner("\n");
        NewIssueViewModel viewModel = getViewModel();

        if (!viewModel.getFirstname().isEmpty() || !viewModel.getLastname().isEmpty()) {
            stringJoiner.add(viewModel.getFirstname() + " " + viewModel.getLastname());
        }

        stringJoiner.add(viewModel.getEmail());

        if (!viewModel.getPhone().isEmpty()) {
            stringJoiner.add(viewModel.getPhone());
        }

        return stringJoiner.toString();
    }
}