package com.foodiary.daily.mapper;

import com.foodiary.daily.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DailyMapper {


// =================== INSERT ====================

    int saveDaily(DailyWriteRequestDto dailyWriteRequestDto);

    int saveDailyComment(DailyCommentWriteRequestDto dailyCommentWriteRequestDto);

    int saveDailyLike(@Param("memberId") int memberId, @Param("dailyId") int dailyId);

    int saveDailyScrap(@Param("dailyId") int dailyId, @Param("memberId") int memberId);

    int saveImage(DailyImageDto imageDto);



// =================== UPDATE ====================

    int updateDaily(DailyEditRequestDto dailyEditRequestDto);

    int updateDailyComment(DailyCommentEditRequestDto dailyCommentEditRequestDto);

    int updateDailyView(@Param("dailyId") int dailyId);




// =================== SELECT ====================

    Optional<DailyDetailsResponseDto> findByDailyId(@Param("dailyId") int dailyId);

    Optional<DailyCommentDetailsResponseDto> findByDailyComment(@Param("commentId") int commentId);

    Optional<Integer> findByDailyScrap(@Param("dailyId") int dailyId, @Param("memberId") int memberId);

    Optional<Integer> findByDailyLikeId(@Param("dailyLikeId") int dailyLikeId);

    Optional<Integer> findByMemberIdAndDailyId(@Param("memberId") int memberId, @Param("dailyId") int dailyId);

    String findByDailyImage(@Param("dailyId") int dailyId);

    List<Integer> findAllDailyId(@Param("dailyId") int dailyId);

    List<DailyCommentDetailsResponseDto> findAllDailyComment(@Param("dailyId") int dailyId);

    List<DailysResponseDto> findAll();



// =================== DELETE ====================

    int deleteDailyLike(@Param("dailyId") int dailyId);

    int deleteDaily(@Param("dailyId") int dailyId);

    int deleteDailyComment(@Param("dailyId") int dailyId, @Param("commentId") int commentId);

    int deleteDailyScrap(@Param("dailyId") int dailyId, @Param("scrapId") int scrapId);

    int deleteDailyImage(@Param("dailyId") int dailyId, @Param("path") String path);
}
