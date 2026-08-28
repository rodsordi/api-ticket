package br.com.cielo.ticket.application.v1.controller;

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/prometheus")
@RequiredArgsConstructor
public class PrometheusController {

    private final PrometheusMeterRegistry prometheusRegistry;

    @GetMapping(produces = "text/plain;charset=UTF-8")
    public String prometheus() {
        return prometheusRegistry.scrape();
    }
}
