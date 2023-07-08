package ru.practicum.stats.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.dto.StatsDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected List<StatsDto> get(String path, @Nullable Map<String, Object> params) {
        return makeAndSendRequest(HttpMethod.GET, path, params, null);
    }

    protected <T> List<StatsDto> post(String path, @Nullable Map<String, Object> params, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, params, body);
    }

    protected <T> List<StatsDto> post(String path, T body) {
        return post(path, null, body);
    }

    private <T> List<StatsDto> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> params,
                                                  @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<List<StatsDto>> ewmServerResponse;
        try {
            if (params != null) {
                ewmServerResponse = rest.exchange(path, method, requestEntity, new ParameterizedTypeReference<>() {
                }, params);
            } else {
                ewmServerResponse = rest.exchange(path, method, requestEntity, new ParameterizedTypeReference<>() {
                });
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
        return prepareStatsResponse(ewmServerResponse);

    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static List<StatsDto> prepareStatsResponse(ResponseEntity<List<StatsDto>> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody()).getBody();
        }
        return Collections.emptyList();
    }
}
