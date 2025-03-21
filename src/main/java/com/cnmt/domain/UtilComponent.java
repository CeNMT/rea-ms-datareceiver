/**
 * Copyright (c) 2024 CNMT
 * All rights reserved.
 */

package com.cnmt.domain;

import ca.uhn.fhir.parser.IParser;
import com.cnmt.client.FhirClient;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class UtilComponent {
    private static final Logger log = LoggerFactory.getLogger(UtilComponent.class);

//    private final ObjectMapper objectMapper = new ObjectMapper();

    private final IParser iParser;

    public UtilComponent(FhirClient fhirClient) {
        iParser = fhirClient.getFhirContext().newJsonParser();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String getJsonString(@NonNull IBaseResource bundle) {
        String json = iParser.encodeResourceToString(bundle);
        log.trace("Got Bundle json: {}", json);
        return json;
    }
}
