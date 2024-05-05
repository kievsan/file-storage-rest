package ru.mail.kievsan.cloud_storage_api.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import ru.mail.kievsan.cloud_storage_api.exception.NotAuthenticateException;
import ru.mail.kievsan.cloud_storage_api.exception.UserNotFoundException;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.*;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
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
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);

//            Map<String, Object> claims = new ConcurrentHashMap<>();
//            String authorities=  user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
//                    .collect(Collectors.joining(","));
//            claims.put("authorities", authorities);
//            var jwtToken = provider.generateToken(claims, user);

            var jwtToken = provider.generateToken(user);
            System.out.printf("login jwtRepo ->\n   %s:  '%s...'\n", user.getUsername(), jwtToken.substring(0,20));

            auth = SecurityContextHolder.getContext().getAuthentication();
            msg += String.format(" with ROLEs = %s authenticated: %s", auth.getAuthorities(), auth.getPrincipal());
            log.info("SUCCESS! {}", msg);
            return new AuthResponse(jwtToken);
        } catch (UsernameNotFoundException ex) {
            msg += String.format(" %s: not found!", errMsg);
            throw new UserNotFoundException(msg, HttpStatus.NOT_FOUND, "AUTH", "'/login'", "'authenticate service'");
        } catch (RuntimeException ex) {
            msg += String.format(" %s! %s", errMsg, ex.getMessage());
            throw new NotAuthenticateException(msg);
        }
    }
//
    public String logout(HttpServletRequest request, HttpServletResponse response, User user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        new SecurityContextLogoutHandler().logout(request, response, auth);
        request.getSession().invalidate();

        System.out.printf("logout jwtRepo ->   %s\n", auth.getPrincipal());
        log.info("User '{}' ({}) was logged out. JWT is disabled.", user.getUsername(), user.getNickname());

        return String.format("Success logout: '%s' (%s)", user.getUsername(), user.getNickname()) ;
    }
}
