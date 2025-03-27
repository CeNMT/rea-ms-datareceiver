package com.cnmt.service;

import ca.uhn.fhir.rest.client.exceptions.FhirClientConnectionException;
import org.hl7.fhir.r4.model.Bundle;

import java.util.Map;

/**
 * Service to get resources from FHIR.
 */
public interface FhirService {
    /**
     * Validate a connection to FHIR server. It is used in the microservice management and monitoring system.<br/>
     * It throws {@link FhirClientConnectionException} in case of no connection.
     *
     * @return outcome of a validated FHIR resource or the string 'Ok' in a case of success
     */
    String validate();

    /**
     * Get FHIR revision version (a client part).
     *
     * @return version
     */
    String getVersion();

    /**
     * Create/update FHIR resources specified in a bundle in JSON format.
     *
     * @param json json to convert to FHIR resources
     * @return map <i>status -> details<i/> details of the resource creation
     */
    Map<Boolean, String> createResource(String json);

    /**
     * Create/update FHIR resources specified in a bundle.
     *
     * @param bundle to convert to FHIR resources
     * @return map <i>status -> details<i/> details of the resource creation
     */
    Map<Boolean, String> createResource(Bundle bundle);
}
