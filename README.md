# spring-security-test-addons

*insert status badges*

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