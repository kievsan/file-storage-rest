package ru.mail.kievsan.cloud_storage_api.security;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

import static org.springframework.security.config.Customizer.withDefaults;
import static ru.mail.kievsan.cloud_storage_api.security.ISecuritySettings.*;

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
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        // public endpoints:
                        .requestMatchers(HttpMethod.POST, SIGN_UP_URI, LOGIN_URI, LOGOUT_URI).permitAll()
                        // private endpoints:
                        .requestMatchers(HttpMethod.GET, USER_URI + "/*/**").hasAnyAuthority("ADMIN") // .hasAnyRole("ADMIN") - не работает???
                        .requestMatchers(HttpMethod.DELETE, USER_URI + "/*/**").hasAnyAuthority("ADMIN")
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
                        .logoutSuccessUrl(LOGIN_URI + "?logout")
//                        .logoutUrl("/api/v1/logout").permitAll()
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/v1/logout")).permitAll()
                );
        http
                .httpBasic(withDefaults())
                .authenticationProvider(authProvider)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedPage(LOGIN_URI)
                );
//        http.securityContext((context) -> context.securityContextRepository(new HttpSessionSecurityContextRepository()));
        return http.build();
    }
}
