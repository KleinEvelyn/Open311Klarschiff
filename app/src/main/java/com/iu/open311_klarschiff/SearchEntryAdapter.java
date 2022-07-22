package com.iu.open311_klarschiff;


import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SearchEntryAdapter extends RecyclerView.Adapter<SearchEntryAdapter.ViewHolder> {

    private final List<ServiceRequest> serviceRequests = new ArrayList<>();
    private final List<ServiceRequest> allServiceRequests = new ArrayList<>();

    private final Resources resources;
    private final LifecycleOwner lifecycleOwner;

    public SearchEntryAdapter(Resources resources, LifecycleOwner lifecycleOwner
    ) {
        this.resources = resources;
        this.lifecycleOwner = lifecycleOwner;
    }

    public void sortServiceRequests(SearchOrderEnum searchOrder) {
        switch (searchOrder) {
            case CATEGORY_DESC:
                this.serviceRequests.sort(Comparator.comparing(ServiceRequest::getServiceName,
                        Comparator.nullsFirst(Comparator.naturalOrder())
                ).reversed());
                break;
            case STATUS_ASC:
                this.serviceRequests.sort(Comparator.comparing(ServiceRequest::getStatus,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ));
                break;
            case STATUS_DESC:
                this.serviceRequests.sort(Comparator.comparing(ServiceRequest::getStatus,
                        Comparator.nullsFirst(Comparator.naturalOrder())
                ).reversed());
                break;
            default:
                this.serviceRequests.sort(Comparator.comparing(ServiceRequest::getServiceName,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ));
                break;
        }

        notifyDataSetChanged();
    }

    public int filterServiceRequests(String searchString) {
        List<ServiceRequest> filteredServiceRequests =
                allServiceRequests.stream().filter(serviceRequest -> {
                    if (serviceRequest.serviceName.toLowerCase()
                            .contains(searchString.toLowerCase())) {
                        return true;
                    }
                    return serviceRequest.description.toLowerCase()
                            .contains(
                                    searchString.toLowerCase(Locale.ROOT));
                }).collect(Collectors.toList());

        this.serviceRequests.clear();
        this.serviceRequests.addAll(filteredServiceRequests);
        this.serviceRequests.sort(Comparator.comparing(ServiceRequest::getRequestedDatetime,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));
        notifyDataSetChanged();

        return filteredServiceRequests.size();
    }

    public void setServiceRequests(List<ServiceRequest> serviceRequests) {
        this.allServiceRequests.clear();
        this.allServiceRequests.addAll(serviceRequests);
        this.serviceRequests.clear();
        this.serviceRequests.addAll(serviceRequests);
        this.serviceRequests.sort(Comparator.comparing(ServiceRequest::getRequestedDatetime,
                Comparator.nullsLast(Comparator.naturalOrder())
        ).reversed());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.service_request_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getServiceName().setText(serviceRequests.get(position).serviceName);
        viewHolder.getStatus()
                .setText(StatusTranslater.determineStatus(resources,
                        serviceRequests.get(position).status
                ));

        viewHolder.getRequestDateTime()
                .setText(DateUtils.formatLocalDateTime(
                        serviceRequests.get(position).requestedDatetime));

        String mediaUrl = serviceRequests.get(position).mediaUrl;
        viewHolder.getImagePreview().setImageDrawable(null);
        if (null != mediaUrl) {
            ImageCache.getImage(mediaUrl).observe(lifecycleOwner, image -> {
                if (null != image) {
                    viewHolder.getImagePreview().setImageBitmap(image);
                }
            });
        }

    }

    public ServiceRequest getByPosition(int position) {
        return serviceRequests.get(position);
    }

    @Override
    public int getItemCount() {
        return this.serviceRequests.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView serviceName;
        private final TextView status;
        private final TextView requestDateTime;
        private final ImageView imagePreview;

        public ViewHolder(View view) {
            super(view);
            serviceName = view.findViewById(R.id.serviceName);
            status = view.findViewById(R.id.status);
            requestDateTime = view.findViewById(R.id.requestDateTime);
            imagePreview = view.findViewById(R.id.imagePreview);
        }

        public TextView getServiceName() {
            return serviceName;
        }

        public TextView getStatus() {
            return status;
        }

        public TextView getRequestDateTime() {
            return requestDateTime;
        }

        public ImageView getImagePreview() {
            return imagePreview;
        }
    }
}