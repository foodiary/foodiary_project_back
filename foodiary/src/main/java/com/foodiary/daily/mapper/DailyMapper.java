package com.foodiary.daily.mapper;

import com.foodiary.daily.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Mapper
public interface DailyMapper {


// =================== INSERT ====================

    void saveDaily(DailyWriteRequestDto dailyWriteRequestDto);

    void saveDailyComment(DailyCommentWriteRequestDto dailyCommentWriteRequestDto);

    void saveDailyLike(@Param("memberId") int memberId, @Param("dailyId") int dailyId);

    void saveDailyScrap(@Param("dailyId") int dailyId, @Param("memberId") int memberId);

    void saveImage(DailyImageDto imageDto);



// =================== UPDATE ====================

    void updateDaily(DailyEditRequestDto dailyEditRequestDto);

    void updateDailyComment(DailyCommentEditRequestDto dailyCommentEditRequestDto);

    void updateDailyView(@Param("dailyId") int dailyId);




// =================== SELECT ====================

    DailyDetailsResponseDto findByDailyId(@Param("dailyId") int dailyId);

    DailyCommentDetailsResponseDto findByDailyComment(@Param("commentId") int commentId);

    Integer findByDailyScrap(@Param("dailyId") int dailyId, @Param("memberId") int memberId);

    Integer findByDailyLikeId(@Param("dailyLikeId") int dailyLikeId);

    Integer findByMemberIdAndDailyId(@Param("memberId") int memberId, @Param("dailyId") int dailyId);

    String findByDailyImage(@Param("dailyId") int dailyId);

    List<Integer> findAllDailyId(@Param("dailyId") int dailyId);

    List<DailyCommentDetailsResponseDto> findAllDailyComment(@Param("dailyId") int dailyId);

    List<DailysResponseDto> findAll();



// =================== DELETE ====================

    void deleteDailyLike(@Param("dailyLikeId") int dailyLikeId);

    void deleteDaily(@Param("dailyId") int dailyId, @Param("memberId") int memberId);

    void deleteDailyComment(@Param("dailyId") int dailyId, @Param("memberId") int memberId, @Param("commentId") int commentId);

    void deleteDailyScrap(@Param("dailyId") int dailyId, @Param("memberId") int memberId, @Param("scrapId") int scrapId);
}
