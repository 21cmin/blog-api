package com.example.blog.configuration.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@RequiredArgsConstructor
public class MyAuthorizationFilter extends OncePerRequestFilter {
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        if(request.getMethod().equals("GET") || servletPath.startsWith("/api/user")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            log.error("not authorization header {}", request);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String accessToken = authorizationHeader.substring("Bearer ".length());
                    log.error("access token {}", accessToken);
                    Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodedToken = verifier.verify(accessToken);
                    String username = decodedToken.getSubject();
                    String[] roles = decodedToken.getClaim("roles").asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(token);
                    filterChain.doFilter(request, response);
                } catch (Exception exception) {
                    log.error("access token verify error");
                    response.setStatus(FORBIDDEN.value());
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
