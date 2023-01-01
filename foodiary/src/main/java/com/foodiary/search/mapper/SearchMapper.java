package com.foodiary.search.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.foodiary.search.model.SearchRequestDto;
import com.foodiary.search.model.SearchResponseDto;

@Mapper
public interface SearchMapper {
    
    List<SearchResponseDto> findbyTitle(SearchRequestDto requestDto);
    
}
