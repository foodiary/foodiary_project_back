package com.foodiary.member.service;

import java.io.IOException;
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
import com.foodiary.member.model.MemberEditPasswordRequestDto;
import com.foodiary.member.model.MemberEditRequestDto;
import com.foodiary.member.model.MemberFaqDto;
import com.foodiary.member.model.MemberFoodsResponseDto;
import com.foodiary.member.model.MemberImageDto;
import com.foodiary.member.model.MemberLikeResponseDto;
import com.foodiary.member.model.MemberNoticeInfoResponseDto;
import com.foodiary.member.model.MemberNoticeResponseDto;
import com.foodiary.member.model.MemberQuestionEditResponseDto;
import com.foodiary.member.model.MemberQuestionImageDto;
import com.foodiary.member.model.MemberQuestionResponseDto;
import com.foodiary.member.model.MemberQuestionWriteResponseDto;
import com.foodiary.member.model.MemberRecipeCommentDto;
import com.foodiary.member.model.MemberRecipeLikeResponseDto;
import com.foodiary.member.model.MemberRecipeScrapResponseDto;
import com.foodiary.member.model.MemberScrapResponseDto;
import com.foodiary.member.model.MemberSignUpRequestDto;
import com.foodiary.recipe.model.RecipeDto;

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
            
            userService.verifySave(mapper.saveMember(memberSignUpDto));

        } else {
            fileCheck(memberImage);
            HashMap<String, String> fileMap = s3Service.upload(memberImage, "member");

            memberSignUpDto.pathUpdate(fileMap.get("url"));
            userService.verifySave(mapper.saveMember(memberSignUpDto));

            MemberDto memberDto = mapper.findByLoginId(memberSignUpDto.getLoginId()).orElseThrow(() -> new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR));

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

        userService.checkUser(id);

        memberEditDto.updateId(id);
        if (memberImage == null) {
            // checkInsertUpdateDelete(mapper.updateMemberInfo(memberEditDto));

        } else {
            fileCheck(memberImage);

            HashMap<String, String> fileMap = s3Service.upload(memberImage, "member");

            memberEditDto.updatePath(fileMap.get("url"));
            // checkInsertUpdateDelete(mapper.updateMemberInfo(memberEditDto));

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
        
        userService.verifyDelete(mapper.deleteMemberImage(id));

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

        userService.verifySave(mapper.saveMemberImage(memberImageDto));

    }

    // 마이페이지에서 비밀번호 수정
    public void EditMemberPassword(MemberEditPasswordRequestDto memberEditPasswordRequestDto, int id) {
        
        // userService.checkUser(id);

        if(memberEditPasswordRequestDto.getMore_password().equals(memberEditPasswordRequestDto.getPassword())) {
            String newPassword = userService.encrypt(memberEditPasswordRequestDto.getPassword());

            userService.verifyUpdate(mapper.updateMemberPassword(newPassword, id));
        }
        throw new BusinessLogicException(ExceptionCode.MORE_PW_ERROR);

    }

    // 아이디 찾기
    public void findmemberInfoId(String email, String type) throws Exception {

        MemberDto memberDto = mapper.findByEmail(email).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        emailService.EmailSend(email, memberDto.getMemberLoginId(), type);
    }

    // 비밀번호 찾기
    public void findmemberInfoPw(String email, String loginId, String type) throws Exception {

        mapper.findByEmailAndId(email, loginId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        // 토큰 생성
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);

        String subject = email;
        Date expiration = jwtProvider.getTokenExpiration(30);

        String jwt = jwtProvider.generateAccessToken(claims, subject, expiration);
        log.info("jwt = {}", jwt);
        emailService.EmailSend(email, jwt, type);
    }

    // 비밀번호 찾기 이후 새 비밀번호로 변경
    public void memberPwConfirm(MemberCheckPwJwtRequestDto memberCheckPwJwtRequestDto) {

            if (memberCheckPwJwtRequestDto.getPassword().equals(memberCheckPwJwtRequestDto.getMore_password())) {
                String email = jwtProvider.getSubject(memberCheckPwJwtRequestDto.getJwt());

                userService.verifyUpdate(mapper.updateMemberPw(email, userService.encrypt(memberCheckPwJwtRequestDto.getPassword())));
                
            } else {
                throw new BusinessLogicException(ExceptionCode.MORE_PW_ERROR);
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

    // 하루 식단 본인이 스크랩 한 글
    public List<MemberDailyScrapResponseDto> detailDadilyScrap(int memberId) {

        // userService.checkUser(memberId);

        List<MemberDailyScrapResponseDto> memberDailyScrapResponseDtoList = mapper.findByDailyScrap(memberId);

        postSize(memberDailyScrapResponseDtoList.size());

        return memberDailyScrapResponseDtoList;
    }

    // 레시피 본인이 스크랩 한 글
    public List<MemberRecipeScrapResponseDto> detailRecipeScrap(int memberId) {

        // userService.checkUser(memberId);

        List<MemberRecipeScrapResponseDto> memberRecipeScrapResponseDtoList = mapper.findByRecipeScrap(memberId);

        postSize(memberRecipeScrapResponseDtoList.size());

        return memberRecipeScrapResponseDtoList;
    }

    public void deleteScrapDaily(int scrapId, int memberId) {
        userService.checkUser(memberId);

        // checkInsertUpdateDelete(mapper.deleteDailyScrap(scrapId));
    }

    public void deleteScrapRecipe(int scrapId, int memberId) {
        userService.checkUser(memberId);

        // checkInsertUpdateDelete(mapper.deleteRecipeScrap(scrapId));
    }

    // 하루 식단 본인이 좋아요 한 글
    public List<MemberDailyLikeResponseDto> detailDailyLike(int memberId) {
        userService.checkUser(memberId);

        List<MemberDailyLikeResponseDto> memberDailyLikeResponseDtoList = mapper.findByDailyLike(memberId);

        postSize(memberDailyLikeResponseDtoList.size());
        return memberDailyLikeResponseDtoList;
    }

    // 레시피 본인이 좋아요 한 글
    public List<MemberRecipeLikeResponseDto> detailRecipeLike(int memberId) {
        userService.checkUser(memberId);

        List<MemberRecipeLikeResponseDto> memberRecipeLikeResponseDtoList = mapper.findByRecipeLike(memberId);

        postSize(memberRecipeLikeResponseDtoList.size());

        return memberRecipeLikeResponseDtoList;
    }

    public void deleteLikeDaily(int likeId, int memberId) {
        userService.checkUser(memberId);

        // checkInsertUpdateDelete(mapper.deleteDailyLike(likeId));
    }

    public void deleteLikeRecipe(int likeId, int memberId) {
        userService.checkUser(memberId);

        // checkInsertUpdateDelete(mapper.deleteRecipeLike(likeId));
    }

    public MemberDto findByMemberIdInfo(int memberId) {

        // userService.checkUser(memberId);
        return mapper.findById(memberId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.SELECT_ERROR));
    }

    public void deleteMemberImage(int memberId) {

        // userService.checkUser(memberId);

        deleteImage(memberId);
        mapper.updateMemberImage(memberId);

    }

    // 회원 탈퇴
    public void deleteMember(int memberId) {

        userService.checkUser(memberId);

        userService.verifyDelete(mapper.deleteMember(memberId));
        
        // 회원 이미지 삭제
        deleteImage(memberId);

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

    // 하루 식단 본인이 쓴 글
    public List<DailyDto> postDailyFind(int memberId) {
        // userService.checkUser(memberId);

        List<DailyDto> dailyList = mapper.findByDaily(memberId);

        postSize(dailyList.size());

        return dailyList;
    }

    // 레시피 본인이 쓴 글
    public List<RecipeDto> postRecipeFind(int memberId) {
        // userService.checkUser(memberId);

        List<RecipeDto> recipeList = mapper.findByRecipe(memberId);

        postSize(recipeList.size());

        return recipeList;
    }

    // 하루 식단 본인이 쓴 댓글
    public List<MemberDailyCommentDto> commentDailyList(int memberId) {
        // userService.checkUser(memberId);

        List<MemberDailyCommentDto> dailyList = mapper.findByDailyComment(memberId);
        
        postSize(dailyList.size());

        return dailyList;
    }

    // 레시피 본인이 쓴 댓글
    public List<MemberRecipeCommentDto> commentRecipeList(int memberId) {
        // userService.checkUser(memberId);

        List<MemberRecipeCommentDto> recipeList = mapper.findByRecipeComment(memberId);

        postSize(recipeList.size());

        return recipeList;
    }

    public void commentDailyEdit(int memberId, int dailyCommentId, MemberCommentRequestDto memberCommentRequestDto) {
        userService.checkUser(memberId);

        // checkInsertUpdateDelete(mapper.updateDailyComment(dailyCommentId, memberCommentRequestDto.getComment()));
    }

    public void commentRecipeEdit(int memberId, int recipeComments, MemberCommentRequestDto memberCommentRequestDto) {
        userService.checkUser(memberId);

        // checkInsertUpdateDelete(mapper.updateRecipeComment(recipeComments, memberCommentRequestDto.getComment()));
    }

    public void commentDailyDelete(int memberId, int dailyCommentId) {
        userService.checkUser(memberId);

        // checkInsertUpdateDelete(mapper.deleteDailyComment(dailyCommentId));
    }

    public void commentRecipeDelete(int memberId, int recipeComments) {
        userService.checkUser(memberId);

        // checkInsertUpdateDelete(mapper.deleteRecipeComment(recipeComments));
    }

    // 공지사항 보기
    public List<MemberNoticeResponseDto> noticeList() {

        List<MemberNoticeResponseDto> memberNoticeDtoList = mapper.findByNotice();

        postSize(memberNoticeDtoList.size());
        
        return memberNoticeDtoList;
    }

    // 공지사항 상세 보기
    public MemberNoticeInfoResponseDto noticeDetail(int noticeId) {

        MemberNoticeInfoResponseDto memberNoticeInfoResponseDto = mapper.findByNoticeId(noticeId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.BAD_REQUEST));

        return memberNoticeInfoResponseDto;
    }

    // faq 보기
    public List<MemberFaqDto> faqList() {
        
        List<MemberFaqDto> memberFaqDtoList = mapper.findByFaq();
        postSize(memberFaqDtoList.size());

        return memberFaqDtoList;
    }

    // question 보기
    public List<MemberQuestionResponseDto> questionList(int memberId) {

        // userService.checkUser(memberId);
        
        List<MemberQuestionResponseDto> memberQuestionResponseDtoList = mapper.findByQuestion(memberId);

        postSize(memberQuestionResponseDtoList.size());

        return memberQuestionResponseDtoList;
    }

    // question 상세보기
    public MemberQuestionResponseDto questionDetail(int memberId, int questionId) {
        
        // userService.checkUser(memberId);
        
        MemberQuestionResponseDto memberQuestionResponseDto = mapper.findByQuestionId(questionId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.BAD_REQUEST));

        return memberQuestionResponseDto;
    }

    // question 작성하기
    public void questionWrite(MemberQuestionWriteResponseDto memberQuestionWriteResponseDto, MultipartFile memberImage) throws IOException{
        // userService.checkUser(memberQuestionWriteResponseDto.getMemberId());
        
        if (memberImage == null) {
            userService.verifySave(mapper.saveQuestion(memberQuestionWriteResponseDto));
            
        }
        else {
            fileCheck(memberImage);

            HashMap<String, String> fileMap = s3Service.upload(memberImage, "question");

            userService.verifySave(mapper.saveQuestion(memberQuestionWriteResponseDto));

            memberQuestionWriteResponseDto.pathUpdate(fileMap.get("url"));

            String fileFullName = memberImage.getOriginalFilename();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
            String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

            MemberQuestionImageDto memberQuestionImageDto = new MemberQuestionImageDto(memberQuestionWriteResponseDto.getMemberId(), memberQuestionWriteResponseDto.getQuestionId(), fileName, fileFullName,
                    fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);

            // 이미지 저장
            createMemberQuestionImage(memberQuestionImageDto);
        }
    }

    public void createMemberQuestionImage(MemberQuestionImageDto memberQuestionImageDto) {

        userService.verifySave(mapper.saveMemberQuestionImage(memberQuestionImageDto));

    }

    // question 수정하기
    public void questionEdit(int memberId, int questionId, MemberQuestionEditResponseDto memberQuestionEditResponseDto, MultipartFile memberImage) throws IOException {
        // userService.checkUser(memberId);

        // 기존 이미지 시퀀스 찾아오기
        MemberQuestionImageDto memberQuestionImageDto = mapper.findByQuestionImage(questionId);

        if(memberImage!=null) {
            // 기존 이미지 있는데 변경
            fileCheck(memberImage);

            if(memberQuestionImageDto!=null) {
                mapper.deleteQuestionImage(questionId);

                String url = "question/" + memberQuestionImageDto.getQuestionFileSaveName();
        
                // s3에서 데이터 삭제
                s3Service.deleteImage(url);

                HashMap<String, String> fileMap = s3Service.upload(memberImage, "question");

                memberQuestionEditResponseDto.pathUpadte(fileMap.get("url"));
                // question 테이블 내용 업데이트
                mapper.updateQuetion(memberQuestionEditResponseDto);

                String fileFullName = memberImage.getOriginalFilename();
                String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
                String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
    
                MemberQuestionImageDto memberQuestionImageDtoSave = new MemberQuestionImageDto(memberId, questionId, fileName, fileFullName,
                        fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);
    
                // 이미지 저장
                createMemberQuestionImage(memberQuestionImageDtoSave);

            }
            // 기존 이미지는 없는데 이미지 추가
            else {

                HashMap<String, String> fileMap = s3Service.upload(memberImage, "question");

                memberQuestionEditResponseDto.pathUpadte(fileMap.get("url"));

                mapper.updateQuetion(memberQuestionEditResponseDto);
    
                String fileFullName = memberImage.getOriginalFilename();
                String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
                String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
    
                MemberQuestionImageDto memberQuestionImageDtoSave = new MemberQuestionImageDto(memberId, questionId, fileName, fileFullName,
                        fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);
    
                // 이미지 저장
                createMemberQuestionImage(memberQuestionImageDtoSave);
            }
        }
        else {

            if(memberQuestionEditResponseDto.getImageDelete().equals("Y")) {
                // 기존 이미지 삭제
                mapper.deleteQuestionImage(questionId);
                String url = "question/" + memberQuestionImageDto.getQuestionFileSaveName();
        
                // s3에서 데이터 삭제
                s3Service.deleteImage(url);

            }

            mapper.updateQuetion(memberQuestionEditResponseDto);
        }
        
    }

    // 문의 내용 삭제
    public void deleteQeustion(int memberId, int questionId) {
        // userService.checkUser(memberId);
        
        mapper.deleteQuetion(questionId);

        MemberQuestionImageDto memberQuestionImageDto = mapper.findByQuestionImage(questionId);

        if(memberQuestionImageDto!=null) {
         
            userService.verifyDelete(mapper.deleteQuestionImage(questionId));

            String url = "question/" + memberQuestionImageDto.getQuestionFileSaveName();
    
            // s3에서 데이터 삭제
            s3Service.deleteImage(url);
            
        }
    }

    // 음식 추천 리스트
    public List<MemberFoodsResponseDto> foods(int memberId) {
        // userService.checkUser(memberId);

        return mapper.findByFoods(memberId);
    }

    // 음식 추천 좋아요, 싫어요 수정
    public void foodEdit(int memberId, int memberFoodId, String like) {
        // userService.checkUser(memberId);
        if(like.equals("Y") || like.equals("N")) {
            userService.verifyUpdate(mapper.updateMemberFood(memberFoodId, like));
        }
        else {
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
    }

    // 게시글이 없을 경우 없다고 알려줌
    private void postSize(int size) {
        if(size==0) {
            throw new BusinessLogicException(ExceptionCode.MYPAGE_NOT_FOUND);
        }
    }

}
