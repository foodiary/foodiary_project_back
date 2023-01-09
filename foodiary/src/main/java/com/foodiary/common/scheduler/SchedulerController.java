package com.foodiary.common.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.foodiary.rank.mapper.RankMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerController {

    private final RankMapper mapper;
    
    @Scheduled(cron="0 0 0/1 * * *")
    public void scheduler() {

        log.info("랭킹 작업 실행");

        mapper.rankDelete();

        mapper.weekWrite();
        mapper.monthWrite();

    }

}
