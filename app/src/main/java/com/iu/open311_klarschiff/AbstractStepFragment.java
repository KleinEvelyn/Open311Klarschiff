package com.iu.open311_klarschiff;


import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

import com.stepstone.stepper.Step;


public abstract class AbstractStepFragment extends Fragment implements Step {

    protected void closeKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected NewIssueViewModel getViewModel() {
        return ((NewIssueActivity) getActivity()).getViewModel();
    }
}