package com.foodiary.common.email.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.foodiary.common.email.model.EmailDto;

@Mapper
public interface EmailMapper {
    
    int saveEmail(EmailDto emailDto);
}
