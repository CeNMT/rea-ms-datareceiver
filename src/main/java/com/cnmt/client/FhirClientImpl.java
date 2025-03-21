package com.cnmt.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.client.apache.GZipContentInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cnmt.configuration.FhirConfig;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
//@Requires(notEnv = {"local"})
public class FhirClientImpl implements FhirClient {
    private static final Logger log = LoggerFactory.getLogger(FhirClientImpl.class);

    private final FhirContext fhirContext;
    private final FhirConfig fhirConfig;

    public FhirClientImpl(@NonNull FhirContext fhirContext, @NonNull FhirConfig fhirConfig) {
        this.fhirContext = fhirContext;
        this.fhirConfig = fhirConfig;
    }

    @Override
    public FhirContext getFhirContext() {
        return fhirContext;
    }

    @Override
    public IGenericClient getClient() {
        IGenericClient genericClient = fhirContext.newRestfulGenericClient(fhirConfig.getBaseurl());
        genericClient.setEncoding(EncodingEnum.JSON);
        genericClient.registerInterceptor(new GZipContentInterceptor());
//        genericClient.registerInterceptor(authInterceptor);
        return genericClient;
    }
}
