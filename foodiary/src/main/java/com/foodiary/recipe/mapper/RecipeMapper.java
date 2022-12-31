package com.foodiary.recipe.mapper;

import com.foodiary.daily.model.*;
import com.foodiary.recipe.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecipeMapper {

    // =================== INSERT ====================

    void saveRecipe(RecipeWriteRequestDto recipeWriteRequestDto);

    void saveRecipeComment(RecipeCommentWriteRequestDto recipeCommentWriteRequestDto);

    void saveRecipeLike(@Param("memberId") int memberId, @Param("recipeId") int recipeId);

    void saveRecipeScrap(@Param("recipeId") int recipeId, @Param("memberId") int memberId);

    void saveImage(RecipeImageDto imageDto);



    // =================== UPDATE ====================

    void updateRecipe(RecipeEditRequestDto recipeEditRequestDto);

    void updateRecipeComment(RecipeCommentEditRequestDto recipeCommentEditRequestDto);

    void updateRecipeView(@Param("recipeId") int recipeId);



    // =================== SELECT ====================

    RecipeDetailsResponseDto findByRecipeId(@Param("recipeId") int recipeId);

    RecipeCommentDetailsResponseDto findByRecipeComment(@Param("commentId") int commentId);

    Integer findByRecipeScrap(@Param("recipeId") int recipeId, @Param("memberId") int memberId);

    Integer findByRecipeLikeId(@Param("recipeLikeId") int recipeLikeId);

    Integer findByMemberIdAndRecipeId(@Param("memberId") int memberId, @Param("recipeId") int recipeId);

    String findByRecipeImage(@Param("recipeId") int recipeId);

    List<Integer> findAllRecipeId(@Param("recipeId") int recipeId);

    List<RecipeCommentDetailsResponseDto> findAllRecipeComment(@Param("recipeId") int recipeId);

    List<RecipesResponseDto> findAll();



    // =================== DELETE ====================

    void deleteRecipeLike(@Param("recipeLikeId") int recipeLikeId);

    void deleteRecipe(@Param("recipeId") int recipeId, @Param("memberId") int memberId);

    void deleteRecipeComment(@Param("recipeId") int recipeId, @Param("memberId") int memberId, @Param("commentId") int commentId);

    void deleteRecipeScrap(@Param("recipeId") int recipeId, @Param("memberId") int memberId, @Param("scrapId") int scrapId);
}
