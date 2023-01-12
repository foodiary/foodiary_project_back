package com.foodiary.daily.service;

import com.foodiary.auth.service.UserService;
import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.common.s3.S3Service;
import com.foodiary.daily.mapper.DailyMapper;
import com.foodiary.daily.model.*;
import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spi.service.contexts.SecurityContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DailyService {

    private final DailyMapper dailyMapper;
    private final MemberMapper memberMapper;
    private final S3Service s3Service;
    private final UserService userService;
    

    // 하루 식단 게시글 추가
    public void addDaily(DailyWriteRequestDto dailyWriteRequestDto, MultipartFile dailyImage) throws IOException {

        userService.checkUser(dailyWriteRequestDto.getMemberId());
        MemberDto member = memberMapper.findByMemberId(dailyWriteRequestDto.getMemberId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        if(dailyImage == null || dailyImage.getOriginalFilename().equals("")) {
            throw new BusinessLogicException(ExceptionCode.IMAGE_BAD_REQUEST);
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
                userService.verifySave(dailyMapper.saveDaily(dailyWriteRequestDto));
                saveImage.setDailyId(dailyWriteRequestDto.getDailyId());
                userService.verifySave(dailyMapper.saveImage(saveImage));
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
    }

    // 하루 식단 댓글 추가
    public void addDailyComment(DailyCommentWriteRequestDto dailyCommentWriteRequestDto) {
        userService.checkUser(dailyCommentWriteRequestDto.getMemberId());
        MemberDto member = memberMapper.findByMemberId(dailyCommentWriteRequestDto.getMemberId()).get();
        dailyCommentWriteRequestDto.setWriter(member.getMemberNickName());

        userService.verifySave(dailyMapper.saveDailyComment(dailyCommentWriteRequestDto));
    }

    // 하루 식단 게시글 좋아요 
    public void addDailyLike(int memberId, int dailyId) {
        userService.checkUser(memberId);

        boolean verifyLike = dailyMapper.findByMemberIdAndDailyId(memberId, dailyId).isEmpty();

        if (!verifyLike) {
            removeDailyLike(dailyId);
        } else {
            userService.verifySave(dailyMapper.saveDailyLike(memberId, dailyId));
        }

    }

    // 하루 식단 게시글 스크랩
    public void addDailyScrap(int dailyId, int memberId) {
        userService.checkUser(memberId);

        if(!dailyMapper.findByDailyScrap(dailyId, memberId).isEmpty()) {
            removeDailyScrap(dailyId, memberId);
        } else {
            userService.verifySave(dailyMapper.saveDailyScrap(dailyId, memberId));
        }

    }


    // 하루 식단 게시글 수정
    public void modifyDaily(DailyEditRequestDto dailyEditRequestDto, MultipartFile dailyImage) {
        userService.checkUser(dailyEditRequestDto.getMemberId());

        DailyDetailsResponseDto verifyDaily = verifyDailyPost(dailyEditRequestDto.getDailyId());

        if(verifyDaily.getDailyPath() != null && dailyImage != null) {
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
        userService.verifyUpdate(dailyMapper.updateDaily(dailyEditRequestDto));
    }


    // 하루 식단 댓글 수정
    public void modifyDailyComment(DailyCommentEditRequestDto dailyCommentEditRequestDto) {
        userService.checkUser(dailyCommentEditRequestDto.getMemberId());

        userService.verifyUpdate(dailyMapper.updateDailyComment(dailyCommentEditRequestDto));
    }

    //하루식단 게시판 조회
    public List<DailysResponseDto> findDailys() {
        List<DailysResponseDto> dailys = dailyMapper.findAll();
        if(dailys.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        return dailys;
    }

    //하루식단 게시판 조회(월, 주, 일)
    public List<DailysResponseDto> findCreateDailys(LocalDateTime start,  LocalDateTime end) {
        List<DailysResponseDto> dailys = dailyMapper.findAllCreate(start, end);
        if(dailys.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        return dailys;
    }


    //하루식단 게시판 최신글 10개 조회
    public List<DailysResponseDto> findTopDailys() {
        List<DailysResponseDto> dailys = dailyMapper.findTopDaily();
        if(dailys.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        return dailys;
    }



    //하루식단 게시글 조회
    public DailyDetailsResponseDto findDaily(int dailyId) {

        //게시글 조회수 업데이트
        userService.verifyUpdate(dailyMapper.updateDailyView(dailyId));

        return verifyDailyPost(dailyId);
    }



    // 하루식단 댓글 조회
    public List<DailyCommentDetailsResponseDto> findDailyComments(int dailyId) {
        verifyDailyPost(dailyId);

        List<DailyCommentDetailsResponseDto> commentList = dailyMapper.findAllDailyComment(dailyId);
        if (commentList.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND);
        }
        return commentList;
    }

    
    //하루식단 게시글 좋아요 취소
    public void removeDailyLike(int dailyId) {
        dailyMapper.deleteDailyLike(dailyId);
    }

    //하루식단 게시글 삭제
    public void removeDaily(int dailyId, int memberId) {
        userService.checkUser(memberId);
        DailyDetailsResponseDto verifyDaily = verifyDailyPost(dailyId);
        DailyImageDto dailyImageDto = dailyMapper.findImageByDailyId(dailyId).get();

        if(verifyDaily.getDailyPath() != null) {
            String url = "daily/" + dailyImageDto.getSaveName();
            s3Service.deleteImage(url);
            dailyMapper.deleteDailyImage(dailyId, verifyDaily.getDailyPath());
        }

        userService.verifyDelete(dailyMapper.deleteDaily(dailyId));
    }


    //하루식단 댓글 삭제
    public void removeDailyComment(int dailyId, int memberId, int commentId) {
        userService.checkUser(memberId);

        userService.verifyDelete(dailyMapper.deleteDailyComment(dailyId, commentId));
    }


    // 하루식단 게시글 스크랩 삭제
    public void removeDailyScrap(int dailyId, int memberId) {
        userService.verifyDelete(dailyMapper.deleteDailyScrap(dailyId, memberId));
    }



    //이미지 확장자 체크제
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
}
