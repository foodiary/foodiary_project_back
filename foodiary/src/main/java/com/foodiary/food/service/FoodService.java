package com.foodiary.food.service;

import com.foodiary.food.mapper.FoodMapper;
import com.foodiary.food.model.FoodDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@RequiredArgsConstructor
@Service
public class FoodService {

    private final FoodMapper foodMapper;

    public FoodDto randomFood(int memberId) {

        //추천 횟수 제한 로직
//        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0));
//        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59));
//
//        List<Integer> verifyMemberFood = foodMapper.findMemberFoodByCreateAt(start, end, memberId);
//        System.out.println(verifyMemberFood.size());
//        if(verifyMemberFood.size() > 4) {
//            throw new BusinessLogicException(ExceptionCode.OVER_REQUEST);
//        }
        Random random = new Random();
        int randomIndex = random.nextInt(686);
        System.out.println(randomIndex);
        FoodDto foodRecommend = foodMapper.findById(randomIndex);
        foodMapper.saveMemberFood(memberId, foodRecommend.getFoodId());

        return foodRecommend;
    }
}
