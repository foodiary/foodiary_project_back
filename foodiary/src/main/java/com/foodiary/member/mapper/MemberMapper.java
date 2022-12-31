package com.foodiary.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberSignUpDto;

@Mapper
public interface MemberMapper {

    void saveMember(MemberSignUpDto memberSignUpDto);

    MemberDto findByEmail(@Param("email") String email);

    MemberDto findById(@Param("memberId") int memberId);

    MemberDto findByEmailAndPw(@Param("email") String email,@Param("pw") String pw);

    // MemberDto findByEmailAndPw(String email, String pw);

}
