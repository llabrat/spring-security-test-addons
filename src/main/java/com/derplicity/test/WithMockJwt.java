package com.derplicity.test;

import com.nimbusds.jose.util.JSONObjectUtils;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.security.oauth2.jwt.Jwt;

import java.lang.annotation.*;


/**
 * <p>This annotation creates a {@link SecurityContext} populated with a {@link JwtAuthenticationToken}
 * for the purposes of testing a {@link MockMvc} with {@link WebTestClient}.
 * <p>
 * <p>The {@link JwtAuthenticationToken} contains a default subject of {@link #value()}, and can be augmented with
 * a custom subject via {@link #subject()}. A list of {@link GrantedAuthority}'s can be supplied via {@link #authorities()},
 * while claims can be supplied via {@link #claims()}.
 *
 * @author Robert Fletcher
 * @since 0.1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(
        factory = WithMockJwtSecurityContextFactory.class
)
public @interface WithMockJwt {

    /**
     * Convenience method for setting JWT subject. Default value is "subject". Overridden by {@link #subject()}.
     * <p>
     * Example:
     * <p>
     * {@code @WithMockJwt("alternate-subject")}
     */
    String value() default "subject";

    /**
     * Allows specification of an alternate subject name for JWT. Overrides {@link #value()}.
     * <p>
     * Example:
     * <p>
     * {@code @WithMockJwt(subject = "alternate-subject")}
     */
    String subject() default "";

    /**
     * The list of authorities to use. A {@link GrantedAuthority} will be created for each value.
     * <p>
     * Example:
     * <p>
     * {@code @WithMockJwt(authorities = {"Authority1", "Authority2"})}
     */
    String[] authorities() default {};

    /**
     * Allows the addition of custom claims to the {@link Jwt}. String argument must
     * be valid JSON and will be parsed by {@link JSONObjectUtils#parse(String)}. The resulting
     * entries will be passed to {@link Jwt.Builder#claim(String, Object)}
     * <p>
     * Example:
     * <p>
     * {@code @WithMockJwt(claims = {"""{"claim": "value"}"""})}
     */
    String claims() default "";

    /**
     * Determines when the {@link SecurityContext} is set up. The default is before
     * {@link TestExecutionEvent#TEST_METHOD}
     */
    @AliasFor(
            annotation = WithSecurityContext.class
    )
    TestExecutionEvent setupBefore() default TestExecutionEvent.TEST_METHOD;

}
