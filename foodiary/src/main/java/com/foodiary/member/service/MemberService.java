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
import com.foodiary.daily.model.DailyDto;
import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberCheckEmailNumRequestDto;
import com.foodiary.member.model.MemberCheckEmailRequestDto;
import com.foodiary.member.model.MemberCheckPwJwtRequestDto;
import com.foodiary.member.model.MemberCommentRequestDto;
import com.foodiary.member.model.MemberCommentResponseDto;
import com.foodiary.member.model.MemberDailyCommentDto;
import com.foodiary.member.model.MemberDailyLikeResponseDto;
import com.foodiary.member.model.MemberDailyScrapResponseDto;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberEditRequestDto;
import com.foodiary.member.model.MemberImageDto;
import com.foodiary.member.model.MemberLikeResponseDto;
import com.foodiary.member.model.MemberPostResponseDto;
import com.foodiary.member.model.MemberRecipeCommentDto;
import com.foodiary.member.model.MemberRecipeLikeResponseDto;
import com.foodiary.member.model.MemberRecipeScrapResponseDto;
import com.foodiary.member.model.MemberScrapResponseDto;
import com.foodiary.member.model.MemberSignUpRequestDto;
import com.foodiary.recipe.model.RecipeDto;

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
            
            checkInsertUpdateDelete(mapper.saveMember(memberSignUpDto));

        } else {
            fileCheck(memberImage);
            HashMap<String, String> fileMap = s3Service.upload(memberImage, "member");

            memberSignUpDto.pathUpdate(fileMap.get("url"));
            checkInsertUpdateDelete(mapper.saveMember(memberSignUpDto));

            MemberDto memberDto = mapper.findByLoginId(memberSignUpDto.getLoginId()).orElseThrow(() -> new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR));

            String fileFullName = memberImage.getOriginalFilename();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
            String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

            MemberImageDto memberImageDto = new MemberImageDto(memberDto.getMemberId(), fileName, fileFullName,
                    fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);

            createMemberImage(memberImageDto);
        }

    }

    public void updateMember(MemberEditRequestDto memberEditDto, int id, MultipartFile memberImage) throws Exception {

        userService.checkUser(id);

        memberEditDto.updateId(id);
        if (memberImage == null) {
            checkInsertUpdateDelete(mapper.updateMemberInfo(memberEditDto));

        } else {
            fileCheck(memberImage);

            HashMap<String, String> fileMap = s3Service.upload(memberImage, "member");

            memberEditDto.updatePath(fileMap.get("url"));
            checkInsertUpdateDelete(mapper.updateMemberInfo(memberEditDto));

            String fileFullName = memberImage.getOriginalFilename();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
            String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

            MemberImageDto memberImageDto = mapper.findByIdFile(id);

            // 기존에 이미지가 있었던 경우
            if (memberImageDto != null) {
                // 기존 이미지 삭제
                deleteImage(id);
            }

            MemberImageDto memberImageDtoUpdate = new MemberImageDto(id, fileName, fileFullName,
                    fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);

            createMemberImage(memberImageDtoUpdate);
        }
    }

    // 회원 이미지 삭제
    private void deleteImage(int id) {
        // 회원 이미지 테이블에서 삭제
        MemberImageDto memberImageDto = mapper.findByIdFile(id);
        String url = "member/" + memberImageDto.getMemberFileSaveName();
        
        // s3에서 데이터 삭제
        s3Service.deleteImage(url);
        
        checkInsertUpdateDelete(mapper.deleteMemberImage(id));

    }

    // 아이디 중복 검사
    public void findMemberLoginId(String loginId) {

        mapper.findByLoginId(loginId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.LOGINID_BAD_REQUEST));

    }

    // 이메일 중복 검사
    public void findmemberEmail(String email) {

        mapper.findByEmail(email).orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMAIL_BAD_REQUEST));
    }

    // 닉네임 중복 검사
    public void findmemberNickname(String nickname) {

        mapper.findByNickname(nickname).orElseThrow(() -> new BusinessLogicException(ExceptionCode.NICKNAME_BAD_REQUEST));
    }

    public void createMemberImage(MemberImageDto memberImageDto) {

        checkInsertUpdateDelete(mapper.saveMemberImage(memberImageDto));

    }

    public void EditMemberPassWord(String password, int id) {

        userService.checkUser(id);

        String newPassword = userService.encrypt(password);

        int updateChack = mapper.updateMemberPassword(newPassword, id);
        if(updateChack < 1) {
            throw new BusinessLogicException(ExceptionCode.UPDATE_ERROR);
        }
    }

    // 아이디 찾기
    public void findmemberInfoId(String email, String type) throws Exception {

        MemberDto memberDto = mapper.findByEmail(email).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        emailService.EmailSend(email, memberDto.getMemberLoginId(), type);
    }

    public void findmemberInfoPw(String email, String loginId, String type) throws Exception {

        MemberDto memberDto = mapper.findByEmailAndId(email, loginId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);

        String subject = email;
        Date expiration = jwtProvider.getTokenExpiration(30);

        String jwt = jwtProvider.generateAccessToken(claims, subject, expiration);
        emailService.EmailSend(email, jwt, type);
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
        userService.checkUser(memberId);

        checkInsertUpdateDelete(mapper.deleteDailyScrap(scrapId));
    }

    public void deleteScrapRecipe(int scrapId, int memberId) {
        userService.checkUser(memberId);

        checkInsertUpdateDelete(mapper.deleteRecipeScrap(scrapId));
    }

    public MemberLikeResponseDto detailLike(int memberId) {

        List<MemberDailyLikeResponseDto> memberDailyLikeResponseDtoList = mapper.findByDailyLike(memberId);

        List<MemberRecipeLikeResponseDto> memberRecipeLikeResponseDtoList = mapper.findByRecipeLike(memberId);

        MemberLikeResponseDto memberLikeResponseDto = new MemberLikeResponseDto(memberDailyLikeResponseDtoList,
                memberRecipeLikeResponseDtoList);
        return memberLikeResponseDto;
    }

    public void deleteLikeDaily(int likeId, int memberId) {
        userService.checkUser(memberId);

        checkInsertUpdateDelete(mapper.deleteDailyLike(likeId));
    }

    public void deleteLikeRecipe(int likeId, int memberId) {
        userService.checkUser(memberId);

        checkInsertUpdateDelete(mapper.deleteRecipeLike(likeId));
    }

    public MemberDto findByMemberIdInfo(int memberId) {

        userService.checkUser(memberId);
        return mapper.findById(memberId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.SELECT_ERROR));
    }

    public void deleteMemberImage(int memberId) {

        deleteImage(memberId);
        mapper.updateMemberImage(memberId);

    }

    public void deleteMember(int memberId) {

        userService.checkUser(memberId);

        checkInsertUpdateDelete(mapper.deleteMember(memberId));
        
        // 회원 이미지 삭제
        deleteImage(memberId);

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

    public MemberPostResponseDto postFind(int memberId) {
        userService.checkUser(memberId);

        List<DailyDto> dailyList = mapper.findByDaily(memberId);

        if(dailyList.size()==0) {
            DailyDto dailyDto = new DailyDto();
            dailyDto.noAdd();
            dailyList.add(dailyDto);
        }
        List<RecipeDto> recipeList = mapper.findByRecipe(memberId);

        if(recipeList.size()==0) {
            RecipeDto recipeDto = new RecipeDto();
            recipeDto.noAdd();
            recipeList.add(recipeDto);
        }

        MemberPostResponseDto memberPostResponseDto = new MemberPostResponseDto(dailyList, recipeList);

        return memberPostResponseDto;
    }

    public MemberCommentResponseDto commentList(int memberId) {
        userService.checkUser(memberId);

        List<MemberDailyCommentDto> dailyList = mapper.findByDailyComment(memberId);
        
        if(dailyList.size()==0) {
            MemberDailyCommentDto memberDailyCommentDto = new MemberDailyCommentDto();
            memberDailyCommentDto.noAdd();
            dailyList.add(memberDailyCommentDto);
        }

        List<MemberRecipeCommentDto> recipeList = mapper.findByRecipeComment(memberId);

        if(recipeList.size() == 0) {
            MemberRecipeCommentDto memberRecipeCommentDto = new MemberRecipeCommentDto();
            memberRecipeCommentDto.noAdd();
            recipeList.add(memberRecipeCommentDto);
        }
        MemberCommentResponseDto memberCommentResponseDto = new MemberCommentResponseDto(dailyList, recipeList);

        return memberCommentResponseDto;
    }

    public void commentDailyEdit(int memberId, int dailyCommentId, MemberCommentRequestDto memberCommentRequestDto) {
        userService.checkUser(memberId);

        checkInsertUpdateDelete(mapper.updateDailyComment(dailyCommentId, memberCommentRequestDto.getComment()));
    }

    public void commentRecipeEdit(int memberId, int recipeComments, MemberCommentRequestDto memberCommentRequestDto) {
        userService.checkUser(memberId);

        checkInsertUpdateDelete(mapper.updateRecipeComment(recipeComments, memberCommentRequestDto.getComment()));
    }

    public void commentDailyDelete(int memberId, int dailyCommentId) {
        userService.checkUser(memberId);

        checkInsertUpdateDelete(mapper.deleteDailyComment(dailyCommentId));
    }

    public void commentRecipeDelete(int memberId, int recipeComments) {
        userService.checkUser(memberId);

        checkInsertUpdateDelete(mapper.deleteRecipeComment(recipeComments));
    }

    // insert, update, delete 확인
    private void checkInsertUpdateDelete(int checkNum) {
        if(checkNum < 1) {
            throw new BusinessLogicException(ExceptionCode.DELETE_ERROR);
        }
    }

}
