package com.foodiary.food.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodiary.auth.service.UserService;
import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.food.mapper.FoodMapper;
import com.foodiary.food.model.FoodDto;
import com.foodiary.food.model.MemberFoodRequestDto;
import com.foodiary.food.model.MenuRecommendResponseDto;
import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberFoodsResponseDto;
import com.foodiary.redis.RedisDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Transactional(rollbackFor = {Exception.class, SQLException.class})
@RequiredArgsConstructor
@Service
public class FoodService {


    private final FoodMapper foodMapper;
    private final RedisDao redisDao;
    private final MemberMapper memberMapper;
    private final UserService userService;


    public FoodDto randomFood(Integer memberId) {

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

        if(memberId == null){
            int randomIndex = random.nextInt(685) + 1;
            FoodDto foodRecommend = foodMapper.findById(randomIndex);

            return foodRecommend;
        } else {

            List<Integer> hateFoodList = foodMapper.findAllHateFood(memberId);
            log.info(String.valueOf(hateFoodList.size()));

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


            return foodRecommend;
        }
    }

    public void weekRecommendMenuAll() throws JsonProcessingException {
        List<MemberDto> memberList = memberMapper.findAll();
        List<FoodDto> FoodList = foodMapper.findAllFood();

        for (int k = 0; k < memberList.size() ; k++) {
            List<Integer> hateFoodList = foodMapper.findAllHateFood(memberList.get(k).getMemberId());
            List<FoodDto> list = new ArrayList<>();

            //중복 음식 방지 로직
            for(int i=0; i<14; i++) {
                list.add(recommendFood(FoodList));
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
            MenuRecommendResponseDto recommendMenu = MenuRecommendResponseDto.builder()
                    .memberId(memberList.get(k).getMemberId())
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


            ObjectMapper objectMapper = new ObjectMapper();
            String saveMenu = null;
            try {
                saveMenu = objectMapper.writeValueAsString(recommendMenu);
            } catch ( JsonProcessingException e) {
                e.printStackTrace();
            }

            LocalDate now = LocalDate.now();
            while (true){
                if(!now.getDayOfWeek().toString().equals("SUNDAY")){
                    now = now.minusDays(1);
                } else break;
            }

            String sun = now.toString();

            String keyMemberId = String.valueOf(memberList.get(k).getMemberId());
            redisDao.setValues("memberId : " + keyMemberId + " " + sun, saveMenu);
        }
        log.info("모든 회원의 식단 구성을 완료하였습니다.");
    }
    public MenuRecommendResponseDto weekRecommendMenu(int memberId) throws JsonProcessingException {

        List<Integer> hateFoodList = foodMapper.findAllHateFood(memberId);
        List<FoodDto> foodList = foodMapper.findAllFood();
        List<FoodDto> list = new ArrayList<>();

        //중복 음식 방지 로직
        for(int i=0; i<14; i++) {
            list.add(recommendFood(foodList));
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
        MenuRecommendResponseDto recommendMenu = MenuRecommendResponseDto.builder()
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



        ObjectMapper objectMapper = new ObjectMapper();
        String saveMenu = null;
        try {
            saveMenu = objectMapper.writeValueAsString(recommendMenu);
        } catch ( JsonProcessingException e) {
            e.printStackTrace();
        }
        LocalDate now = LocalDate.now();
        while (true){
            if(!now.getDayOfWeek().toString().equals("SUNDAY")){
                now = now.minusDays(1);
            } else break;
        }

        String sun = now.toString();

        String keyMemberId = String.valueOf(memberId);
        redisDao.setValues("memberId : " + keyMemberId + " " + sun, saveMenu);


         String findMenu = redisDao.getValues("memberId : " + keyMemberId + " " + sun);
        return objectMapper.readValue(findMenu, MenuRecommendResponseDto.class);
    }



    public MenuRecommendResponseDto findMenuRecommendWeek(int memberId, String date) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        String keyMemberId = String.valueOf(memberId);
        String findMenu = redisDao.getValues("memberId : " + keyMemberId + " " + date);
        return objectMapper.readValue(findMenu, MenuRecommendResponseDto.class);
    }


    public void patchLikeFood(MemberFoodRequestDto memberFoodRequestDto) {
        userService.checkUser(memberFoodRequestDto.getMemberId());

        if(foodMapper.findByMemberFood(memberFoodRequestDto).isPresent()){
            userService.verifyUpdate(foodMapper.updateFoodLike(memberFoodRequestDto.getFoodId(), memberFoodRequestDto.getMemberId()));
        }else {
            userService.verifySave( foodMapper.saveMemberFoodLike(memberFoodRequestDto));
        }

    }

    public void patchHateFood(MemberFoodRequestDto memberFoodRequestDto) {
        userService.checkUser(memberFoodRequestDto.getMemberId());

        if(foodMapper.findByMemberFood(memberFoodRequestDto).isPresent()){
            userService.verifyUpdate(foodMapper.updateFoodHate(memberFoodRequestDto.getFoodId(), memberFoodRequestDto.getMemberId()));
        } else {
            userService.verifySave(foodMapper.saveMemberFoodHate(memberFoodRequestDto));
        }

    }
    
    public FoodDto recommendFood(List<FoodDto> foodDto) {
        Random random = new Random();
        Integer randomIndex = random.nextInt(685) + 1;
        return foodDto.get(randomIndex);
    }
}
