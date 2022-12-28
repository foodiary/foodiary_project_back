package com.foodiary.member.service;

import org.springframework.stereotype.Service;

import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberSignUpDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberMapper mapper;

    public void createdMember(MemberSignUpDto memberSignUpDto) {
        mapper.saveMember(memberSignUpDto);
    }
}
