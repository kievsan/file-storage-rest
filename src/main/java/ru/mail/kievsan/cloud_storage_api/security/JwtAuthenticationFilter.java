package ru.mail.kievsan.cloud_storage_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.mail.kievsan.cloud_storage_api.exception.UnauthorizedUserException;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUserDetails jwtUserDetails;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws UnauthorizedUserException, ServletException, IOException {
        final String jwt = request.getHeader("auth-token");
        log.info("  Start JwtAuthenticationFilter.doFilterInternal() :  token  {}", jwt);
        if(jwt != null ) {
            try {
                UserDetails user = jwtUserDetails.loadUserByJWT(jwt);
                log.info("     userDetails:  '{}', {}", user.getUsername(), user.getAuthorities());
                var authToken = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception ex) {
                SecurityContextHolder.clearContext();
                response.sendError(404, ex.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
