package com.foodiary.member.mapper;
import com.foodiary.member.entity.Member;
import org.apache.ibatis.annotations.Mapper;

import com.foodiary.member.dto.MemberPostDto;

// import com.foodiary.member.dto.MemberPostDto;
// import com.foodiary.member.entity.Member;


@Mapper
public interface MemberMapper {

    void saveMember(MemberPostDto memberPostDto);

     Member findByEmail(String email);
    
}
