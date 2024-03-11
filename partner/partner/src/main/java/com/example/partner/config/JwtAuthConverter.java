package com.example.partner.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final String REALM_ACCESS = "realm_access";
    private final String RESOURCE_ACCESS = "resource_access";
    private final String ROLES = "roles";

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Collection<GrantedAuthority> roles = extractAuthorities(source);
        return new JwtAuthenticationToken(source, roles);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<GrantedAuthority> roles = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        if (jwt.getClaim(REALM_ACCESS) != null) {
            Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS);
            List<String> keycloakRoles = objectMapper.convertValue(realmAccess.get(ROLES), List.class);
            keycloakRoles.forEach(role -> roles.add((GrantedAuthority) () -> role));
        }
        if (jwt.getClaim(RESOURCE_ACCESS) != null) {
            Map<String, Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS);

            // entry resource access
            resourceAccess.forEach((key, value) -> {
                Map<String, Object> clientAccess = objectMapper.convertValue(value, Map.class);
                List<String> clientRoles = objectMapper.convertValue(clientAccess.get(ROLES), List.class);
                clientRoles.forEach(role -> roles.add((GrantedAuthority) () -> key + "_" + role));
            });
        }

        return roles;
    }

}
