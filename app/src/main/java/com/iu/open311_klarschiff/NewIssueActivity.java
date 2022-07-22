package com.iu.open311_klarschiff;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.iu.open311_klarschiff.databinding.ActivityNewIssueBinding;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

public class NewIssueActivity extends DefaultActivity implements StepperLayout.StepperListener {

    private ActivityNewIssueBinding binding;
    private StepperLayout stepperLayout;
    private NewIssueViewModel viewModel;
    private boolean hasNecessaryPermissions = false;

    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    permissionMap -> {
                        hasNecessaryPermissions = !permissionMap.values().contains(false);
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewIssueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this,
                new NewIssueViewModelFactory(Database.getInstance(getApplicationContext()))
        ).get(NewIssueViewModel.class);

        setTitle(R.string.new_issue);
        stepperLayout = findViewById(R.id.stepperLayout);
        stepperLayout.setAdapter(
                new NewIssueFragment.StepAdapter(getSupportFragmentManager(), this));
        stepperLayout.setListener(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        checkNecessaryPermissions();
    }

    private void checkNecessaryPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
            hasNecessaryPermissions = true;
        } else {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return super.onSupportNavigateUp();
    }

    @Override
    public void onCompleted(View completeButton) {
        Client apiClient = Client.getInstance(getApplicationContext(),
                getResources().getString(R.string.open311_api_key)
        );
        MutableLiveData<Result<Integer>> resultMutable = apiClient.postRequests(getViewModel());

        resultMutable.observe(this, result -> {
            if (null == result) {
                return;
            }

            if (result instanceof Result.Success) {
                MyServiceRequestRepository repository = MyServiceRequestRepository.getInstance(
                        Database.getInstance(getApplicationContext()));


                ThreadExecutorSupplier.getInstance().getMajorBackgroundTasks().execute(() -> {
                    Result<Boolean> insertResult =
                            repository.insert((Integer) ((Result.Success) result).getData());
                    if (insertResult instanceof Result.Error) {
                        String errorMessage =
                                getResources().getString(R.string.error_add_new_issue) + " " +
                                        getResources().getString(
                                                R.string.error_add_new_issue_database);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Intent data = new Intent();
                        data.putExtra(MainActivity.INTENT_EXTRA_SHOW_MY_REQUESTS, true);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
            } else {
                String errorMessage = getResources().getString(R.string.error_add_new_issue) + " " +
                        ((Result.Error) result).getError();
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onError(VerificationError verificationError) {
        Toast.makeText(this, verificationError.getErrorMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStepSelected(int newStepPosition) {
    }

    @Override
    public void onReturn() {
        finish();
    }

    public NewIssueViewModel getViewModel() {
        return viewModel;
    }

    public boolean hasNecessaryPermissions() {
        return hasNecessaryPermissions;
    }
}