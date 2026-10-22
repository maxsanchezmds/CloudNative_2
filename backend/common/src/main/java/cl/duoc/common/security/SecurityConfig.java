package cl.duoc.common.security;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

@Configuration
public class SecurityConfig {
  @Bean SecurityFilterChain security(HttpSecurity http, CorsConfigurationSource cors) throws Exception {
    return http.csrf(c -> c.disable()).cors(c -> c.configurationSource(cors))
      .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(a -> a.requestMatchers("/actuator/health").permitAll()
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().anyRequest().authenticated())
      .oauth2ResourceServer(o -> o.jwt(j -> {})).build();
  }
  @Bean JwtDecoder decoder(@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuer,
      @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwks,
      @Value("${app.audience}") String audience) {
    NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwks).build();
    OAuth2TokenValidator<Jwt> client = token -> {
      // Cognito access tokens use client_id; OIDC ID tokens and Keycloak use aud.
      boolean valid = (token.getAudience()!=null && token.getAudience().contains(audience)) || audience.equals(token.getClaimAsString("client_id"));
      return valid ? OAuth2TokenValidatorResult.success() : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Unexpected audience/client_id", null));
    };
    decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer), client));
    return decoder;
  }
  @Bean CorsConfigurationSource cors(@Value("${app.cors-origin}") String origin) {
    CorsConfiguration c = new CorsConfiguration(); c.setAllowedOrigins(List.of(origin));
    c.setAllowedMethods(List.of("GET","POST","DELETE","OPTIONS")); c.setAllowedHeaders(List.of("Authorization","Content-Type"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/**",c); return source;
  }
}
