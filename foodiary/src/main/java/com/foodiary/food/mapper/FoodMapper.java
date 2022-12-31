package com.foodiary.food.mapper;


import org.apache.ibatis.annotations.Mapper;

import com.foodiary.food.model.FoodDto;
import org.apache.ibatis.annotations.Param;


import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FoodMapper {

    void saveFood(FoodDto food);

    void saveMemberFood(@Param("memberId") int memberId, @Param("foodId") int foodId);

    List<Integer> findMemberFoodByCreateAt(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("memberId") int memberId);

    List<FoodDto> findByFoodCetegory(String cetegory);

    FoodDto findById(@Param("foodId") int foodId);



}
