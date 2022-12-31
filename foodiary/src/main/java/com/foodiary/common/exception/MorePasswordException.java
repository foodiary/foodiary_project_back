package com.foodiary.common.exception;

public class MorePasswordException extends RuntimeException{
    
    private VaildErrorResponseDto vaildErrorResponseDto;

    public MorePasswordException(VaildErrorResponseDto vaildErrorResponseDto) {
        super(vaildErrorResponseDto.getMessage());
        this.vaildErrorResponseDto = vaildErrorResponseDto;
    }

    public VaildErrorResponseDto getVaildErrorResponseDto() {
        return this.vaildErrorResponseDto;
    }

}
