package com.example.blog.service;

import com.example.blog.entity.Member;
import com.example.blog.entity.Role;

import java.util.List;
import java.util.Optional;

public interface MemberService {

    List<Member> findAllMembers();

    Member saveMember(Member member);

    Optional<Member> findMemberById(Long id);

    Optional<Member> findMemberByName(String name);

    void deleteMember(Long id);

    void makeRole();

    Optional<Role> findRoleByName(String name);
}
