package com.foodiary.common.exception;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VaildErrorResponseDto {
    
    @ApiModelProperty(value="문제발생한 코드", required = true)
    private String code;

    @ApiModelProperty(value="에러 메세지", required = true)
    private String message;

    @ApiModelProperty(value="상태 코드", required = true)
    private int status;
 
}
