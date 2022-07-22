package com.iu.open311_klarschiff;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

public class ServiceCategoryRepository {
    private static volatile ServiceCategoryRepository instance;
    private final Database database;

    public ServiceCategoryRepository(@NonNull Database database) {
        this.database = database;
    }

    public static ServiceCategoryRepository getInstance(@NonNull Database database
    ) {
        if (instance == null) {
            instance = new ServiceCategoryRepository(database);
        }
        return instance;
    }

    public Result<List<ServiceCategory>> findAll() {
        try {
            List<ServiceCategory> categories = database.serviceCategoryDao().findAll();
            return new Result.Success<List<ServiceCategory>>(categories);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error while fetching service categories", e));
        }
    }
}

