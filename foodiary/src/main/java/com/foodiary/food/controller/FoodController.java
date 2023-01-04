package com.foodiary.food.controller;

import com.foodiary.food.model.FoodDto;
import com.foodiary.food.model.FoodRecommendResponseDto;
import com.foodiary.food.model.MenuRecommendResponseDto;
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
    public ResponseEntity<FoodRecommendResponseDto> foodRecommend(@RequestParam int memberId) {
        FoodDto food = foodService.randomFood(memberId);
        FoodRecommendResponseDto response = FoodRecommendResponseDto.builder()
                .foodName(food.getFoodName())
                .foodCategory(food.getFoodCategory())
                .foodId(food.getFoodId())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "menu recommend", description = "식단 추천")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @GetMapping("/menu")
    public ResponseEntity<MenuRecommendResponseDto> findWeekMenu(@RequestParam int memberId) {
        MenuRecommendResponseDto response = foodService.weekRecommendMenu(memberId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "food like", description = "음식 추천 좋아요")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @PatchMapping("/like/{member-id}/{member-food-id}")
    public ResponseEntity<String> foodLike(@PathVariable("member-id") int memberId, @PathVariable("member-food-id") int memberFoodId) {
        foodService.patchLikeFood(memberFoodId, memberId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "food hate", description = "음식 추천 싫어요")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @PatchMapping("/hate/{member-id}/{member-food-id}")
    public ResponseEntity<String> foodHate(@PathVariable("member-id") int memberId, @PathVariable("member-food-id") int memberFoodId) {
        foodService.patchHateFood(memberFoodId, memberId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
