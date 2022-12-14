# spring-security-test-addons

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/llabrat/spring-security-test-addons/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/llabrat/spring-security-test-addons/tree/master)
[![DeepSource](https://deepsource.io/gh/llabrat/spring-security-test-addons.svg/?label=active+issues&show_trend=true&token=G_8uF8Av-AZvlueqNMOETPTi)](https://deepsource.io/gh/llabrat/spring-security-test-addons/?ref=repository-badge)
[![Known Vulnerabilities](https://snyk.io/test/github/llabrat/spring-security-test-addons/badge.svg)](https://snyk.io/test/github/llabrat/spring-security-test-addons)

## Description

## Usage

### Dependency
```xml
        <dependency>
            <groupId>com.derplicity</groupId>
            <artifactId>spring-security-test-addons</artifactId>
            <version>${version}</version>
        </dependency>
```

### Examples

Basic JWT authentication, no specific claims or authorities added.
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

Changing subject

```java
    @Test
    @WithMockJwt("changed-subject")
    void exampleTest() {
        webTestClient
                .get()
                .uri("/example")

                .exchange()

                .expectStatus().isOk();
    }
```

Adding authorities

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

Adding claims

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