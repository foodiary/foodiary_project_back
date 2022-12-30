package com.foodiary.member.service;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberImageDto;
import com.foodiary.member.model.MemberSignUpRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberMapper mapper;

    public void createdMember(MemberSignUpRequestDto memberSignUpDto) {
        mapper.saveMember(memberSignUpDto);
    }

    public MemberDto findMemberLoginId(String loginId) {
        return mapper.findByLoginId(loginId);
    }

    public MemberDto findmemberEmail(String email) {
        return mapper.findByEmail(email);
    }

    public MemberDto findmemberId(String id) {
        return mapper.findById(id);
    }

    public void createMemberImage(MemberImageDto memberImageDto) {
        mapper.saveMemberImage(memberImageDto);
    }

    // public MemberDto createFile(int id, ) {
    //     return mapper.findById(id);
    // }

}
