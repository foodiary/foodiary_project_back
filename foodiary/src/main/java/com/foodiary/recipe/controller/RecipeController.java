package com.foodiary.recipe.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.recipe.model.*;
import com.foodiary.recipe.service.RecipeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.foodiary.recipe.model.RecipeCommentDetailsResponseDto;
import com.foodiary.recipe.model.RecipeDetailsResponseDto;
import com.foodiary.recipe.model.RecipeEditRequestDto;
import com.foodiary.recipe.model.RecipeWriteRequestDto;
import com.foodiary.recipe.model.RecipesResponseDto;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RequiredArgsConstructor
@Slf4j
@Controller
public class RecipeController {

        private final RecipeService recipeService;

        @Operation(summary = "recipe write", description = "레시피 게시글 작성")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @PostMapping(value = "/recipe")
        public ResponseEntity<?> RecipeWrite(
                @RequestPart(value = "recipeWrite") @Valid RecipeWriteRequestDto recipeWriteRequestDto,
                @Parameter(description = "사진 이미지")
                @RequestPart(value = "recipeImage", required = false) MultipartFile recipeImage
        ) throws Exception {

                recipeService.addRecipe(recipeWriteRequestDto, recipeImage);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }




        @Operation(summary = "recipe modify", description = "레시피 게시글 수정")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @ResponseBody
        @PostMapping(value = "/recipe/{recipeId}/{memberId}")
        public ResponseEntity<String> RecipeModify(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
                @PathVariable @ApiParam(value = "회원 시퀀스", required = true)int memberId,
                @RequestPart(value = "recipeEdit") RecipeEditRequestDto recipeEditRequestDto,
                @Parameter(description = "사진 이미지")
                @RequestPart(value = "recipeImage", required = false) MultipartFile recipeImage
        ) throws Exception {
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
                @ApiParam(value = "게시판 페이지", required = false) int page,
                @ApiParam(value = "게시판 페이지 사이즈", required = false) int size
        ) throws Exception {
                if(page <= 0 || size <= 0){
                        throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
                }
                PageHelper.startPage(page, size);
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
        @ResponseBody
        @GetMapping(value = "/recipe/datils")
        public ResponseEntity<RecipeDetailsResponseDto> getRecipeDetails (
                @ApiParam(value = "게시글 시퀀스", required = true) int recipeId
        ) throws Exception {

                RecipeDetailsResponseDto response = recipeService.findRecipe(recipeId);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe delete", description = "레시피 게시글 삭제")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @DeleteMapping(value = "/recipe/{recipeId}/{memberId}")
        public ResponseEntity<String> RecipeDelete(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
                @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
        ) throws Exception {

                recipeService.removeRecipe(recipeId, memberId);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe comment write", description = "레시피 게시글 댓글 작성")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
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
        @ResponseBody
        @GetMapping(value = "/recipe/comment")
        public ResponseEntity<?> RecipeCommentDetails(
                @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
                @ApiParam(value = "댓글 페이지", required = true) int page,
                @ApiParam(value = "댓글 갯수", required = true) int size// string으로 받고 interger로 변환 필요, int로 받으면 null 값일떄 에러남
        ) throws Exception {
                if(page <= 0 || size <= 0){
                        throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
                }

                PageHelper.startPage(page, size);

                List<RecipeCommentDetailsResponseDto> response = recipeService.findRecipeComments(recipeId);
                return new ResponseEntity<>(PageInfo.of(response), HttpStatus.OK);
        }

        @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe comment modify", description = "레시피 게시글 댓글 수정")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @PatchMapping(value = "/recipe/comment//{recipeId}/{memberId}/{commentId}")
        public ResponseEntity<String> RecipeCommentModify(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
                @PathVariable @ApiParam(value = "회원 시퀀스", required = true)int memberId,
                @PathVariable @ApiParam(value = "댓글 시퀀스", required = true) int commentId,
                @RequestBody RecipeCommentEditRequestDto recipeCommentEditRequestDto
        ) throws Exception {
                recipeCommentEditRequestDto.setRecipeId(recipeId);
                recipeCommentEditRequestDto.setMemberId(memberId);
                recipeCommentEditRequestDto.setCommentId(commentId);
                recipeService.modifyRecipeComment(recipeCommentEditRequestDto);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe comment delete", description = "레시피 게시글 댓글 삭제")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @DeleteMapping(value = "/recipe/comment/{recipeId}/{commentId}/{memberId}")
        public ResponseEntity<String> recipeCommentRemove(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
                @PathVariable @ApiParam(value = "댓글 시퀀스", required = true) int commentId,
                @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
        ) throws Exception {
                recipeService.removeRecipeComment(recipeId, memberId, commentId);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe like", description = "레시피 게시글 좋아요")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @PostMapping(value = "/recipe/like/{recipeId}/{memberId}")
        public ResponseEntity<String> recipeLike(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
                @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
        ) throws Exception {
                recipeService.addRecipeLike(memberId, recipeId);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe like cancle", description = "레시피 게시글 좋아요 취소")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @DeleteMapping(value = "/recipe/like/{recipeLikeId}")
        public ResponseEntity<String> recipeLikeCancle(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeLikeId
        ) throws Exception {
                recipeService.removeRecipeLike(recipeLikeId);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe scrap", description = "레시피 게시글 스크랩")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @PostMapping(value = "/recipe/scrap/{recipeId}/{memberId}")
        public ResponseEntity<String> recipeScrap(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
                @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
        ) throws Exception {
                recipeService.addRecipeScrap(recipeId, memberId);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe scrap remove", description = "레시피 게시글 스크랩 삭제")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @DeleteMapping("/recipe/scrap/{recipeId}/{memberId}/{scrapId}")
        public ResponseEntity<String> recipeScrapRemove(
                @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
                @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId,
                @PathVariable @ApiParam(value = "게시글 스크랩 시퀀스", required = true) int scrapId
        ) throws Exception {
                recipeService.removeRecipeScrap(recipeId, memberId, scrapId);
                return new ResponseEntity<>("OK", HttpStatus.OK);
        }
}
