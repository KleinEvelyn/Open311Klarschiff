package com.iu.open311_klarschiff;


import androidx.annotation.NonNull;

import java.io.IOException;

public class MyServiceRequestRepository {
    private static volatile MyServiceRequestRepository instance;
    private Database database;

    public MyServiceRequestRepository(@NonNull Database database) {
        this.database = database;
    }

    public static MyServiceRequestRepository getInstance(@NonNull Database database
    ) {
        if (instance == null) {
            instance = new MyServiceRequestRepository(database);
        }

        return instance;
    }

    public Result<Boolean> insert(Integer requestId) {
        try {
            MyServiceRequest myServiceRequest = new MyServiceRequest();
            myServiceRequest.serviceRequestId = requestId;
            database.myServiceRequestDao().insert(myServiceRequest);
            return new Result.Success<Boolean>(true);
        } catch (Exception e) {
            return new Result.Error(new IOException("Could not save to database", e));
        }

    }
}