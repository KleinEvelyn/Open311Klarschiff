package com.iu.open311_klarschiff;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.stepstone.stepper.VerificationError;

public class Step4Fragment extends AbstractStepFragment {

    private View view;
    private TextInputEditText descriptionField;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_issue_4, container, false);
        descriptionField = view.findViewById(R.id.inputDescription);

        addEditTextChangeHandling();

        return view;
    }

    @Override
    public VerificationError verifyStep() {
        closeKeyboard(view);

        if (null == getViewModel().getDescription() || getViewModel().getDescription().isEmpty()) {
            return new VerificationError(getResources().getString(R.string.error_step4));
        }

        return null;
    }

    @Override
    public void onSelected() {
        getActivity().setTitle(R.string.new_issue_step4);
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    private void addEditTextChangeHandling() {
        if (null != descriptionField) {
            descriptionField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    getViewModel().setDescription(editable.toString());
                }
            });
        }
    }
}
