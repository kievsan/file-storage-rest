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
import ru.mail.kievsan.cloud_storage_api.util.ILogUtils;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    static String className = ILogUtils.className_.apply(JwtAuthenticationFilter.class);

    private final JWTUserDetails details;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws UnauthorizedUserException, ServletException, IOException {

        final String uri = request.getRequestURI(), method = request.getMethod();
        final String jwt = request.getHeader(ISecuritySettings.JWT_HEADER_NAME);

        log.info("{}< {} :  {} >----- Start {},  token  '{}'", ILogUtils.prefix, uri, method, className, details.presentJWT(jwt));

        if (jwt == null || jwt.isBlank()) {
            String service = String.format("'%s' %s request", uri, method);
            String err = String.format(" no JWT header '%s'.", ISecuritySettings.JWT_HEADER_NAME);
            shutDown(new UnauthorizedUserException(err, null, null, service, className));
        } else {
            try {
                final UserDetails user = details.loadUserByJWT(jwt);
                log.info("     userDetails:  '{}', {}", user.getUsername(), user.getAuthorities());
                var authToken = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception ex) {
                shutDown(new UnauthorizedUserException(ex.getMessage(), null, null, uri, className));
                //                response.sendError(401, ex.getMessage());
                //                return;
            }
        }
        filterChain.doFilter(request, response);
        log.info("{} Finish {} :  {},  token  '{}'", ILogUtils.prefix, className,
                details.presentAuthenticated(), details.presentJWT(jwt));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        final String uri = request.getRequestURI(), method = request.getMethod();
        final String jwt = request.getHeader(ISecuritySettings.JWT_HEADER_NAME);
        return ISecuritySettings.LOGIN_URI.equals(uri)
                || (jwt == null || jwt.isBlank())
                && ISecuritySettings.POST_FREE_ENTRY_POINTS.contains(uri) && "POST".equals(method);
    }

    private void shutDown(RuntimeException ex) throws UnauthorizedUserException {
        log.error("{} Failed to execute {}:\t{}", ILogUtils.prefix, className, ex.getMessage());
        SecurityContextHolder.clearContext();
        throw ex;
    }
}
