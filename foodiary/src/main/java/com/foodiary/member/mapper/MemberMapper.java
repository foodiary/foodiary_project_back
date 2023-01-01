package com.foodiary.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.foodiary.member.model.MemberDailyLikeResponseDto;
import com.foodiary.member.model.MemberDailyScrapResponseDto;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberEditRequestDto;
import com.foodiary.member.model.MemberEditResponseDto;
import com.foodiary.member.model.MemberImageDto;
import com.foodiary.member.model.MemberRecipeLikeResponseDto;
import com.foodiary.member.model.MemberRecipeScrapResponseDto;
import com.foodiary.member.model.MemberSignUpRequestDto;

@Mapper
public interface MemberMapper {

    void saveMember(MemberSignUpRequestDto memberSignUpDto);

    MemberDto findByEmail(@Param("email") String email);

    MemberDto findByMemberId(@Param("memberId") int memberId);

    MemberDto findByEmailAndPw(@Param("email") String email,@Param("pw") String pw);

    MemberDto findByLoginId(@Param("loginId") String loginId);

    MemberDto findByNickname(@Param("nickname") String nickname);

    MemberDto findById(@Param("id") String id);

    void saveMemberImage(MemberImageDto memberImageDto);

    void updateMemberPassword(@Param("password") String password, @Param("id") int id);

    void updateMemberInfo(MemberEditRequestDto memberEditDto);

    void deleteMemberImage(@Param("id") int id);

    MemberImageDto findByIdFile(@Param("id") int id);

    List<MemberDailyScrapResponseDto> findByDailyScrap(@Param("id") int id);

    List<MemberRecipeScrapResponseDto> findByRecipeScrap(@Param("id") int id);

    List<MemberDailyLikeResponseDto> findByDailyLike(@Param("id") int id);

    List<MemberRecipeLikeResponseDto> findByRecipeLike(@Param("id") int id);

    void deleteDailyScrap(@Param("scrapId") int likeId, @Param("memberId") int memberId);

    void deleteRecipeScrap(@Param("scrapId") int likeId, @Param("memberId") int memberId);

    void deleteDailyLike(@Param("likeId") int likeId, @Param("memberId") int memberId);

    void deleteRecipeLike(@Param("likeId") int likeId, @Param("memberId") int memberId);

    MemberEditResponseDto findByMemberIdEdit(@Param("memberId") int memberId);

    void updateMemberImage(@Param("memberId") int memberId);

    void deleteMember(@Param("id") int id);

}
