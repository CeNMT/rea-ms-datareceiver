package com.cnmt.service;

import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Hl7ServiceImpl implements Hl7Service {
    private static final Logger log = LoggerFactory.getLogger(Hl7ServiceImpl.class);

    private final HL7ToFHIRConverter hl7ToFHIRConverter;

    public Hl7ServiceImpl() {
        this.hl7ToFHIRConverter = new HL7ToFHIRConverter();
    }

    @Override
    public String getFhirResource(String hl7message) {
        log.info("Converting HL7 {}", hl7message);
        return hl7ToFHIRConverter.convert(hl7message);
    }
}
