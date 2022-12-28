package com.foodiary.food.service;

import com.foodiary.food.mapper.FoodMapper;
import com.foodiary.food.model.FoodDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class FoodService {

    private final FoodMapper foodMapper;

    public FoodDto randomFood(String category) {

        List<FoodDto> foods = foodMapper.findByFoodCetegory(category);
        Random random = new Random();
        int randomIndex = random.nextInt(foods.size());
        return foods.get(randomIndex);
    }
}
