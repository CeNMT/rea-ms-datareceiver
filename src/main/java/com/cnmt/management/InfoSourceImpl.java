package com.cnmt.management;

import com.cnmt.service.FhirService;
import io.micronaut.context.env.MapPropertySource;
import io.micronaut.context.env.PropertySource;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.core.version.VersionUtils;
import io.micronaut.management.endpoint.info.InfoSource;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Singleton
public class InfoSourceImpl implements InfoSource {
    private static final Logger log = LoggerFactory.getLogger(InfoSourceImpl.class);
    private final FhirService fhirService;

    public InfoSourceImpl(FhirService fhirService) {
        this.fhirService = fhirService;
    }

    @Override
    public Publisher<PropertySource> getSource() {
        return Publishers.just(getPropertySource());
    }

    private PropertySource getPropertySource() {
        final List<Map<String, ?>> info = List.of(
                Map.of("Micronaut version", VersionUtils.getMicronautVersion()),
                Map.of("FHIR version", fhirService.getVersion()),
                Map.of("Health Data Receiver", "TODO")); // todo
        log.info(info.toString());
        return new MapPropertySource("info", Map.of("Info", info));
    }
}
