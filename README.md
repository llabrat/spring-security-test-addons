# spring-security-test-addons

[![Maven Central](https://img.shields.io/maven-central/v/com.derplicity/spring-security-test-addons.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.derplicity%22%20AND%20a:%22spring-security-test-addons%22)
[![CircleCI](https://dl.circleci.com/status-badge/img/gh/llabrat/spring-security-test-addons/tree/master.svg?style=shield)](https://dl.circleci.com/status-badge/redirect/gh/llabrat/spring-security-test-addons/tree/master)
[![DeepSource](https://deepsource.io/gh/llabrat/spring-security-test-addons.svg/?label=active+issues&show_trend=true&token=G_8uF8Av-AZvlueqNMOETPTi)](https://deepsource.io/gh/llabrat/spring-security-test-addons/?ref=repository-badge)
[![Security Score](https://snyk-widget.herokuapp.com/badge/mvn/com.derplicity/spring-security-test-addons/badge.svg)](https://snyk.io/test/github/llabrat/spring-security-test-addons)
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
    <version>0.1.2</version>
</dependency>
```

### Examples

```java
// Configure MockMvc
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebTestClientTests {

    WebTestClient webTestClient;

    // Create WebTestClient
    @Autowired
    void setMockMvc(MockMvc mockMvc) {
        this.webTestClient = MockMvcWebTestClient.bindTo(mockMvc)
                .build();
    }
    
    // Basic mocked JWT authentication token, no specific claims or authorities added.
    @Test
    @WithMockJwt
    void exampleTest() {
        webTestClient
                .get()
                .uri("/example")

                .exchange()

                .expectStatus().isOk();
    }
    
    // Subject of JWT can be changed via the `subject` member.
    @Test
    @WithMockJwt(subject = "changed-subject")
    void exampleTest() {
        webTestClient
                .get()
                .uri("/example")

                .exchange()

                .expectStatus().isOk();
    }

    // Authorities can be defined via a `String[]` assigned to `authorities` member.
    @Test
    @WithMockJwt(authorities = {"EXAMPLE1", "EXAMPLE2"})
    void exampleTest() {
        webTestClient
                .get()
                .uri("/example")

                .exchange()

                .expectStatus().isOk();
    }

    // Custom claims can be added to the JWT via the `claims` member. The member is a 
    // string and expects a JSON object which will be parsed and added to the claims. 
    // Malformed JSON will result in a `JsonParseException`.
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
}
```