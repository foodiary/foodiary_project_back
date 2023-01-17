package com.foodiary.member.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import com.foodiary.member.model.MemberDailyCommentDetailResponseDto;
import com.foodiary.member.model.MemberDailyCommentDto;
import com.foodiary.member.model.MemberDailyResponseDto;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberEditPasswordRequestDto;
import com.foodiary.member.model.MemberEditRequestDto;
import com.foodiary.member.model.MemberFaqDto;
import com.foodiary.member.model.MemberFoodsResponseDto;
import com.foodiary.member.model.MemberImageDto;
import com.foodiary.member.model.MemberNoticeInfoResponseDto;
import com.foodiary.member.model.MemberNoticeResponseDto;
import com.foodiary.member.model.MemberOtherMemberResponseDto;
import com.foodiary.member.model.MemberPostLikeResponseDto;
import com.foodiary.member.model.MemberPostScrapResponseDto;
import com.foodiary.member.model.MemberQuestionDetailResponseDto;
import com.foodiary.member.model.MemberQuestionEditResponseDto;
import com.foodiary.member.model.MemberQuestionImageDto;
import com.foodiary.member.model.MemberQuestionResponseDto;
import com.foodiary.member.model.MemberQuestionWriteResponseDto;
import com.foodiary.member.model.MemberRecipeCommentDetailResponseDto;
import com.foodiary.member.model.MemberRecipeCommentDto;
import com.foodiary.member.model.MemberResponseDto;
import com.foodiary.member.model.MemberSignUpRequestDto;
import com.foodiary.recipe.model.RecipeDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(rollbackFor = {Exception.class, BusinessLogicException.class, MorePasswordException.class})
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

    // 회원 정보 수정
    public void updateMember(MemberEditRequestDto memberEditDto, int id) throws Exception {

        // userService.checkUser(id);

        MemberResponseDto memberDto = mapper.findById(id).orElseThrow(() -> new BusinessLogicException(ExceptionCode.SELECT_ERROR));

        if(!memberDto.getMemberNickName().equals(memberEditDto.getNickName()) && mapper.findByNickname(memberEditDto.getNickName()).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.NICKNAME_BAD_REQUEST);
        }

        memberEditDto.updateId(id);

        // 회원 정보 수정
        userService.verifyUpdate(mapper.updateMemberInfo(memberEditDto));

        // 데일리 테이블 작성자 업데이트
        userService.verifyUpdate(mapper.updateDailyWriter(id, memberEditDto.getNickName()));

        // 데일리 코멘트 테이블 작성자 업데이트
        userService.verifyUpdate(mapper.updateDailyCommentWriter(id, memberEditDto.getNickName()));

        // 레시피 테이블 작성자 업데이트
        // userService.verifyUpdate(mapper.updateRecipeWriter(id, memberEditDto.getNickName()));

        // 레시피 코멘트 테이블 작성자 업데이트
        // userService.verifyUpdate(mapper.updateRecipeCommentWriter(id, memberEditDto.getNickName()));

    }

    // 회원 이미지 삭제 -> s3랑 이미지테이블에서 정보 삭제
    private void deleteImage(int id) {
        
        MemberImageDto memberImageDto = mapper.findByIdFile(id);
        
        if(memberImageDto!=null) {
            String url = "member/" + memberImageDto.getMemberFileSaveName();
        
            // s3에서 데이터 삭제
            s3Service.deleteImage(url);
            
            userService.verifyDelete(mapper.deleteMemberImage(id));
        }

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

    // 마이페이지에서 이미지 수정
    public void editMemberImage(int memberId, MultipartFile memberImage, String memberPath) throws IOException{

        // userService.checkUser(memberId);
        
        if(memberImage!=null) {
            if(!memberImage.isEmpty()) {
            fileCheck(memberImage);

            if(memberPath!=null) {
                if(!memberPath.isBlank()) {
                    int index = memberPath.indexOf("member");
                    System.out.println("패스 확인 : "+memberPath.substring(index));
                    String deletePath = memberPath.substring(index);
                    s3Service.deleteImage(deletePath);
        
                    // 이미지 테이블에서 이미지 정보 삭제
                    userService.verifyDelete(mapper.deleteMemberImage(memberId));
                }
            }
            HashMap<String, String> fileMap = s3Service.upload(memberImage, "member");

            String fileFullName = memberImage.getOriginalFilename();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
            String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

            MemberImageDto memberImageDto = new MemberImageDto(memberId, fileName, fileFullName,
                    fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);

            // 이미지 테이블에 저장
            createMemberImage(memberImageDto);

            // 이미지 경로만 업데이트 추가
            userService.verifyUpdate(mapper.updateMemberImage(memberId , fileMap.get("url")));
            }
            else {
                throw new BusinessLogicException(ExceptionCode.FILE_NOT_FOUND);
            }
        }
        else {
            throw new BusinessLogicException(ExceptionCode.FILE_NOT_FOUND);
        }

    }
    
    // 마이페이지에서 비밀번호 수정
    public void editMemberPassword(MemberEditPasswordRequestDto memberEditPasswordRequestDto, int id) {
        
        // suserService.checkUser(id);

        if(memberEditPasswordRequestDto.getMore_password().equals(memberEditPasswordRequestDto.getPassword())) {
            String newPassword = userService.encrypt(memberEditPasswordRequestDto.getPassword());

            userService.verifyUpdate(mapper.updateMemberPassword(newPassword, id));
        }
        else {
            throw new BusinessLogicException(ExceptionCode.MORE_PW_ERROR);
        }

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

    // 본인이 스크랩 한 글
    public List<MemberPostScrapResponseDto> detailScrap(int memberId) {

        // userService.checkUser(memberId);

        List<MemberPostScrapResponseDto> memberPostScrapResponseDtoList  = mapper.postScrap(memberId);

        postSize(memberPostScrapResponseDtoList.size());

        return memberPostScrapResponseDtoList;
    }

    // 본인이 좋아요 한 글
    public List<MemberPostLikeResponseDto> detailLike(int memberId) {
        // userService.checkUser(memberId);

        List<MemberPostLikeResponseDto> memberPostLikeResponseDtoList = mapper.postLike(memberId);

        postSize(memberPostLikeResponseDtoList.size());

        return memberPostLikeResponseDtoList;
    }

    // 회원 정보 보기(본인꺼) 마이페이지
    public MemberResponseDto findByMemberIdInfo(int memberId) {

        // userService.checkUser(memberId);
        return mapper.findById(memberId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.SELECT_ERROR));
    }

    // 회원 이미지 삭제
    public void deleteMemberImage(int memberId) {

        // userService.checkUser(memberId);

        deleteImage(memberId);

        userService.verifyUpdate(mapper.updateMemberImageDelete(memberId));

    }

    // 회원 탈퇴
    public void deleteMember(int memberId) {

        // userService.checkUser(memberId);

        // 회원 이미지 삭제
        deleteImage(memberId);

        userService.verifyDelete(mapper.deleteMember(memberId));

    }

    // 회원가입 이메일 발송
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

    // 회원가입 이메일 확인
    public void mailSendConfirm(MemberCheckEmailNumRequestDto memberCheckEmailNumRequestDto) {

        if(redisTemplate.hasKey("signup:" + memberCheckEmailNumRequestDto.getEmail())) {
            String confirm = redisTemplate.opsForValue().get("signup:" + memberCheckEmailNumRequestDto.getEmail());
        
            if(confirm==null) {
                throw new BusinessLogicException(ExceptionCode.NUM_TIMEOUT);
            }
            if(!confirm.equals(memberCheckEmailNumRequestDto.getNum())) {
                throw new BusinessLogicException(ExceptionCode.NUM_BAD_REQUEST);
            }
        }
    }

    // 하루 식단 본인이 쓴 글 
    public List<MemberDailyResponseDto> postDailyFind(int memberId) {
        // userService.checkUser(memberId);

        List<MemberDailyResponseDto> dailyList = mapper.findByDaily(memberId);

        postSize(dailyList.size());

        return dailyList;
    }

    // 다른 사람 프로필 조회 (게시글, 닉네임, 프로필 이미지, 프로필 메세지) 
    public List<MemberOtherMemberResponseDto> findMember(int memberId) {

        MemberDto memberDto = mapper.findByProfile(memberId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        if(memberDto.getMemberYn().equals("Y")) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_EXISTS);
        }
        List<MemberOtherMemberResponseDto> dailyList = mapper.findByMember(memberId);

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

        // 하루 식단 본인이 쓴 댓글 상세조회
        public MemberDailyCommentDetailResponseDto commentDailyDetail(int memberId, int dailyId, int dailyCommentId) {
            // userService.checkUser(memberId);
    
            MemberDailyCommentDetailResponseDto memberDailyCommentDetailResponseDto = mapper.findByDailyCommentId(memberId, dailyId, dailyCommentId)
                            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BAD_REQUEST));
            
            return memberDailyCommentDetailResponseDto;
        }
    
        // 레시피 본인이 쓴 댓글 상세조회
        public MemberRecipeCommentDetailResponseDto commentRecipeDetail(int memberId, int recipeId, int recipeCommentId) {
            // userService.checkUser(memberId);
    
            MemberRecipeCommentDetailResponseDto memberRecipeCommentDetailResponseDto = mapper.findByRecipeCommentId(memberId, recipeId, recipeCommentId)
                            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BAD_REQUEST));
    
            return memberRecipeCommentDetailResponseDto;
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
    public MemberQuestionDetailResponseDto questionDetail(int memberId, int questionId) {
        
        // userService.checkUser(memberId);
        
        MemberQuestionDetailResponseDto memberQuestionResponseDto = mapper.findByQuestionId(questionId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.BAD_REQUEST));

        return memberQuestionResponseDto;
    }

    // question 작성하기
    public void questionWrite(MemberQuestionWriteResponseDto memberQuestionWriteResponseDto, MultipartFile memberImage) throws IOException{
        // userService.checkUser(memberQuestionWriteResponseDto.getMemberId());
        
        if (memberImage == null) {
            userService.verifySave(mapper.saveQuestion(memberQuestionWriteResponseDto));
        }
        else {
            if(memberImage.isEmpty()) {
                userService.verifySave(mapper.saveQuestion(memberQuestionWriteResponseDto));
            }
            else {
                fileCheck(memberImage);

                HashMap<String, String> fileMap = s3Service.upload(memberImage, "question");
    
                memberQuestionWriteResponseDto.pathUpdate(fileMap.get("url"));

                userService.verifySave(mapper.saveQuestion(memberQuestionWriteResponseDto));
    
                String fileFullName = memberImage.getOriginalFilename();
                String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
                String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
    
                MemberQuestionImageDto memberQuestionImageDto = new MemberQuestionImageDto(memberQuestionWriteResponseDto.getMemberId(), memberQuestionWriteResponseDto.getQuestionId(), fileName, fileFullName,
                        fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);
    
                // 이미지 저장
                createMemberQuestionImage(memberQuestionImageDto);
    
            }
        }
    }

    // 이미지 테이블에 이미지 저장
    public void createMemberQuestionImage(MemberQuestionImageDto memberQuestionImageDto) {

        userService.verifySave(mapper.saveMemberQuestionImage(memberQuestionImageDto));

    }

    // QNA s3에 이미지 업로드 및 이미지 테이블에 이미지 저장
    private String qnaImageUploadS3(MultipartFile memberImage, String type, int memberId, int questionId) throws IOException{
        
        fileCheck(memberImage);
        
        HashMap<String, String> fileMap = s3Service.upload(memberImage, type);
    
        String fileFullName = memberImage.getOriginalFilename();
        String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
        String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

        MemberQuestionImageDto memberQuestionImageDtoSave = new MemberQuestionImageDto(memberId, questionId, fileName, fileFullName,
                fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);

        // 이미지 저장
        createMemberQuestionImage(memberQuestionImageDtoSave);

        log.info("이미지 저장 완료 : 이미지 경로 {} ", fileMap.get("url"));
        return fileMap.get("url");
    }

    private void qnaImageDeleteS3(String memberPath, int questionId, int memberId) {

        userService.verifyDelete(mapper.deleteQuestionImage(questionId, memberId)); // memberId 조건 추가
                    
        int index = memberPath.indexOf("question");

        // TODO : 테스트 끝나고 시스아웃 지우기
        System.out.println("패스 확인 : "+memberPath.substring(index));
        String deletePath = memberPath.substring(index);

        // s3에서 데이터 삭제
        s3Service.deleteImage(deletePath);
    }

    // question 수정하기
    public void questionEdit(int memberId, int questionId, MemberQuestionEditResponseDto memberQuestionEditResponseDto, MultipartFile memberImage) throws IOException {
        // userService.checkUser(memberId);

        // 수정하기
        if(memberImage==null) {
            // 기존 이미지 있을 경우
            String memberPath = memberQuestionEditResponseDto.getQuestionPath();
            if(memberPath!=null) {
                if(!memberPath.isBlank()) {
                    // 기존 이미지 지우고 싶어서 이미지 파일을 안줌 -> 넘겨준 이미지패스로 이미지를 삭제함
                    qnaImageDeleteS3(memberPath, questionId, memberId);
                    memberQuestionEditResponseDto.pathUpadte(null);
                }
                else {
                    throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
                }
            }
            else {
                // 기존 이미지를 변경안하려고 이미지 파일을 안줌 ->
                MemberQuestionImageDto memberQuestionImageDto = 
                    mapper.findByQuestionImage(questionId, memberId);
                if(memberQuestionImageDto!=null) {
                    memberQuestionEditResponseDto.pathUpadte(memberQuestionImageDto.getQuestionFilePath());
                //     throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
                }
                else {
                    memberQuestionEditResponseDto.pathUpadte(null);
                }
                
            }
        }
        else {
            // 이미지 파일을 준경우
            if(!memberImage.isEmpty()) {
                // 새로운 이미지 업데이트, 기존 이미지 삭제
                MemberQuestionImageDto memberQuestionImageDto = mapper.findByQuestionImage(questionId, memberId);
                if(memberQuestionImageDto!=null) {
                    qnaImageDeleteS3(memberQuestionImageDto.getQuestionFilePath(), questionId, memberId);
                }
                String url = qnaImageUploadS3(memberImage, "question", memberId, questionId);
                memberQuestionEditResponseDto.pathUpadte(url);
            }
            else {
                // 이미지가 비어있을때
                throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
            }
        }
        // 회원 정보 저장
        memberQuestionEditResponseDto.memberIdUpadte(memberId);
        memberQuestionEditResponseDto.questionIdUpadte(questionId);
        userService.verifyUpdate(mapper.updateQuetion(memberQuestionEditResponseDto)); // memberId 조건 추가
        
    }

    // 문의 내용 삭제
    public void deleteQeustion(int memberId, int questionId) {
        // userService.checkUser(memberId);
        
        userService.verifyDelete(mapper.deleteQuetion(questionId, memberId));

        MemberQuestionImageDto memberQuestionImageDto = mapper.findByQuestionImage(questionId, memberId);
        
        if(memberQuestionImageDto!=null) {
            userService.verifyDelete(mapper.deleteQuestionImage(questionId, memberId));

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
