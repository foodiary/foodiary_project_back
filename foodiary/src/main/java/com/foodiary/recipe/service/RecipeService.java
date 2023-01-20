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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
        userService.checkUser(recipeWriteRequestDto.getMemberId());
        MemberDto member = memberMapper.findByMemberId(recipeWriteRequestDto.getMemberId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));


        // 파일 url 담을 리스트
        List<String> fileUrlList = new ArrayList<>();

        List<RecipeImageDto> saveImageList = new ArrayList<>();

        if(recipeImage.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.IMAGE_BAD_REQUEST);
        } else {
            for(int i=0; i < recipeImage.size(); i++) {
                MultipartFile file = recipeImage.get(i);
                fileCheck(file);
                HashMap<String, String> fileMap = s3Service.upload(file, "recipe");
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
            userService.verifySave(recipeMapper.saveRecipe(recipeWriteRequestDto));

            int recipeId = recipeMapper.findByRecipeId1(recipeWriteRequestDto.getPath1())
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));

            for (int i = 0; i < saveImageList.size(); i++) {
                saveImageList.get(i).setRecipeId(recipeId);
                userService.verifySave(recipeMapper.saveImage(saveImageList.get(i)));
            }

            recipeWriteRequestDto.getIngredients()
                    .forEach(m -> {
                        m.setRecipeId(recipeWriteRequestDto.getRecipeId());
                        userService.verifySave(recipeMapper.saveIngredient(m));
                    });
        }
    }

    // 레시피 식단 댓글 추가
    public void addRecipeComment(RecipeCommentWriteRequestDto recipeCommentWriteRequestDto) {
        userService.checkUser(recipeCommentWriteRequestDto.getMemberId());
        verifyRecipePost(recipeCommentWriteRequestDto.getRecipeId());
        MemberDto member = memberMapper.findByMemberId(recipeCommentWriteRequestDto.getMemberId()).get();
        recipeCommentWriteRequestDto.setWriter(member.getMemberNickName());

        userService.verifySave(recipeMapper.saveRecipeComment(recipeCommentWriteRequestDto));
    }

    
    // 레시피 식단 게시글 좋아요
    public void addRecipeLike(int memberId, int recipeId) {
        userService.checkUser(memberId);
        boolean verifyLike = recipeMapper.findByMemberIdAndRecipeId(memberId, recipeId).isEmpty();

        if (!verifyLike) {
            removeRecipeLike(recipeId, memberId);
        } else {
            userService.verifySave(recipeMapper.saveRecipeLike(memberId, recipeId));
        }
    }

    // 레시피 식단 게시글 스크랩
    public void addRecipeScrap(int recipeId, int memberId) {
        userService.checkUser(memberId);
        verifyRecipePost(recipeId);
        if(!recipeMapper.findByRecipeScrap(recipeId, memberId).isEmpty()) {
            removeRecipeScrap(memberId, recipeId);
        } else {
            userService.verifySave(recipeMapper.saveRecipeScrap(recipeId, memberId));
        }

    }



    // TODO : 파일 수정 어떻게 할것인가? 
    // 레시피 식단 게시글 수정
    public void modifyRecipe(RecipeEditRequestDto recipeEditRequestDto, List<MultipartFile> recipeImage) throws IOException {
        userService.checkUser(recipeEditRequestDto.getMemberId());
        verifyRecipePost(recipeEditRequestDto.getRecipeId());
            memberMapper.findByMemberId(recipeEditRequestDto.getMemberId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

            // 이미지값이 있을때, 싹다 교체
            // 재료 지우고
            recipeMapper.deleteIngredient(recipeEditRequestDto.getRecipeId());
            if(recipeEditRequestDto.getIngredients().size() != 0) {
                recipeEditRequestDto.getIngredients()
                        .forEach(m -> {
                            m.setRecipeId(recipeEditRequestDto.getRecipeId());
                            userService.verifySave(recipeMapper.saveIngredient(m));
                        });
            }

            // path2YN Y 이미지를 변경하겠다는 거잖아요, multipart null로 왔어요, 기존 이미지 있을때
            // 기존 이미지 삭제

            // path2YN Y 이미지를 변경하겠다는 거잖아요, multipart 값이 있으면로 왔어요, 기존 이미지 있을때
            // 기존 이미지 삭제, 새로운 이미지 추가

            // path2YN Y 이미지를 변경하겠다는 거잖아요, multipart 값이 있으면로 왔어요, 기존 이미지 없을때
            // 새로운 이미지 추가
            

            // 기존 게시물의 파일 경로 삭제 및 S3 파일 삭제
            RecipeDetailsResponseDto verifyRecipe = verifyRecipePost(recipeEditRequestDto.getRecipeId());

            // 이미지 3개 다 업데이트할때의 경우
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

            // 만약에 기존이미지1, 2가 있고, 제가 2만 교체 한다고 했을때
            // image2만 들어오겠죠?


            // 새로 첨부 받은 이미지 파일로 업데이트
            for (int i = 0; i < recipeImage.size(); i++) {
                MultipartFile file = recipeImage.get(i);
                fileCheck(file);
                HashMap<String, String> fileMap = s3Service.upload(file, "recipe");
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
                userService.verifySave(recipeMapper.saveImage(saveImage));
                log.info("파일이 업로드 되었습니다.");
            }

            // 게시글 DB에 이미지 경로 저장
            recipeEditRequestDto.setPath1(fileUrlList.get(0));
            if(fileUrlList.size() > 1) {
                recipeEditRequestDto.setPath2(fileUrlList.get(1));
            }
            if (fileUrlList.size() > 2) {
                recipeEditRequestDto.setPath3(fileUrlList.get(2));
            }
            userService.verifyUpdate(recipeMapper.updateRecipe(recipeEditRequestDto));
    }


    // 레시피 식단 댓글 수정
    public void modifyRecipeComment(RecipeCommentEditRequestDto recipeCommentEditRequestDto) {

        userService.checkUser(recipeCommentEditRequestDto.getMemberId());
        verifyRecipeComment(recipeCommentEditRequestDto.getCommentId());

        userService.verifyUpdate(recipeMapper.updateRecipeComment(recipeCommentEditRequestDto));
    }


    //레시피 게시판 조회
    public List<RecipesResponseDto> findRecipes() {
        List<RecipesResponseDto> recipes = recipeMapper.findAll();
        if (recipes.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        return recipes;
    }

    //레시피 게시판 조회 (일, 주, 월)
    public List<RecipesResponseDto> findCreateRecipes(LocalDateTime start, LocalDateTime end) {
        List<RecipesResponseDto> recipes = recipeMapper.findCreateAll(start, end);
        if (recipes.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        return recipes;
    }

    //레시피 게시글 조회
    public RecipeDetailsResponseDto findRecipe(int recipeId) {

        recipeMapper.updateRecipeView(recipeId);
        RecipeDetailsResponseDto recipeResponse = verifyRecipePost(recipeId);
        recipeResponse.setIngredient(recipeMapper.findAllIngredient(recipeId));

        return recipeResponse;
    }

    //레시피 게시판 최신글 10개 조회
    public List<RecipesResponseDto> findTopRecipes() {
        List<RecipesResponseDto> recipes = recipeMapper.findTopRecipes();
        if (recipes.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        return recipes;
    }

    // 레시피 댓글 조회
    public List<RecipeCommentDetailsResponseDto> findRecipeComments(int recipeId) {
        verifyRecipePost(recipeId);
        List<RecipeCommentDetailsResponseDto> recipeComments = recipeMapper.findAllRecipeComment(recipeId);
        if(recipeComments.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND);
        }
        return recipeComments;
    }

    
    //레시피 게시글 좋아요 취소
    public void removeRecipeLike(int recipeId, int memberId) {
        userService.checkUser(memberId);

        userService.verifyDelete(recipeMapper.deleteRecipeLike(recipeId));
    }

    //레시피 게시글 삭제
    public void removeRecipe(int recipeId, int memberId) {
        userService.checkUser(memberId);
        RecipeDetailsResponseDto verifyRecipe = verifyRecipePost(recipeId);
        List<RecipeImageDto> recipeImage = recipeMapper.findImageByRecipeId(recipeId);

        if(verifyRecipe.getRecipePath1() != null) {

            s3Service.deleteImage( recipeImage.get(0).getSaveName());
            userService.verifyDelete(recipeMapper.deleteRecipeImage(recipeId, verifyRecipe.getRecipePath1()));
        }
        if(verifyRecipe.getRecipePath2() != null) {
            s3Service.deleteImage( recipeImage.get(1).getSaveName());
            userService.verifyDelete(recipeMapper.deleteRecipeImage(recipeId, verifyRecipe.getRecipePath2()));
        }
        if(verifyRecipe.getRecipePath3() != null) {
            s3Service.deleteImage( recipeImage.get(2).getSaveName());
            userService.verifyDelete(recipeMapper.deleteRecipeImage(recipeId, verifyRecipe.getRecipePath3()));
        }

        userService.verifyDelete(recipeMapper.deleteRecipe(recipeId));
    }


    //레시피 댓글 삭제
    public void removeRecipeComment(int recipeId, int memberId, int commentId) {
        userService.checkUser(memberId);
        verifyRecipeComment(commentId);

        userService.verifyDelete(recipeMapper.deleteRecipeComment(recipeId, commentId, memberId));
    }



    // 래시피 게시글 스크랩 삭제
    public void removeRecipeScrap(int memberId, int recipeId) {

        userService.verifyDelete(recipeMapper.deleteRecipeScrap(memberId, recipeId));
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
