package com.cnmt.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

/**
 * Interface for a FHIR server.
 */
public interface FhirClient {
    /**
     * Get a FHIR context.
     *
     * @return context
     */
    FhirContext getFhirContext();

    /**
     * Get a generic RESTful client.
     *
     * @return client
     */
    IGenericClient getClient();
}
