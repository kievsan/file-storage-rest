package ru.mail.kievsan.cloud_storage_api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authProvider;
    private final JwtAuthenticationEntryPoint authEntryPoint;

    private final HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(
            new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.COOKIES));

    @Value("${cors.origins}")
    private String origins;

    @Value("${cors.credentials}")
    private Boolean credentials;

    @Value("${cors.methods}")
    private String methods;

    @Value("${cors.headers}")
    private String headers;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Add JWT token filter:
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        // Enable CORS and disable CSRF:
        http.cors(withDefaults()).csrf(AbstractHttpConfigurer::disable);
        // Set session management to stateless:
        http.sessionManagement(cfg -> cfg.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Set permissions on endpoints:
        http
                .authorizeHttpRequests(authz -> authz
                        // public endpoints:
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/user/reg", "/api/v1/login", "/api/v1/logout")
                        .permitAll()
                        // private endpoints:
                        .anyRequest().authenticated()
                );
        http
//                .formLogin(form -> form.loginPage("/api/v1/login").permitAll())
                .formLogin(withDefaults())
                .logout(withDefaults())
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .addLogoutHandler(clearSiteData)
                        .logoutSuccessUrl("/api/v1/login?logout")
//                        .logoutUrl("/api/v1/logout").permitAll()
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/v1/logout")).permitAll()
                );
        http
                .httpBasic(withDefaults())
                .authenticationProvider(authProvider)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedPage("/api/v1/login")
                );
        return http.build();
    }

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
