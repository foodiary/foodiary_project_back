package com.foodiary.member.service;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.foodiary.member.dto.MemberPostDto;
import com.foodiary.member.dto.MemberResponseDto;
import com.foodiary.member.entity.Member;
import com.foodiary.member.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberMapper mapper;

    public void createdMember(MemberPostDto postDto) {
        mapper.saveMember(postDto);
    }

    public Member getMember(String email) {
        Member response =  mapper.findByEmail(email);

        return response;
    }
}
