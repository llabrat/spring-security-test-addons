package com.derplicity.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = WebTestClientTests.TestApplication.class)
class WebTestClientTests {


    public WebTestClient webTestClient;

    @Autowired
    void setMockMvc(MockMvc mockMvc) {
        this.webTestClient = MockMvcWebTestClient.bindTo(mockMvc)
                .build();
    }

    @Test
    void webTestClientMutation() {
        String message = "If this test fails, we might not need this annotation anymore";

        try {
            webTestClient
                    .mutateWith(mockJwt())
                    .get()
                    .uri("/basic")

                    .exchange()

                    .expectStatus().isOk();
        } catch (NullPointerException ex) {
            message = ex.getMessage();
        }

        assertThat(message).contains("because \"httpHandlerBuilder\" is null");
    }

    @Test
    @WithMockJwt
    void basic() {
        webTestClient
                .get()
                .uri("/basic")

                .exchange()

                .expectStatus().isOk();
    }

    @Test
    void basicNoAuth() {
        webTestClient
                .get()
                .uri("/basic")

                .exchange()

                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockJwt(authorities = {"TEST_AUTHORITY"})
    void hasAuthority() {
        webTestClient
                .get()
                .uri("/authority")

                .exchange()

                .expectStatus().isOk();
    }

    @Test
    void hasAuthorityNoAuth() {
        webTestClient
                .get()
                .uri("/authority")

                .exchange()

                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockJwt(claims = """
            {
                "testString": "stringValue",
                "testNumber": 1,
                "testArray": [
                    "arrayEntry1", "arrayEntry2", "arrayEntry3"
                ],
                testObject: {
                    "testNested": "nestedValue"
                }
            }
            """)
    void withClaims() {
        webTestClient
                .get()
                .uri("/inspection")

                .exchange()

                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.claims.testString").isEqualTo("stringValue")
                .jsonPath("$.claims.testNumber").isNumber()
                .jsonPath("$.claims.testNumber").isEqualTo(1)
                .jsonPath("$.claims.testArray").isArray()
                .jsonPath("$.claims.testArray[0]").isEqualTo("arrayEntry1")
                .jsonPath("$.claims.testArray[1]").isEqualTo("arrayEntry2")
                .jsonPath("$.claims.testArray[2]").isEqualTo("arrayEntry3")
                .jsonPath("$.claims.testObject").isMap()
                .jsonPath("$.claims.testObject.testNested").isEqualTo("nestedValue");
    }

    @RestController
    @SpringBootApplication
    @SuppressWarnings("SameReturnValue")
    static class TestApplication {

        @GetMapping("/basic")
        public String basic() {
            return "basic";
        }

        @GetMapping("/authority")
        @PreAuthorize("hasAuthority('TEST_AUTHORITY')")
        public String hasAuthority() {
            return "authority";
        }

        @GetMapping("/inspection")
        public Jwt inspectJwt(@AuthenticationPrincipal Jwt principal) {
            return principal;
        }

        public static void main(String[] args) {
            SpringApplication.run(TestApplication.class, args);
        }

        @Configuration
        public static class SecurityConfig {

            @Bean
            SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                        .authorizeHttpRequests(
                                authorize -> authorize.anyRequest().authenticated()
                        )
                        .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

                return http.build();
            }
        }

    }
}

