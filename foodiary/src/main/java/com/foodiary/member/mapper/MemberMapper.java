package com.foodiary.member.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.foodiary.member.model.MemberDailyCommentDetailResponseDto;
import com.foodiary.member.model.MemberDailyCommentDto;
import com.foodiary.member.model.MemberDailyResponseDto;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberEditRequestDto;
import com.foodiary.member.model.MemberEditResponseDto;
import com.foodiary.member.model.MemberFaqDto;
import com.foodiary.member.model.MemberFoodsResponseDto;
import com.foodiary.member.model.MemberImageDto;
import com.foodiary.member.model.MemberNoticeInfoResponseDto;
import com.foodiary.member.model.MemberNoticeResponseDto;
import com.foodiary.member.model.MemberOtherMemberResponseDto;
import com.foodiary.member.model.MemberPostLikeResponseDto;
import com.foodiary.member.model.MemberPostScrapResponseDto;
import com.foodiary.member.model.MemberQuestionDetailResponseDto;
import com.foodiary.member.model.MemberQuestionEditResponseDto;
import com.foodiary.member.model.MemberQuestionImageDto;
import com.foodiary.member.model.MemberQuestionResponseDto;
import com.foodiary.member.model.MemberQuestionWriteResponseDto;
import com.foodiary.member.model.MemberRecipeCommentDetailResponseDto;
import com.foodiary.member.model.MemberRecipeCommentDto;
import com.foodiary.member.model.MemberResponseDto;
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

    Optional<MemberResponseDto> findById(@Param("id") int id);

    List<MemberDto> findAll();

    int saveMemberImage(MemberImageDto memberImageDto);

    int updateMemberPassword(@Param("password") String password, @Param("id") int id);

    int updateMemberInfo(MemberEditRequestDto memberEditDto);

    int updateDailyWriter(@Param("memberId") int memberId, @Param("nickname") String nickname);

    int updateDailyCommentWriter(@Param("memberId") int memberId, @Param("nickname") String nickname);

    int updateRecipeWriter(@Param("memberId") int memberId, @Param("nickname") String nickname);

    int updateRecipeCommentWriter(@Param("memberId") int memberId, @Param("nickname") String nickname);

    int deleteMemberImage(@Param("id") int id);

    MemberImageDto findByIdFile(@Param("id") int id);

    List<MemberDailyResponseDto> findByDaily(@Param("memberId") int id);

    List<MemberOtherMemberResponseDto> findByMember(@Param("memberId") int id);

    List<RecipeDto> findByRecipe(@Param("id") int id);

    List<MemberDailyCommentDto> findByDailyComment(@Param("id") int id);

    List<MemberRecipeCommentDto> findByRecipeComment(@Param("id") int id);

    List<MemberPostScrapResponseDto> postScrap(@Param("memberId") int memberId);

    List<MemberPostLikeResponseDto> postLike(@Param("memberId") int memberId);

    int deleteDailyScrap(@Param("scrapId") int likeId, @Param("memberId") int memberId);

    int deleteRecipeScrap(@Param("scrapId") int likeId, @Param("memberId") int memberId);

    int deleteDailyLike(@Param("likeId") int likeId, @Param("memberId") int memberId);

    int deleteRecipeLike(@Param("likeId") int likeId, @Param("memberId") int memberId);

    MemberEditResponseDto findByMemberIdEdit(@Param("memberId") int memberId);

    int updateMemberImageDelete(@Param("memberId") int memberId);

    int updateMemberImage(@Param("memberId") int memberId, @Param("path") String path);

    int deleteMember(@Param("id") int id);
    
    int updateMemberPw(@Param("email") String email, @Param("pw") String pw);

    List<MemberNoticeResponseDto> findByNotice();

    Optional<MemberNoticeInfoResponseDto> findByNoticeId(@Param("noticeId") int noticeId);

    List<MemberFaqDto> findByFaq();

    List<MemberQuestionResponseDto> findByQuestion(@Param("memberId") int memberId);

    Optional<MemberQuestionDetailResponseDto> findByQuestionId(@Param("questionId") int questionId);

    int saveQuestion(MemberQuestionWriteResponseDto memberQuestionWriteResponseDto);

    int saveMemberQuestionImage(MemberQuestionImageDto memberQuestionImageDto);

    MemberQuestionImageDto findByQuestionImage(@Param("questionId") int questionId, @Param("memberId") int memberId);

    int deleteQuestionImage(@Param("questionId") int questionId, @Param("memberId") int memberId);

    int updateQuetion(MemberQuestionEditResponseDto memberQuestionEditResponseDto);

    int deleteQuetion(@Param("questionId") int questionId, @Param("memberId") int memberId);

    List<MemberFoodsResponseDto> findByFoods(@Param("memberId") int memberId);

    int updateMemberFood(@Param("memberFoodId") int memberFoodId, @Param("like") String like);

    Optional<MemberDailyCommentDetailResponseDto> findByDailyCommentId(@Param("memberId") int memberId, @Param("dailyId") int dailyId, @Param("dailyCommentId") int dailyCommentId);

    Optional<MemberRecipeCommentDetailResponseDto> findByRecipeCommentId(@Param("memberId") int memberId, @Param("recipeId") int recipeId, @Param("recipeCommentId") int recipeCommentId);

    Optional<MemberDto> findByProfile(@Param("memberId") int memberId);

}

