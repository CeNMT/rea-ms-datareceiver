package com.cnmt.service;

import io.micronaut.core.annotation.NonNull;

/**
 * Service to convert HL7 to a FHIR resources.
 */
public interface Hl7Service {
    /**
     * Get FHIR resource from a HL7 message
     * @param hl7message message
     * @return FHIR resource
     */
    String getFhirResource(@NonNull String hl7message);
}
