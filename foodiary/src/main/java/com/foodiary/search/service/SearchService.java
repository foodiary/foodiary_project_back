package com.foodiary.search.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.foodiary.auth.service.UserService;
import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.search.mapper.SearchMapper;
import com.foodiary.search.model.SearchResponseMemberDto;
import com.foodiary.search.model.SearchDailyResponseDto;
import com.foodiary.search.model.SearchRecipeResponseDto;
import com.foodiary.search.model.SearchRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SearchService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    private final SearchMapper mapper;

    private final UserService userService;

    // 하루 식단 검색하기
    public List<SearchDailyResponseDto> searchDaily(SearchRequestDto searchRequestDto) {
        
        if(searchRequestDto.getMemberId() > 0) {

            userService.checkUser(searchRequestDto.getMemberId());
            String hashkey = "dailySearch:"+ searchRequestDto.getMemberId();
            HashOperations<String, Integer, String> hashOperations = redisTemplate.opsForHash();
            
            long size = hashOperations.size(hashkey);
            
            if(size > 0) {
                Map<Integer, String> map = hashOperations.entries(hashkey);
    
                int min = Collections.min(map.keySet());
                int max = Collections.max(map.keySet());
    
                if(size > 9) {
                    // 제일 먼저 들어온 최근 검색어 지우기
                    if(map.containsValue(searchRequestDto.getKeyword())==false) {
                        hashOperations.delete(hashkey, min);
                        addKeywordDaily(max, searchRequestDto, hashOperations,hashkey); 
                    }
                }
                else {
                    if(map.containsValue(searchRequestDto.getKeyword())==false) {
                        addKeywordDaily(max, searchRequestDto, hashOperations,hashkey); 

                    }
                }
            }
            else {
                addKeywordDaily(0, searchRequestDto, hashOperations,hashkey); 
            }

        }   

        List<SearchDailyResponseDto> searchResponseDto = mapper.findbyDaily(searchRequestDto);
        if(searchResponseDto.size()>0) {
            return searchResponseDto; 
        }
        else {
            throw new BusinessLogicException(ExceptionCode.SEARCH_NOT_FOUND);
        }
    }

    // 하루 식단 검색 보기
    public List<SearchResponseMemberDto> searchViewDaily(int memberId) {

        userService.checkUser(memberId);
        HashOperations<String, Integer, String> hashOperations = redisTemplate.opsForHash();
        List<SearchResponseMemberDto> searchMemberDtoList = new ArrayList<>();
        Map<Integer, String> map = hashOperations.entries("dailySearch:"+memberId);
        if(map.size()==0) {
            throw new BusinessLogicException(ExceptionCode.LAST_SEARCH_NOT_FOUND);
        }
        for (int key : map.keySet()) {
            SearchResponseMemberDto searchMemberDto = new SearchResponseMemberDto(memberId, key, map.get(key));
            searchMemberDtoList.add(searchMemberDto);
        }
        return searchMemberDtoList;
    }

    // 하루 식단 검색어 삭제
    public void deleteDaily(int memberId, int keywordId) {
        userService.checkUser(memberId);
        HashOperations<String, Integer, String> hashOperations = redisTemplate.opsForHash();
        if(hashOperations.hasKey("dailySearch:"+memberId, keywordId)) {
            hashOperations.delete("dailySearch:"+memberId, keywordId);
        }
        else {
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);            
        }
    }

    private void addKeywordDaily(int max, SearchRequestDto searchRequestDto, HashOperations<String, Integer, String> hashOperations, String hashkey) {
        hashOperations.put(hashkey, max+1, searchRequestDto.getKeyword());

        redisTemplate.expire("dailySearch:"+ searchRequestDto.getMemberId(), 30, TimeUnit.DAYS);
    }

    // 레시피 검색하기
    public List<SearchRecipeResponseDto> searchRecipe(SearchRequestDto searchRequestDto) {
        
        if(searchRequestDto.getMemberId() > 0) {

            userService.checkUser(searchRequestDto.getMemberId());
            String hashkey = "recipeSearch:" + searchRequestDto.getMemberId();
            HashOperations<String, Integer, String> hashOperations = redisTemplate.opsForHash();
            
            long size = hashOperations.size(hashkey);
            
            if(size > 0) {
                Map<Integer, String> map = hashOperations.entries(hashkey);
    
                int min = Collections.min(map.keySet());
                int max = Collections.max(map.keySet());
    
                if(size > 9) {
                    // 제일 먼저 들어온 최근 검색어 지우기
                    if(map.containsValue(searchRequestDto.getKeyword())==false) {
                        hashOperations.delete(hashkey, min);
                        addKeywordRecipe(max, searchRequestDto, hashOperations,hashkey);
    
                    }
                }
                else {
                    if(map.containsValue(searchRequestDto.getKeyword())==false) {
                        addKeywordRecipe(max, searchRequestDto, hashOperations,hashkey);
                    }
                }
            }
            else {
                addKeywordRecipe(0, searchRequestDto, hashOperations,hashkey);
            }

        }   

        List<SearchRecipeResponseDto> searchResponseDto = mapper.findbyRecipe(searchRequestDto);
        if(searchResponseDto.size()>0) {
            return searchResponseDto; 
        }
        else {
            throw new BusinessLogicException(ExceptionCode.SEARCH_NOT_FOUND);
        }
    }

    // 레시피 검색 보기
    public List<SearchResponseMemberDto> searchViewRecipe(int memberId) {
        userService.checkUser(memberId);
        HashOperations<String, Integer, String> hashOperations = redisTemplate.opsForHash();
        List<SearchResponseMemberDto> searchMemberDtoList = new ArrayList<>();
        Map<Integer, String> map = hashOperations.entries("recipeSearch:"+memberId);
        if(map.size()==0) {
            throw new BusinessLogicException(ExceptionCode.LAST_SEARCH_NOT_FOUND);
        }
        for (int key : map.keySet()) {
            SearchResponseMemberDto searchMemberDto = new SearchResponseMemberDto(memberId, key, map.get(key));
            searchMemberDtoList.add(searchMemberDto);
        }
        return searchMemberDtoList;
    }

    // 레시피 검색어 삭제
    public void deleteRecipe(int memberId, int keywordId) {

        userService.checkUser(memberId);
        HashOperations<String, Integer, String> hashOperations = redisTemplate.opsForHash();

        if(hashOperations.hasKey("recipeSearch:"+memberId, keywordId)) {
            hashOperations.delete("recipeSearch:"+memberId, keywordId);
        }
        else {
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);            
        }
    }

    private void addKeywordRecipe(int max, SearchRequestDto searchRequestDto, HashOperations<String, Integer, String> hashOperations, String hashkey) {
        hashOperations.put(hashkey, max+1, searchRequestDto.getKeyword());
        
        redisTemplate.expire("recipeSearch:"+searchRequestDto.getMemberId(), 30, TimeUnit.DAYS);
    }

}
