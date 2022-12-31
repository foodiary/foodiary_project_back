package com.foodiary.recipe.model;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

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

    @Setter
    @ApiModelProperty(value="게시글 이미지 경로1", required = true)
    private String path1;

    @Setter
    @ApiModelProperty(value="게시글 이미지 경로2", required = false)
    private String path2;

    @Setter
    @ApiModelProperty(value="게시글 이미지 경로3", required = false)
    private String path3;

    @Setter
    @ApiModelProperty(value="게시글 좋아요 수", required = true)
    private int like;

    @ApiModelProperty(value="게시글 조회 수", required = true)
    private int recipeView;

    @ApiModelProperty(value="작성일", required = true)
    private LocalDateTime recipeCreate;

    @Setter
    @ApiModelProperty(value="댓글 수", required = true)
    private int comment;

    // 이미지 1개일때
    public RecipeDetailsResponseDto(int recipeId, int memberId, String title, String content, String path1, int like, int view,
                                    LocalDateTime create, int comment) {
        this.recipeId = recipeId;
        this.memberId = memberId;
        this.recipeTitle = title;
        this.recipeBody = content;
        this.path1 = path1;
        this.like = like;
        this.recipeView = view;
        this.recipeCreate = create;
        this.comment = comment;
    }

    // 이미지 2개일때
    public RecipeDetailsResponseDto(int recipeId, int memberId, String title, String content, String path1, String path2,
                                    int like, int view, LocalDateTime create, int comment) {
        this.recipeId = recipeId;
        this.memberId = memberId;
        this.recipeTitle = title;
        this.recipeBody = content;
        this.path1 = path1;
        this.path2 = path2;
        this.like = like;
        this.recipeView = view;
        this.recipeCreate = create;
        this.comment = comment;
    }

    
    

    // @ApiModelProperty(value="댓글 리스트", required = true)
    // List<RecipeCommentDetailsDto> dailyCommentDtoList;
}
