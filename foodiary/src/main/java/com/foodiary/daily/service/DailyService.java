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

    private DailyImageDto fileHandler(MultipartFile file, int memberId) throws IOException {
        fileCheck(file);
        HashMap<String, String> fileMap = s3Service.upload(file, "daily");

        String fileFullName = file.getOriginalFilename();
        String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
        String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
       return DailyImageDto.builder()
                .memberId(memberId)
               .dailyFileOriginalName(fileName)
               .dailyFileFullName(fileFullName)
               .dailyFileSaveName(fileMap.get("serverName"))
               .dailyFilePath(fileMap.get("url"))
               .dailyFileSize(file.getSize())
               .dailyFileType(ext).build();
    }

    // 하루 식단 게시글 추가
    public void addDaily(DailyWriteRequestDto dailyWriteRequestDto, MultipartFile thumbnail, List<MultipartFile> dailyImageList) throws IOException {

        userService.checkUser(dailyWriteRequestDto.getMemberId());
        MemberDto member = memberMapper.findByMemberId(dailyWriteRequestDto.getMemberId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        List<DailyImageDto> saveImageList = new ArrayList<>();

        if(thumbnail == null) {
            throw new BusinessLogicException(ExceptionCode.IMAGE_BAD_REQUEST);
        } else {

            DailyImageDto dailyThumbnail = fileHandler(thumbnail, dailyWriteRequestDto.getMemberId());
            for(MultipartFile file : dailyImageList) {
                DailyImageDto saveImage = fileHandler(file, dailyWriteRequestDto.getMemberId());
                saveImageList.add(saveImage);
            }

            dailyWriteRequestDto.setWriter(member.getMemberNickName());
            dailyWriteRequestDto.setThumbnail(dailyThumbnail.getDailyFilePath());
            userService.verifySave(dailyMapper.saveDaily(dailyWriteRequestDto));

            int dailyId = dailyMapper.findDailyIdByPath(dailyWriteRequestDto.getThumbnail())
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));

            dailyThumbnail.setDailyId(dailyId);
            userService.verifySave(dailyMapper.saveImage(dailyThumbnail));

            saveImageList.forEach(image -> {
                image.setDailyId(dailyId);
                userService.verifySave(dailyMapper.saveImage(image));
            });
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

        //썸네일을 변경한다는 요청을 하였는데 변경할 이미지를 선택하지 않거나 새로운 파일을 추가 안했을 경우 예외처리
        if((dailyEditRequestDto.isThumbnailYn() && dailyImage == null) && (dailyEditRequestDto.isThumbnailYn() && dailyEditRequestDto.getThumbnailPath() == null)) {
            throw new BusinessLogicException(ExceptionCode.THUMBNAUL_BAD_REQUEST);
        }

              verifyDailyPost(dailyEditRequestDto.getDailyId());
        List<DailyImageDto> imageList = dailyMapper.findAllImageDtoList(dailyEditRequestDto.getDailyId()); // 기존 저장된 이미지 리스트
        List<DailyImageDto> newImageList = new ArrayList<>();
        List<String> deletePathList = dailyEditRequestDto.getDeletePath(); // 삭제할 이미지 경로 리스트

       // 삭제할 이미지 경로가 없을경우
        if(deletePathList == null){

           // 이미지가 추가된 경우
            if(dailyImage != null) {

                // 이미지 합계가 5개 초과되면 예외처리
                if (imageList.size() + dailyImage.size() > 5) {
                    throw new BusinessLogicException(ExceptionCode.IMAGE_ERROR);
                }
                for (MultipartFile image : dailyImage) {
                    DailyImageDto saveImage = fileHandler(image, dailyEditRequestDto.getMemberId());
                    newImageList.add(saveImage);
                    saveImage.setDailyId(dailyEditRequestDto.getDailyId());
                    dailyMapper.saveImage(saveImage);
                }
            }
            // 삭제할 이미지 경로가 있는 경우
        } else {
            List<DailyImageDto> deleteImageList = new ArrayList<>();
            // 기존 이미지 리스트에서 삭제할 이미지들만 추출
            for(DailyImageDto image : imageList) {
                for(String path : deletePathList) {
                    if(image.getDailyFilePath().equals(path)){
                        deleteImageList.add(image);
                    }
                }
            }
            // 삭제할 이미지 경로가 잘못 입력되었을 경우 예외처리
            if(deleteImageList.isEmpty()) {
                throw new BusinessLogicException(ExceptionCode.IMAGE_NOT_FOUND);
            }

            // S3, db에서 이미지 삭제
            for (DailyImageDto image : deleteImageList) {
                String url = "daily/" + image.getDailyFileSaveName();
                s3Service.deleteImage(url);
                dailyMapper.deleteDailyImage(dailyEditRequestDto.getDailyId(), image.getDailyFilePath());
            }

            // 이미지가 추가된 경우
            if(dailyImage != null){

                // 이미지 합계가 5개 초과되면 예외처리
                if(imageList.size() + dailyImage.size() - deleteImageList.size() > 5){
                    throw new BusinessLogicException(ExceptionCode.IMAGE_ERROR);
                }
                for(MultipartFile image : dailyImage) {
                    DailyImageDto saveImage = fileHandler(image, dailyEditRequestDto.getMemberId());
                    saveImage.setDailyId(dailyEditRequestDto.getDailyId());
                    newImageList.add(saveImage);
                    dailyMapper.saveImage(saveImage);
                }
            }
        }
        // 썸네일 이미지를 교체할 경우
        if(dailyEditRequestDto.isThumbnailYn()){
            // 기존 이미지에서 썸네일 대체할 경우
            if(dailyEditRequestDto.getThumbnailPath() != null) {
                userService.verifyUpdate(dailyMapper.updateThumbnailPath(dailyEditRequestDto.getThumbnailPath(), dailyEditRequestDto.getDailyId()));

                // 새로운 파일에서 썸네일 대체할 경우
            } else {
                userService.verifyUpdate(dailyMapper.updateThumbnailPath(newImageList.get(0).getDailyFilePath(), dailyEditRequestDto.getDailyId()));
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
    public DailyDetailsResponseDto findDaily(int dailyId, int memberId) {

        //게시글 조회수 업데이트
        userService.verifyUpdate(dailyMapper.updateDailyView(dailyId));

        DailyDetailsResponseDto daily = verifyDailyPost(dailyId);
        List<String> imageList = dailyMapper.findAllImageList(dailyId);
        daily.setDailyImageList(imageList);

        if(daily.getMemberId() == memberId){
            daily.setUserCheck(true);
        } else {
            daily.setUserCheck(false);
        }
        return daily;
    }



    // 하루식단 댓글 조회
    public List<DailyCommentDetailsResponseDto> findDailyComments(int dailyId, int memberId) {
        verifyDailyPost(dailyId);

        List<DailyCommentDetailsResponseDto> commentList = dailyMapper.findAllDailyComment(dailyId);
        if (commentList.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND);
        }
        return commentList.stream()
                    .map(comment -> {
                        if(comment.getMemberId() == memberId) comment.setUserCheck(true);
                        else comment.setUserCheck(false);
                        return comment;
                    }).collect(Collectors.toList());
    }

    
    //하루식단 게시글 좋아요 취소
    public void removeDailyLike(int dailyId) {
        dailyMapper.deleteDailyLike(dailyId);
    }

    //하루식단 게시글 삭제
    public void removeDaily(int dailyId, int memberId) {
        userService.checkUser(memberId);
        verifyDailyPost(dailyId);
        List<DailyImageDto> dailyImageDto = dailyMapper.findImageByDailyId(dailyId);

        dailyImageDto.forEach(image -> {
            String url = "daily/" + image.getDailyFileSaveName();
            s3Service.deleteImage(url);
        });
        dailyMapper.deleteAllDailyImage(dailyId);
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
