package com.foodiary.recipe.controller;

import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.recipe.model.*;
import com.foodiary.recipe.service.RecipeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
public class RecipeController {

        private final RecipeService recipeService;

        @Operation(summary = "recipe write", description = "레시피 게시글 작성")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        
        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @PostMapping(value = "/recipe")
        public ResponseEntity<?> RecipeWrite(
                @RequestPart(value = "recipeWrite") @Valid RecipeWriteRequestDto recipeWriteRequestDto,
                @Parameter(description = "사진 이미지")
                @RequestPart(value = "recipeImage1", required = true) MultipartFile recipeImage1,
                @RequestPart(value = "recipeImage2", required = false) MultipartFile recipeImage2,
                @RequestPart(value = "recipeImage3", required = false) MultipartFile recipeImage3
        ) throws Exception {
                List<MultipartFile> recipeImage = new ArrayList<>();
                if(recipeImage1 != null) recipeImage.add(recipeImage1);
                if(recipeImage2 != null) recipeImage.add(recipeImage2);
                if(recipeImage3 != null) recipeImage.add(recipeImage3);

                log.info( recipeWriteRequestDto.getIngredients().get(0).getIngredient());
                recipeService.addRecipe(recipeWriteRequestDto, recipeImage);
                return new ResponseEntity<>("OK", HttpStatus.CREATED);
        }




        @Operation(summary = "recipe modify", description = "레시피 게시글 수정")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        
        @PostMapping(value = "/recipe/{recipeId}/{memberId}")
        public ResponseEntity<String> RecipeModify(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) @Positive int recipeId,
                @PathVariable @ApiParam(value = "회원 시퀀스", required = true) @Positive int memberId,
                @RequestPart(value = "recipeEdit") RecipeEditRequestDto recipeEditRequestDto,
                @Parameter(description = "사진 이미지")
                @RequestPart(value = "recipeImage1", required = true) MultipartFile recipeImage1,
                @RequestPart(value = "recipeImage2", required = false) MultipartFile recipeImage2,
                @RequestPart(value = "recipeImage3", required = false) MultipartFile recipeImage3

        ) throws Exception {

                List<MultipartFile> recipeImage = new ArrayList<>();
                if(recipeImage1 != null) recipeImage.add(recipeImage1);
                if(recipeImage2 != null) recipeImage.add(recipeImage2);
                if(recipeImage3 != null) recipeImage.add(recipeImage3);

                recipeEditRequestDto.setRecipeId(recipeId);
                recipeEditRequestDto.setMemberId(memberId);
                recipeService.modifyRecipe(recipeEditRequestDto, recipeImage);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }




        @Operation(summary = "recipe list", description = "레시피 게시판 보기")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @GetMapping(value = "/recipes")
        public ResponseEntity<?> Recipes(
                @ApiParam(value = "게시판 페이지", required = false) int page
        ) throws Exception {
                if(page <= 0){
                        throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
                }
                PageHelper.startPage(page, 10);
                List<RecipesResponseDto> response = recipeService.findRecipes();
                return new ResponseEntity<>(PageInfo.of(response), HttpStatus.OK);
        }

        @Operation(summary = "recipe list", description = "레시피 게시글 상세 보기")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        
        @GetMapping(value = "/recipe/datils")
        public ResponseEntity<RecipeDetailsResponseDto> getRecipeDetails (
                @ApiParam(value = "게시글 시퀀스", required = true) @Positive int recipeId
        ) throws Exception {

                RecipeDetailsResponseDto response = recipeService.findRecipe(recipeId);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe delete", description = "레시피 게시글 삭제")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })


        
        @DeleteMapping(value = "/recipe/{recipeId}/{memberId}")
        public ResponseEntity<String> RecipeDelete(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) @Positive int recipeId,
                @PathVariable @ApiParam(value = "회원 시퀀스", required = true) @Positive int memberId
        ) throws Exception {

                recipeService.removeRecipe(recipeId, memberId);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }



        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe comment write", description = "레시피 게시글 댓글 작성")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        
        @PostMapping(value = "/recipe/comment")
        public ResponseEntity<String> RecipeCommentWrite(@RequestBody RecipeCommentWriteRequestDto recipeCommentWriteRequestDto)
                throws Exception {
                recipeService.addRecipeComment(recipeCommentWriteRequestDto);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }




        @Operation(summary = "recipe comment view", description = "레시피 게시글 댓글 조회")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        
        @GetMapping(value = "/recipe/comment")
        public ResponseEntity<?> RecipeCommentDetails(
                @ApiParam(value = "게시글 시퀀스", required = true) @Positive int recipeId,
                @ApiParam(value = "댓글 페이지", required = true) @Positive int page
        ) throws Exception {
                if(page <= 0){
                        throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
                }

                PageHelper.startPage(page, 10);

                List<RecipeCommentDetailsResponseDto> response = recipeService.findRecipeComments(recipeId);
                return new ResponseEntity<>(PageInfo.of(response), HttpStatus.OK);
        }

        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe comment modify", description = "레시피 게시글 댓글 수정")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        
        @PatchMapping(value = "/recipe/comment//{recipeId}/{memberId}/{commentId}")
        public ResponseEntity<String> RecipeCommentModify(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) @Positive int recipeId,
                @PathVariable @ApiParam(value = "회원 시퀀스", required = true) @Positive int memberId,
                @PathVariable @ApiParam(value = "댓글 시퀀스", required = true) @Positive int commentId,
                @RequestBody RecipeCommentEditRequestDto recipeCommentEditRequestDto
        ) throws Exception {
                recipeCommentEditRequestDto.setRecipeId(recipeId);
                recipeCommentEditRequestDto.setMemberId(memberId);
                recipeCommentEditRequestDto.setCommentId(commentId);
                recipeService.modifyRecipeComment(recipeCommentEditRequestDto);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe comment delete", description = "레시피 게시글 댓글 삭제")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        
        @DeleteMapping(value = "/recipe/comment/{recipeId}/{commentId}/{memberId}")
        public ResponseEntity<String> recipeCommentRemove(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) @Positive int recipeId,
                @PathVariable @ApiParam(value = "댓글 시퀀스", required = true) @Positive int commentId,
                @PathVariable @ApiParam(value = "회원 시퀀스", required = true) @Positive int memberId
        ) throws Exception {
                recipeService.removeRecipeComment(recipeId, memberId, commentId);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe like", description = "레시피 게시글 좋아요")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        
        @PostMapping(value = "/recipe/like/{recipeId}/{memberId}")
        public ResponseEntity<String> recipeLike(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) @Positive int recipeId,
                @PathVariable @ApiParam(value = "회원 시퀀스", required = true) @Positive int memberId
        ) throws Exception {
                recipeService.addRecipeLike(memberId, recipeId);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }


        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe scrap", description = "레시피 게시글 스크랩")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        
        @PostMapping(value = "/recipe/scrap/{recipeId}/{memberId}")
        public ResponseEntity<String> recipeScrap(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) @Positive int recipeId,
                @PathVariable @ApiParam(value = "회원 시퀀스", required = true) @Positive int memberId
        ) throws Exception {
                recipeService.addRecipeScrap(recipeId, memberId);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe scrap remove", description = "레시피 게시글 스크랩 삭제")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        
        @DeleteMapping("/recipe/scrap/{recipeId}/{memberId}/{scrapId}")
        public ResponseEntity<String> recipeScrapRemove(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) @Positive int recipeId,
                @PathVariable @ApiParam(value = "회원 시퀀스", required = true) @Positive int memberId,
                @PathVariable @ApiParam(value = "게시글 스크랩 시퀀스", required = true) @Positive int scrapId
        ) throws Exception {
                recipeService.removeRecipeScrap(recipeId, memberId, scrapId);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }
}
