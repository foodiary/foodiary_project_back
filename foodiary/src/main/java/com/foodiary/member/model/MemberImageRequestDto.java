package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberImageRequestDto {

    @ApiModelProperty(value="이미지 시퀀스", required = true)
    private String imageId;
    
    @ApiModelProperty(value="사용자 시퀀스", required = true)
    private String memberId;
    
    @ApiModelProperty(value="이미지 원본 파일명", required = true)
    private String originalName;

    @ApiModelProperty(value="저장한 파일명", required = true)
    private String saveName;

    @ApiModelProperty(value="저장한 경로", required = true)
    private String path;

    @ApiModelProperty(value="이미지 크기", required = true)
    private String size;

    @ApiModelProperty(value="이미지 확장자", required = true)
    private String ext;

}
