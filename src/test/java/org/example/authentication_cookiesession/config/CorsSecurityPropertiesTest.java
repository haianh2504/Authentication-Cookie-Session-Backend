package org.example.authentication_cookiesession.config;

import org.example.authentication_cookiesession.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorsSecurityPropertiesTest {

    private static final CorsSecurityProperties PROPERTIES = new CorsSecurityProperties(
            List.of("http://localhost:3000"),
            List.of("GET", "POST", "DELETE", "OPTIONS"),
            List.of("Accept", "Content-Type", "X-XSRF-TOKEN"),
            3600);

    @Test
    void createsCredentialedConfigurationForAllowedOrigin() {
        CorsConfiguration configuration = configurationFor("http://localhost:3000");

        assertNotNull(configuration);
        assertEquals("http://localhost:3000", configuration.checkOrigin("http://localhost:3000"));
        assertTrue(configuration.getAllowCredentials());
        assertEquals(List.of("GET", "POST", "DELETE", "OPTIONS"), configuration.getAllowedMethods());
        assertEquals(List.of("Accept", "Content-Type", "X-XSRF-TOKEN"), configuration.getAllowedHeaders());
        assertEquals(3600L, configuration.getMaxAge());
    }

    @Test
    void rejectsOriginOutsideAllowlist() {
        CorsConfiguration configuration = configurationFor("http://localhost:4000");

        assertNotNull(configuration);
        assertNull(configuration.checkOrigin("http://localhost:4000"));
    }

    private CorsConfiguration configurationFor(String origin) {
        CorsConfigurationSource source = new SecurityConfig().corsConfigurationSource(PROPERTIES);
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/v1/auth/admin");
        request.addHeader("Origin", origin);
        return source.getCorsConfiguration(request);
    }
}
