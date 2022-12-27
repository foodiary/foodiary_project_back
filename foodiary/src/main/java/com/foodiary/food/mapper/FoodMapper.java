package com.foodiary.food.mapper;

import com.foodiary.food.entity.Food;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FoodMapper {

    void saveFood(Food food);

    List<Food> findByFoodCetegory(String cetegory);
}
