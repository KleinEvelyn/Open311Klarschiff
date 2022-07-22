package com.iu.open311_klarschiff;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private static Map<String, Bitmap> imageCache = new HashMap<>();

    public static MutableLiveData<Bitmap> getImage(String mediaUrl) {
        MutableLiveData<Bitmap> bitmap = new MutableLiveData<>();
        Bitmap image = imageCache.get(mediaUrl);
        if (null != image) {
            bitmap.postValue(image);
        } else {
            ThreadExecutorSupplier.getInstance().getMinorBackgroundTasks().execute(() -> {
                loadImage(mediaUrl, bitmap);
            });
        }

        return bitmap;
    }

    private static void loadImage(String mediaUrl, @Nullable MutableLiveData<Bitmap> bitmap) {
        try {
            URL url = new URL(mediaUrl);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imageCache.put(mediaUrl, image);

            if (null != bitmap) {
                bitmap.postValue(image);
            }
        } catch (Exception e) {
            Log.e(ImageCache.class.getSimpleName(), "Could not load external image", e);
        }
    }
}