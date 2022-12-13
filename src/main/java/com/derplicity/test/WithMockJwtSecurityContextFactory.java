package com.derplicity.test;

import com.nimbusds.jose.shaded.gson.JsonParseException;
import com.nimbusds.jose.util.JSONObjectUtils;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.text.ParseException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WithMockJwtSecurityContextFactory implements WithSecurityContextFactory<WithMockJwt> {
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    WithMockJwtSecurityContextFactory() {
    }

    public SecurityContext createSecurityContext(WithMockJwt mockJwt) {
        var context = this.securityContextHolderStrategy.createEmptyContext();
        Map<String, Object> claimsJson = new HashMap<>();

        if (!mockJwt.claims().isEmpty()) {
            try {
                claimsJson = JSONObjectUtils.parse(mockJwt.claims());
            } catch (ParseException e) {
                throw new JsonParseException("Invalid claims format.");
            }
        }

        var jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .audience(List.of("https://audience.example.org"))
                .expiresAt(Instant.MAX)
                .issuedAt(Instant.MIN)
                .issuer("https://issuer.example.org")
                .jti("jti")
                .notBefore(Instant.MIN)
                .subject(mockJwt.value());

        claimsJson.forEach(jwt::claim);

        var token = new JwtAuthenticationToken(jwt.build(), AuthorityUtils.createAuthorityList(mockJwt.authorities()));

        context.setAuthentication(token);
        return context;
    }
}
