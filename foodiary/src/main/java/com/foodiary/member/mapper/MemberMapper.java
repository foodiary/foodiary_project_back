package com.foodiary.member.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.foodiary.daily.model.DailyDto;
import com.foodiary.member.model.MemberDailyCommentDto;
import com.foodiary.member.model.MemberDailyLikeResponseDto;
import com.foodiary.member.model.MemberDailyScrapResponseDto;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberEditRequestDto;
import com.foodiary.member.model.MemberEditResponseDto;
import com.foodiary.member.model.MemberImageDto;
import com.foodiary.member.model.MemberRecipeCommentDto;
import com.foodiary.member.model.MemberRecipeLikeResponseDto;
import com.foodiary.member.model.MemberRecipeScrapResponseDto;
import com.foodiary.member.model.MemberSignUpRequestDto;
import com.foodiary.recipe.model.RecipeDto;

@Mapper
public interface MemberMapper {

    int saveMember(MemberSignUpRequestDto memberSignUpDto);

    Optional<MemberDto> findByEmail(@Param("email") String email);

    Optional<MemberDto> findByEmailAndId(@Param("email") String email, @Param("loginId") String loginId);

    Optional<MemberDto> findByMemberId(@Param("memberId") int memberId);

    Optional<MemberDto> findByLoginIdAndPw(@Param("loginId") String loginId, @Param("pw") String pw);

    MemberDto findByEmailAndPw(@Param("email") String email, @Param("pw") String pw);

    Optional<MemberDto> findByLoginId(@Param("loginId") String loginId);

    Optional<MemberDto> findByNickname(@Param("nickname") String nickname);

    Optional<MemberDto> findById(@Param("id") int id);

    int saveMemberImage(MemberImageDto memberImageDto);

    int updateMemberPassword(@Param("password") String password, @Param("id") int id);

    int updateMemberInfo(MemberEditRequestDto memberEditDto);

    int deleteMemberImage(@Param("id") int id);

    MemberImageDto findByIdFile(@Param("id") int id);

    List<DailyDto> findByDaily(@Param("id") int id);

    List<RecipeDto> findByRecipe(@Param("id") int id);

    List<MemberDailyCommentDto> findByDailyComment(@Param("id") int id);

    List<MemberRecipeCommentDto> findByRecipeComment(@Param("id") int id);

    List<MemberDailyScrapResponseDto> findByDailyScrap(@Param("id") int id);

    List<MemberRecipeScrapResponseDto> findByRecipeScrap(@Param("id") int id);

    List<MemberDailyLikeResponseDto> findByDailyLike(@Param("id") int id);

    List<MemberRecipeLikeResponseDto> findByRecipeLike(@Param("id") int id);

    int deleteDailyScrap(@Param("scrapId") int likeId);

    int deleteRecipeScrap(@Param("scrapId") int likeId);

    int deleteDailyLike(@Param("likeId") int likeId);

    int deleteRecipeLike(@Param("likeId") int likeId);

    MemberEditResponseDto findByMemberIdEdit(@Param("memberId") int memberId);

    void updateMemberImage(@Param("memberId") int memberId);

    int deleteMember(@Param("id") int id);
    
    int updateMemberPw(@Param("email") String email, @Param("pw") String pw);

    int deleteDailyComment(@Param("dailyCommentId") int dailyCommentId);

    int deleteRecipeComment(@Param("recipeComments") int recipeComments);

    int updateDailyComment(@Param("dailyCommentId") int dailyCommentId, @Param("comment") String comment);

    int updateRecipeComment(@Param("recipeComments") int recipeComments, @Param("comment") String comment);

}
