package com.foodiary.member.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foodiary.auth.service.UserService;
import com.foodiary.common.email.EmailService;
import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.common.exception.MorePasswordException;
import com.foodiary.common.exception.VaildErrorResponseDto;
import com.foodiary.common.s3.S3Service;
import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberCheckEmailRequestDto;
import com.foodiary.member.model.MemberDailyLikeResponseDto;
import com.foodiary.member.model.MemberDailyScrapResponseDto;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberEditRequestDto;
import com.foodiary.member.model.MemberEditResponseDto;
import com.foodiary.member.model.MemberImageDto;
import com.foodiary.member.model.MemberLikeResponseDto;
import com.foodiary.member.model.MemberRecipeLikeResponseDto;
import com.foodiary.member.model.MemberRecipeScrapResponseDto;
import com.foodiary.member.model.MemberScrapResponseDto;
import com.foodiary.member.model.MemberSignUpRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberMapper mapper;

    private final UserService userService;

    private final S3Service s3Service;

    private final EmailService emailService;

    public void createdMember(MemberSignUpRequestDto memberSignUpDto, MultipartFile memberImage) throws Exception{
        
        if(memberSignUpDto.getMore_password().equals(memberSignUpDto.getPassword())==false) {
            
            VaildErrorResponseDto vaildErrorDto = new VaildErrorResponseDto("more_password", "비밀번호가 일치하지 않습니다");

            throw new MorePasswordException(vaildErrorDto);
        }

        String newPassword = userService.encrypt(memberSignUpDto.getPassword());

        memberSignUpDto.passwordUpdate(newPassword);

        if(memberImage==null) {
            mapper.saveMember(memberSignUpDto);
        }
        else {
            fileCheck(memberImage);
            HashMap<String, String> fileMap = s3Service.upload(memberImage, "member");

            memberSignUpDto.pathUpdate(fileMap.get("url"));
            mapper.saveMember(memberSignUpDto);
            
            MemberDto memberDto = mapper.findByLoginId(memberSignUpDto.getLoginId());

            String fileFullName = memberImage.getOriginalFilename();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
            String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

            MemberImageDto memberImageDto = new MemberImageDto(memberDto.getMemberId(), fileName, fileFullName, fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);
            
            createMemberImage(memberImageDto);
        }
    
    }

    public void updateMember(MemberEditRequestDto memberEditDto, int id, MultipartFile memberImage) throws Exception{
        
        memberEditDto.updateId(id);
        if(memberImage==null) {
            mapper.updateMemberInfo(memberEditDto);
        }
        else {
            fileCheck(memberImage);

            HashMap<String, String> fileMap = s3Service.upload(memberImage, "member");

            memberEditDto.updatePath(fileMap.get("url"));
            mapper.updateMemberInfo(memberEditDto);

            String fileFullName = memberImage.getOriginalFilename();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
            String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

            MemberImageDto memberImageDto = mapper.findByIdFile(id);
            
            // 기존에 이미지가 있었던 경우
            if(memberImageDto!=null) {
                String url = "member/"+memberImageDto.getMemberFileSaveName();
                s3Service.deleteImage(url);
    
                // 기존 이미지 삭제
                deleteImage(id);
            }

            MemberImageDto memberImageDtoUpdate = new MemberImageDto(id, fileName, fileFullName, fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);
            
            createMemberImage(memberImageDtoUpdate);
        }
    }

    private void deleteImage(int id) {
        MemberImageDto memberImageDto = mapper.findByIdFile(id);
        String url = "member/"+memberImageDto.getMemberFileSaveName();
        s3Service.deleteImage(url);
        mapper.deleteMemberImage(id);
    }

    public void findMemberLoginId(String loginId) {
        
        MemberDto memberDto = mapper.findByLoginId(loginId);
        
        if(memberDto!=null) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_BAD_REQUEST);
        }
    }

    public void findmemberEmail(String email) {

        MemberDto memberDto = mapper.findByEmail(email);
        
        if(memberDto!=null) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_BAD_REQUEST);
        }
    }

    public MemberDto findmemberId(String id) {
        return mapper.findById(id);
    }

    public void findmemberNickname(String nickname) {

        MemberDto memberDto = mapper.findByNickname(nickname);
        if(memberDto!=null) {
            throw new BusinessLogicException(ExceptionCode.NICKNAME_BAD_REQUEST);
        }
    }

    public void createMemberImage(MemberImageDto memberImageDto) {
        mapper.saveMemberImage(memberImageDto);
    }

    public void EditMemberPassWord(String password, int id) {
        String newPassword = userService.encrypt(password);

        mapper.updateMemberPassword(newPassword, id);
    }

    public void findmemberInfo(String email, String type) throws Exception{

        MemberDto memberDto = mapper.findByEmail(email);
        
        if(memberDto==null) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
        else {
            if(type.equals("id")) {
                // TODO: 메일 발송 로직 추가, id 정보를 메일로 발송
                emailService.EmailSend(email, memberDto.getMemberLoginId(), type);
            }
            else {
                // TODO: 메일 발송 로직 추가, 비밀번호를 새로 바꿀수 있는 링크(토큰 만들어서, 토큰 디비저장) 발송
                emailService.EmailSend(email, memberDto.getMemberLoginId(), type);
            }

        }
    }

    // 파일 확장자 검사
    public void fileCheck(MultipartFile memberImage) {
        String ext = memberImage.getOriginalFilename().substring(memberImage.getOriginalFilename().lastIndexOf(".") + 1);

        if(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png")) {
        }
        else {
            throw new BusinessLogicException(ExceptionCode.FILE_BAD_REQUEST);
        }
    }
    
    public MemberScrapResponseDto detailScrap(int memberId) {

        List<MemberDailyScrapResponseDto> memberDailyScrapResponseDtoList = mapper.findByDailyScrap(memberId);
        
        List<MemberRecipeScrapResponseDto> memberRecipeScrapResponseDtoList = mapper.findByRecipeScrap(memberId);

        MemberScrapResponseDto memberScrapResponseDto = new MemberScrapResponseDto(memberDailyScrapResponseDtoList, memberRecipeScrapResponseDtoList);
        return memberScrapResponseDto;
    }

    public void deleteScrapDaily(int scrapId, int memberId) {
        mapper.deleteDailyScrap(scrapId, memberId);
    }

    public void deleteScrapRecipe(int scrapId, int memberId) {
        mapper.deleteRecipeScrap(scrapId, memberId);
    }

    public MemberLikeResponseDto detailLike(int memberId) {

        List<MemberDailyLikeResponseDto> memberDailyLikeResponseDtoList = mapper.findByDailyLike(memberId);
        
        List<MemberRecipeLikeResponseDto> memberRecipeLikeResponseDtoList = mapper.findByRecipeLike(memberId);

        MemberLikeResponseDto memberLikeResponseDto = new MemberLikeResponseDto(memberDailyLikeResponseDtoList, memberRecipeLikeResponseDtoList);
        return memberLikeResponseDto;
    }

    public void deleteLikeDaily(int likeId, int memberId) {
        mapper.deleteDailyLike(likeId, memberId);
    }

    public void deleteLikeRecipe(int likeId, int memberId) {
        mapper.deleteRecipeLike(likeId, memberId);
    }

    public MemberEditResponseDto findByMemberIdInfo(int memberId) {
        
        return mapper.findByMemberIdEdit(memberId);
    }

    public void deleteMemberImage(int memberId) {
        
        deleteImage(memberId);
        mapper.updateMemberImage(memberId);

    }

    public void deleteMember(int memberId) {
        
        mapper.deleteMember(memberId);

    }

    public void mailSend(MemberCheckEmailRequestDto memberCheckEmailRequestDto) {
        
        // TODO : 이메일 인증 번호 생성 및 redis 저장, 시간 제한 5분
        //TODO : 이메일 발송 로직
        
    }

}
