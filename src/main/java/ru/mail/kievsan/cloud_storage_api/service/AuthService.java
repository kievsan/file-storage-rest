package ru.mail.kievsan.cloud_storage_api.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.*;
import ru.mail.kievsan.cloud_storage_api.security.JWTRepo;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;
import ru.mail.kievsan.cloud_storage_api.security.JwtProvider;

import java.util.NoSuchElementException;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserJPARepo userRepo;
    private final JWTRepo jwtRepo;
    private final JwtProvider provider;
    private final AuthenticationManager authManager;

    Function<String, AuthResponse> OK_RESPONSE = token-> AuthOkResponse.builder().authToken(token).build();
    Function<String, AuthResponse> BAD_RESPONSE = msg-> AuthErrResponse.builder().message(msg).id(0).build();
//
    public ResponseEntity<AuthResponse> authenticate(AuthRequest request) {
        ResponseEntity<AuthResponse> response = null;
        String msg = String.format("User '%s'", request.getLogin());
        String errMsg = "was not authenticated";

        try {
            var user = userRepo.findByEmail(request.getLogin()).orElseThrow();
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            var jwtToken = provider.generateToken(user);
            jwtRepo.remove(user.getUsername());
            jwtRepo.put(user.getUsername(), jwtToken);
            System.out.printf("login jwtRepo ->\n   %s:  %s\n", user.getUsername(), jwtToken);
            jwtRepo.print();

            msg += String.format(
                    "(%s) authentication, current ROLE = %s. JWT: %s",
                    user.getNickname(), auth.getAuthorities(), jwtToken);
            response = new ResponseEntity<>(OK_RESPONSE.apply(jwtToken), HttpStatus.OK);
        } catch (AuthenticationException ex) {
            msg += String.format(" %s! %s", errMsg, ex.getMessage());
            response = new ResponseEntity<>(BAD_RESPONSE.apply(msg), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException ex) {
            msg += String.format(" %s: not found!", errMsg);
            response = new ResponseEntity<>(BAD_RESPONSE.apply(msg), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException ex) {
            msg += String.format(" %s: %s", errMsg, ex.getMessage());
            response = new ResponseEntity<>(BAD_RESPONSE.apply(msg), HttpStatus.BAD_REQUEST);
        }
        log.info(msg);
        return response;
    }
//
    public ResponseEntity<String> logout(String token,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // почему-то AnonymousAuthenticationToken   ???
        var usernameByAuth = auth.getName(); // anonymousUser  ???

        var username = provider.extractUsername(token);

        new SecurityContextLogoutHandler().logout(request, response, auth);  // для кого тогда logout ???
        request.getSession().invalidate();

        jwtRepo.removeByToken(token);
        System.out.printf("logout jwtRepo ->\n   %s:  %s\n", username, token);
        jwtRepo.print();

        log.info("User '{}' was logged out. JWT is disabled.", username);

        return ResponseEntity.ok("Success logout: " + username);
    }
}
