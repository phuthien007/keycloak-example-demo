package com.example.partner;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.config.annotation.web.oauth2.client.OAuth2ClientSecurityMarker;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
@RequestMapping("/api")
@SecurityScheme(
        name = "Keycloak",
        openIdConnectUrl = "http://localhost:8085/realms/SpringBootKeycloak/.well-known/openid-configuration",
        scheme = "bearer",
        type = SecuritySchemeType.OPENIDCONNECT,
        in = SecuritySchemeIn.HEADER,
        bearerFormat
                = "JWT"
)
public class PartnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartnerApplication.class, args);
    }

    // call through the gateway
    // http://localhost:8080/partner
    @GetMapping("/hello")
    public String getPartner() {
        return "Partner";
    }

    @GetMapping("/token")
    public String getToken(@OAuth2ClientSecurityMarker String token) {
        return token;
    }
    // get role
    @GetMapping("/role")
    public String getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
                ;
        return role;
    }

}
