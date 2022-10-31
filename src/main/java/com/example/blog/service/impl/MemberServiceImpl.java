package com.example.blog.service.impl;

import com.example.blog.constants.Roles;
import com.example.blog.entity.Member;
import com.example.blog.entity.Role;
import com.example.blog.repository.MemberRepository;
import com.example.blog.repository.RoleRepository;
import com.example.blog.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public Member saveMember(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(member);
    }
    @Override
    public Optional<Member> findMemberById(Long id) {
        return memberRepository.findById(id);
    }

    @Override
    public Optional<Member> findMemberByName(String name) {
        return memberRepository.findByUsername(name);
    }

    @Override
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    @Override
    public void makeRole() {
        Optional<Role> findRole = roleRepository.findByName(Roles.ROLE_USER.name());
        if (findRole.isEmpty()) roleRepository.save(new Role(null, Roles.ROLE_USER.name()));
    }

    @Override
    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> foundUser = memberRepository.findByUsername(username);
        if (foundUser.isEmpty())
            throw new UsernameNotFoundException(String.format("member %s can not found in database", username));
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        foundUser.get().getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new User(foundUser.get().getUsername(), foundUser.get().getPassword(), authorities);
    }
}
