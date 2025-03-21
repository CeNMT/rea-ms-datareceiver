/**
 * Copyright (c) 2025 CNMT
 * All rights reserved.
 */

package com.cnmt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"meta"})
@JsonPropertyOrder({
        "resourceType",
        "id",
        "meta",
        "type",
        "entry"
})
@Generated("jsonschema2pojo")
public class BundleDto implements Serializable {
    private final static long serialVersionUID = -878791908651142844L;

    @JsonProperty("resourceType")
    public String resourceType;

    @JsonProperty("id")
    public String id;

    @JsonProperty("type")
    public String type;

    @JsonProperty("entry")
    @Valid
    public List<EntryDto> entry;

//    @JsonIgnore
//    @Valid
//    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

//    @JsonAnyGetter
//    public Map<String, Object> getAdditionalProperties() {
//        return this.additionalProperties;
//    }
//
//    @JsonAnySetter
//    public void setAdditionalProperty(String name, Object value) {
//        this.additionalProperties.put(name, value);
//    }
}
