package com.foodiary.food.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberFoodMapper {

    void saveMemberFood(int memberId, int foodId);
}
