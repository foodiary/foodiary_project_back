package com.foodiary.rank.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.foodiary.rank.model.RanksResponseDto;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RankMapper {
    
    List<RanksResponseDto> rankWeekList();

    List<RanksResponseDto> rankMonthList();

    List<Integer> findByWeekDailyId();

    List<Integer> findByMonDailyId();

    Optional<Integer> findWeekByDailyId(@Param("dailyId") int dailyId);

    Optional<Integer> findMonByDailyId(@Param("dailyId") int dailyId);

    void weekWrite();

    void monthWrite();

    void rankDelete();

}
