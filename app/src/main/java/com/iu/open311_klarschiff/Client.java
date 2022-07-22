package com.iu.open311_klarschiff;


import static java.net.HttpURLConnection.HTTP_OK;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacksonandroidnetworking.JacksonParserFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @see "https://github.com/bfpi/klarschiff-citysdk"
 */

public class Client {
    private static Client instance;

    private final MutableLiveData<List<ServiceRequest>> serviceRequests = new MutableLiveData<>();
    private final MutableLiveData<List<ServiceRequest>> myServiceRequests = new MutableLiveData<>();
    private final String apiKey;

    private Client(String apiKey) {
        this.apiKey = apiKey;
    }

    public synchronized static Client getInstance(Context context, String apiKey) {
        if (null == instance) {
            instance = new Client(apiKey);
            AndroidNetworking.initialize(context);
            AndroidNetworking.setParserFactory(new JacksonParserFactory());
        }
        return instance;
    }

    public MutableLiveData<List<ServiceRequest>> getServiceRequests() {
        return serviceRequests;
    }

    public MutableLiveData<List<ServiceRequest>> getMyServiceRequests() {
        return myServiceRequests;
    }

    public MutableLiveData<Result<ServiceRequest>> loadRequest(Integer serviceRequestId) {

        MutableLiveData<Result<ServiceRequest>> result = new MutableLiveData<>();

        AndroidNetworking.get(createApiRequestUrl("requests/" + serviceRequestId))
                .build()
                .getAsObjectList(ServiceRequest.class,
                        new ParsedRequestListener<List<ServiceRequest>>() {
                            @Override
                            public void onResponse(List<ServiceRequest> serviceRequests
                            ) {
                                if (serviceRequests.size() > 0) {
                                    result.postValue(new Result.Success<ServiceRequest>(
                                            serviceRequests.get(0)));
                                } else {
                                    result.postValue(new Result.Error(
                                            new Exception("No data found")));
                                }
                            }

                            @Override
                            public void onError(ANError error) {
                                String errorMessage = error.getErrorDetail() + ": " +
                                        (null == error.getResponse() ?
                                                "" :
                                                error.getResponse().message()
                                        );
                                Log.e(Client.class.getSimpleName(), errorMessage);
                                result.postValue(
                                        new Result.Error(new Exception(errorMessage)));
                            }
                        }
                );

        return result;
    }

    public void loadMyRequests(Database database) {
        ThreadExecutorSupplier.getInstance().getMajorBackgroundTasks().execute(() -> {
            List<Integer> serviceRequestIds = database.myServiceRequestDao()
                    .findAll()
                    .stream()
                    .map(serviceRequest -> serviceRequest.serviceRequestId)
                    .collect(Collectors.toList());

            if (serviceRequestIds.isEmpty()) {
                myServiceRequests.postValue(new ArrayList<>());
                return;
            }

            StringJoiner stringJoiner = new StringJoiner(",");
            serviceRequestIds.forEach(id -> stringJoiner.add(String.valueOf(id)));
            String apiRequestUrl = createApiRequestUrl("requests") + "&service_request_id=" +
                    stringJoiner.toString();

            AndroidNetworking.get(apiRequestUrl)
                    .build()
                    .getAsObjectList(ServiceRequest.class,
                            new ParsedRequestListener<List<ServiceRequest>>() {
                                @Override
                                public void onResponse(List<ServiceRequest> serviceRequests
                                ) {
                                    Client.this.myServiceRequests.postValue(
                                            serviceRequests);
                                }

                                @Override
                                public void onError(ANError error) {
                                    String errorMessage = error.getErrorDetail() + ": " + (
                                            null == error.getResponse() ?
                                                    "" :
                                                    error.getResponse().message()
                                    );
                                    Log.e(Client.class.getSimpleName(), errorMessage);
                                }
                            }
                    );
        });
    }

    public void loadRequests() {
        AndroidNetworking.get(createApiRequestUrl("requests"))
                .build()
                .getAsObjectList(ServiceRequest.class,
                        new ParsedRequestListener<List<ServiceRequest>>() {
                            @Override
                            public void onResponse(List<ServiceRequest> serviceRequests
                            ) {
                                Client.this.serviceRequests.postValue(serviceRequests);
                            }

                            @Override
                            public void onError(ANError error) {
                                String errorMessage = error.getErrorDetail() + ": " +
                                        (null == error.getResponse() ?
                                                "" :
                                                error.getResponse().message()
                                        );
                                Log.e(Client.class.getSimpleName(), errorMessage);
                            }
                        }
                );
    }

    public MutableLiveData<Result<List<ServiceCategory>>> loadServices() {
        MutableLiveData<Result<List<ServiceCategory>>> mutableResult = new MutableLiveData<>();

        AndroidNetworking.get(createApiRequestUrl("services"))
                .build()
                .getAsObjectList(ServiceCategory.class,
                        new ParsedRequestListener<List<ServiceCategory>>() {
                            @Override
                            public void onResponse(List<ServiceCategory> serviceCategories
                            ) {
                                mutableResult.postValue(
                                        new Result.Success<List<ServiceCategory>>(
                                                serviceCategories));
                            }

                            @Override
                            public void onError(ANError error) {
                                String errorMessage = error.getErrorDetail() + ": " +
                                        (null == error.getResponse() ?
                                                "" :
                                                error.getResponse().message()
                                        );
                                Log.e(Client.class.getSimpleName(), errorMessage);
                                mutableResult.postValue(
                                        new Result.Error(new Exception(errorMessage)));
                            }
                        }
                );

        return mutableResult;
    }

    public MutableLiveData<Result<Integer>> postRequests(NewIssueViewModel viewModel
    ) {
        MutableLiveData<Result<Integer>> mutableResult = new MutableLiveData<>();

        String requestUrl = createApiRequestUrl("requests");

        StringJoiner urlJoiner = new StringJoiner("&");
        urlJoiner.add(requestUrl);

        Map<String, String> apiParams = new HashMap<>();
        apiParams.put("api_key", apiKey);
        apiParams.put("email", viewModel.getEmail());
        apiParams.put("service_code", String.valueOf(viewModel.getSelectedServiceCategory().first));
        apiParams.put("description", viewModel.getDescription());

        if (null != viewModel.getPosition()) {
            apiParams.put("lat", String.valueOf(viewModel.getPosition().latitude));
            apiParams.put("long", String.valueOf(viewModel.getPosition().longitude));
        } else {
            apiParams.put("address_string", viewModel.getAddress());
        }

        Log.d(Client.class.getSimpleName(), "Sending request to: " + urlJoiner.toString());

        ParsedRequestListener<PostRequestResponse> resultListener =
                new ParsedRequestListener<PostRequestResponse>() {
                    @Override
                    public void onResponse(PostRequestResponse response) {
                        if (null == response.statusCode || response.statusCode.equals(HTTP_OK)) {
                            mutableResult.postValue(
                                    new Result.Success<Integer>(response.serviceRequestId));
                        } else {
                            mutableResult.postValue(
                                    new Result.Error(new Exception(response.description)));
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        String errorMessage = error.getErrorDetail() + ": " +
                                ((null == error.getResponse()) ?
                                        "" :
                                        error.getResponse().message()
                                );
                        Log.e(Client.class.getSimpleName(), errorMessage);

                        try {
                            Object errorObject =
                                    (new ObjectMapper()).readValue(error.getErrorBody(),
                                            Object.class
                                    );
                            if (errorObject instanceof ArrayList) {
                                ArrayList<?> errorList = (ArrayList<?>) errorObject;
                                if (errorList.size() > 0 &&
                                        errorList.get(0) instanceof LinkedHashMap &&
                                        ((LinkedHashMap) errorList.get(0)).containsKey(
                                                "description")) {
                                    String description =
                                            (String) ((LinkedHashMap) errorList.get(0)).get(
                                                    "description");

                                    errorMessage = (description.contains(
                                            "Zuständigkeit muss ausgefüllt werden") ?
                                            "Ortsangabe außerhalb Rostocks ist unzulässig" :
                                            description
                                    );
                                }
                            }
                        } catch (Throwable t) {
                            Log.e(this.getClass().getSimpleName(),
                                    "could not determine error details", t
                            );
                        }

                        mutableResult.postValue(new Result.Error(new Exception(errorMessage)));
                    }
                };

        if (null != viewModel.getPhoto()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            viewModel.getPhoto().compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            AndroidNetworking.post(urlJoiner.toString())
                    .addBodyParameter(apiParams)
                    .addBodyParameter("photo_required", "true")
                    .addBodyParameter("media",
                            Base64.encodeToString(byteArray, Base64.DEFAULT)
                    )
                    .build()
                    .getAsObject(PostRequestResponse.class, resultListener);
        } else {
            AndroidNetworking.post(urlJoiner.toString())
                    .addBodyParameter(apiParams)
                    .build()
                    .getAsObject(PostRequestResponse.class, resultListener);
        }

        return mutableResult;
    }


    private String createApiRequestUrl(String service) {
        return BuildConfig.API_BASE_URL + service + ".json?jurisdiction_id=" +
                BuildConfig.JURISDICTION_ID;
    }
}
