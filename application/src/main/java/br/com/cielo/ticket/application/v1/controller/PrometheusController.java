package br.com.cielo.ticket.application.v1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/prometheus")
public class PrometheusController {

    @GetMapping(produces = "text/plain")
    public String prometheus() {
        return "# HELP jvm_threads_live_threads The current number of live threads\n" +
               "# TYPE jvm_threads_live_threads gauge\n" +
               "jvm_threads_live_threads 10.0\n" +
               "# HELP http_server_requests_seconds_count Total requests\n" +
               "# TYPE http_server_requests_seconds_count counter\n" +
               "http_server_requests_seconds_count 100.0\n";
    }
}
