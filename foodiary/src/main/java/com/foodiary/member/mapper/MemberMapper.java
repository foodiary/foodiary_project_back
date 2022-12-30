package com.foodiary.member.mapper;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberImageDto;
import com.foodiary.member.model.MemberSignUpRequestDto;

@Mapper
public interface MemberMapper {

    void saveMember(MemberSignUpRequestDto memberSignUpDto);

    MemberDto findByEmail(@Param("email") String email);

    MemberDto findByEmailAndPw(@Param("email") String email, @Param("pw") String pw);

    MemberDto findByLoginId(@Param("loginId") String loginId);

    MemberDto findById(@Param("id") String id);

    void saveMemberImage(MemberImageDto memberImageDto);

}
