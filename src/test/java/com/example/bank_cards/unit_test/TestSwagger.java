package com.example.bank_cards.unit_test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;

@Component
public class TestSwagger {

    private WebTestClient webTestClient;

    @Autowired
    public void setWebTestClient(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Test
    void apiDocsGeneration() {
        webTestClient.get().uri("/v3/api-docs.yaml").exchange()
                .expectStatus().isOk();
    }
}
