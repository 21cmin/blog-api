package com.example.blog.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.blog.constants.Roles;
import com.example.blog.dto.MemberDto;
import com.example.blog.entity.Member;
import com.example.blog.entity.Role;
import com.example.blog.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class MemberController {
    private final MemberService memberService;

    @Value("${SECRET_KEY}")
    private String secretKey;

    @PostMapping
    public ResponseEntity<MemberDto> saveUser(@RequestBody Member member) {
        Optional<Member> duplicate = memberService.findMemberByName(member.getUsername());
        if (duplicate.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Optional<Role> userRole = memberService.findRoleByName(Roles.ROLE_USER.name());
        if (userRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        member.getRoles().add(userRole.get());
        Member newMember = memberService.saveMember(member);
        MemberDto memberDto = new MemberDto(newMember.getId(), newMember.getUsername());
        return ResponseEntity.status(CREATED).body(memberDto);
    }

    @GetMapping("/refresh")
    public void issueAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        HashMap<String, String> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            map.put("error_message", "No refresh token");
            mapper.writeValue(response.getOutputStream(), map);
        } else {
            Optional<Cookie> refreshCookie = stream(cookies).filter(cookie -> cookie.getName().equals("refresh_token")).findFirst();
            try {
                if (refreshCookie.isEmpty()) throw new RuntimeException();

                Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedToken = verifier.verify(refreshCookie.get().getValue());
                String username = decodedToken.getSubject();
                Optional<Member> member = memberService.findMemberByName(username);
                if (member.isEmpty()) throw new NoSuchElementException();
                String accessToken = JWT.create()
                        .withSubject(member.get().getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", member.get().getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                map.put("access_token", accessToken);
                mapper.writeValue(response.getOutputStream(), map);
                response.setStatus(OK.value());
            } catch (Exception exception) {
                response.setStatus(UNAUTHORIZED.value());
                map.put("error_message", exception.getMessage());
                mapper.writeValue(response.getOutputStream(), map);
            }
        }
    }

    @GetMapping("/logout")
    public void logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .secure(true)
                .sameSite("None")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.setStatus(OK.value());
    }

    @GetMapping
    public ResponseEntity<List<Member>> findAllMembers() {
        return ResponseEntity.ok().body(memberService.findAllMembers());
    }

}
