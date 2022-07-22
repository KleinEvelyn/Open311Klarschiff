package com.iu.open311_klarschiff;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceRequest {

    @JsonProperty("service_request_id")
    public Integer serviceRequestId;

    @JsonProperty("status_notes")
    public String statusNotes;

    @JsonProperty("status")
    public String status;

    @JsonProperty("service_code")
    public Integer serviceCode;

    @JsonProperty("service_name")
    public String serviceName;

    @JsonProperty("description")
    public String description;

    @JsonProperty("agency_responsible")
    public String agencyResponsible;

    @JsonProperty("requested_datetime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime requestedDatetime;

    @JsonProperty("updated_datetime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime updatedDatetime;

    @JsonProperty("address")
    public String address;

    @JsonProperty("lat")
    public Float latitude;

    @JsonProperty("long")
    public Float longitude;

    @JsonProperty("media_url")
    public String mediaUrl;

    public String getStatus() {
        return status;
    }

    public String getServiceName() {
        return serviceName;
    }

    public LocalDateTime getRequestedDatetime() {
        return requestedDatetime;
    }
}