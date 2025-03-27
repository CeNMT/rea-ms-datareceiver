package com.cnmt.service;

import org.hl7.fhir.r4.model.Bundle;

import java.util.Map;

/**
 * Service to convert HL7 to a FHIR resources.
 */
public interface Hl7Service {
    /**
     * Get FHIR resource from a HL7 message.<br/>
     * It throws {@link IllegalArgumentException} or {@link UnsupportedOperationException} if the message cannot be parsed.
     *
     * @param hl7message message
     * @return FHIR resource in json
     */
    String getFhirResourceJson(String hl7message);

    /**
     * Get FHIR resource from a HL7 message.<br/>
     * It throws {@link IllegalArgumentException} or {@link UnsupportedOperationException} if the message cannot be parsed.
     *
     * @param hl7message message
     * @return FHIR resource as a Bundle
     */
    Bundle getFhirResource(String hl7message);

    /**
     * Create FHIR resources from a HL7 message.
     *
     * @param dateTime   datetime for tracking messages
     * @param hl7message message
     * @return map <i>status -> details<i/> details of the resource creation
     */
    Map<Boolean, String> createFhirResources(String dateTime, String hl7message);
}
