package com.iu.open311_klarschiff;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class NewIssueViewModelFactory implements ViewModelProvider.Factory {
    private Database database;

    public NewIssueViewModelFactory(Database database) {
        this.database = database;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass
    ) {
        if (modelClass.isAssignableFrom(NewIssueViewModel.class)) {
            return (T) new NewIssueViewModel(ServiceCategoryRepository.getInstance(database));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
