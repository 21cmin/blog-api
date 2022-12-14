package com.example.blog.configuration.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class MyAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final String secretKey;
    private final AuthenticationManager manager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.error("attempt login user: {}", username);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        return manager.authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User principal = (User) authResult.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());

        String accessToken = JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .path("/")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        Map<String, String> map = new HashMap<>();
        map.put("username", principal.getUsername());
        map.put("access_token", accessToken);
        new ObjectMapper().writeValue(response.getOutputStream(), map);
    }
}
