package com.foodiary.rank.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foodiary.rank.mapper.RankMapper;
import com.foodiary.rank.model.RanksResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RankService {
    
    private final RankMapper mapper;

    public List<RanksResponseDto> rankWeekView() {
        
        return mapper.rankWeekList();
    }

    public List<RanksResponseDto> rankMonthView() {
        
        return mapper.rankMonthList();
    }

    public List<RanksResponseDto> rankRecommendView() {
        
        return mapper.rankRecommendList();
    }
}
