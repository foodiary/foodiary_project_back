package com.foodiary.food.controller;

import com.foodiary.food.model.FoodDto;
import com.foodiary.food.model.FoodPostDto;
import com.foodiary.food.model.FoodResponseDto;
import com.foodiary.food.service.FoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/food")
public class FoodController {

    private final FoodService foodService;

    // 랜덤 추천
    @GetMapping
    public ResponseEntity<FoodResponseDto> postFood(@RequestParam String category) {
        FoodDto food = foodService.randomFood(category);
        FoodResponseDto response = FoodResponseDto.builder()
                .foodName(food.getFoodName())
                .foodCategory(food.getFoodCategory())
                .foodId(food.getFoodId())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
