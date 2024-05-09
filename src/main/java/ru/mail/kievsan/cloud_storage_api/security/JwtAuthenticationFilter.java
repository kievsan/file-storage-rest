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
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws UnauthorizedUserException, ServletException, IOException {

        final String jwt = request.getHeader(SecuritySettings.JWT_HEADER_NAME);
        log.info(">--------------< Start JwtAuthenticationFilter.doFilterInternal() :  token  '{}'", jwtUserDetails.jwtPresent(jwt));

        if (jwt == null || jwt.isBlank()) {
            shutDownIfRequiredTokenURL(request.getRequestURI(), request.getMethod());
        } else {
            try {
                UserDetails user = jwtUserDetails.loadUserByJWT(jwt);
                log.info("     userDetails:  '{}', {}", user.getUsername(), user.getAuthorities());
                var authToken = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception ex) {
                shutDown(new UnauthorizedUserException(ex.getMessage(), null, null, null, "'JwtAuthenticationFilter'"));
//                response.sendError(401, ex.getMessage());
//                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void shutDownIfRequiredTokenURL(String uri, String method) {
        if (!SecuritySettings.FREE_ENTRY_POINTS.contains(uri)) {
            String service = String.format("'%s' %s request", uri, method);
            String err = String.format(" no JWT header '%s'.", SecuritySettings.JWT_HEADER_NAME);
            shutDown(new UnauthorizedUserException(err, null, null, service, "'JwtAuthenticationFilter'"));
        }
    }

    private void shutDown(RuntimeException ex) {
        log.error(">--------------< Failed to execute JwtAuthenticationFilter.doFilterInternal():\t{}", ex.getMessage());
        SecurityContextHolder.clearContext();
        throw ex;
    }
}
