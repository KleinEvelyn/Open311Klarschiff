package com.iu.open311_klarschiff;


import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class NewIssueViewModel extends ViewModel {
    private final MutableLiveData<List<ServiceCategory>> serviceCategories =
            new MutableLiveData<>();
    private final ServiceCategoryRepository categoryRepository;
    private boolean startedLoadingServiceCategories = false;
    private Pair<Integer, String> selectedServiceCategoryGroup;
    private Pair<Integer, String> selectedServiceCategory;
    private String address;
    private LatLng position;
    private String description;
    private Bitmap photo;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;

    public NewIssueViewModel(ServiceCategoryRepository categoryRepository) {

        this.categoryRepository = categoryRepository;
    }

    public MutableLiveData<List<ServiceCategory>> getServiceCategories() {
        return serviceCategories;
    }

    public Pair<Integer, String> getSelectedServiceCategoryGroup() {
        return selectedServiceCategoryGroup;
    }

    public void setSelectedServiceCategoryGroup(Pair<Integer, String> selectedServiceCategoryGroup
    ) {
        this.selectedServiceCategoryGroup = selectedServiceCategoryGroup;
    }

    public Pair<Integer, String> getSelectedServiceCategory() {
        return selectedServiceCategory;
    }

    public void setSelectedServiceCategory(Pair<Integer, String> selectedServiceCategory) {
        this.selectedServiceCategory = selectedServiceCategory;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void loadServiceCategories() {
        if (!startedLoadingServiceCategories) {
            startedLoadingServiceCategories = true;
            ThreadExecutorSupplier.getInstance().getMajorBackgroundTasks().execute(() -> {
                Result<List<ServiceCategory>> result = categoryRepository.findAll();
                if (result instanceof Result.Success) {
                    this.serviceCategories.postValue(
                            (List<ServiceCategory>) ((Result.Success<?>) result).getData());
                } else {
                    Log.e(this.getClass().getSimpleName(), "Could not load service categories");
                    this.serviceCategories.postValue(new ArrayList<>());
                }
            });
        }
    }
}