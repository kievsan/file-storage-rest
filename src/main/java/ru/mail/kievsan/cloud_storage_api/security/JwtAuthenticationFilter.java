package ru.mail.kievsan.cloud_storage_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider provider;
    private final JWTUserDetails jwtUserDetails;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
//        final String jwt = provider.resolveToken(request, "auth-token");
        final String jwt = request.getHeader("auth-token");
        log.info("  Start JwtAuthenticationFilter.doFilterInternal() :  token  {}", jwt);
        if(jwt != null ) {
            try {
                final String username = provider.extractUsername(jwt);
                if(username != null) {
                    UserDetails userDetails = jwtUserDetails.loadUserByUsername(username);
                    if(provider.isTokenValid(jwt, userDetails)) {
                        var authToken = provider.getAuthentication(jwt);
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (UsernameNotFoundException ex) {
                SecurityContextHolder.clearContext();
                response.sendError(404, ex.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
