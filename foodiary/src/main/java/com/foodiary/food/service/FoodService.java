package com.foodiary.food.service;

import com.foodiary.food.mapper.FoodMapper;
import com.foodiary.food.mapper.MemberFoodMapper;
import com.foodiary.food.model.FoodDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class FoodService {

    private final FoodMapper foodMapper;
    private final MemberFoodMapper memberFoodMapper;

    public FoodDto randomFood(int memberId) {

        Random random = new Random();
        int randomIndex = random.nextInt(686);
        System.out.println(randomIndex);
        FoodDto foodRecommend = foodMapper.findById(randomIndex);
        memberFoodMapper.saveMemberFood(memberId, foodRecommend.getFoodId());

        return foodRecommend;
    }
}
