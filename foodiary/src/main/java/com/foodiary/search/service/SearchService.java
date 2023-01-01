package com.foodiary.search.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.search.mapper.SearchMapper;
import com.foodiary.search.model.SearchRequestDto;
import com.foodiary.search.model.SearchRequestMemberDto;
import com.foodiary.search.model.SearchResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SearchService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    private final SearchMapper mapper;

    public List<SearchResponseDto> search(SearchRequestDto searchRequestDto) {
        
        if(searchRequestDto.getMemberId() > 0) {
            RedisOperations<String, String> operations = redisTemplate.opsForList().getOperations();
            
            long size = operations.opsForList().size("search:"+searchRequestDto.getMemberId());

            if(size > 9) {
                redisTemplate.opsForList().leftPop("search:"+searchRequestDto.getMemberId());
            }
            List<String> searchList = operations.opsForList().range("search:"+searchRequestDto.getMemberId(), 0, size);

            if(searchList.contains(searchRequestDto.getKeyword())==false) {
                redisTemplate.opsForList().rightPush("search:"+searchRequestDto.getMemberId(), 
                searchRequestDto.getKeyword());
                redisTemplate.expire("search:"+searchRequestDto.getMemberId(), 30, TimeUnit.DAYS);
            }
        }   

        List<SearchResponseDto> searchResponseDto = mapper.findbyTitle(searchRequestDto);
        if(searchResponseDto.size()>0) {
            return searchResponseDto; 
        }
        else {
            throw new BusinessLogicException(ExceptionCode.SEARCH_NOT_FOUND);
        }
    }

    public List<String> searchView(SearchRequestMemberDto sRequestMemberDto) {

        RedisOperations<String, String> operations = redisTemplate.opsForList().getOperations();
            
        long size = operations.opsForList().size("search:"+sRequestMemberDto.getMemberId());

        return operations.opsForList().range("search:"+sRequestMemberDto.getMemberId(), 0, size);

    }

}
