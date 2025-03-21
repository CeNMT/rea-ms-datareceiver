package com.cnmt.configuration;

import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;

@Singleton
public class ServiceConfig {
    private final Hl7Config hl7Config;

    public ServiceConfig(@NonNull Hl7Config hl7Config) {
        this.hl7Config = hl7Config;
    }

    public int getHl7Port() {
        return hl7Config.getHl7Port();
    }

    public int getHl7AlarmPort() {
        return hl7Config.getHl7AlarmPort();
    }

    public int getHl7Threads() {
        return hl7Config.getHl7Threads();
    }
}
