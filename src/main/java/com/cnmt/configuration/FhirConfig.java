package com.cnmt.configuration;

import ca.uhn.fhir.context.FhirContext;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Factory
public class FhirConfig {
    private static final Logger log = LoggerFactory.getLogger(FhirConfig.class);
    private static final int TIMEOUT = 60_000;

    @Value("${fhir.baseurl}")
    private String baseurl;

    @Value("${fhir.context}")
    private String context;

    @Singleton
    public FhirContext fhirContext() {
        log.info("Starting FHIR context: base url={} context={} ...", baseurl, context);

        FhirContext fhirContext = FhirContext.forR4();
        if ("r5".equalsIgnoreCase(context)) {
            fhirContext = FhirContext.forR5();
        }
        fhirContext.getRestfulClientFactory().setConnectTimeout(TIMEOUT);
        fhirContext.getRestfulClientFactory().setSocketTimeout(TIMEOUT);
        fhirContext.getRestfulClientFactory().setConnectionRequestTimeout(TIMEOUT);

        return fhirContext;
    }

    public String getBaseurl() {
        return baseurl;
    }
}
