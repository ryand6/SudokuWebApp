package com.github.ryand6.sudokuweb.services.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final RestClient restClient;
    private final String from;

    public EmailService(@Value("${resend.api-key}") String apiKey,
                        @Value("${mail.from}") String from) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(10));

        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .requestFactory(factory)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        this.from = from;
    }

    public void sendOtpEmail(String emailAddress, String otp) {
        Map<String, Object> body = Map.of(
                "from", from,
                "to", List.of(emailAddress),
                "subject", "Your account recovery code",
                "text", "Your one-time recovery code is: " + otp
                        + "\n\nThis code expires in 10 minutes.\n\nIf you did not request this, please ignore this email."
        );

        restClient.post()
                .uri("/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

}
