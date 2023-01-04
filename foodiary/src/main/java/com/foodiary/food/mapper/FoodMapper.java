package com.foodiary.food.mapper;


import com.foodiary.food.model.MenuRecommendRequestDto;
import com.foodiary.food.model.MenuRecommendResponseDto;
import org.apache.ibatis.annotations.Mapper;

import com.foodiary.food.model.FoodDto;
import org.apache.ibatis.annotations.Param;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface FoodMapper {

    void saveFood(FoodDto food);

    void saveMemberFood(@Param("memberId") int memberId, @Param("foodId") int foodId);

    void saveWeekRecommendMenu(MenuRecommendRequestDto menuRecommendRequestDto);


    void updateFoodLike(@Param("memberFoodId") int memberFoodId);

    void updateFoodHate(@Param("memberFoodId") int memberFoodId);

    List<Integer> findAllHateFood(@Param("memberId") int memberId);


    List<Integer> findMemberFoodByCreateAt(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("memberId") int memberId);

    List<FoodDto> findByFoodCetegory(String cetegory);

    Optional<Integer> findMemberFoodById(@Param("memberFoodId") int memberFoodId);

    FoodDto findById(@Param("foodId") int foodId);

    MenuRecommendResponseDto findByMenu(@Param("menuId") int menuId, @Param("memberId") int memberId);



}
