package com.iu.open311_klarschiff;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputEditText;
import com.stepstone.stepper.VerificationError;

public class Step6Fragment extends AbstractStepFragment {

    private SharedPreferences defaultSharedPreferences;
    private View view;
    private TextInputEditText firstname;
    private TextInputEditText lastname;
    private TextInputEditText email;
    private TextInputEditText phone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        view = inflater.inflate(R.layout.fragment_new_issue_6, container, false);

        initFirstnameField();
        initLastnameField();
        initEmailField();
        initPhoneField();

        return view;
    }

    @Override
    public VerificationError verifyStep() {
        closeKeyboard(view);

        if (null == getViewModel().getEmail() || getViewModel().getEmail().isEmpty()) {
            return new VerificationError(getResources().getString(R.string.error_step6));
        }
        return null;
    }

    @Override
    public void onSelected() {
        getActivity().setTitle(R.string.new_issue_step6);
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    private void initFirstnameField() {
        firstname = view.findViewById(R.id.inputFirstname);
        String firstname = defaultSharedPreferences.getString("firstname", "");
        this.firstname.setText(firstname);
        getViewModel().setFirstname(firstname);
        this.firstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getViewModel().setFirstname(editable.toString());
            }
        });
    }

    private void initLastnameField() {
        lastname = view.findViewById(R.id.inputLastname);
        String lastname = defaultSharedPreferences.getString("lastname", "");
        this.lastname.setText(lastname);
        getViewModel().setLastname(lastname);
        this.lastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getViewModel().setLastname(editable.toString());
            }
        });
    }

    private void initEmailField() {
        email = view.findViewById(R.id.inputEmail);
        String email = defaultSharedPreferences.getString("email", "");
        this.email.setText(email);
        getViewModel().setEmail(email);
        this.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getViewModel().setEmail(editable.toString());
            }
        });
    }

    private void initPhoneField() {
        phone = view.findViewById(R.id.inputPhone);
        String phone = defaultSharedPreferences.getString("phone", "");
        this.phone.setText(phone);
        getViewModel().setPhone(phone);
        this.phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getViewModel().setPhone(editable.toString());
            }
        });
    }
}