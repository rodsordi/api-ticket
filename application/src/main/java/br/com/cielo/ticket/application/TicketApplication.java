package br.com.cielo.ticket.application;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication(scanBasePackages = "br.com.cielo")
public class TicketApplication {

    static void main(String[] args) {
        run(TicketApplication.class, args);
    }
}
