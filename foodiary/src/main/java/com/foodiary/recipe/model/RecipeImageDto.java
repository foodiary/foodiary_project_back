package com.foodiary.recipe.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class RecipeImageDto {

    @ApiModelProperty(value="레시피 게시판 시퀀스", required = true)
    private int recipeId;

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

    public RecipeImageDto(int recipeId, int memberId, String originalName, String originalFullName, String saveName, String path, long size, String ext) {
        this.recipeId = recipeId;
        this.memberId = memberId;
        this.originalName = originalName;
        this.originalFullName = originalFullName;
        this.saveName = saveName;
        this.path = path;
        this.size = size;
        this.ext = ext;
    }
}
