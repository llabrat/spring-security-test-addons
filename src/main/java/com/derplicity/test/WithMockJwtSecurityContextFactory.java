package com.derplicity.test;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.Instant;
import java.util.List;

public class WithMockJwtSecurityContextFactory implements WithSecurityContextFactory<WithMockJwt> {
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    WithMockJwtSecurityContextFactory() {
    }

    public SecurityContext createSecurityContext(WithMockJwt mockJwt) {
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();

        var jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .audience(List.of("https://audience.example.org"))
                .expiresAt(Instant.MAX)
                .issuedAt(Instant.MIN)
                .issuer("https://issuer.example.org")
                .jti("jti")
                .notBefore(Instant.MIN)
                .subject(mockJwt.value());

        var token = new JwtAuthenticationToken(jwt.build(), AuthorityUtils.createAuthorityList(mockJwt.authorities()));

        context.setAuthentication(token);
        return context;
    }
}
