package com.foodiary.food.mapper;


import com.foodiary.food.model.MemberFoodRequestDto;
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

    int saveFood(FoodDto food);

    int saveMemberFoodLike(MemberFoodRequestDto memberFoodRequestDto);

    int saveMemberFoodHate(MemberFoodRequestDto memberFoodRequestDto);

    int saveWeekRecommendMenu(MenuRecommendRequestDto menuRecommendRequestDto);


    int updateFoodLike(@Param("foodId") int foodId, @Param("memberId") int memberId);

    int updateFoodHate(@Param("foodId") int foodId, @Param("memberId") int memberId);

    List<Integer> findAllHateFood(@Param("memberId") int memberId);


    List<Integer> findMemberFoodByCreateAt(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("memberId") int memberId);

    List<FoodDto> findByFoodCetegory(String cetegory);

    List<FoodDto> findAllFood();

    Optional<Integer> findMemberFoodById(@Param("memberFoodId") int memberFoodId);

    FoodDto findById(@Param("foodId") int foodId);

    MenuRecommendResponseDto findByMenu(@Param("menuId") int menuId, @Param("memberId") int memberId);

    Optional<Integer> findByMemberFood(MemberFoodRequestDto memberFoodRequestDto);



}
