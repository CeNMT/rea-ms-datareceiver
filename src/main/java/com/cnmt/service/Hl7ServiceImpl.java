package com.cnmt.service;

import io.github.linuxforhealth.fhir.FHIRContext;
import io.github.linuxforhealth.hl7.ConverterOptions;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;
import io.github.linuxforhealth.hl7.message.HL7MessageEngine;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

@Singleton
public class Hl7ServiceImpl implements Hl7Service {
    private static final Logger log = LoggerFactory.getLogger(Hl7ServiceImpl.class);

//    private static final String GENERATOR = "Generator";
//    private static final StringType VERSION = new StringType("Health Data Receiver service v0.1");

    private static final ConverterOptions converterOptions = new ConverterOptions.Builder().withBundleType(Bundle.BundleType.TRANSACTION).build();
    private static final HL7ToFHIRConverter hl7ToFHIRConverter = new HL7ToFHIRConverter();
    private static final HL7MessageEngine hl7messageEngine = new HL7MessageEngine(new FHIRContext(false, false), Bundle.BundleType.TRANSACTION);
    private final FhirService fhirService;

    public Hl7ServiceImpl(FhirService fhirService) {
        this.fhirService = fhirService;
    }

    @Override
    public String getFhirResourceJson(@NonNull String hl7message) {
        log.trace("Converting HL7 {} to json", hl7message);
        return hl7ToFHIRConverter.convert(hl7message, converterOptions);
    }

    @Override
    public Bundle getFhirResource(String hl7message) {
        log.trace("Converting HL7 {} to bundle", hl7message);
        return hl7ToFHIRConverter.convertToBundle(hl7message, converterOptions, hl7messageEngine);
    }

    @Override
    public Map<Boolean, String> createFhirResources(@NonNull String dateTime, @NonNull String hl7message) {
        log.info("[{}] Creating FHIR resources from HL7 size={}", dateTime, hl7message.length()); // todo trace
        Bundle bundle = null;
        try {
            bundle = getFhirResource(hl7message);
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            log.warn(String.format("[%s] Unable to parse HL7 size=%s\n\t%s", dateTime, hl7message.length(), hl7message), e);
            return Collections.singletonMap(false, e.getMessage());
        }
        assert bundle != null;
        addRequestToBundleEntry(bundle);
        log.info("FHIR resource bundle: {}", bundle); // todo trace

        return fhirService.createResource(bundle);
    }

    private static void addRequestToBundleEntry(Bundle bundle) { // todo add reference to Device ()
        bundle.getEntry().forEach(
                entry -> entry.setRequest(new Bundle.BundleEntryRequestComponent()
                        .setMethod(Bundle.HTTPVerb.POST))
                        .setFullUrl(entry.fhirType()));
//                        .setProperty(GENERATOR, VERSION));
    }
}
