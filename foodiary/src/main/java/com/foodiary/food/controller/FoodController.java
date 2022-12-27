package com.foodiary.food.controller;

import com.foodiary.food.dto.FoodPostDto;
import com.foodiary.food.dto.FoodResponseDto;
import com.foodiary.food.entity.Food;
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

    @GetMapping
    public ResponseEntity<FoodResponseDto> postFood(@RequestParam String category) {
        Food food = foodService.randomFood(category);
        FoodResponseDto response = FoodResponseDto.builder()
                .foodName(food.getFoodName())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
