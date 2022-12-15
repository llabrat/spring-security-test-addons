# spring-security-test-addons

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/llabrat/spring-security-test-addons/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/llabrat/spring-security-test-addons/tree/master)
[![DeepSource](https://deepsource.io/gh/llabrat/spring-security-test-addons.svg/?label=active+issues&show_trend=true&token=G_8uF8Av-AZvlueqNMOETPTi)](https://deepsource.io/gh/llabrat/spring-security-test-addons/?ref=repository-badge)
[![Known Vulnerabilities](https://snyk.io/test/github/llabrat/spring-security-test-addons/badge.svg)](https://snyk.io/test/github/llabrat/spring-security-test-addons)

## Description

Test annotation for mocking JWT authentication when testing MockMVC with WebTestClient. Workaround for issue introduced with spring security 5.3, details of which can be found [here](https://github.com/spring-projects/spring-security/issues/9257).

This annotation was heavily influenced by the work @rwinch did with the existing [spring security test annotations](https://github.com/spring-projects/spring-security/tree/main/test/src/main/java/org/springframework/security/test/context/support), as well as the workaround he proposed in the above referenced issue.

## Usage

### Dependency
```xml
<dependency>
    <groupId>com.derplicity</groupId>
    <artifactId>spring-security-test-addons</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Examples

Configuring WebTestClient for MockMVC
```java
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebTestClientTests {

    public WebTestClient webTestClient;

    @Autowired
    void setMockMvc(MockMvc mockMvc) {
        this.webTestClient = MockMvcWebTestClient.bindTo(mockMvc)
                .build();
    }
    
    // ... test all the things
}
```

Basic mocked JWT authentication token, no specific claims or authorities added.
```java
    @Test
    @WithMockJwt
    void exampleTest() {
        webTestClient
                .get()
                .uri("/example")

                .exchange()

                .expectStatus().isOk();
    }
```

Subject of JWT can be changed via the `value` member.

```java
    @Test
    @WithMockJwt(value = "changed-subject")
    void exampleTest() {
        webTestClient
                .get()
                .uri("/example")

                .exchange()

                .expectStatus().isOk();
    }
```

Authoritied can be defined via a `String[]` assigned to `authorities` member.

```java
    @Test
    @WithMockJwt(authorities = {"EXAMPLE1", "EXAMPLE2"})
    void exampleTest() {
        webTestClient
                .get()
                .uri("/example")

                .exchange()

                .expectStatus().isOk();
    }
```

Custom claims can be added to the JWT via the `claims` member. The member is a string and expects a JSON object which will be parsed and added to the claims. Malformed JSON will result in a `JsonParseException`.

```java
    @Test
    @WithMockJwt(claims = """
            {
              "exampleClaim": "exampleValue"
            }
            """)
    void exampleTest() {
        webTestClient
                .get()
                .uri("/example")

                .exchange()

                .expectStatus().isOk();
    }
```