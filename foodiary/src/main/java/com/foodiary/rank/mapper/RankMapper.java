package com.foodiary.rank.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.foodiary.rank.model.RanksResponseDto;

@Mapper
public interface RankMapper {
    
    List<RanksResponseDto> rankWeekList();

    List<RanksResponseDto> rankMonthList();

    List<Integer> findByWeekDailyId();

    List<Integer> findByMonDailyId();

    void weekWrite();

    void monthWrite();

    void rankDelete();

}
