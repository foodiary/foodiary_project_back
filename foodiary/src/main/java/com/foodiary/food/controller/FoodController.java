package com.foodiary.food.controller;

import com.foodiary.food.model.FoodDto;
import com.foodiary.food.model.FoodPostDto;
import com.foodiary.food.model.FoodResponseDto;
import com.foodiary.food.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "food recommend", description = "음식 추천")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    // 랜덤 추천
    @GetMapping
    public ResponseEntity<FoodResponseDto> FoodRecommend(@RequestParam int memberId) {
        FoodDto food = foodService.randomFood(memberId);
        FoodResponseDto response = FoodResponseDto.builder()
                .foodName(food.getFoodName())
                .foodCategory(food.getFoodCategory())
                .foodId(food.getFoodId())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
