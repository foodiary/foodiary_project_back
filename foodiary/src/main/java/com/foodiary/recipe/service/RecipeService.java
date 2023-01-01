package com.foodiary.recipe.service;

import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.common.s3.S3Service;
import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberDto;
import com.foodiary.recipe.mapper.RecipeMapper;
import com.foodiary.recipe.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeMapper recipeMapper;
    private final MemberMapper memberMapper;
    private final S3Service s3Service;

    // 레시피 게시글 추가
    public void addRecipe(RecipeWriteRequestDto recipeWriteRequestDto, List<MultipartFile> recipeImage) throws IOException {

        MemberDto member = memberMapper.findByMemberId(recipeWriteRequestDto.getMemberId());
        if(member == null) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
        if(recipeImage == null) {
            recipeWriteRequestDto.setWrite(member.getMemberNickName());
            recipeMapper.saveRecipe(recipeWriteRequestDto);
        } else {
            // 파일 url 담을 리스트
            List<String> fileUrlList = new ArrayList<>();

            for(int i=0; i < recipeImage.size(); i++) {
                MultipartFile file = recipeImage.get(i);
                fileCheck(file);
                HashMap<String, String> fileMap = s3Service.upload(file, "daily");
                fileUrlList.add(fileMap.get("url"));

                    String fileFullName = file.getOriginalFilename();
                    String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
                    String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
                    RecipeImageDto saveImage = RecipeImageDto.builder()
                            .memberId(recipeWriteRequestDto.getMemberId())
                            .originalName(fileName)
                            .originalFullName(fileFullName)
                            .saveName(fileMap.get("serverName"))
                            .path(fileMap.get("url"))
                            .size(file.getSize())
                            .ext(ext).build();
                    saveImage.setRecipeId(recipeWriteRequestDto.getRecipeId());
                    recipeMapper.saveImage(saveImage);
            }
            //파일 갯수
            log.info(Integer.toString(fileUrlList.size()));

            switch(fileUrlList.size()){
                case 1 :
                    recipeWriteRequestDto.setPath1(fileUrlList.get(0));
                    break;
                case 2 :
                    recipeWriteRequestDto.setPath1(fileUrlList.get(0));
                    recipeWriteRequestDto.setPath2(fileUrlList.get(1));
                    break;
                case 3 :
                    recipeWriteRequestDto.setPath1(fileUrlList.get(0));
                    recipeWriteRequestDto.setPath2(fileUrlList.get(1));
                    recipeWriteRequestDto.setPath3(fileUrlList.get(2));
                    break;
            }
            recipeWriteRequestDto.setWrite(member.getMemberNickName());
            recipeMapper.saveRecipe(recipeWriteRequestDto);

            switch(fileUrlList.size()){
                case 1 :
                    // 이미지테이블의 dailyId 업데이
                    int recipeId01 = recipeMapper.findByRecipeId1(recipeWriteRequestDto.getPath1());
                    recipeMapper.updateRecipeId(recipeId01, recipeWriteRequestDto.getPath1());
                    break;
                case 2 :
                    int recipeId001 = recipeMapper.findByRecipeId1(recipeWriteRequestDto.getPath1());
                    int recipeId002 = recipeMapper.findByRecipeId2(recipeWriteRequestDto.getPath2());
                    recipeMapper.updateRecipeId(recipeId001, recipeWriteRequestDto.getPath1());
                    recipeMapper.updateRecipeId(recipeId002, recipeWriteRequestDto.getPath2());
                    break;
                case 3 :
                    int recipeId0001 = recipeMapper.findByRecipeId1(recipeWriteRequestDto.getPath1());
                    int recipeId0002 = recipeMapper.findByRecipeId2(recipeWriteRequestDto.getPath2());
                    int recipeId0003 = recipeMapper.findByRecipeId3(recipeWriteRequestDto.getPath3());
                    recipeMapper.updateRecipeId(recipeId0001, recipeWriteRequestDto.getPath1());
                    recipeMapper.updateRecipeId(recipeId0002, recipeWriteRequestDto.getPath2());
                    recipeMapper.updateRecipeId(recipeId0003, recipeWriteRequestDto.getPath3());
                    break;
            }
        }
    }

    // 레시피 식단 댓글 추가
    public void addRecipeComment(RecipeCommentWriteRequestDto recipeCommentWriteRequestDto) {
        RecipeDetailsResponseDto verifyRecipe = recipeMapper.findByRecipeId(recipeCommentWriteRequestDto.getRecipeId());
        if(verifyRecipe == null) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        recipeMapper.saveRecipeComment(recipeCommentWriteRequestDto);
    }

    // 레시피 식단 게시글 좋아요
    public void addRecipeLike(int memberId, int recipeId) {
        RecipeDetailsResponseDto verifyRecipe = recipeMapper.findByRecipeId(recipeId);
        Integer verifyLike = recipeMapper.findByMemberIdAndRecipeId(memberId, recipeId);
        if(verifyRecipe == null) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        } else if (verifyLike != null) {
            throw new BusinessLogicException(ExceptionCode.LIKE_EXISTS);
        }
        recipeMapper.saveRecipeLike(memberId, recipeId);
    }

    // 레시피 식단 게시글 스크랩
    public void addRecipeScrap(int recipeId, int memberId) {
        Integer verifyRecipeScrap = recipeMapper.findByRecipeScrap(recipeId, memberId);
        RecipeDetailsResponseDto verifyRecipe = recipeMapper.findByRecipeId(recipeId);
        if(verifyRecipeScrap != null) {
            throw new BusinessLogicException(ExceptionCode.SCRAP_EXISTS);
        }
        if (verifyRecipe == null) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        recipeMapper.saveRecipeScrap(recipeId, memberId);
    }

    // 레시피 식단 게시글 수정
    public void modifyRecipe(RecipeEditRequestDto recipeEditRequestDto, List<MultipartFile> recipeImage) {
        RecipeDetailsResponseDto verifyRecipe = recipeMapper.findByRecipeId(recipeEditRequestDto.getRecipeId());
        MemberDto member = memberMapper.findByMemberId(recipeEditRequestDto.getMemberId());
        if(member == null) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
        if (verifyRecipe == null) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
//        int fileCount = recipeImage.size();
//        verifyRecipe.
        recipeMapper.updateRecipe(recipeEditRequestDto);
    }


    // 레시피 식단 댓글 수정
    public void modifyRecipeComment(RecipeCommentEditRequestDto recipeCommentEditRequestDto) {

        RecipeCommentDetailsResponseDto verifyRecipeComment = recipeMapper.findByRecipeComment(recipeCommentEditRequestDto.getCommentId());
        String verifyImage = recipeMapper.findByRecipeImage(recipeCommentEditRequestDto.getRecipeId());
        if (verifyRecipeComment == null) {
            throw new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND);
        }
        recipeMapper.updateRecipeComment(recipeCommentEditRequestDto);
    }

    //레시피 게시판 조회
    public List<RecipesResponseDto> findRecipes() {
        List<RecipesResponseDto> recipes = recipeMapper.findAll();
        return recipes.stream()
                    .map(d -> {
                        d.setPath(recipeMapper.findByRecipeImage(d.getRecipeId()));
                        return d;})
                    .collect(Collectors.toList());
    }

    //레시피 게시글 조회
    public RecipeDetailsResponseDto findRecipe(int recipeId) {

        RecipeDetailsResponseDto verifyRecipe = recipeMapper.findByRecipeId(recipeId);
        if(verifyRecipe == null) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        //좋아요 수
        int recipeLikeCount = recipeMapper.findAllRecipeId(recipeId).size();

        //댓글 수
        int recipeCommentCount = recipeMapper.findAllRecipeComment(recipeId).size();

        //이미지 경로
        String recipeImage = recipeMapper.findByRecipeImage(recipeId);
        recipeMapper.updateRecipeView(recipeId);

        RecipeDetailsResponseDto recipeResponse = recipeMapper.findByRecipeId(recipeId);
        recipeResponse.setLike(recipeLikeCount);
        recipeResponse.setComment(recipeCommentCount);
        recipeResponse.setRecipePath1(recipeImage);

        return recipeResponse;
    }

    // 레시피 댓글 조회
    public List<RecipeCommentDetailsResponseDto> findRecipeComments(int recipeId) {
        RecipeDetailsResponseDto verifyRecipe = recipeMapper.findByRecipeId(recipeId);
        if(verifyRecipe == null) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        return recipeMapper.findAllRecipeComment(recipeId);
    }

    
    //레시피 게시글 좋아요 취소
    public void removeRecipeLike(int recipeLikeId) {
        Integer result = recipeMapper.findByRecipeLikeId(recipeLikeId);
        if(result == null) {
            throw new BusinessLogicException(ExceptionCode.LIKE_NOT_FOUND);
        }
        recipeMapper.deleteRecipeLike(recipeLikeId);
    }

    //레시피 게시글 삭제
    public void removeRecipe(int recipeId, int memberId) {
        RecipeDetailsResponseDto verifyRecipe = recipeMapper.findByRecipeId(recipeId);
        if(verifyRecipe == null) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        if(verifyRecipe.getRecipePath1() != null) {
            s3Service.deleteImage(verifyRecipe.getRecipePath1());
            recipeMapper.deleteRecipeImage(recipeId, verifyRecipe.getRecipePath1());
        }
        if(verifyRecipe.getRecipePath2() != null) {
            s3Service.deleteImage(verifyRecipe.getRecipePath2());
            recipeMapper.deleteRecipeImage(recipeId, verifyRecipe.getRecipePath2());
        }
        if(verifyRecipe.getRecipePath3() != null) {
            s3Service.deleteImage(verifyRecipe.getRecipePath3());
            recipeMapper.deleteRecipeImage(recipeId, verifyRecipe.getRecipePath3());
        }

        recipeMapper.deleteRecipe(recipeId, memberId);
    }


    //레시피 댓글 삭제
    public void removeRecipeComment(int recipeId, int memberId, int commentId) {
        RecipeCommentDetailsResponseDto verifyComment = recipeMapper.findByRecipeComment(commentId);
        if(verifyComment == null){
            throw new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND);
        }
        recipeMapper.deleteRecipeComment(recipeId, memberId, commentId);
    }

    // 래시피 게시글 스크랩 삭제
    public void removeRecipeScrap(int recipeId, int memberId, int scrapId) {
        Integer verifyRecipeScrap = recipeMapper.findByRecipeScrap(recipeId, memberId);
        if(verifyRecipeScrap == null) {
            throw new BusinessLogicException(ExceptionCode.SCRAP_NOT_FOUND);
        }
        recipeMapper.deleteRecipeScrap(recipeId, memberId, scrapId);
    }

    // DB에 저장할 이미지 DTO 생성
    private RecipeImageDto of(int recipeId, int memberId, String originalName, String originalPullName, String saveName, String path, long size, String ext) {
        return RecipeImageDto.builder()
                    .recipeId(recipeId)
                    .memberId(memberId)
                    .originalFullName(originalPullName)
                    .originalName(originalName)
                    .saveName(saveName)
                    .path(path)
                    .size(size)
                    .ext(ext)
                    .build();
    }

    public void fileCheck(MultipartFile image) {
        String ext = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);

        if(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png")) {
        }
        else {
            throw new BusinessLogicException(ExceptionCode.FILE_BAD_REQUEST);
        }
    }


}
