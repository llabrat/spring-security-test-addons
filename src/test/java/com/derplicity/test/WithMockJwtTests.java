package com.derplicity.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SecurityTestExecutionListeners
class WithMockJwtTests {

    @Test
    @WithMockJwt
    void withMockJwtRegisters() throws MalformedURLException {
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("subject");

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertThat(jwt.getSubject()).isEqualTo("subject");
        assertThat(jwt.getIssuer()).isEqualTo(new URL("https://issuer.example.org"));
        assertThat(jwt.getAudience()).isEqualTo(List.of("https://audience.example.org"));
        assertThat(jwt.getId()).isEqualTo("jti");
    }

    @Test
    void defaults() {
        WithMockJwt mockJwt = AnnotatedElementUtils.findMergedAnnotation(Annotated.class, WithMockJwt.class);

        assertThat(mockJwt).isNotNull();
        assertThat(mockJwt.value()).isEqualTo("subject");
        assertThat(mockJwt.claims()).isEmpty();
        assertThat(mockJwt.authorities()).isEmpty();
        assertThat(mockJwt.setupBefore()).isEqualByComparingTo(TestExecutionEvent.TEST_METHOD);

        WithSecurityContext context = AnnotatedElementUtils.findMergedAnnotation(WithMockJwtTests.Annotated.class,
                WithSecurityContext.class);

        assertThat(context).isNotNull();
        assertThat(context.setupBefore()).isEqualTo(TestExecutionEvent.TEST_METHOD);
    }

    @Test
    void findMergedAnnotationWhenSetupExplicitThenOverridden() {
        WithSecurityContext context = AnnotatedElementUtils.findMergedAnnotation(SetupExplicit.class,
                WithSecurityContext.class);

        assertThat(context).isNotNull();
        assertThat(context.setupBefore()).isEqualTo(TestExecutionEvent.TEST_METHOD);
    }

    @Test
    void findMergedAnnotationWhenSetupOverriddenThenOverridden() {
        WithSecurityContext context = AnnotatedElementUtils.findMergedAnnotation(SetupOverridden.class,
                WithSecurityContext.class);

        assertThat(context).isNotNull();
        assertThat(context.setupBefore()).isEqualTo(TestExecutionEvent.TEST_EXECUTION);
    }

    @WithMockJwt
    private static class Annotated {

    }

    @WithMockJwt(setupBefore = TestExecutionEvent.TEST_METHOD)
    private static class SetupExplicit {

    }

    @WithMockJwt(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    private static class SetupOverridden {

    }
}
