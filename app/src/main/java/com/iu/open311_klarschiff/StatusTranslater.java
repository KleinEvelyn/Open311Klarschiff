package com.iu.open311_klarschiff;

import android.content.res.Resources;

public class StatusTranslater {

    public static String determineStatus(Resources resources, String statusKey) {
        switch (statusKey.toLowerCase()) {
            case "pending":
                return resources.getString(R.string.status_pending);
            case "received":
                return resources.getString(R.string.status_received);
            case "in_process":
                return resources.getString(R.string.status_in_process);
            case "reviewed":
                return resources.getString(R.string.status_reviewed);
            case "processed":
                return resources.getString(R.string.status_processed);
            case "rejected":
                return resources.getString(R.string.status_rejected);
            case "closed":
                return resources.getString(R.string.status_closed);
            default:
                return statusKey;
        }
    }
}

