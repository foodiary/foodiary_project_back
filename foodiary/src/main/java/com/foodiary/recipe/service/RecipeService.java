package com.foodiary.recipe.service;

import com.foodiary.auth.service.UserService;
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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeMapper recipeMapper;
    private final MemberMapper memberMapper;
    private final S3Service s3Service;
    private final UserService userService;

    // 레시피 게시글 추가
    public void addRecipe(RecipeWriteRequestDto recipeWriteRequestDto, List<MultipartFile> recipeImage) throws IOException {

        MemberDto member = memberMapper.findByMemberId(recipeWriteRequestDto.getMemberId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        if(recipeImage == null) {
            throw new BusinessLogicException(ExceptionCode.IMAGE_BAD_REQUEST);
        } else {
            // 파일 url 담을 리스트
            List<String> fileUrlList = new ArrayList<>();

            List<RecipeImageDto> saveImageList = new ArrayList<>();

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
                saveImageList.add(saveImage);
            }
            //파일 갯수
            log.info(Integer.toString(fileUrlList.size()));

            // 게시글 경로
            recipeWriteRequestDto.setPath1(fileUrlList.get(0));

            if(fileUrlList.size()>1) {
                recipeWriteRequestDto.setPath2(fileUrlList.get(1));
                if(fileUrlList.size()>2) {
                    recipeWriteRequestDto.setPath3(fileUrlList.get(2));
                }
            }

            recipeWriteRequestDto.setWriter(member.getMemberNickName());
            recipeMapper.saveRecipe(recipeWriteRequestDto);

            int recipeId = recipeMapper.findByRecipeId1(recipeWriteRequestDto.getPath1())
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));

            for (int i = 0; i < saveImageList.size(); i++) {
                saveImageList.get(i).setRecipeId(recipeId);
                recipeMapper.saveImage(saveImageList.get(i));
            }
        }
    }

    // 레시피 식단 댓글 추가
    public void addRecipeComment(RecipeCommentWriteRequestDto recipeCommentWriteRequestDto) {
        verifyRecipePost(recipeCommentWriteRequestDto.getRecipeId());

        recipeMapper.saveRecipeComment(recipeCommentWriteRequestDto);
    }

    // 레시피 식단 게시글 좋아요
    public void addRecipeLike(int memberId, int recipeId) {
        verifyRecipePost(recipeId);
        boolean verifyLike = recipeMapper.findByMemberIdAndRecipeId(memberId, recipeId).isEmpty();

        if (!verifyLike) {
            removeRecipeLike(recipeId, memberId);
        } else {
            recipeMapper.saveRecipeLike(memberId, recipeId);
        }
    }

    // 레시피 식단 게시글 스크랩
    public void addRecipeScrap(int recipeId, int memberId) {
        recipeMapper.findByRecipeScrap(recipeId, memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.SCRAP_EXISTS));
        verifyRecipePost(recipeId);

        recipeMapper.saveRecipeScrap(recipeId, memberId);
    }



    // 레시피 식단 게시글 수정
    public void modifyRecipe(RecipeEditRequestDto recipeEditRequestDto, List<MultipartFile> recipeImage) throws IOException {
        userService.checkUser(recipeEditRequestDto.getMemberId());
        verifyRecipePost(recipeEditRequestDto.getRecipeId());
            memberMapper.findByMemberId(recipeEditRequestDto.getMemberId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        if(recipeImage == null) {
            throw new BusinessLogicException(ExceptionCode.IMAGE_BAD_REQUEST);
        } else {

            // 기존 게시물의 파일 경로 삭제 및 S3 파일 삭제
            RecipeDetailsResponseDto verifyRecipe = verifyRecipePost(recipeEditRequestDto.getRecipeId());

            if(verifyRecipe.getRecipePath1() != null) {
                s3Service.deleteImage(verifyRecipe.getRecipePath1());
                recipeMapper.deleteRecipeImage(recipeEditRequestDto.getRecipeId(), verifyRecipe.getRecipePath1());
            }
            if(verifyRecipe.getRecipePath2() != null) {
                s3Service.deleteImage(verifyRecipe.getRecipePath2());
                recipeMapper.deleteRecipeImage(recipeEditRequestDto.getRecipeId(), verifyRecipe.getRecipePath2());
            }
            if(verifyRecipe.getRecipePath3() != null) {
                s3Service.deleteImage(verifyRecipe.getRecipePath3());
                recipeMapper.deleteRecipeImage(recipeEditRequestDto.getRecipeId(), verifyRecipe.getRecipePath3());
            }

            // 파일 url 담을 리스트
            List<String> fileUrlList = new ArrayList<>();


            // 새로 첨부 받은 이미지 파일로 업데이트
            for (int i = 0; i < recipeImage.size(); i++) {
                MultipartFile file = recipeImage.get(i);
                fileCheck(file);
                HashMap<String, String> fileMap = s3Service.upload(file, "daily");
                fileUrlList.add(fileMap.get("url"));

                String fileFullName = file.getOriginalFilename();
                String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
                String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
                RecipeImageDto saveImage = RecipeImageDto.builder()
                        .memberId(recipeEditRequestDto.getMemberId())
                        .originalName(fileName)
                        .originalFullName(fileFullName)
                        .saveName(fileMap.get("serverName"))
                        .path(fileMap.get("url"))
                        .size(file.getSize())
                        .ext(ext).build();
                saveImage.setRecipeId(recipeEditRequestDto.getRecipeId());
                recipeMapper.saveImage(saveImage);
            }

            // 게시글 DB에 이미지 경로 저장
            recipeEditRequestDto.setPath1(fileUrlList.get(0));
            if(fileUrlList.size() > 1) {
                recipeEditRequestDto.setPath2(fileUrlList.get(1));
            } else if (fileUrlList.size() > 2) {
                recipeEditRequestDto.setPath2(fileUrlList.get(2));
            }

            recipeMapper.updateRecipe(recipeEditRequestDto);
        }
    }


    // 레시피 식단 댓글 수정
    public void modifyRecipeComment(RecipeCommentEditRequestDto recipeCommentEditRequestDto) {

        userService.checkUser(recipeCommentEditRequestDto.getMemberId());
        verifyRecipeComment(recipeCommentEditRequestDto.getCommentId());

        recipeMapper.updateRecipeComment(recipeCommentEditRequestDto);
    }

    //레시피 게시판 조회
    public List<RecipesResponseDto> findRecipes() {
        List<RecipesResponseDto> recipes = recipeMapper.findAll();
        return recipes;
    }

    //레시피 게시글 조회
    public RecipeDetailsResponseDto findRecipe(int recipeId) {

        recipeMapper.updateRecipeView(recipeId);
        RecipeDetailsResponseDto recipeResponse = verifyRecipePost(recipeId);

        recipeResponse.setUserCheck(userService.verifyUser(recipeResponse.getMemberId()));

        return recipeResponse;
    }

    // 레시피 댓글 조회
    public List<RecipeCommentDetailsResponseDto> findRecipeComments(int recipeId) {
        verifyRecipePost(recipeId);

        return recipeMapper.findAllRecipeComment(recipeId);
    }

    
    //레시피 게시글 좋아요 취소
    public void removeRecipeLike(int recipeId, int memberId) {

        recipeMapper.deleteRecipeLike(recipeId, memberId);
    }

    //레시피 게시글 삭제
    public void removeRecipe(int recipeId, int memberId) {
        userService.checkUser(memberId);
        RecipeDetailsResponseDto verifyRecipe = verifyRecipePost(recipeId);

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
        userService.checkUser(memberId);
        verifyRecipeComment(commentId);

        recipeMapper.deleteRecipeComment(recipeId, memberId, commentId);
    }



    // 래시피 게시글 스크랩 삭제
    public void removeRecipeScrap(int recipeId, int memberId, int scrapId) {
        userService.checkUser(memberId);
        recipeMapper.findByRecipeScrap(recipeId, memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.SCRAP_NOT_FOUND));

        recipeMapper.deleteRecipeScrap(recipeId, memberId, scrapId);
    }



    public void fileCheck(MultipartFile image) {
        String ext = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);

        if(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png")) {
        }
        else {
            throw new BusinessLogicException(ExceptionCode.FILE_BAD_REQUEST);
        }
    }


    // 레시피 포스트 유무 확인
    private RecipeDetailsResponseDto verifyRecipePost(int recipeId) {
        return recipeMapper.findByRecipeId(recipeId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));
    }

    //레시피 코멘트 유무 확인
    private void verifyRecipeComment(int commentId) {
        recipeMapper.findByRecipeComment(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
    }


}
