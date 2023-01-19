package com.foodiary.daily.mapper;

import com.foodiary.daily.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
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

    int updateThumbnailImage(@Param("name") String name);

    int updateThumbnailPath(@Param("path") String path, @Param("dailyId") int dailyId);




// =================== SELECT ====================

    Optional<DailyDetailsResponseDto> findByDailyId(@Param("dailyId") int dailyId);

    Optional<DailyCommentDetailsResponseDto> findByDailyComment(@Param("commentId") int commentId);

    List<DailyImageDto> findImageByDailyId(@Param("dailyId") int dailyId);

    Optional<Integer> findByDailyScrap(@Param("dailyId") int dailyId, @Param("memberId") int memberId);

    Optional<Integer> findByMemberIdAndDailyId(@Param("memberId") int memberId, @Param("dailyId") int dailyId);

    Optional<Integer> findDailyIdByPath(@Param("path") String path);

    List<DailyCommentDetailsResponseDto> findAllDailyComment(@Param("dailyId") int dailyId);

    List<DailysResponseDto> findAllCreate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<DailysResponseDto> findAll();

    List<DailysResponseDto> findTopDaily();

    List<String> findAllImageList(@Param("dailyId") int dailyId);

    List<DailyImageDto> findAllImageDtoList(@Param("dailyId") int dailyId);




// =================== DELETE ====================

    int deleteDailyLike(@Param("dailyId") int dailyId);

    int deleteDaily(@Param("dailyId") int dailyId);

    int deleteDailyComment(@Param("dailyId") int dailyId, @Param("commentId") int commentId, @Param("memberId") int memberId);

    int deleteDailyScrap(@Param("dailyId") int dailyId, @Param("memberId") int memberId);

    int deleteAllDailyImage(@Param("dailyId") int dailyId);

    int deleteDailyImage(@Param("dailyId") int dailyId, @Param("path") String path);
}
