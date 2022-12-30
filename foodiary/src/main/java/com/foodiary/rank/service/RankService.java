package com.foodiary.rank.service;

import org.springframework.stereotype.Service;

import com.foodiary.rank.mapper.RankMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RankService {
    
    private final RankMapper mapper;

    
}
