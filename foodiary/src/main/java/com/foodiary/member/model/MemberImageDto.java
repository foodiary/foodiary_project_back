package com.foodiary.member.model;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberImageDto {

    @ApiModelProperty(value="이미지 시퀀스", required = false)
    private String memberFileId;
    
    @ApiModelProperty(value="사용자 시퀀스", required = true)
    private int memberId;
    
    @ApiModelProperty(value="이미지 원본 파일명, test.png면 test", required = true)
    private String memberFileOriginalName;

    @ApiModelProperty(value="이미지 원본 파일명(확장자 포함) test.png", required = true)
    private String memberFileFullName;

    @ApiModelProperty(value="저장한 파일명 uuid+밀리초", required = true)
    private String memberFileSaveName;

    @ApiModelProperty(value="저장한 경로 s3 주소", required = true)
    private String memberFilePath;

    @ApiModelProperty(value="이미지 크기, byte를 기준으로 저장", required = true)
    private long memberFileSize;

    @ApiModelProperty(value="이미지 확장자, png", required = true)
    private String memberFileType;

    private LocalDateTime memberCreate;

    private LocalDateTime memberUpdate;

    public MemberImageDto(int memberId, String memberFileOriginalName, String memberFileFullName,
            String memberFileSaveName, String memberFilePath, long memberFileSize, String memberFileType) {
        this.memberId = memberId;
        this.memberFileOriginalName = memberFileOriginalName;
        this.memberFileFullName = memberFileFullName;
        this.memberFileSaveName = memberFileSaveName;
        this.memberFilePath = memberFilePath;
        this.memberFileSize = memberFileSize;
        this.memberFileType = memberFileType;
    }

}
