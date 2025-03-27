package com.cnmt.service;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.cnmt.client.FhirClient;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Singleton
public class FhirServiceImpl implements FhirService {
    private static final Logger log = LoggerFactory.getLogger(FhirServiceImpl.class);

    private final IParser iParser;
    private final FhirClient fhirClient;

    public FhirServiceImpl(FhirClient fhirClient) {
        this.iParser = fhirClient.getFhirContext().newJsonParser();
        this.fhirClient = fhirClient;
    }

    @Override
    public String validate() {
        Device dummyDevice = new Device(); // todo change to the real device creation and add references to it for created resources in FHIR
        dummyDevice.setId(String.valueOf(UUID.randomUUID()));

        MethodOutcome outcome;
        try {
            outcome = fhirClient.getClient().validate().resource(dummyDevice).execute();
        } catch (ResourceNotFoundException e) {
            log.trace("FHIR validation: Dummy Device/{} not found as expected", dummyDevice.getId());
            return "Ok";
        }
        log.info("FHIR validation: Dummy Device/{} {}", dummyDevice.getId(), outcome); // todo trace
        return outcome.toString();
    }

    @Override
    public String getVersion() {
        FhirVersionEnum version = fhirClient.getFhirContext().getVersion().getVersion();
        return version.getFhirVersionString() + "/" + version.name();
    }

    @Override
    public Map<Boolean, String> createResource(@NonNull String json) {
        log.info("Json size={}", json.length());
        log.trace("Json={}", json);
        String resp;
        try {
            resp = fhirClient.getClient().transaction().withBundle(json).execute();
        } catch (Exception e) {
            log.error("Error execute transaction to create resource json size={}", json.length(), e);
            return Collections.singletonMap(false, e.getMessage());
        }
        log.info("FHIR response {}", resp);
        return Collections.singletonMap(true, "");
    }

    @Override
    public Map<Boolean, String> createResource(Bundle bundle) {
        bundle.setUserData("Health data receiver", Collections.singletonMap("testKey", "testValue")); // todo delete?
        Bundle resp;
        try {
            resp = fhirClient.getClient().transaction().withBundle(bundle).execute();
        } catch (Exception e) {
            log.error("Error execute transaction to create resource from bundle {}", bundle, e);
            return Collections.singletonMap(false, e.getMessage());
        }
        log.info("FHIR response {}", iParser.setPrettyPrint(true).encodeResourceToString(resp)); // todo trace
        return Collections.singletonMap(true, "");
    }
}
