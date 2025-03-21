package com.cnmt.service;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.cnmt.client.FhirClient;
import jakarta.inject.Singleton;
import org.hl7.fhir.r4.model.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Singleton
public class FhirServiceImpl implements FhirService {
    private static final Logger log = LoggerFactory.getLogger(FhirServiceImpl.class);

    private final FhirClient fhirClient;

    public FhirServiceImpl(FhirClient fhirClient) {
        this.fhirClient = fhirClient;
    }

    @Override
    public String validate() {
        Device dummyDevice = new Device();
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
}
