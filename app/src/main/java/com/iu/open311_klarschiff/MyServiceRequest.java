package com.iu.open311_klarschiff;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "service_request")
public class MyServiceRequest {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "service_request_id")
    public Integer serviceRequestId;
}

