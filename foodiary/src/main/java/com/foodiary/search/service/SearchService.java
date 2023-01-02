package com.foodiary.search.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.search.mapper.SearchMapper;
import com.foodiary.search.model.SearchResponseMemberDto;
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

            String hashkey = "search:"+searchRequestDto.getMemberId();
            HashOperations<String, Integer, String> hashOperations = redisTemplate.opsForHash();
            
            long size = hashOperations.size(hashkey);
            
            if(size > 0) {
                Map<Integer, String> map = hashOperations.entries(hashkey);
                Set<Integer> set = map.keySet();
    
                int min = Collections.min(map.keySet());
                int max = Collections.max(map.keySet());
    
                if(size > 9) {
                    // 제일 먼저 들어온 최근 검색어 지우기
                    if(map.containsValue(searchRequestDto.getKeyword())==false) {
                        hashOperations.delete(hashkey, min);
                        addKeyword(max, searchRequestDto, hashOperations,hashkey);
    
                    }
                }
                else {
                    if(map.containsValue(searchRequestDto.getKeyword())==false) {
                        addKeyword(max, searchRequestDto, hashOperations,hashkey);
                    }
                }
            }
            else {
                addKeyword(0, searchRequestDto, hashOperations,hashkey);
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

    public List<SearchResponseMemberDto> searchView(SearchRequestMemberDto sRequestMemberDto) {

        HashOperations<String, Integer, String> hashOperations = redisTemplate.opsForHash();
        List<SearchResponseMemberDto> searchMemberDtoList = new ArrayList<>();
        Map<Integer, String> map = hashOperations.entries("search:"+sRequestMemberDto.getMemberId());
        if(map.size()==0) {
            throw new BusinessLogicException(ExceptionCode.LAST_SEARCH_NOT_FOUND);
        }
        for (int key : map.keySet()) {
            SearchResponseMemberDto searchMemberDto = new SearchResponseMemberDto(sRequestMemberDto.getMemberId(), key, map.get(key));
            searchMemberDtoList.add(searchMemberDto);
        }
        return searchMemberDtoList;
    }

    private void addKeyword(int max, SearchRequestDto searchRequestDto, HashOperations<String, Integer, String> hashOperations, String hashkey) {
        hashOperations.put(hashkey, max+1, searchRequestDto.getKeyword());
        redisTemplate.expire("search:"+searchRequestDto.getMemberId(), 30, TimeUnit.DAYS);
    }

    public void delete(int memberId, int keywordId) {
        HashOperations<String, Integer, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.delete("search:"+memberId, keywordId);
    }

}
