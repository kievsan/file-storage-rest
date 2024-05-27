package ru.mail.kievsan.cloud_storage_api.config;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@NoArgsConstructor
public class CorsConfig {

    @Value("${cors.origins}")
    private String origins;

    @Value("${cors.credentials}")
    private Boolean credentials;

    @Value("${cors.methods}")
    private String methods;

    @Value("${cors.headers}")
    private String headers;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(credentials);
        configuration.setAllowedOrigins(List.of(origins));
        configuration.setAllowedMethods(List.of(methods));
        configuration.setAllowedHeaders(List.of(headers));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource(CorsEndpointProperties corsProperties) {
//        var source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsProperties.toCorsConfiguration());
//        return source;
//    }

}
