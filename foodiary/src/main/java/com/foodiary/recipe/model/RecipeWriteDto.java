package com.foodiary.recipe.model;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeWriteDto {
    
    @ApiModelProperty(value="회원 시퀀스", required = true)
    private int memberId;
    
    @NotBlank(message = "제목이 비어있습니다.")
    @ApiModelProperty(value="게시글 제목", required = true)
    private String title;

    @NotBlank(message = "내용이 비어있습니다.")
    @ApiModelProperty(value="게시글 내용", required = true)
    private String content;

}
