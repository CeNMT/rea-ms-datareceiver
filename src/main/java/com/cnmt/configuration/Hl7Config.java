package com.cnmt.configuration;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import lombok.Getter;

@Factory
@Getter
public class Hl7Config {
    @Value("${data.hl7.port}")
    private int hl7Port;

    @Value("${data.hl7.alarm_port}")
    private int hl7AlarmPort;

    @Value("${data.hl7.thread_pool}")
    private int hl7Threads;
}
