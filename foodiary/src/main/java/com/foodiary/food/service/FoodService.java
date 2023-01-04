package com.foodiary.food.service;

import com.foodiary.auth.service.UserService;
import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.food.mapper.FoodMapper;
import com.foodiary.food.model.FoodDto;

import com.foodiary.food.model.FoodRecommendResponseDto;
import com.foodiary.food.model.MenuRecommendRequestDto;
import com.foodiary.food.model.MenuRecommendResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class FoodService {

    private final FoodMapper foodMapper;
    private final UserService userService;

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

        List<Integer> hateFoodList = foodMapper.findAllHateFood(memberId);
        log.info(String.valueOf(hateFoodList.size()));
        Random random = new Random();
        int randomIndex = random.nextInt(685) + 1;
        System.out.println(randomIndex);
        for (int i = 0; i < hateFoodList.size(); i++) {
            if(hateFoodList.get(i) == randomIndex){
                randomIndex = random.nextInt(685) + 1;
                log.info("싫어하는 음식이 나왔습니다.");
                i = 0;
            }
        }
        FoodDto foodRecommend = foodMapper.findById(randomIndex);
        foodMapper.saveMemberFood(memberId, foodRecommend.getFoodId());

        return foodRecommend;
    }

    public MenuRecommendResponseDto weekRecommendMenu(int memberId) {

        List<Integer> hateFoodList = foodMapper.findAllHateFood(memberId);
        List<FoodDto> list = new ArrayList<>();

        //중복 음식 방지 로직
        for(int i=0; i<14; i++) {
            list.add(recommendFood());
            for(int j=0; j<i; j++){
                if(list.get(i).getFoodId() == list.get(j).getFoodId()){
                    log.info("중복된 숫자가 나왔습니다.");
                    list.remove(i);
                    i--;
                    continue;
                }
            }
            for(int z=0; z<hateFoodList.size(); z++) {
                if (list.get(i).getFoodId() == hateFoodList.get(z)){
                    log.info("싫어하는 음식이 나왔습니다.");
                    list.remove(i);
                    i--;
                }
            }
        }
        MenuRecommendRequestDto recommendMenu = MenuRecommendRequestDto.builder()
                .memberId(memberId)
                .menuMonLunchCategory(list.get(0).getFoodCategory()).menuMonLunch(list.get(0).getFoodName())
                .menuMonDinnerCategory(list.get(1).getFoodCategory()).menuMonDinner(list.get(1).getFoodName())
                .menuTueLunchCategory(list.get(2).getFoodCategory()).menuTueLunch(list.get(2).getFoodName())
                .menuTueDinnerCategory(list.get(3).getFoodCategory()).menuTueDinner(list.get(3).getFoodName())
                .menuWedLunchCategory(list.get(4).getFoodCategory()).menuWedLunch(list.get(4).getFoodName())
                .menuWedDinnerCategory(list.get(5).getFoodCategory()).menuWedDinner(list.get(5).getFoodName())
                .menuThuLunchCategory(list.get(6).getFoodCategory()).menuThuLunch(list.get(6).getFoodName())
                .menuThuDinnerCategory(list.get(7).getFoodCategory()).menuThuDinner(list.get(7).getFoodName())
                .menuFriLunchCategory(list.get(8).getFoodCategory()).menuFriLunch(list.get(8).getFoodName())
                .menuFriDinnerCategory(list.get(9).getFoodCategory()).menuFriDinner(list.get(9).getFoodName())
                .menuSatLunchCategory(list.get(10).getFoodCategory()).menuSatLunch(list.get(10).getFoodName())
                .menuSatDinnerCategory(list.get(11).getFoodCategory()).menuSatDinner(list.get(11).getFoodName())
                .menuSunLunchCategory(list.get(12).getFoodCategory()).menuSunLunch(list.get(12).getFoodName())
                .menuSunDinnerCategory(list.get(13).getFoodCategory()).menuSunDinner(list.get(13).getFoodName())
                .build();

        foodMapper.saveWeekRecommendMenu(recommendMenu);
        return foodMapper.findByMenu(recommendMenu.getMenuId(), recommendMenu.getMemberId());
    }

    public void patchLikeFood(int memberFoodId, int memberId){
//        userService.checkUser(memberId);
        foodMapper.findMemberFoodById(memberFoodId)
                        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.LIKE_NOT_FOUND));
        foodMapper.updateFoodLike(memberFoodId);
    }

    public void patchHateFood(int memberFoodId, int memberId){
//        userService.checkUser(memberId);
        foodMapper.findMemberFoodById(memberFoodId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.LIKE_NOT_FOUND));
        foodMapper.updateFoodHate(memberFoodId);
    }
    
    private FoodDto recommendFood() {
        Random random = new Random();
        Integer randomIndex = random.nextInt(685) + 1;
        return foodMapper.findById(randomIndex);
    }
}
