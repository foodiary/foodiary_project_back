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

    // ?????? ?????? ????????? ??????
    public void addDaily(DailyWriteRequestDto dailyWriteRequestDto, List<MultipartFile> dailyImageList) throws IOException {

        userService.checkUser(dailyWriteRequestDto.getMemberId());
        MemberDto member = memberMapper.findByMemberId(dailyWriteRequestDto.getMemberId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        List<DailyImageDto> saveImageList = new ArrayList<>();

        if(dailyImageList.size() < 1) {
            throw new BusinessLogicException(ExceptionCode.IMAGE_BAD_REQUEST);
        } else {

            if(dailyImageList != null && dailyImageList.size() > 0){
                for(MultipartFile file : dailyImageList) {
                    DailyImageDto saveImage = fileHandler(file, dailyWriteRequestDto.getMemberId());
                    saveImageList.add(saveImage);
                }
            }
            dailyWriteRequestDto.setWriter(member.getMemberNickName());
            dailyWriteRequestDto.setThumbnail(saveImageList.get(0).getDailyFilePath());
            userService.verifySave(dailyMapper.saveDaily(dailyWriteRequestDto));

            int dailyId = dailyMapper.findDailyIdByPath(dailyWriteRequestDto.getThumbnail())
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));


            if(saveImageList.size() > 0) {
                saveImageList.forEach(image -> {
                    image.setDailyId(dailyId);
                    userService.verifySave(dailyMapper.saveImage(image));
                });
            }
        }
    }



    // ?????? ?????? ?????? ??????
    public void addDailyComment(DailyCommentWriteRequestDto dailyCommentWriteRequestDto) {
        userService.checkUser(dailyCommentWriteRequestDto.getMemberId());
        MemberDto member = memberMapper.findByMemberId(dailyCommentWriteRequestDto.getMemberId()).get();
        dailyCommentWriteRequestDto.setWriter(member.getMemberNickName());

        userService.verifySave(dailyMapper.saveDailyComment(dailyCommentWriteRequestDto));
    }

    // ?????? ?????? ????????? ????????? 
    public void addDailyLike(int memberId, int dailyId) {
        userService.checkUser(memberId);

        boolean verifyLike = dailyMapper.findByMemberIdAndDailyId(memberId, dailyId).isEmpty();

        if (!verifyLike) {
            removeDailyLike(dailyId, memberId);
        } else {
            userService.verifySave(dailyMapper.saveDailyLike(memberId, dailyId));
        }

    }

    // ?????? ?????? ????????? ?????????
    public void addDailyScrap(int dailyId, int memberId) {
        userService.checkUser(memberId);

        if(!dailyMapper.findByDailyScrap(dailyId, memberId).isEmpty()) {
            removeDailyScrap(dailyId, memberId);
        } else {
            userService.verifySave(dailyMapper.saveDailyScrap(dailyId, memberId));
        }

    }


    // ?????? ?????? ????????? ??????
    public void modifyDaily(DailyEditRequestDto dailyEditRequestDto, List<MultipartFile> dailyImage) throws IOException {
        userService.checkUser(dailyEditRequestDto.getMemberId());
        verifyDailyPost(dailyEditRequestDto.getDailyId());
        memberMapper.findByMemberId(dailyEditRequestDto.getMemberId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        //???????????? ??????????????? ????????? ???????????? ????????? ???????????? ???????????? ????????? ????????? ????????? ?????? ????????? ?????? ????????????
//        if((dailyEditRequestDto.isThumbnailYn() && dailyImage == null) && (dailyEditRequestDto.isThumbnailYn() && dailyEditRequestDto.getThumbnailPath() == null)) {
//            throw new BusinessLogicException(ExceptionCode.THUMBNAUL_BAD_REQUEST);
//        }

              verifyDailyPost(dailyEditRequestDto.getDailyId());
        List<DailyImageDto> imageList = dailyMapper.findAllImageDtoList(dailyEditRequestDto.getDailyId()); // ?????? ????????? ????????? ?????????
        List<DailyImageDto> newImageList = new ArrayList<>(); // s3??? ???????????? ????????? ????????? ?????????
        List<String> deletePathList = dailyEditRequestDto.getDeletePath(); // ????????? ????????? ?????? ?????????

       // ????????? ????????? ????????? ????????????
        if(deletePathList == null){

           // ???????????? ????????? ??????
            if(dailyImage != null) {

                // ????????? ????????? 5??? ???????????? ????????????
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
            // ????????? ????????? ????????? ?????? ??????
        } else {
            if(imageList.size() - deletePathList.size() < 1){
                throw new BusinessLogicException(ExceptionCode.IMAGE_UPDATE_BAD_REQUEST);
            }
            List<DailyImageDto> deleteImageList = new ArrayList<>();
            // ?????? ????????? ??????????????? ????????? ??????????????? ??????
            for(DailyImageDto image : imageList) {
                for(String path : deletePathList) {
                    if(image.getDailyFilePath().equals(path)){
                        deleteImageList.add(image);
                    }
                }
            }
            // ????????? ????????? ????????? ?????? ??????????????? ?????? ????????????
            if(deleteImageList.isEmpty()) {
                throw new BusinessLogicException(ExceptionCode.IMAGE_NOT_FOUND);
            }

            // ???????????? ????????? ??????
            if(dailyImage != null){

                // ????????? ????????? 5??? ???????????? ????????????
                if(imageList.size() + dailyImage.size() - deleteImageList.size() > 5){
                    throw new BusinessLogicException(ExceptionCode.IMAGE_ERROR);
                }
                for(MultipartFile image : dailyImage) {
                    DailyImageDto saveImage = fileHandler(image, dailyEditRequestDto.getMemberId());
                    saveImage.setDailyId(dailyEditRequestDto.getDailyId());
                    newImageList.add(saveImage);
                    dailyMapper.saveImage(saveImage);
                }

                // S3, db?????? ????????? ??????
                for (DailyImageDto image : deleteImageList) {
                    String url = "daily/" + image.getDailyFileSaveName();
                    s3Service.deleteImage(url);
                    dailyMapper.deleteDailyImage(dailyEditRequestDto.getDailyId(), image.getDailyFilePath());
                }
                if(deletePathList.size() == imageList.size()){
                    dailyMapper.updateThumbnailPath(newImageList.get(0).getDailyFilePath(), dailyEditRequestDto.getDailyId());
                }
            } else {
                for (DailyImageDto image : deleteImageList) {
                    String url = "daily/" + image.getDailyFileSaveName();
                    s3Service.deleteImage(url);
                    dailyMapper.deleteDailyImage(dailyEditRequestDto.getDailyId(), image.getDailyFilePath());
                }
                String updateThumbnailPath = dailyMapper.findAllImageDtoList(dailyEditRequestDto.getDailyId()).get(0).getDailyFilePath();
                dailyMapper.updateThumbnailPath(updateThumbnailPath, dailyEditRequestDto.getDailyId());
            }
            
        }
        // ????????? ???????????? ????????? ??????
//        if(dailyEditRequestDto.isThumbnailYn()){
//            // ?????? ??????????????? ????????? ????????? ??????
//            if(dailyEditRequestDto.getThumbnailPath() != null) {
//                userService.verifyUpdate(dailyMapper.updateThumbnailPath(dailyEditRequestDto.getThumbnailPath(), dailyEditRequestDto.getDailyId()));
//
//                // ????????? ???????????? ????????? ????????? ??????
//            } else {
//                userService.verifyUpdate(dailyMapper.updateThumbnailPath(newImageList.get(0).getDailyFilePath(), dailyEditRequestDto.getDailyId()));
//            }
//        }
        userService.verifyUpdate(dailyMapper.updateDaily(dailyEditRequestDto));
    }


    // ?????? ?????? ?????? ??????
    public void modifyDailyComment(DailyCommentEditRequestDto dailyCommentEditRequestDto) {
        userService.checkUser(dailyCommentEditRequestDto.getMemberId());
        dailyMapper.findByDailyComment(dailyCommentEditRequestDto.getDailyId())
                        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));

        userService.verifyUpdate(dailyMapper.updateDailyComment(dailyCommentEditRequestDto));
    }

    //???????????? ????????? ??????
    public List<DailysResponseDto> findDailys() {
        List<DailysResponseDto> dailys = dailyMapper.findAll();
        if(dailys.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        return setDailyRank(dailys);
    }

    //???????????? ????????? ??????(???, ???, ???)
    public List<DailysResponseDto> findCreateDailys(LocalDateTime start,  LocalDateTime end) {
        List<DailysResponseDto> dailys = dailyMapper.findAllCreate(start, end);
        if(dailys.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        return setDailyRank(dailys);

    }


    //???????????? ????????? ????????? 10??? ??????
    public List<DailysResponseDto> findTopDailys() {
        List<DailysResponseDto> dailys = dailyMapper.findTopDaily();
        if(dailys.size() == 0) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        return setDailyRank(dailys);
    }



    //???????????? ????????? ??????
    public DailyDetailsResponseDto findDaily(int dailyId, int memberId) {

        //????????? ????????? ????????????
        userService.verifyUpdate(dailyMapper.updateDailyView(dailyId));

        DailyDetailsResponseDto daily = verifyDailyPost(dailyId);
        List<String> imageList = dailyMapper.findAllImageList(dailyId);
        daily.setDailyImageList(imageList);

        if(daily.getMemberId() == memberId){
            daily.setUserCheck(true);
        } else {
            daily.setUserCheck(false);
        }
        if(rankMapper.findWeekByDailyId(dailyId).isPresent()) daily.setWeekRank(true);
        else daily.setWeekRank(false);

        if(rankMapper.findMonByDailyId(dailyId).isPresent()) daily.setMonRank(true);
        else daily.setMonRank(false);

        if(dailyMapper.findByDailyScrap(dailyId, memberId).isPresent()) daily.setScrapCheck(true);
        else daily.setScrapCheck(false);

        if(dailyMapper.findByDailyLike(dailyId, memberId).isPresent()) daily.setLikeCheck(true);
        else daily.setLikeCheck(false);

        return daily;
    }



    // ???????????? ?????? ??????
    public List<DailyCommentDetailsResponseDto> findDailyComments(int dailyId, int memberId) {

        List<DailyCommentDetailsResponseDto> commentList = dailyMapper.findAllDailyComments(dailyId);
        if (commentList.size() <= 0) {
            throw new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND);
        }

        for(DailyCommentDetailsResponseDto comment : commentList) {
            if (comment.getMemberId() == memberId) comment.setUserCheck(true);
            else comment.setUserCheck(false);
        }

        return commentList;
    }

    
    //???????????? ????????? ????????? ??????
    public void removeDailyLike(int dailyId, int memberId) {
        dailyMapper.deleteDailyLike(dailyId, memberId);
    }

    //???????????? ????????? ??????
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


    //???????????? ?????? ??????
    public void removeDailyComment(int dailyId, int memberId, int commentId) {
        userService.checkUser(memberId);

        userService.verifyDelete(dailyMapper.deleteDailyComment(dailyId, commentId, memberId));
    }


    // ???????????? ????????? ????????? ??????
    public void removeDailyScrap(int dailyId, int memberId) {
        userService.verifyDelete(dailyMapper.deleteDailyScrap(dailyId, memberId));
    }



    //????????? ????????? ?????????
    public void fileCheck(MultipartFile image) {
        String ext = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);

        if(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png")) {
        }
        else {
            throw new BusinessLogicException(ExceptionCode.FILE_BAD_REQUEST);
        }
    }

    //????????? ????????? ?????? ??????
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
