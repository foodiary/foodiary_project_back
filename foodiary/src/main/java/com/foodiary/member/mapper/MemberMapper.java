package com.foodiary.member.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.foodiary.member.model.MemberSignUpDto;

@Mapper
public interface MemberMapper {

    void saveMember(MemberSignUpDto memberSignUpDto);
}
