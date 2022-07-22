package com.iu.open311_klarschiff;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stepstone.stepper.VerificationError;

public class Step5Fragment extends AbstractStepFragment {

    private View view;
    private ImageView imagePreview;

    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Intent data = result.getData();

                                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                                Bitmap scaledBitmap =
                                        Bitmap.createScaledBitmap(imageBitmap, 300, 300, false);
                                getViewModel().setPhoto(scaledBitmap);

                                imagePreview.setImageBitmap(scaledBitmap);
                                imagePreview.setVisibility(View.VISIBLE);
                                (view.findViewById(R.id.cameraHint)).setVisibility(View.GONE);
                            }
                        }
                    }
            );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        view = inflater.inflate(R.layout.fragment_new_issue_5, container, false);
        imagePreview = view.findViewById(R.id.imagePreview);

        initImage();

        view.findViewById(R.id.btnCamera).setOnClickListener(view -> {
            launchCamera(view);
        });


        return view;
    }

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {
        getActivity().setTitle(R.string.new_issue_step5);
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    private void initImage() {
        if (null != getViewModel().getPhoto()) {
            imagePreview.setImageBitmap(getViewModel().getPhoto());
            imagePreview.setVisibility(View.VISIBLE);
            (view.findViewById(R.id.cameraHint)).setVisibility(View.GONE);
        }
    }

    private void launchCamera(View view) {
        // https://developer.android.com/training/camera/photobasics
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(takePictureIntent);
    }

}