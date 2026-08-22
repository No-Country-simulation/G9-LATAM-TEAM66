package com.team66.backend.client;

import com.team66.backend.dto.MlPrediccionRequest;
import com.team66.backend.dto.MlPrediccionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Component
public class MlServiceClient {

    private final RestClient restClient;

    public MlServiceClient(@Value("${ml.service.url}") String mlServiceUrl) {

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        this.restClient = RestClient.builder()
                .baseUrl(mlServiceUrl)
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }

    public MlPrediccionResponse predecir(MlPrediccionRequest request) {
        return restClient.post()
                .uri("/predecir")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(MlPrediccionResponse.class);
    }
}
