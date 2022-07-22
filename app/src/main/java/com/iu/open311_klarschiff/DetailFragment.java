package com.iu.open311_klarschiff;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iu.open311_klarschiff.databinding.FragmentDetailBinding;

public class DetailFragment extends Fragment implements OnMapReadyCallback {

    public static String ARGS_SERVICE_REQUEST_ID = "serviceRequestId";

    private FragmentDetailBinding binding;
    private MapView mapView;

    private MutableLiveData<Result<ServiceRequest>> serviceRequestResult = new MutableLiveData<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState
    ) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        mapView = binding.mapView;

        initServiceRequestData();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void initServiceRequestData() {
        int serviceRequestId = getArguments().getInt(ARGS_SERVICE_REQUEST_ID);
        serviceRequestResult =
                Client.getInstance(getContext(), getResources().getString(R.string.open311_api_key))
                        .loadRequest(serviceRequestId);

        serviceRequestResult.observe(getViewLifecycleOwner(), result -> {
            if (null == result) {
                return;
            }

            if (result instanceof Result.Error) {
                String errorMessage = getResources().getString(R.string.error_detail) + ": " +
                        ((Result.Error) result).getError();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                return;
            }


            ServiceRequest serviceRequest = (ServiceRequest) ((Result.Success) result).getData();

            binding.contentWrapper.setVisibility(View.VISIBLE);
            binding.loading.setVisibility(View.GONE);

            binding.serviceName.setText(serviceRequest.serviceName);
            binding.description.setText(serviceRequest.description);
            binding.address.setText(serviceRequest.address);
            binding.agencyResponsible.setText(serviceRequest.agencyResponsible);
            binding.status.setText(
                    StatusTranslater.determineStatus(getResources(), serviceRequest.status));
            binding.requestDateTime.setText(
                    DateUtils.formatLocalDateTime(serviceRequest.requestedDatetime));
            binding.updatedDatetime.setText(
                    DateUtils.formatLocalDateTime(serviceRequest.updatedDatetime));

            if (null != serviceRequest.mediaUrl) {

                ImageCache.getImage(serviceRequest.mediaUrl)
                        .observe(getViewLifecycleOwner(), image -> {
                            if (null != image) {

                                binding.imagePreview.setImageBitmap(image);
                            }
                        });
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        serviceRequestResult.observe(getViewLifecycleOwner(), result -> {
            if (null != result && result instanceof Result.Success) {
                ServiceRequest serviceRequest =
                        (ServiceRequest) ((Result.Success) result).getData();
                LatLng position = new LatLng(54.083336, 12.108811); // Rostock
                if (null != serviceRequest.longitude && null != serviceRequest.latitude) {
                    position = new LatLng(serviceRequest.latitude, serviceRequest.longitude);
                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18));
                googleMap.addMarker(new MarkerOptions().position(position)
                        .title(serviceRequest.address)
                        .icon(BitmapDescriptorFactory.defaultMarker())
                        .draggable(false)
                        .visible(true));
            }
        });
    }
}
