package com.iu.open311_klarschiff;


import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {ServiceCategory.class, MyServiceRequest.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public static String databaseName = "open311";
    private static Database instance;

    public synchronized static Database getInstance(Context context) {
        if (null == instance) {
            instance = buildDatabase(context);
        }
        return instance;
    }

    private static Database buildDatabase(final Context context) {
        return Room.databaseBuilder(context, Database.class, databaseName).build();
    }

    public abstract MyServiceRequestDao myServiceRequestDao();

    public abstract ServiceCategoryDao serviceCategoryDao();
}