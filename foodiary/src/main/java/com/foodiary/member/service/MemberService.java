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
import com.foodiary.member.model.MemberEditPasswordRequestDto;
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
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberService {

    private final MemberMapper mapper;

    private final UserService userService;

    private final S3Service s3Service;

    private final EmailService emailService;

    private final RedisTemplate<String, String> redisTemplate;

    private final JwtProvider jwtProvider;

    public void createdMember(MemberSignUpRequestDto memberSignUpDto, MultipartFile memberImage) throws Exception {

        // 한번 더 입력한 비밀번호 검사
        if (memberSignUpDto.getMore_password().equals(memberSignUpDto.getPassword()) == false) {

            VaildErrorResponseDto vaildErrorDto = new VaildErrorResponseDto("more_password", "비밀번호가 일치하지 않습니다");

            throw new MorePasswordException(vaildErrorDto);
        }

        // 약관동의 검사
        if (memberSignUpDto.getRequiredTerms().equals("N")) {
            throw new BusinessLogicException(ExceptionCode.TERMS_ERROR);
        }

        String newPassword = userService.encrypt(memberSignUpDto.getPassword());

        memberSignUpDto.passwordUpdate(newPassword);

        // 이미지 있을 경우와 없을 경우 분리
        if (memberImage == null) {
            int saveCheck = mapper.saveMember(memberSignUpDto);
            if(saveCheck < 1) {
                throw new BusinessLogicException(ExceptionCode.SAVE_ERROR);
            }
        } else {
            fileCheck(memberImage);
            HashMap<String, String> fileMap = s3Service.upload(memberImage, "member");

            memberSignUpDto.pathUpdate(fileMap.get("url"));
            int saveCheck = mapper.saveMember(memberSignUpDto);

            if(saveCheck < 1) {
                throw new BusinessLogicException(ExceptionCode.SAVE_ERROR);
            }
            MemberDto memberDto = mapper.findByLoginId(memberSignUpDto.getLoginId()).orElseThrow(() -> new BusinessLogicException(ExceptionCode.SELECT_ERROR));

            String fileFullName = memberImage.getOriginalFilename();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
            String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

            MemberImageDto memberImageDto = new MemberImageDto(memberDto.getMemberId(), fileName, fileFullName,
                    fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);

            // 이미지 저장
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

    // 아이디 중복 검사
    public void findMemberLoginId(String loginId) {

        if(mapper.findByLoginId(loginId).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.LOGINID_BAD_REQUEST);
        }
    }

    // 이메일 중복 검사
    public void findmemberEmail(String email) {

        if(mapper.findByEmail(email).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_BAD_REQUEST);
        }
    }

    // 닉네임 중복 검사
    public void findmemberNickname(String nickname) {

        if(mapper.findByNickname(nickname).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.NICKNAME_BAD_REQUEST);
        }
    }

    public void createMemberImage(MemberImageDto memberImageDto) {
        int saveCheck = mapper.saveMemberImage(memberImageDto);
        if(saveCheck < 1) {
            throw new BusinessLogicException(ExceptionCode.SAVE_ERROR);
        }
    }

    // 비밀번호 수정
    public void EditMemberPassword(MemberEditPasswordRequestDto memberEditPasswordRequestDto, int id) {
        
        if(memberEditPasswordRequestDto.getMore_password().equals(memberEditPasswordRequestDto.getPassword())) {
            String newPassword = userService.encrypt(memberEditPasswordRequestDto.getPassword());

            int updateChack = mapper.updateMemberPassword(newPassword, id);
            if(updateChack < 1) {
                throw new BusinessLogicException(ExceptionCode.UPDATE_ERROR);
            }
        }
        throw new BusinessLogicException(ExceptionCode.MORE_PW_ERROR);

    }

    // 아이디 찾기
    public void findmemberInfoId(String email, String type) throws Exception {

        MemberDto memberDto = mapper.findByEmail(email).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        emailService.EmailSend(email, memberDto.getMemberLoginId(), type);
    }

    public void findmemberInfoPw(String email, String loginId, String type) throws Exception {

        mapper.findByEmailAndId(email, loginId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);

        String subject = email;
        Date expiration = jwtProvider.getTokenExpiration(30);

        String jwt = jwtProvider.generateAccessToken(claims, subject, expiration);
        log.info("jwt = {}",jwt);
        emailService.EmailSend(email, jwt, type);
    }

    public void memberPwConfirm(MemberCheckPwJwtRequestDto memberCheckPwJwtRequestDto) {

        try {
            if (memberCheckPwJwtRequestDto.getPassword().equals(memberCheckPwJwtRequestDto.getMore_password())) {
                String email = jwtProvider.getSubject(memberCheckPwJwtRequestDto.getJwt());

                int update = mapper.updateMemberPw(email, userService.encrypt(memberCheckPwJwtRequestDto.getPassword()));
                if (update < 1) {
                    throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
                } 
            } else {
                throw new BusinessLogicException(ExceptionCode.MORE_PW_ERROR);
            }
        } catch (ExpiredJwtException e) {
            // 토큰 시간 초과
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
        int a = mapper.updateMemberImage(memberId);
        System.out.println("int : "+a);

    }

    public void deleteMember(int memberId) {

        mapper.deleteMember(memberId);

    }

    public void mailSend(MemberCheckEmailRequestDto memberCheckEmailRequestDto) throws Exception {

        // 이메일 중복 검사 한번더 
        findmemberEmail(memberCheckEmailRequestDto.getEmail());

        // 6자리 인증번호 생성
        int authNo = (int) (Math.random() * (999999 - 100000 + 1)) + 100000;
        String num = Integer.toString(authNo);
        
        // 레디스에 값 추가
        redisTemplate.opsForValue().set("signup:" + memberCheckEmailRequestDto.getEmail(), num);
        redisTemplate.expire("signup:" + memberCheckEmailRequestDto.getEmail(), 5, TimeUnit.MINUTES);

        emailService.EmailSend(memberCheckEmailRequestDto.getEmail(), num, "signup");

    }

    public void mailSendConfirm(MemberCheckEmailNumRequestDto memberCheckEmailNumRequestDto) {

        String confirm = redisTemplate.opsForValue().get("signup:" + memberCheckEmailNumRequestDto.getEmail());
        
        if(confirm==null) {
            throw new BusinessLogicException(ExceptionCode.NUM_TIMEOUT);
        }
        if(!confirm.equals(memberCheckEmailNumRequestDto.getNum())) {
            throw new BusinessLogicException(ExceptionCode.NUM_BAD_REQUEST);
        }
    }

}
