package com.foodiary.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VaildErrorDto {
    
    private String code;
    private String message;
    private int status;
 
}
