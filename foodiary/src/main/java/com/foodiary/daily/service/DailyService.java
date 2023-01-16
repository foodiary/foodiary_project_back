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
import com.foodiary.daily.model.DailyDetailsResponseDto;
import com.foodiary.daily.model.DailyImageDto;
import com.foodiary.rank.mapper.RankMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spi.service.contexts.SecurityContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final RankMapper rankMapper;
    private final S3Service s3Service;
    private final UserService userService;
    

    // 하루 식단 게시글 추가
    public void addDaily(DailyWriteRequestDto dailyWriteRequestDto, List<MultipartFile> dailyImage) throws IOException {

        userService.checkUser(dailyWriteRequestDto.getMemberId());
        MemberDto member = memberMapper.findByMemberId(dailyWriteRequestDto.getMemberId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));


        // 파일 url 담을 리스트
        List<String> fileUrlList = new ArrayList<>();

        List<DailyImageDto> saveImageList = new ArrayList<>();

        if(dailyImage.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.IMAGE_BAD_REQUEST);
        } else {
            for(int i=0; i < dailyImage.size(); i++) {
                MultipartFile file = dailyImage.get(i);
                fileCheck(file);
                HashMap<String, String> fileMap = s3Service.upload(file, "daily");
                fileUrlList.add(fileMap.get("url"));

                String fileFullName = file.getOriginalFilename();
                String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
                String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
                DailyImageDto saveImage = DailyImageDto.builder()
                        .memberId(dailyWriteRequestDto.getMemberId())
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
            dailyWriteRequestDto.setPath1(fileUrlList.get(0));

            if(fileUrlList.size()>1) {
                dailyWriteRequestDto.setPath2(fileUrlList.get(1));
                if(fileUrlList.size()>2) {
                    dailyWriteRequestDto.setPath3(fileUrlList.get(2));
                }
            }

            dailyWriteRequestDto.setWriter(member.getMemberNickName());
            userService.verifySave(dailyMapper.saveDaily(dailyWriteRequestDto));

            int dailyId = dailyMapper.findDailyIdByPath(dailyWriteRequestDto.getPath1())
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));

            for (int i = 0; i < saveImageList.size(); i++) {
                saveImageList.get(i).setDailyId(dailyId);
                userService.verifySave(dailyMapper.saveImage(saveImageList.get(i)));
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
    public void modifyDaily(DailyEditRequestDto dailyEditRequestDto, List<MultipartFile> dailyImage) throws IOException {
        userService.checkUser(dailyEditRequestDto.getMemberId());
        verifyDailyPost(dailyEditRequestDto.getDailyId());
        memberMapper.findByMemberId(dailyEditRequestDto.getMemberId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));


        // 기존 게시물의 파일 경로 삭제 및 S3 파일 삭제
        DailyDetailsResponseDto verifyDaily = verifyDailyPost(dailyEditRequestDto.getDailyId());

        // 이미지 3개 다 업데이트할때의 경우
        if(verifyDaily.getDailyPath1() != null) {
            s3Service.deleteImage(verifyDaily.getDailyPath1());
            dailyMapper.deleteDailyImage(dailyEditRequestDto.getDailyId(), verifyDaily.getDailyPath1());
        }
        if(verifyDaily.getDailyPath2() != null) {
            s3Service.deleteImage(verifyDaily.getDailyPath2());
            dailyMapper.deleteDailyImage(dailyEditRequestDto.getDailyId(), verifyDaily.getDailyPath2());
        }
        if(verifyDaily.getDailyPath3() != null) {
            s3Service.deleteImage(verifyDaily.getDailyPath3());
            dailyMapper.deleteDailyImage(dailyEditRequestDto.getDailyId(), verifyDaily.getDailyPath3());
        }

        // 파일 url 담을 리스트
        List<String> fileUrlList = new ArrayList<>();

        // 만약에 기존이미지1, 2가 있고, 제가 2만 교체 한다고 했을때
        // image2만 들어오겠죠?


        // 새로 첨부 받은 이미지 파일로 업데이트
        for (int i = 0; i < dailyImage.size(); i++) {
            MultipartFile file = dailyImage.get(i);
            fileCheck(file);
            HashMap<String, String> fileMap = s3Service.upload(file, "daily");
            fileUrlList.add(fileMap.get("url"));

            String fileFullName = file.getOriginalFilename();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
            String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
            DailyImageDto saveImage = DailyImageDto.builder()
                    .memberId(dailyEditRequestDto.getMemberId())
                    .originalName(fileName)
                    .originalFullName(fileFullName)
                    .saveName(fileMap.get("serverName"))
                    .path(fileMap.get("url"))
                    .size(file.getSize())
                    .ext(ext).build();
            saveImage.setDailyId(dailyEditRequestDto.getDailyId());
            userService.verifySave(dailyMapper.saveImage(saveImage));
            log.info("파일이 업로드 되었습니다.");
        }

        // 게시글 DB에 이미지 경로 저장
        dailyEditRequestDto.setPath1(fileUrlList.get(0));
        if(fileUrlList.size() > 1) {
            dailyEditRequestDto.setPath2(fileUrlList.get(1));
        }
        if (fileUrlList.size() > 2) {
            dailyEditRequestDto.setPath3(fileUrlList.get(2));
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
        return setDailyRank(dailys);
    }

    //하루식단 게시판 조회(월, 주, 일)
    public List<DailysResponseDto> findCreateDailys(LocalDateTime start,  LocalDateTime end) {
        List<DailysResponseDto> dailys = dailyMapper.findAllCreate(start, end);
        if(dailys.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        return setDailyRank(dailys);

    }


    //하루식단 게시판 최신글 10개 조회
    public List<DailysResponseDto> findTopDailys() {
        List<DailysResponseDto> dailys = dailyMapper.findTopDaily();
        if(dailys.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        return setDailyRank(dailys);
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

        if(verifyDaily.getDailyPath1() != null) {
            String url = "daily/" + dailyImageDto.getSaveName();
            s3Service.deleteImage(url);
            dailyMapper.deleteDailyImage(dailyId, verifyDaily.getDailyPath1());
        }

        userService.verifyDelete(dailyMapper.deleteDaily(dailyId));
    }


    //하루식단 댓글 삭제
    public void removeDailyComment(int dailyId, int memberId, int commentId) {
        userService.checkUser(memberId);

        userService.verifyDelete(dailyMapper.deleteDailyComment(dailyId, commentId, memberId));
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

    private List<DailysResponseDto> setDailyRank(List<DailysResponseDto> list) {
        List<Integer> weekDailyIdList = rankMapper.findByWeekDailyId();
        List<Integer> monDailyIdList = rankMapper.findByMonDailyId();

        return list.stream()
                    .map(daily -> {
                        weekDailyIdList.forEach(id -> {
                            if (id == daily.getDailyId()) {
                                daily.setWeekRank(true);
                            }
                        });
                        monDailyIdList.forEach(id -> {
                            if (id == daily.getDailyId()) {
                                daily.setMonRank(true);
                            }
                        });
                        return daily;
                    }).collect(Collectors.toList());
    }
}
