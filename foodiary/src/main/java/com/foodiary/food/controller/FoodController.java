package com.foodiary.food.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.foodiary.food.model.FoodDto;
import com.foodiary.food.model.FoodRecommendResponseDto;
import com.foodiary.food.model.MemberFoodRequestDto;
import com.foodiary.food.model.MenuRecommendResponseDto;
import com.foodiary.food.service.FoodService;
import com.foodiary.member.model.MemberFoodsResponseDto;
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
    public ResponseEntity<FoodRecommendResponseDto> foodRecommend(@RequestParam(required = false) Integer memberId) {
        FoodDto food = foodService.randomFood(memberId);
        FoodRecommendResponseDto response = FoodRecommendResponseDto.builder()
                .foodName(food.getFoodName())
                .foodCategory(food.getFoodCategory())
                .foodId(food.getFoodId())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "menu recommend", description = "식단 추천")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @GetMapping("/menu")
    public ResponseEntity<MenuRecommendResponseDto> recommendWeekMenu(@RequestParam int memberId) throws JsonProcessingException {
        MenuRecommendResponseDto response = foodService.weekRecommendMenu(memberId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "verify menu recommend", description = "식단 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @GetMapping("/menu/week")
    public ResponseEntity<MenuRecommendResponseDto> findWeekMenu(@RequestParam int memberId) throws JsonProcessingException {
        MenuRecommendResponseDto response = foodService.findMenuRecommendWeek(memberId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "food like", description = "음식 추천 좋아요")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @PostMapping("/like")
    public ResponseEntity<String> foodLike(@RequestBody MemberFoodRequestDto memberFoodRequestDto) {
        foodService.patchLikeFood(memberFoodRequestDto);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "food hate", description = "음식 추천 싫어요")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @PostMapping("/hate")
    public ResponseEntity<String> foodHate(@RequestBody MemberFoodRequestDto memberFoodRequestDto) {
        foodService.patchHateFood(memberFoodRequestDto);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
