package com.foodiary.auth.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberDto;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        // TODO : 임의로 변경, 로직 검토 부탁드려요 민택님
        MemberDto member = memberMapper
                .findByEmail(userEmail).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
         return new MemberDetails(member);
    }
}
