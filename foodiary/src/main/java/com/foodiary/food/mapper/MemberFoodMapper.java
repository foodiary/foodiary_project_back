package com.foodiary.food.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberFoodMapper {

    void saveMemberFood(@Param("memberId") int memberId, @Param("foodId") int foodId);
}
