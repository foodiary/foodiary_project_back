package com.foodiary.auth.jwt;

import com.foodiary.member.model.MemberDto;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class CustomUserDetails implements UserDetails {

    private final MemberDto member;

    public CustomUserDetails(MemberDto member) {
        this.member = member;
    }


    // 유저 권한 목록
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    // 비밀번호
    @Override
    public String getPassword() {
        return member.getMemberPassword();
    }

    // PK값
    @Override
    public String getUsername() {
        return String.valueOf(member.getMemberId());
    }

    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 회원 탈퇴 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //사용자 활성화 여부 true : 활성화, false : 비활성화
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
