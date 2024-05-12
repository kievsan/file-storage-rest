package ru.mail.kievsan.cloud_storage_api.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.mail.kievsan.cloud_storage_api.exception.UnauthorizedUserException;
import ru.mail.kievsan.cloud_storage_api.security.JWTUserDetails;
import ru.mail.kievsan.cloud_storage_api.security.SecuritySettings;
import ru.mail.kievsan.cloud_storage_api.util.LogUtils;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileStorageFilter extends OncePerRequestFilter {

    static String className = LogUtils.className_.apply(FileStorageFilter.class);

    private final JWTUserDetails details;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws UnauthorizedUserException, ServletException, IOException {

        final String uri = request.getRequestURI(), method = request.getMethod();

        log.info("{}< {} :  {} >----- Start {},  {}", LogUtils.prefix, uri, method, className, details.presentAuthenticated());

        filterChain.doFilter(request, response);

        log.info("{} Finish {} :  {}", LogUtils.prefix, className, details.presentAuthenticated());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !SecuritySettings.FILE_URI.equals(request.getRequestURI());
    }
}
