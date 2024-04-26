package ru.mail.kievsan.cloud_storage_api.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import ru.mail.kievsan.cloud_storage_api.exception.AuthNotAuthenticateException;
import ru.mail.kievsan.cloud_storage_api.exception.AuthUserNotFoundException;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.*;
import ru.mail.kievsan.cloud_storage_api.security.JWTUserDetails;
import ru.mail.kievsan.cloud_storage_api.security.JwtProvider;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JWTUserDetails userDetails;
    private final JwtProvider provider;
    private final AuthenticationManager authManager;
//
    public AuthResponse authenticate(AuthRequest request) throws RuntimeException {
        String msg = String.format("User '%s'", request.getLogin());
        String errMsg = "was not authenticated";

        try {
            var user = userDetails.loadUserByUsername(request.getLogin());
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            var jwtToken = provider.generateToken(user);
            System.out.printf("login jwtRepo ->\n   %s:  %s\n", user.getUsername(), jwtToken);

            auth = SecurityContextHolder.getContext().getAuthentication();
            msg += String.format(" with ROLEs = %s authentication: %s", auth.getAuthorities(), auth.getPrincipal());
            log.info(msg);
            return new AuthResponse(jwtToken);
        } catch (UsernameNotFoundException ex) {
            msg += String.format(" %s: not found!", errMsg);
            throw new AuthUserNotFoundException(msg);
        } catch (RuntimeException ex) {
            msg += String.format(" %s! %s", errMsg, ex.getMessage());
            throw new AuthNotAuthenticateException(msg);
        }
    }
//
    public String logout(String token,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // почему-то AnonymousAuthenticationToken   ???
        var usernameByAuth = auth.getName(); // anonymousUser  ???

        var username = provider.extractUsername(token);

        new SecurityContextLogoutHandler().logout(request, response, auth);  // для кого тогда logout ???
        request.getSession().invalidate();

        System.out.printf("logout jwtRepo ->   %s:  %s\n", username, token);
        log.info("User '{}' was logged out. JWT is disabled.", username);

        return "Success logout: " + username;
    }
}
