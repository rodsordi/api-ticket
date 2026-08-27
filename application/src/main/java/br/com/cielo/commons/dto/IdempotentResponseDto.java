package br.com.cielo.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotentResponseDto {

    private int statusCode;
    private Map<String, String> headers;
    private String jsonBody;
}
