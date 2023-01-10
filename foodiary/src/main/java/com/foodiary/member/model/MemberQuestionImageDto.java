package com.foodiary.member.model;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberQuestionImageDto {
    
    @ApiModelProperty(value="이미지 시퀀스", required = false)
    private String questionFileId;

    @ApiModelProperty(value="사용자 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int questionId;
    
    @ApiModelProperty(value="이미지 원본 파일명, test.png면 test", required = true)
    private String questionFileOriginalName;

    @ApiModelProperty(value="이미지 원본 파일명(확장자 포함) test.png", required = true)
    private String questionFileFullName;

    @ApiModelProperty(value="저장한 파일명 uuid+밀리초", required = true)
    private String questionFileSaveName;

    @ApiModelProperty(value="저장한 경로 s3 주소", required = true)
    private String questionFilePath;

    @ApiModelProperty(value="이미지 크기, byte를 기준으로 저장", required = true)
    private long questionFileSize;

    @ApiModelProperty(value="이미지 확장자, png", required = true)
    private String questionFileType;

    private LocalDateTime questionCreate;

    private LocalDateTime questionUpdate;

    public MemberQuestionImageDto(int memberId, int questionId, String questionFileOriginalName,
            String questionFileFullName, String questionFileSaveName, String questionFilePath, long questionFileSize,
            String questionFileType) {
        this.memberId = memberId;
        this.questionId = questionId;
        this.questionFileOriginalName = questionFileOriginalName;
        this.questionFileFullName = questionFileFullName;
        this.questionFileSaveName = questionFileSaveName;
        this.questionFilePath = questionFilePath;
        this.questionFileSize = questionFileSize;
        this.questionFileType = questionFileType;
    }

}
