package com.foodiary.daily.service;

import com.foodiary.auth.service.UserService;
import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.common.s3.S3Service;
import com.foodiary.daily.mapper.DailyMapper;
import com.foodiary.daily.model.*;
import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberDto;
import com.foodiary.daily.model.DailyImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DailyService {

    private final DailyMapper dailyMapper;
    private final MemberMapper memberMapper;
    private final S3Service s3Service;

    @Autowired
    private UserService userService;

    // 하루 식단 게시글 추가
    public void addDaily(DailyWriteRequestDto dailyWriteRequestDto, MultipartFile dailyImage) throws IOException {

        MemberDto member = memberMapper.findByMemberId(dailyWriteRequestDto.getMemberId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        if(member == null) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
        if(dailyImage == null) {
            dailyWriteRequestDto.setWrite(member.getMemberNickName());
            dailyMapper.saveDaily(dailyWriteRequestDto);
        } else {
            fileCheck(dailyImage);
            try {
                HashMap<String, String> fileMap = s3Service.upload(dailyImage, "daily");

                String fileFullName = dailyImage.getOriginalFilename();
                String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
                String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
                dailyWriteRequestDto.setPath(fileMap.get("url"));
                DailyImageDto saveImage = DailyImageDto.builder()
                        .memberId(dailyWriteRequestDto.getMemberId())
                        .originalName(fileName)
                        .originalFullName(fileFullName)
                        .saveName(fileMap.get("serverName"))
                        .path(fileMap.get("url"))
                        .size(dailyImage.getSize())
                        .ext(ext).build();
                dailyWriteRequestDto.setWrite(member.getMemberNickName());
                dailyMapper.saveDaily(dailyWriteRequestDto);
                saveImage.setDailyId(dailyWriteRequestDto.getDailyId());
                dailyMapper.saveImage(saveImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 하루 식단 댓글 추가
    public void addDailyComment(DailyCommentWriteRequestDto dailyCommentWriteRequestDto) {
        verifyDailyPost(dailyCommentWriteRequestDto.getDailyId());

        dailyMapper.saveDailyComment(dailyCommentWriteRequestDto);
    }

    // 하루 식단 게시글 좋아요 
    public void addDailyLike(int memberId, int dailyId) {

        verifyDailyPost(dailyId);
        boolean verifyLike = dailyMapper.findByMemberIdAndDailyId(memberId, dailyId).isEmpty();

        if (!verifyLike) {
            removeDailyLike(dailyId, memberId);
        } else {
            dailyMapper.saveDailyLike(memberId, dailyId);
        }

    }

    // 하루 식단 게시글 스크랩
    public void addDailyScrap(int dailyId, int memberId) {

        boolean verifyDailyScrap = dailyMapper.findByDailyScrap(dailyId, memberId).isEmpty();
        verifyDailyPost(dailyId);
        if(!verifyDailyScrap) {
            throw new BusinessLogicException(ExceptionCode.SCRAP_EXISTS);
        }
        dailyMapper.saveDailyScrap(dailyId, memberId);
    }

    // 하루 식단 게시글 수정
    public void modifyDaily(DailyEditRequestDto dailyEditRequestDto, MultipartFile dailyImage) {
        userService.checkUser(dailyEditRequestDto.getMemberId());
        verifyDailyPost(dailyEditRequestDto.getDailyId());

        DailyDetailsResponseDto verifyDaily = verifyDailyPost(dailyEditRequestDto.getDailyId());

        if(verifyDaily.getDailyPath() != null) {
            s3Service.deleteImage(verifyDaily.getDailyPath());
            dailyMapper.deleteDailyImage(dailyEditRequestDto.getDailyId(), verifyDaily.getDailyPath());
        }

        if(dailyImage == null) {
            dailyMapper.updateDaily(dailyEditRequestDto);
        } else {
            fileCheck(dailyImage);
            try {
                HashMap<String, String> fileMap = s3Service.upload(dailyImage, "daily");

                String fileFullName = dailyImage.getOriginalFilename();
                String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
                String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
                dailyEditRequestDto.setPath(fileMap.get("url"));
                DailyImageDto saveImage = DailyImageDto.builder()
                        .memberId(dailyEditRequestDto.getMemberId())
                        .originalName(fileName)
                        .originalFullName(fileFullName)
                        .saveName(fileMap.get("serverName"))
                        .path(fileMap.get("url"))
                        .size(dailyImage.getSize())
                        .ext(ext).build();
                dailyMapper.updateDaily(dailyEditRequestDto);
                saveImage.setDailyId(dailyEditRequestDto.getDailyId());
                dailyMapper.saveImage(saveImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        dailyMapper.updateDaily(dailyEditRequestDto);
    }


    // 하루 식단 댓글 수정
    public void modifyDailyComment(DailyCommentEditRequestDto dailyCommentEditRequestDto) {
        userService.checkUser(dailyCommentEditRequestDto.getMemberId());
        verifyDailyComment(dailyCommentEditRequestDto.getCommentId());

        dailyMapper.updateDailyComment(dailyCommentEditRequestDto);
    }

    //하루식단 게시판 조회
    public List<DailysResponseDto> findDailys() {
        List<DailysResponseDto> dailys = dailyMapper.findAll();
        return dailys;
    }

    //하루식단 게시글 조회
    public DailyDetailsResponseDto findDaily(int dailyId) {

        //게시글 조회수 업데이트
        dailyMapper.updateDailyView(dailyId);

        DailyDetailsResponseDto dailyResponse = verifyDailyPost(dailyId);

        dailyResponse.setUserCheck(userService.verifyUser(dailyResponse.getMemberId()));

        return dailyResponse;
    }


    // 하루식단 댓글 조회
    public List<DailyCommentDetailsResponseDto> findDailyComments(int dailyId) {
        verifyDailyPost(dailyId);

        return dailyMapper.findAllDailyComment(dailyId);
    }

    
    //하루식단 게시글 좋아요 취소
    public void removeDailyLike(int dailyId, int memberId) {
        dailyMapper.deleteDailyLike(dailyId, memberId);
    }

    //하루식단 게시글 삭제
    public void removeDaily(int dailyId, int memberId) {
        userService.checkUser(memberId);
        DailyDetailsResponseDto verifyDaily = verifyDailyPost(dailyId);

        if(verifyDaily.getDailyPath() != null) {
            s3Service.deleteImage(verifyDaily.getDailyPath());
            dailyMapper.deleteDailyImage(dailyId, verifyDaily.getDailyPath());
        }

        dailyMapper.deleteDaily(dailyId, memberId);
    }


    //하루식단 댓글 삭제
    public void removeDailyComment(int dailyId, int memberId, int commentId) {
        userService.checkUser(memberId);
        verifyDailyComment(commentId);

        dailyMapper.deleteDailyComment(dailyId, memberId, commentId);
    }


    // 하루식단 게시글 스크랩 삭제
    public void removeDailyScrap(int dailyId, int memberId, int scrapId) {
        userService.checkUser(memberId);
        verifyDailyScrap(dailyId, memberId);

        dailyMapper.deleteDailyScrap(dailyId, memberId, scrapId);
    }



    //이미지 확장자 체크
    public void fileCheck(MultipartFile image) {
        String ext = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);

        if(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png")) {
        }
        else {
            throw new BusinessLogicException(ExceptionCode.FILE_BAD_REQUEST);
        }
    }

    //데일리 포스트 유무 체크
    private DailyDetailsResponseDto verifyDailyPost(int dailyId) {
        return dailyMapper.findByDailyId(dailyId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));
    }

    //데일리 스크랩 유무 확인
    private void verifyDailyScrap(int dailyId, int memberId) {
        dailyMapper.findByDailyScrap(dailyId, memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.SCRAP_NOT_FOUND));
    }

    // 데일리 코멘트 유무 확인
    private void verifyDailyComment(int commentId) {
        dailyMapper.findByDailyComment(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
    }


}
