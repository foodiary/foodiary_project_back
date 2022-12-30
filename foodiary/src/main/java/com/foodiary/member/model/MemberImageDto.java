package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberImageDto {

    // @ApiModelProperty(value="이미지 시퀀스", required = true)
    // private String imageId;
    
    @ApiModelProperty(value="사용자 시퀀스", required = true)
    private int memberId;
    
    @ApiModelProperty(value="이미지 원본 파일명, test.png면 test", required = true)
    private String originalName;

    @ApiModelProperty(value="이미지 원본 파일명(확장자 포함) test.png", required = true)
    private String originalFullName;

    @ApiModelProperty(value="저장한 파일명 uuid+밀리초", required = true)
    private String saveName;

    @ApiModelProperty(value="저장한 경로 s3 주소", required = true)
    private String path;

    @ApiModelProperty(value="이미지 크기, byte를 기준으로 저장", required = true)
    private long size;

    @ApiModelProperty(value="이미지 확장자, png", required = true)
    private String ext;

}
