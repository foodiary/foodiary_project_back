package com.foodiary.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponseDto {

    private Date date; //예외 발생 날짜
    private String msg; //예외 메세지
    private String content; //예외가 어느 요청에서 났는지 확인
}
