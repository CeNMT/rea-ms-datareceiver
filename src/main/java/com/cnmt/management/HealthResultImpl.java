package com.cnmt.management;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.health.HealthStatus;
import io.micronaut.management.health.indicator.HealthResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HealthResultImpl implements HealthResult {
    private static final Logger log = LoggerFactory.getLogger(HealthResultImpl.class);
    private final String name;
    private final HealthStatus healthStatus;
    private final List<String> statusDetails;

    public HealthResultImpl(@NonNull String name, boolean status, @NonNull List<String> statusDetails) {
        this.name = name;
        healthStatus = status ? HealthStatus.UP : HealthStatus.DOWN;
        this.statusDetails = statusDetails;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public HealthStatus getStatus() {
        log.trace("Status: {}", healthStatus);
        return healthStatus;
    }

    @Override
    public Object getDetails() {
        log.trace("Details: {} {}", healthStatus,
                HealthStatus.UP.equals(healthStatus) ?
                        HealthStatus.UP.getDescription().orElse("No description") :
                        HealthStatus.DOWN.getDescription().orElse("No description"));
        return statusDetails;
    }
}
