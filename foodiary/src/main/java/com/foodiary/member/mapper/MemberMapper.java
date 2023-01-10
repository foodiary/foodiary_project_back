package com.foodiary.member.mapper;

import java.util.List;
import java.util.Optional;

import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.foodiary.daily.model.DailyDto;
import com.foodiary.member.model.MemberDailyCommentDetailResponseDto;
import com.foodiary.member.model.MemberDailyCommentDto;
import com.foodiary.member.model.MemberDailyLikeResponseDto;
import com.foodiary.member.model.MemberDailyScrapResponseDto;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberEditRequestDto;
import com.foodiary.member.model.MemberEditResponseDto;
import com.foodiary.member.model.MemberFaqDto;
import com.foodiary.member.model.MemberFoodsResponseDto;
import com.foodiary.member.model.MemberImageDto;
import com.foodiary.member.model.MemberNoticeInfoResponseDto;
import com.foodiary.member.model.MemberNoticeResponseDto;
import com.foodiary.member.model.MemberQuestionEditResponseDto;
import com.foodiary.member.model.MemberQuestionImageDto;
import com.foodiary.member.model.MemberQuestionResponseDto;
import com.foodiary.member.model.MemberQuestionWriteResponseDto;
import com.foodiary.member.model.MemberRecipeCommentDetailResponseDto;
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

    List<MemberDto> findAll();

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

    int deleteDailyScrap(@Param("scrapId") int likeId, @Param("memberId") int memberId);

    int deleteRecipeScrap(@Param("scrapId") int likeId, @Param("memberId") int memberId);

    int deleteDailyLike(@Param("likeId") int likeId, @Param("memberId") int memberId);

    int deleteRecipeLike(@Param("likeId") int likeId, @Param("memberId") int memberId);

    MemberEditResponseDto findByMemberIdEdit(@Param("memberId") int memberId);

    int updateMemberImage(@Param("memberId") int memberId);

    int deleteMember(@Param("id") int id);
    
    int updateMemberPw(@Param("email") String email, @Param("pw") String pw);

    List<MemberNoticeResponseDto> findByNotice();

    Optional<MemberNoticeInfoResponseDto> findByNoticeId(@Param("noticeId") int noticeId);

    List<MemberFaqDto> findByFaq();

    List<MemberQuestionResponseDto> findByQuestion(@Param("memberId") int memberId);

    Optional<MemberQuestionResponseDto> findByQuestionId(@Param("questionId") int questionId);

    int saveQuestion(MemberQuestionWriteResponseDto memberQuestionWriteResponseDto);

    int saveMemberQuestionImage(MemberQuestionImageDto memberQuestionImageDto);

    MemberQuestionImageDto findByQuestionImage(@Param("questionId") int questionId);

    int deleteQuestionImage(@Param("questionId") int questionId);

    int updateQuetion(MemberQuestionEditResponseDto memberQuestionEditResponseDto);

    int deleteQuetion(@Param("questionId") int questionId);

    List<MemberFoodsResponseDto> findByFoods(@Param("memberId") int memberId);

    int updateMemberFood(@Param("memberFoodId") int memberFoodId, @Param("like") String like);

    Optional<MemberDailyCommentDetailResponseDto> findByDailyCommentId(@Param("dailyId") int dailyId, @Param("dailyCommentId") int dailyCommentId);

    Optional<MemberRecipeCommentDetailResponseDto> findByRecipeCommentId(@Param("recipeId") int recipeId, @Param("recipeCommentId") int recipeCommentId);

}

