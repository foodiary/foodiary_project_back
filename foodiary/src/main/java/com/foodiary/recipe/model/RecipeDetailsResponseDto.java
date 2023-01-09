package com.foodiary.recipe.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDetailsResponseDto {
    
    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int recipeId;

    @ApiModelProperty(value="회원 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="게시글 제목", required = true)
    private String recipeTitle;

    @ApiModelProperty(value="게시글 내용", required = true)
    private String recipeBody;

    @ApiModelProperty(value="게시글 작성자", required = true)
    private String recipeWriter;

    @Setter
    @ApiModelProperty(value="재료", required = true)
    private List<IngredientResponseDto> ingredient;

    @Setter
    @ApiModelProperty(value="게시글 이미지 경로1", required = false)
    private String recipePath1;

    @Setter
    @ApiModelProperty(value="게시글 이미지 경로2", required = false)
    private String recipePath2;

    @Setter
    @ApiModelProperty(value="게시글 이미지 경로3", required = false)
    private String recipePath3;

    @Setter
    @ApiModelProperty(value="게시글 좋아요 수", required = true)
    private int recipeLike;

    @ApiModelProperty(value="게시글 조회 수", required = true)
    private int recipeView;

    @ApiModelProperty(value="작성일", required = true)
    private LocalDateTime recipeCreate;

    @Setter
    @ApiModelProperty(value="댓글 수", required = true)
    private int recipeComment;

    @Setter
    @ApiModelProperty(value="본인 인증", required = true)
    private boolean userCheck;


    
    

    // @ApiModelProperty(value="댓글 리스트", required = true)
    // List<RecipeCommentDetailsDto> dailyCommentDtoList;
}
