package com.foodiary.auth.jwt;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.foodiary.member.entity.Member;

import lombok.Getter;

@Getter
public class MemberDetails extends User {

    private final Member member;

    public MemberDetails(Member member) {
        super(member.getMemberEmail(), member.getMemberNickName(), List.of(new SimpleGrantedAuthority("USER")));
        this.member = member;
    }
}
