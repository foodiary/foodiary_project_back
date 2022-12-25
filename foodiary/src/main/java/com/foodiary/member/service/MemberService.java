package com.foodiary.member.service;

import org.springframework.stereotype.Service;

import com.foodiary.member.dto.MemberPostDto;
import com.foodiary.member.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberMapper mapper;

    public void createdMember(MemberPostDto postDto) {
        mapper.saveMember(postDto);
    }
}
