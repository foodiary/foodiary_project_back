package com.foodiary.member.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foodiary.auth.jwt.JwtProvider;
import com.foodiary.auth.service.UserService;
import com.foodiary.common.email.EmailService;
import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.common.exception.MorePasswordException;
import com.foodiary.common.exception.VaildErrorResponseDto;
import com.foodiary.common.s3.S3Service;
import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberCheckEmailNumRequestDto;
import com.foodiary.member.model.MemberCheckEmailRequestDto;
import com.foodiary.member.model.MemberCheckPwJwtRequestDto;
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

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberMapper mapper;

    private final UserService userService;

    private final S3Service s3Service;

    private final EmailService emailService;

    private final RedisTemplate<String, String> redisTemplate;

    private final JwtProvider jwtProvider;

    public void createdMember(MemberSignUpRequestDto memberSignUpDto, MultipartFile memberImage) throws Exception {

        if (memberSignUpDto.getMore_password().equals(memberSignUpDto.getPassword()) == false) {

            VaildErrorResponseDto vaildErrorDto = new VaildErrorResponseDto("more_password", "비밀번호가 일치하지 않습니다");

            throw new MorePasswordException(vaildErrorDto);
        }

        String newPassword = userService.encrypt(memberSignUpDto.getPassword());

        memberSignUpDto.passwordUpdate(newPassword);

        if (memberImage == null) {
            mapper.saveMember(memberSignUpDto);
        } else {
            fileCheck(memberImage);
            HashMap<String, String> fileMap = s3Service.upload(memberImage, "member");

            memberSignUpDto.pathUpdate(fileMap.get("url"));
            mapper.saveMember(memberSignUpDto);

            MemberDto memberDto = mapper.findByLoginId(memberSignUpDto.getLoginId());

            String fileFullName = memberImage.getOriginalFilename();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
            String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

            MemberImageDto memberImageDto = new MemberImageDto(memberDto.getMemberId(), fileName, fileFullName,
                    fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);

            createMemberImage(memberImageDto);
        }

    }

    public void updateMember(MemberEditRequestDto memberEditDto, int id, MultipartFile memberImage) throws Exception {

        memberEditDto.updateId(id);
        if (memberImage == null) {
            mapper.updateMemberInfo(memberEditDto);
        } else {
            fileCheck(memberImage);

            HashMap<String, String> fileMap = s3Service.upload(memberImage, "member");

            memberEditDto.updatePath(fileMap.get("url"));
            mapper.updateMemberInfo(memberEditDto);

            String fileFullName = memberImage.getOriginalFilename();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
            String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

            MemberImageDto memberImageDto = mapper.findByIdFile(id);

            // 기존에 이미지가 있었던 경우
            if (memberImageDto != null) {
                String url = "member/" + memberImageDto.getMemberFileSaveName();
                s3Service.deleteImage(url);

                // 기존 이미지 삭제
                deleteImage(id);
            }

            MemberImageDto memberImageDtoUpdate = new MemberImageDto(id, fileName, fileFullName,
                    fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);

            createMemberImage(memberImageDtoUpdate);
        }
    }

    private void deleteImage(int id) {
        MemberImageDto memberImageDto = mapper.findByIdFile(id);
        String url = "member/" + memberImageDto.getMemberFileSaveName();
        s3Service.deleteImage(url);
        mapper.deleteMemberImage(id);
    }

    public void findMemberLoginId(String loginId) {

        MemberDto memberDto = mapper.findByLoginId(loginId);

        if (memberDto != null) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_BAD_REQUEST);
        }
    }

    public void findmemberEmail(String email) {

        MemberDto memberDto = mapper.findByEmail(email);

        if (memberDto != null) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_BAD_REQUEST);
        }
    }

    public MemberDto findmemberId(String id) {
        return mapper.findById(id);
    }

    public void findmemberNickname(String nickname) {

        MemberDto memberDto = mapper.findByNickname(nickname);
        if (memberDto != null) {
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

    public void findmemberInfoId(String email, String type) throws Exception {

        MemberDto memberDto = mapper.findByEmail(email);

        if (memberDto == null) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        } else {
            emailService.EmailSend(email, memberDto.getMemberLoginId(), type);
        }
    }

    public void findmemberInfoPw(String email, String loginId, String type) throws Exception {

        MemberDto memberDto = mapper.findByEmailAndId(email, loginId);

        if (memberDto == null) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        } else {
            Map<String, Object> claims = new HashMap<>();
            claims.put("email", email);

            String subject = email;
            Date expiration = jwtProvider.getTokenExpiration(30);

            String jwt = jwtProvider.generateAccessToken(claims, subject, expiration);
            emailService.EmailSend(email, jwt, type);
        }
    }

    public void memberPwConfirm(MemberCheckPwJwtRequestDto memberCheckPwJwtRequestDto) {

        try {
            if (memberCheckPwJwtRequestDto.getPassword().equals(memberCheckPwJwtRequestDto.getMore_password())) {
                String email = jwtProvider.getSubject(memberCheckPwJwtRequestDto.getJwt());
                if (email.equals(memberCheckPwJwtRequestDto.getEmail())) {

                    int update = mapper.updateMemberPw(email,
                            userService.encrypt(memberCheckPwJwtRequestDto.getPassword()));
                    if (update < 1) {
                        throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
                    }
                } else {
                    throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
                }
            } else {
                throw new BusinessLogicException(ExceptionCode.MORE_PW_ERROR);
            }
        } catch (ExpiredJwtException e) {
            throw new BusinessLogicException(ExceptionCode.NUM_TIMEOUT);
        }
    }

    // 파일 확장자 검사
    public void fileCheck(MultipartFile memberImage) {
        String ext = memberImage.getOriginalFilename()
                .substring(memberImage.getOriginalFilename().lastIndexOf(".") + 1);

        if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png")) {
        } else {
            throw new BusinessLogicException(ExceptionCode.FILE_BAD_REQUEST);
        }
    }

    public MemberScrapResponseDto detailScrap(int memberId) {

        List<MemberDailyScrapResponseDto> memberDailyScrapResponseDtoList = mapper.findByDailyScrap(memberId);

        List<MemberRecipeScrapResponseDto> memberRecipeScrapResponseDtoList = mapper.findByRecipeScrap(memberId);

        MemberScrapResponseDto memberScrapResponseDto = new MemberScrapResponseDto(memberDailyScrapResponseDtoList,
                memberRecipeScrapResponseDtoList);
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

        MemberLikeResponseDto memberLikeResponseDto = new MemberLikeResponseDto(memberDailyLikeResponseDtoList,
                memberRecipeLikeResponseDtoList);
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

    public void mailSend(MemberCheckEmailRequestDto memberCheckEmailRequestDto) throws Exception {

        findmemberEmail(memberCheckEmailRequestDto.getEmail());

        int authNo = (int) (Math.random() * (999999 - 100000 + 1)) + 100000;
        String num = Integer.toString(authNo);
        redisTemplate.opsForValue().set("signup:" + memberCheckEmailRequestDto.getEmail(), num);
        redisTemplate.expire("signup:" + memberCheckEmailRequestDto.getEmail(), 5, TimeUnit.MINUTES);

        emailService.EmailSend(memberCheckEmailRequestDto.getEmail(), num, "signup");

    }


    public void mailSendConfirm(MemberCheckEmailNumRequestDto memberCheckEmailNumRequestDto) {

        String confirm = redisTemplate.opsForValue().get("signup:" + memberCheckEmailNumRequestDto.getEmail());
        
        if(confirm==null || !confirm.equals(memberCheckEmailNumRequestDto.getNum())) {
            throw new BusinessLogicException(ExceptionCode.NUM_TIMEOUT);
        }
    }

}
