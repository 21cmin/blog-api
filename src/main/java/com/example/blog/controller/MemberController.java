package com.example.blog.controller;

import com.example.blog.constants.Roles;
import com.example.blog.dto.MemberDto;
import com.example.blog.entity.Member;
import com.example.blog.entity.Role;
import com.example.blog.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class MemberController {
    private final MemberService memberService;

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
}
