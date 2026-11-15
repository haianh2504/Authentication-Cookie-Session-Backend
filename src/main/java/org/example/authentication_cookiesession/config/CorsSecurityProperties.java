package org.example.authentication_cookiesession.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/** Cấu hình origin và các thành phần CORS cho frontend gọi API bằng cookie. */
@Validated
@ConfigurationProperties(prefix = "app.security.cors")
public record CorsSecurityProperties(
        @NotEmpty List<@NotBlank String> allowedOrigins,
        @NotEmpty List<@NotBlank String> allowedMethods,
        @NotEmpty List<@NotBlank String> allowedHeaders,
        @Positive long maxAge
) {
}
