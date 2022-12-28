package com.foodiary.food.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.foodiary.food.model.FoodDto;

import java.util.List;

@Mapper
public interface FoodMapper {

    void saveFood(FoodDto food);

    List<FoodDto> findByFoodCetegory(String cetegory);
}
