package com.foodiary.common.scheduler;

import com.foodiary.food.mapper.FoodMapper;
import com.foodiary.food.model.FoodDto;
import com.foodiary.food.model.MenuRecommendResponseDto;
import com.foodiary.food.service.FoodService;
import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.foodiary.rank.mapper.RankMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerController {

    private final RankMapper mapper;
    private final FoodMapper foodMapper;
    private final MemberMapper memberMapper;
    private final FoodService foodService;
    private final RedisTemplate redisTemplate;
    
    @Scheduled(cron="0 0 0/1 * * *")
    public void scheduler() {

        log.info("랭킹 작업 실행");

        mapper.rankDelete();

        mapper.weekWrite();
        mapper.monthWrite();

    }

    @Scheduled(cron="0 0 * * * SUN")
    public void menuScheduler() {

        List<MemberDto> memberList = memberMapper.findAll();

        for (int k = 1; k <= memberList.size() ; k++) {
            List<Integer> hateFoodList = foodMapper.findAllHateFood(k);
            List<FoodDto> list = new ArrayList<>();

            //중복 음식 방지 로직
            for(int i=0; i<14; i++) {
                list.add(foodService.recommendFood());
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
                    .memberId(k)
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


            redisTemplate.opsForValue().set(memberList.get(k-1).getMemberNickName(), recommendMenu);
        }
        log.info("모든 회원의 식단 구성을 완료하였습니다.");
    }
}
