package com.foodiary.food.service;

import com.foodiary.food.entity.Food;
import com.foodiary.food.mapper.FoodMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class FoodService {

    private final FoodMapper foodMapper;

    public Food randomFood(String category) {

        List<Food> foods = foodMapper.findByFoodCetegory(category);
        Random random = new Random();
        int randomIndex = random.nextInt(foods.size());
        return foods.get(randomIndex);
    }
}
