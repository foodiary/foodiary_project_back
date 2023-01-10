package com.foodiary.search.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.foodiary.search.model.SearchRequestDto;
import com.foodiary.search.model.SearchDailyResponseDto;
import com.foodiary.search.model.SearchRecipeResponseDto;

@Mapper
public interface SearchMapper {
    
    List<SearchRecipeResponseDto> findbyRecipe(SearchRequestDto requestDto);

    List<SearchDailyResponseDto> findbyDaily(SearchRequestDto requestDto);

    
}
