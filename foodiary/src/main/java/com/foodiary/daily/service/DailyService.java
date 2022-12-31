package com.foodiary.daily.service;

import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.common.s3.Image;
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

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DailyService {

    private final DailyMapper dailyMapper;
    private final MemberMapper memberMapper;
    private final S3Service s3Service;

    // 하루 식단 게시글 추가
    public void addDaily(DailyWriteRequestDto dailyWriteRequestDto, MultipartFile dailyImage) throws IOException {
        dailyMapper.saveDaily(dailyWriteRequestDto);
        System.out.println(dailyWriteRequestDto.getDailyId());

        String originalFilename = dailyImage.getOriginalFilename();
        String saveName = UUID.randomUUID().toString();
        String contentType = dailyImage.getContentType();
        long size = dailyImage.getSize();

        MemberDto member = memberMapper.findById(dailyWriteRequestDto.getMemberId());
        if(member == null) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
        dailyWriteRequestDto.setWrite(member.getMemberNickName());

        dailyMapper.saveDaily(dailyWriteRequestDto);
        DailyImageDto imageDto = of(dailyWriteRequestDto.getDailyId(),
                dailyWriteRequestDto.getMemberId(),
                "dddddd",
                "ssssss",
                "dddddddddddddddddddddddt554547476474747",
                "ddddddddddd",
                100,
                "jpg");
        dailyMapper.saveImage(imageDto);
    }

    // 하루 식단 댓글 추가
    public void addDailyComment(DailyCommentWriteRequestDto dailyCommentWriteRequestDto) {
        DailyDetailsResponseDto verifyDaily = dailyMapper.findByDailyId(dailyCommentWriteRequestDto.getDailyId());
        if(verifyDaily == null) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        dailyMapper.saveDailyComment(dailyCommentWriteRequestDto);
    }

    // 하루 식단 게시글 좋아요 
    public void addDailyLike(int memberId, int dailyId) {
        DailyDetailsResponseDto verifyDaily = dailyMapper.findByDailyId(dailyId);
        Integer verifyLike = dailyMapper.findByMemberIdAndDailyId(memberId, dailyId);
        if(verifyDaily == null) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        } else if (verifyLike != null) {
            throw new BusinessLogicException(ExceptionCode.LIKE_EXISTS);
        }
        dailyMapper.saveDailyLike(memberId, dailyId);
    }

    // 하루 식단 게시글 스크랩
    public void addDailyScrap(int dailyId, int memberId) {
        Integer verifyDailyScrap = dailyMapper.findByDailyScrap(dailyId, memberId);
        DailyDetailsResponseDto verifyDaily = dailyMapper.findByDailyId(dailyId);
        if(verifyDailyScrap != null) {
            throw new BusinessLogicException(ExceptionCode.SCRAP_EXISTS);
        } else if (verifyDaily == null) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        dailyMapper.saveDailyScrap(dailyId, memberId);
    }

    // 하루 식단 게시글 수정
    public void modifyDaily(DailyEditRequestDto dailyEditRequestDto, MultipartFile dailyImage) {
        DailyDetailsResponseDto verifyDaily = dailyMapper.findByDailyId(dailyEditRequestDto.getDailyId());
        if (verifyDaily == null) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        dailyMapper.updateDaily(dailyEditRequestDto);
    }


    // 하루 식단 댓글 수정
    public void modifyDailyComment(DailyCommentEditRequestDto dailyCommentEditRequestDto) {

        DailyCommentDetailsResponseDto verifyDailyComment = dailyMapper.findByDailyComment(dailyCommentEditRequestDto.getCommentId());
        String verifyImage = dailyMapper.findByDailyImage(dailyCommentEditRequestDto.getDailyId());
        if (verifyDailyComment == null) {
            throw new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND);
        }
        dailyMapper.updateDailyComment(dailyCommentEditRequestDto);
    }

    //하루식단 게시판 조회
    public List<DailysResponseDto> findDailys() {
        List<DailysResponseDto> dailys = dailyMapper.findAll();
        return dailys.stream()
                    .map(d -> {
                        d.setDailyFilePath(dailyMapper.findByDailyImage(d.getDailyId()));
                        return d;})
                    .collect(Collectors.toList());
    }

    //하루식단 게시글 조회
    public DailyDetailsResponseDto findDaily(int dailyId) {

        DailyDetailsResponseDto verifyDaily = dailyMapper.findByDailyId(dailyId);
        if(verifyDaily == null) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        //좋아요 수
        int dailyLikeCount = dailyMapper.findAllDailyId(dailyId).size();

        //댓글 수
        int daiyCommentCount = dailyMapper.findAllDailyComment(dailyId).size();

        //게시글 조회수 업데이트
        dailyMapper.updateDailyView(dailyId);

        DailyDetailsResponseDto dailyResponse = dailyMapper.findByDailyId(dailyId);
        dailyResponse.setLike(dailyLikeCount);
        dailyResponse.setComment(daiyCommentCount);

        return dailyResponse;
    }

    // 하루식단 댓글 조회
    public List<DailyCommentDetailsResponseDto> findDailyComments(int dailyId) {
        DailyDetailsResponseDto verifyDaily = dailyMapper.findByDailyId(dailyId);
        if(verifyDaily == null) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        return dailyMapper.findAllDailyComment(dailyId);
    }

    
    //하루식단 게시글 좋아요 취소
    public void removeDailyLike(int dailyLikeId) {
        Integer result = dailyMapper.findByDailyLikeId(dailyLikeId);
        if(result == null) {
            throw new BusinessLogicException(ExceptionCode.LIKE_NOT_FOUND);
        }
        dailyMapper.deleteDailyLike(dailyLikeId);
    }

    //하루식단 게시글 삭제
    public void removeDaily(int dailyId, int memberId) {
        DailyDetailsResponseDto verifyDaily = dailyMapper.findByDailyId(dailyId);
        if(verifyDaily == null) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        dailyMapper.deleteDaily(dailyId, memberId);
    }


    //하루식단 댓글 삭제
    public void removeDailyComment(int dailyId, int memberId, int commentId) {
        DailyCommentDetailsResponseDto verifyComment = dailyMapper.findByDailyComment(commentId);
        if(verifyComment == null){
            throw new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND);
        }
        dailyMapper.deleteDailyComment(dailyId, memberId, commentId);
    }

    // 하루식단 게시글 스크랩 삭제
    public void removeDailyScrap(int dailyId, int memberId, int scrapId) {
        Integer verifyDailyScrap = dailyMapper.findByDailyScrap(dailyId, memberId);
        if(verifyDailyScrap == null) {
            throw new BusinessLogicException(ExceptionCode.SCRAP_NOT_FOUND);
        }
        dailyMapper.deleteDailyScrap(dailyId, memberId, scrapId);
    }

    // DB에 저장할 이미지 DTO 생성
    private DailyImageDto of(int dailyId, int memberId, String originalName, String originalPullName, String saveName, String path, long size, String ext) {
        return DailyImageDto.builder()
                    .dailyId(dailyId)
                    .memberId(memberId)
                    .originalName(originalName)
                    .saveName(saveName)
                    .path(path)
                    .size(size)
                    .ext(ext)
                    .build();
    }


}
