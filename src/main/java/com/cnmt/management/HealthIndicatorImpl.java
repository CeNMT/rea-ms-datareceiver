package com.cnmt.management;

import ca.uhn.fhir.rest.client.exceptions.FhirClientConnectionException;
import com.cnmt.server.TcpServer;
import com.cnmt.service.FhirService;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.health.HealthStatus;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;
import io.micronaut.management.health.indicator.annotation.Readiness;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@Singleton
@Readiness
public class HealthIndicatorImpl implements HealthIndicator {
    private static final Logger log = LoggerFactory.getLogger(HealthIndicatorImpl.class);

    private final ArrayList<String> statusDetails = new ArrayList<>();

    private final TcpServer tcpServer;
    private final FhirService fhirService;

    public HealthIndicatorImpl(TcpServer tcpServer, FhirService fhirService) {
        this.tcpServer = tcpServer;
        this.fhirService = fhirService;
    }

    @Override
    public Publisher<HealthResult> getResult() {
        final boolean status = getStatus();
        if (status) {
            log.info("Status={} details={}", HealthStatus.UP, statusDetails);
        } else {
            log.warn("Status={} details={}", HealthStatus.DOWN, statusDetails);
        }
        return Publishers.just(new HealthResultImpl("datareceiver", status, statusDetails));
    }

    private boolean getStatus() {
        statusDetails.clear();

        boolean status = tcpServer.isStarted();
        String serverConnectionValidation = String.format("TCP receiver is %s", status ? "running" : "NOT running");
        if (!status) {
            statusDetails.add(serverConnectionValidation);
            log.warn(serverConnectionValidation);
        }

        String fhirConnectionValidation;
        try {
            fhirConnectionValidation = fhirService.validate();
            if (fhirConnectionValidation == null) {
                status = false;
                fhirConnectionValidation = "Unable to check connection with FHIR";
                statusDetails.add(fhirConnectionValidation);
                log.warn(fhirConnectionValidation);
            } else {
                log.trace(fhirConnectionValidation);
            }
        } catch (FhirClientConnectionException e) {
            status = false;
            fhirConnectionValidation = "No connection to FHIR";
            statusDetails.add(fhirConnectionValidation);
            log.warn(fhirConnectionValidation, e);
        }
        if (status) {
            statusDetails.add(String.format("Connected to FHIR; %s", serverConnectionValidation));
        }

        return status;
    }
}
