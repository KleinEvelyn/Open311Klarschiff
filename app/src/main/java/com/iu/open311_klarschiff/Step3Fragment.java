package com.iu.open311_klarschiff;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.stepstone.stepper.VerificationError;

import java.util.List;
import java.util.StringJoiner;

public class Step3Fragment extends AbstractStepFragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private View view;
    private MapView mapView;
    private TextInputEditText addressField;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        view = inflater.inflate(R.layout.fragment_new_issue_3, container, false);
        addressField = view.findViewById(R.id.inputAddress);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        if (!((NewIssueActivity) getActivity()).hasNecessaryPermissions()) {
            view.findViewById(R.id.mapViewPermissionsHint).setVisibility(View.VISIBLE);
            view.findViewById(R.id.mapViewHint).setVisibility(View.GONE);
        }

        addEditTextChangeHandling();

        return view;
    }

    @Override
    public VerificationError verifyStep() {
        closeKeyboard(view);

        if ((null == getViewModel().getAddress() || getViewModel().getAddress().isEmpty()) &&
                null == getViewModel().getPosition()) {
            return new VerificationError(getResources().getString(R.string.error_step3));
        }

        return null;
    }

    @Override
    public void onSelected() {
        getActivity().setTitle(R.string.new_issue_step3);
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.setMyLocationEnabled(true);

        LocationManager locationManager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(new Criteria(), false);
        Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
        LatLng startPosition = new LatLng(54.083336, 12.108811); // Rostock
        if (null != getViewModel().getPosition()) {
            startPosition = getViewModel().getPosition();
            addressField.setText(getViewModel().getAddress());
        } else if (null != lastKnownLocation) {
            startPosition =
                    new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            updateAddressFromLatLng(startPosition);
        }

        // https://developers.google.com/maps/documentation/android-sdk/views
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPosition, 18));

        // https://developers.google.com/maps/documentation/android-sdk/marker
        googleMap.addMarker(new MarkerOptions().position(startPosition)
                .title(getResources().getString(
                        R.string.label_marker))
                .icon(BitmapDescriptorFactory.defaultMarker())
                .draggable(true)
                .visible(true));

        googleMap.setOnMarkerDragListener(this);
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

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        LatLng currentPosition = marker.getPosition();
        updateAddressFromLatLng(currentPosition);
    }

    private void addEditTextChangeHandling() {
        if (null != addressField) {
            addressField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    getViewModel().setAddress(editable.toString());
                }
            });
        }
    }

    private void updateAddressFromLatLng(LatLng position) {

        Geocoder geocoder = new Geocoder(getContext());
        String addressFromLatLong = "";

        List<Address> list;
        try {
            list = geocoder.getFromLocation(position.latitude, position.longitude, 1);
            Address address = list.get(0);
            StringJoiner stringJoiner = new StringJoiner(" ");
            if (null != address.getThoroughfare() && !address.getThoroughfare().isEmpty()) {
                stringJoiner.add(address.getThoroughfare());
            }
            if (null != address.getSubThoroughfare() && !address.getSubThoroughfare().isEmpty()) {
                stringJoiner.add(address.getSubThoroughfare() + ",");
            }
            if (null != address.getPostalCode() && !address.getPostalCode().isEmpty()) {
                stringJoiner.add(address.getPostalCode());
            }
            if (null != address.getLocality() && !address.getLocality().isEmpty()) {
                stringJoiner.add(address.getLocality());
            }
            if (null != address.getSubLocality() && !address.getSubLocality().isEmpty()) {
                stringJoiner.add("OT " + address.getSubLocality());
            }

            addressFromLatLong = stringJoiner.toString();
        } catch (Throwable e) {
            Log.e(getClass().getSimpleName(), "Could not determine address", e);
        }

        // update view model
        getViewModel().setPosition(position);
        getViewModel().setAddress(addressFromLatLong);

        // change address field input
        if (null != addressField) {
            addressField.setText(addressFromLatLong);
        }
    }
}