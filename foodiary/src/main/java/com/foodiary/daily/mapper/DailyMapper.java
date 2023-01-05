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

    void deleteDailyLike(@Param("dailyId") int dailyId, @Param("memberId") int memberId);

    void deleteDaily(@Param("dailyId") int dailyId, @Param("memberId") int memberId);

    void deleteDailyComment(@Param("dailyId") int dailyId, @Param("memberId") int memberId, @Param("commentId") int commentId);

    void deleteDailyScrap(@Param("dailyId") int dailyId, @Param("memberId") int memberId, @Param("scrapId") int scrapId);

    void deleteDailyImage(@Param("dailyId") int dailyId, @Param("path") String path);
}
