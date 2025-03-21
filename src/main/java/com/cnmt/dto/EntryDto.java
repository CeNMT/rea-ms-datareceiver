/**
 * Copyright (c) 2025 CNMT
 * All rights reserved.
 */

package com.cnmt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Generated;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"fullUrl"})
@Generated("jsonschema2pojo")
public class EntryDto implements Serializable {
    private final static long serialVersionUID = 2170554060582252571L;

//    @JsonProperty("resource")
//    @Valid
//    public Resource resource;

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
