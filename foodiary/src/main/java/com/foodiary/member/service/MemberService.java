package com.foodiary.member.service;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.foodiary.auth.service.UserService;
import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberImageDto;
import com.foodiary.member.model.MemberSignUpRequestDto;
import com.foodiary.member.model.MemberEditPasswordRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberMapper mapper;

    private final UserService userService;


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

    public MemberDto findmemberNickname(String nickname) {
        return mapper.findByNickname(nickname);
    }

    public void createMemberImage(MemberImageDto memberImageDto) {
        mapper.saveMemberImage(memberImageDto);
    }

    public void EditMemberPassWord(String password, int id) {
        mapper.updateMemberPassword(password, id);
    }

    // public MemberDto createFile(int id, ) {
    //     return mapper.findById(id);
    // }

}
