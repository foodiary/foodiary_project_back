package com.foodiary.recipe.mapper;

import com.foodiary.recipe.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface RecipeMapper {

    // =================== INSERT ====================

    int saveRecipe(RecipeWriteRequestDto recipeWriteRequestDto);

    int saveRecipeComment(RecipeCommentWriteRequestDto recipeCommentWriteRequestDto);

    int saveRecipeLike(@Param("memberId") int memberId, @Param("recipeId") int recipeId);

    int saveRecipeScrap(@Param("recipeId") int recipeId, @Param("memberId") int memberId);

    int saveImage(RecipeImageDto imageDto);

    int saveIngredient(IngredientRequestDto ingredientRequestDto);



    // =================== UPDATE ====================

    int updateRecipe(RecipeEditRequestDto recipeEditRequestDto);

    int updateRecipeComment(RecipeCommentEditRequestDto recipeCommentEditRequestDto);

    int updateRecipeView(@Param("recipeId") int recipeId);

    int updateRecipeId(@Param("recipeId") int recipeId, @Param("path") String path);



    // =================== SELECT ====================

    Optional<RecipeDetailsResponseDto> findByRecipeId(@Param("recipeId") int recipeId);

    Optional<RecipeCommentDetailsResponseDto> findByRecipeComment(@Param("commentId") int commentId);

    Optional<Integer> findByRecipeScrap(@Param("recipeId") int recipeId, @Param("memberId") int memberId);

    Optional<Integer> findByMemberIdAndRecipeId(@Param("memberId") int memberId, @Param("recipeId") int recipeId);

    Optional<Integer> findByRecipeId1(@Param("path1") String path);

    List<IngredientResponseDto> findAllIngredient(@Param("recipeId") int recipeId);

    List<RecipeCommentDetailsResponseDto> findAllRecipeComment(@Param("recipeId") int recipeId);

    List<RecipesResponseDto> findAll();

    List<RecipesResponseDto> findTopRecipes();

    List<RecipesResponseDto> findCreateAll(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<RecipeImageDto> findImageByRecipeId(@Param("recipeId") int recipeId);



    // =================== DELETE ====================

    int deleteRecipeLike(@Param("recipeId") int recipeLikeId);

    int deleteRecipe(@Param("recipeId") int recipeId);

    int deleteRecipeComment(@Param("recipeId") int recipeId, @Param("commentId") int commentId);

    int deleteRecipeScrap(@Param("memberId") int memberId, @Param("recipeId") int recipeId);

    int deleteRecipeImage(@Param("recipeId") int recipeId, @Param("path") String path);

    int deleteIngredient(@Param("recipeId") int recipeId);


}
