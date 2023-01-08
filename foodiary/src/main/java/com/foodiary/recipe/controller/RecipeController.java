package com.foodiary.recipe.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

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

import com.foodiary.recipe.model.RecipeCommentDetailsDto;
import com.foodiary.recipe.model.RecipeDetailsDto;
import com.foodiary.recipe.model.RecipeEditDto;
import com.foodiary.recipe.model.RecipeWriteDto;
import com.foodiary.recipe.model.RecipesDto;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Controller
public class RecipeController {
    
        @Operation(summary = "recipe write", description = "레시피 공유 게시글 작성")
        @ApiResponses({ 
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @ResponseBody
        @PostMapping(value = "/recipe")
        public ResponseEntity<String> recipeWrite(
            @RequestPart @Valid RecipeWriteDto recipeWriteDto,
            // @Parameter(description="회원 시퀀스", example = "3498", required = true)
            // @RequestPart("memberId") String memberId, // int로 안받아져서 string으로 받음
            // @Parameter(description="게시글 제목", example = "제목입니다", required = true)
            // @RequestPart("title") String title,
            // @Parameter(description="게시글 내용", example = "내용입니다", required = true)
            // @RequestPart("content") String content,
            @Parameter(description = "사진 이미지")
            @RequestPart(value = "recipeImage", required = true) List<MultipartFile> recipeImage
        ) throws Exception {
    
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
    
        @Operation(summary = "recipe modify", description = "레시피 공유 게시글 수정")
        @ApiResponses({ 
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @ResponseBody
        @PatchMapping(value = "/recipe/{recipeId}/{memberId}")
        public ResponseEntity<String> recipeModify(
            @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
            @PathVariable @ApiParam(value = "회원 시퀀스", required = true)int memberId,
            @RequestPart RecipeEditDto recipeEditDto,
            @Parameter(description = "사진 이미지")
            @RequestPart(value = "recipeImage", required = false) List<MultipartFile> recipeImage
        ) throws Exception {
    
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
    
        @Operation(summary = "recipe list", description = "레시피 공유 게시판 보기")
        @ApiResponses({ 
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @GetMapping(value = "/recipes")
        public ResponseEntity<List<RecipesDto>> recipes(
            @ApiParam(value = "게시판 페이지", required = false) String pageNum
        ) throws Exception {
    
            RecipesDto recipesDto = new RecipesDto(1, "제목입니다.", "이미지경로입니다.", 1, 2, LocalDateTime.now(), 5);
            List<RecipesDto> recipeList = new ArrayList<>();
    
            recipeList.add(recipesDto);
    
            return new ResponseEntity<>(recipeList, HttpStatus.OK);
        }
    
        @Operation(summary = "recipe list", description = "레시피 공유 게시글 상세 보기")
        @ApiResponses({ 
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @GetMapping(value = "/recipe/details")
        public ResponseEntity<List<RecipeDetailsDto>> recipeDefails(
            @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
            @ApiParam(value = "게시판 페이지", required = false) String pageNum
        ) throws Exception {
    
            RecipeCommentDetailsDto recipeCommentDto = new RecipeCommentDetailsDto(1, recipeId, 1, "댓글 작성자", "댓글 내용입니다.");
    
            // List<RecipeCommentDetailsDto> recipeCommentDtoList = new ArrayList<>();
    
            // recipeCommentDtoList.add(recipeCommentDto);
            
            RecipeDetailsDto recipeDto = new RecipeDetailsDto(recipeId, 1, "제목입니다.", "내용입니다", "이미지경로입니다", 5, 7, LocalDateTime.now(), 5);
    
            List<RecipeDetailsDto> recipeDetailsDtoList = new ArrayList<>();
    
            recipeDetailsDtoList.add(recipeDto);
    
            return new ResponseEntity<>(recipeDetailsDtoList, HttpStatus.OK);
        }
    
        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe delete", description = "레시피 공유 게시글 삭제")
        @ApiResponses({ 
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @DeleteMapping(value = "/recipe/{recipeId}/{memberId}")
        public ResponseEntity<String> recipeDelete(
            @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
            @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
        ) throws Exception {
    
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
    
        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe comment write", description = "레시피 공유 게시글 댓글 작성")
        @ApiResponses({ 
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @PostMapping(value = "/recipe/comment/{recipeId}/{memberId}")
        public ResponseEntity<String> recipeCommentWrite(
            @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
            @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId,
            @RequestBody @ApiParam(value = "댓글 내용", required = true) String content // TODO : 리퀘스트 바디 설명 안나옴, 수정필요
        ) throws Exception {
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
    
        @Operation(summary = "recipe comment view", description = "레시피 공유 게시글 댓글 조회")
        @ApiResponses({ 
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @GetMapping(value = "/recipe/comment")
        public ResponseEntity<List<RecipeCommentDetailsDto>> recipeCommentDetails(
            @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
            @ApiParam(value = "댓글 페이지", required = false) String pageNum // string으로 받고 interger로 변환 필요, int로 받으면 null 값일떄 에러남
        ) throws Exception {
            RecipeCommentDetailsDto recipeCommentDetailsDto = new RecipeCommentDetailsDto(1, recipeId, 1, "댓글 작성자", "댓글 내용입니다.");
            List<RecipeCommentDetailsDto> detailsDtos = new ArrayList<>();
            detailsDtos.add(recipeCommentDetailsDto);
            return new ResponseEntity<>(detailsDtos, HttpStatus.OK);
        }
    
        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe comment modify", description = "레시피 공유 게시글 댓글 수정")
        @ApiResponses({ 
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @PatchMapping(value = "/recipe/comment/{recipeId}/{commentId}/{memberId}")
        public ResponseEntity<String> recipeCommentModify(
            @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
            @PathVariable @ApiParam(value = "댓글 시퀀스", required = true) int commentId,
            @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId,
            @RequestBody String content
        ) throws Exception {
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
    
        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe comment delete", description = "레시피 공유 게시글 댓글 삭제")
        @ApiResponses({ 
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @DeleteMapping(value = "/recipe/comment/{recipeId}/{commentId}/{memberId}")
        public ResponseEntity<String> recipeCommentDelete(
            @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
            @PathVariable @ApiParam(value = "댓글 시퀀스", required = true) int commentId,
            @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
        ) throws Exception {
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
    
        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe like", description = "레시피 공유 게시글 좋아요")
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
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
    
        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe like cancle", description = "레시피 공유 게시글 좋아요 취소")
        @ApiResponses({ 
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
                @ApiResponse(responseCode = "404", description = "NOT FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
        })
        @ResponseBody
        @DeleteMapping(value = "/recipe/like/{recipeId}/{recipeLikeId}/{memberId}")
        public ResponseEntity<String> recipeLikeCancle(
            @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
            @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeLikeId,
            @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
        ) throws Exception {
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
    
        @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
        @Operation(summary = "recipe scrap", description = "레시피 공유 게시글 스크랩")
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
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
}
