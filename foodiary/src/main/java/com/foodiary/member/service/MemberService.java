package com.foodiary.member.service;

import java.io.IOException;
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
import com.foodiary.common.email.service.EmailService;
import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.common.exception.MorePasswordException;
import com.foodiary.common.exception.VaildErrorResponseDto;
import com.foodiary.common.s3.S3Service;
import com.foodiary.food.service.FoodService;
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
import com.foodiary.member.model.MemberProfileResponseDto;
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

    private final FoodService foodService;

    public void createdMember(MemberSignUpRequestDto memberSignUpDto, MultipartFile memberImage) throws Exception {

        // ?????? ??? ????????? ???????????? ??????
        if (memberSignUpDto.getMore_password().equals(memberSignUpDto.getPassword()) == false) {

            VaildErrorResponseDto vaildErrorDto = new VaildErrorResponseDto("more_password", "??????????????? ???????????? ????????????");

            throw new MorePasswordException(vaildErrorDto);
        }

        // ???????????? ??????
        if (memberSignUpDto.getRequiredTerms().equals("N")) {
            throw new BusinessLogicException(ExceptionCode.TERMS_ERROR);
        }

        String newPassword = userService.encrypt(memberSignUpDto.getPassword());

        memberSignUpDto.passwordUpdate(newPassword);

        // ????????? ?????? ????????? ?????? ?????? ??????
        if (memberImage == null) {
            
            userService.verifySave(mapper.saveMember(memberSignUpDto));
            foodService.weekRecommendMenu(memberSignUpDto.getMemberId());

        } else {
            fileCheck(memberImage);
            HashMap<String, String> fileMap = s3Service.upload(memberImage, "member");

            memberSignUpDto.pathUpdate(fileMap.get("url"));
            userService.verifySave(mapper.saveMember(memberSignUpDto));
            foodService.weekRecommendMenu(memberSignUpDto.getMemberId());

            String fileFullName = memberImage.getOriginalFilename();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
            String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

            MemberImageDto memberImageDto = new MemberImageDto(memberSignUpDto.getMemberId(), fileName, fileFullName,
                    fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);

            // ????????? ??????
            createMemberImage(memberImageDto);
        }

    }

    // ?????? ?????? ??????
    public void updateMember(MemberEditRequestDto memberEditDto, int id) throws Exception {

        userService.checkUser(id);

        MemberResponseDto memberDto = mapper.findById(id).orElseThrow(() -> new BusinessLogicException(ExceptionCode.SELECT_ERROR));

        if(!memberDto.getMemberNickName().equals(memberEditDto.getNickName()) && mapper.findByNickname(memberEditDto.getNickName()).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.NICKNAME_BAD_REQUEST);
        }

        memberEditDto.updateId(id);

        // ?????? ?????? ??????
        userService.verifyUpdate(mapper.updateMemberInfo(memberEditDto));

        // ????????? ????????? ????????? ????????????
        if(mapper.findByMemberDaily(id).size()>0) {
            userService.verifyUpdate(mapper.updateDailyWriter(id, memberEditDto.getNickName()));
        }

        // ????????? ????????? ????????? ????????? ????????????
        if(mapper.findByMemberDailyComment(id).size()>0) {
            userService.verifyUpdate(mapper.updateDailyCommentWriter(id, memberEditDto.getNickName()));
        }

        // ????????? ????????? ????????? ????????????
        // userService.verifyUpdate(mapper.updateRecipeWriter(id, memberEditDto.getNickName()));

        // ????????? ????????? ????????? ????????? ????????????
        // userService.verifyUpdate(mapper.updateRecipeCommentWriter(id, memberEditDto.getNickName()));

    }

    // ?????? ????????? ?????? -> s3??? ???????????????????????? ?????? ??????
    private void deleteImage(int id) {
        
        MemberImageDto memberImageDto = mapper.findByIdFile(id);
        
        if(memberImageDto!=null) {
            String url = "member/" + memberImageDto.getMemberFileSaveName();
        
            // s3?????? ????????? ??????
            s3Service.deleteImage(url);
            
            userService.verifyDelete(mapper.deleteMemberImage(id));
        }

    }

    // ????????? ?????? ??????
    public void findMemberLoginId(String loginId) {

        if(mapper.findByLoginId(loginId).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.LOGINID_BAD_REQUEST);
        }
    }

    // ????????? ?????? ??????
    public void findmemberEmail(String email) {

        if(mapper.findByEmail(email).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_BAD_REQUEST);
        }
    }

    // ????????? ?????? ??????
    public void findmemberNickname(String nickname) {

        if(mapper.findByNickname(nickname).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.NICKNAME_BAD_REQUEST);
        }
    }

    public void createMemberImage(MemberImageDto memberImageDto) {

        userService.verifySave(mapper.saveMemberImage(memberImageDto));

    }

    // ????????????????????? ????????? ??????
    public void editMemberImage(int memberId, MultipartFile memberImage, String memberPath) throws IOException{

        userService.checkUser(memberId);
        
        if(memberImage!=null) {
            if(!memberImage.isEmpty()) {
            fileCheck(memberImage);

            if(memberPath!=null) {
                if(!memberPath.isBlank()) {
                    int index = memberPath.indexOf("member");
                    System.out.println("?????? ?????? : "+memberPath.substring(index));
                    String deletePath = memberPath.substring(index);
                    s3Service.deleteImage(deletePath);
        
                    // ????????? ??????????????? ????????? ?????? ??????
                    userService.verifyDelete(mapper.deleteMemberImage(memberId));
                }
            }
            HashMap<String, String> fileMap = s3Service.upload(memberImage, "member");

            String fileFullName = memberImage.getOriginalFilename();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
            String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

            MemberImageDto memberImageDto = new MemberImageDto(memberId, fileName, fileFullName,
                    fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);

            // ????????? ???????????? ??????
            createMemberImage(memberImageDto);

            // ????????? ????????? ???????????? ??????
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
    
    // ????????????????????? ???????????? ??????
    public void editMemberPassword(MemberEditPasswordRequestDto memberEditPasswordRequestDto, int id) {
        
        userService.checkUser(id);

        if(memberEditPasswordRequestDto.getMore_password().equals(memberEditPasswordRequestDto.getPassword())) {
            String newPassword = userService.encrypt(memberEditPasswordRequestDto.getPassword());

            userService.verifyUpdate(mapper.updateMemberPassword(newPassword, id));
        }
        else {
            throw new BusinessLogicException(ExceptionCode.MORE_PW_ERROR);
        }

    }

    // ????????? ??????
    public void findmemberInfoId(String email, String type) throws Exception {

        MemberDto memberDto = mapper.findByEmail(email).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        emailService.EmailSend(email, memberDto.getMemberLoginId(), type);
    }

    // ???????????? ??????
    public void findmemberInfoPw(String email, String loginId, String type) throws Exception {

        mapper.findByEmailAndId(email, loginId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        // ?????? ??????
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);

        String subject = email;
        Date expiration = jwtProvider.getTokenExpiration(30);

        String jwt = jwtProvider.generateAccessToken(claims, subject, expiration);
        log.info("jwt = {}", jwt);
        emailService.EmailSend(email, jwt, type);
    }

    // ???????????? ?????? ?????? ??? ??????????????? ??????
    public void memberPwConfirm(MemberCheckPwJwtRequestDto memberCheckPwJwtRequestDto) {

        if (memberCheckPwJwtRequestDto.getPassword().equals(memberCheckPwJwtRequestDto.getMore_password())) {
            String email = jwtProvider.getSubject(memberCheckPwJwtRequestDto.getJwt());

            userService.verifyUpdate(mapper.updateMemberPw(email, userService.encrypt(memberCheckPwJwtRequestDto.getPassword())));
            
        } else {
            throw new BusinessLogicException(ExceptionCode.MORE_PW_ERROR);
        }
    }

    // ?????? ????????? ??????
    public void fileCheck(MultipartFile memberImage) {
        String ext = memberImage.getOriginalFilename()
                .substring(memberImage.getOriginalFilename().lastIndexOf(".") + 1);

        if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png")) {
        } else {
            throw new BusinessLogicException(ExceptionCode.FILE_BAD_REQUEST);
        }
    }

    // ????????? ????????? ??? ???
    public List<MemberPostScrapResponseDto> detailScrap(int memberId) {

        userService.checkUser(memberId);

        List<MemberPostScrapResponseDto> memberPostScrapResponseDtoList  = mapper.postScrap(memberId);

        postSize(memberPostScrapResponseDtoList.size());

        return memberPostScrapResponseDtoList;
    }

    // ????????? ????????? ??? ???
    public List<MemberPostLikeResponseDto> detailLike(int memberId) {
        userService.checkUser(memberId);

        List<MemberPostLikeResponseDto> memberPostLikeResponseDtoList = mapper.postLike(memberId);

        postSize(memberPostLikeResponseDtoList.size());

        return memberPostLikeResponseDtoList;
    }

    // ?????? ?????? ??????(?????????) ???????????????
    public MemberResponseDto findByMemberIdInfo(int memberId) {

        userService.checkUser(memberId);
        return mapper.findById(memberId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.SELECT_ERROR));
    }

    // ?????? ????????? ??????
    public void deleteMemberImage(int memberId) {

        userService.checkUser(memberId);

        deleteImage(memberId);

        userService.verifyUpdate(mapper.updateMemberImageDelete(memberId));

    }

    // ?????? ??????
    public void deleteMember(int memberId) {

        userService.checkUser(memberId);

        // ?????? ????????? ??????
        deleteImage(memberId);

        userService.verifyDelete(mapper.deleteMember(memberId));

    }

    // ???????????? ????????? ??????
    public void mailSend(MemberCheckEmailRequestDto memberCheckEmailRequestDto) throws Exception {

        // ????????? ?????? ?????? ????????? 
        findmemberEmail(memberCheckEmailRequestDto.getEmail());

        // 6?????? ???????????? ??????
        int authNo = (int) (Math.random() * (999999 - 100000 + 1)) + 100000;
        String num = Integer.toString(authNo);
        
        // ???????????? ??? ??????
        redisTemplate.opsForValue().set("signup:" + memberCheckEmailRequestDto.getEmail(), num);
        redisTemplate.expire("signup:" + memberCheckEmailRequestDto.getEmail(), 5, TimeUnit.MINUTES);

        emailService.EmailSend(memberCheckEmailRequestDto.getEmail(), num, "signup");

    }

    // ???????????? ????????? ??????
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

    // ?????? ?????? ????????? ??? ??? 
    public List<MemberDailyResponseDto> postDailyFind(int memberId) {
        userService.checkUser(memberId);

        List<MemberDailyResponseDto> dailyList = mapper.findByDaily(memberId);

        postSize(dailyList.size());

        return dailyList;
    }

    // ?????? ?????? ????????? ?????? (?????????, ?????????, ????????? ?????????, ????????? ?????????) 
    public MemberOtherMemberResponseDto findMember(int memberId) {

        MemberDto memberDto = mapper.findByProfile(memberId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        if(memberDto.getMemberYn().equals("Y")) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_EXISTS);
        }
        List<MemberDailyResponseDto> memberDailyResponseDtos = mapper.findByDaily(memberId);

        MemberProfileResponseDto member = new MemberProfileResponseDto(memberDto.getMemberNickName(), memberDto.getMemberProfile(), memberDto.getMemberPath());

        MemberOtherMemberResponseDto memberOtherMemberResponseDto = new MemberOtherMemberResponseDto(memberId, member, memberDailyResponseDtos);

        return memberOtherMemberResponseDto;
    }

    // ????????? ????????? ??? ???
    public List<RecipeDto> postRecipeFind(int memberId) {
        userService.checkUser(memberId);

        List<RecipeDto> recipeList = mapper.findByRecipe(memberId);

        postSize(recipeList.size());

        return recipeList;
    }

    // ?????? ?????? ????????? ??? ??????
    public List<MemberDailyCommentDto> commentDailyList(int memberId) {
        userService.checkUser(memberId);

        List<MemberDailyCommentDto> dailyList = mapper.findByDailyComment(memberId);
        
        postSize(dailyList.size());

        return dailyList;
    }

    // ????????? ????????? ??? ??????
    public List<MemberRecipeCommentDto> commentRecipeList(int memberId) {
        userService.checkUser(memberId);

        List<MemberRecipeCommentDto> recipeList = mapper.findByRecipeComment(memberId);

        postSize(recipeList.size());

        return recipeList;
    }

        // ?????? ?????? ????????? ??? ?????? ????????????
        public MemberDailyCommentDetailResponseDto commentDailyDetail(int memberId, int dailyId, int dailyCommentId) {
            userService.checkUser(memberId);
    
            MemberDailyCommentDetailResponseDto memberDailyCommentDetailResponseDto = mapper.findByDailyCommentId(memberId, dailyId, dailyCommentId)
                            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BAD_REQUEST));
            
            return memberDailyCommentDetailResponseDto;
        }
    
        // ????????? ????????? ??? ?????? ????????????
        public MemberRecipeCommentDetailResponseDto commentRecipeDetail(int memberId, int recipeId, int recipeCommentId) {
            userService.checkUser(memberId);
    
            MemberRecipeCommentDetailResponseDto memberRecipeCommentDetailResponseDto = mapper.findByRecipeCommentId(memberId, recipeId, recipeCommentId)
                            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BAD_REQUEST));
    
            return memberRecipeCommentDetailResponseDto;
        }


    // ???????????? ??????
    public List<MemberNoticeResponseDto> noticeList() {

        List<MemberNoticeResponseDto> memberNoticeDtoList = mapper.findByNotice();

        postSize(memberNoticeDtoList.size());
        
        return memberNoticeDtoList;
    }

    // ???????????? ?????? ??????
    public MemberNoticeInfoResponseDto noticeDetail(int noticeId) {

        MemberNoticeInfoResponseDto memberNoticeInfoResponseDto = mapper.findByNoticeId(noticeId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.BAD_REQUEST));

        return memberNoticeInfoResponseDto;
    }

    // faq ??????
    public List<MemberFaqDto> faqList() {
        
        List<MemberFaqDto> memberFaqDtoList = mapper.findByFaq();
        postSize(memberFaqDtoList.size());

        return memberFaqDtoList;
    }

    // question ??????
    public List<MemberQuestionResponseDto> questionList(int memberId) {

        userService.checkUser(memberId);
        
        List<MemberQuestionResponseDto> memberQuestionResponseDtoList = mapper.findByQuestion(memberId);

        postSize(memberQuestionResponseDtoList.size());

        return memberQuestionResponseDtoList;
    }

    // question ????????????
    public MemberQuestionDetailResponseDto questionDetail(int memberId, int questionId) {
        
        userService.checkUser(memberId);
        
        MemberQuestionDetailResponseDto memberQuestionResponseDto = mapper.findByQuestionId(questionId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.BAD_REQUEST));

        return memberQuestionResponseDto;
    }

    // question ????????????
    public void questionWrite(MemberQuestionWriteResponseDto memberQuestionWriteResponseDto, MultipartFile memberImage) throws IOException{
        userService.checkUser(memberQuestionWriteResponseDto.getMemberId());
        
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
    
                // ????????? ??????
                createMemberQuestionImage(memberQuestionImageDto);
    
            }
        }
    }

    // ????????? ???????????? ????????? ??????
    public void createMemberQuestionImage(MemberQuestionImageDto memberQuestionImageDto) {

        userService.verifySave(mapper.saveMemberQuestionImage(memberQuestionImageDto));

    }

    // QNA s3??? ????????? ????????? ??? ????????? ???????????? ????????? ??????
    private String qnaImageUploadS3(MultipartFile memberImage, String type, int memberId, int questionId) throws IOException{
        
        fileCheck(memberImage);
        
        HashMap<String, String> fileMap = s3Service.upload(memberImage, type);
    
        String fileFullName = memberImage.getOriginalFilename();
        String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
        String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

        MemberQuestionImageDto memberQuestionImageDtoSave = new MemberQuestionImageDto(memberId, questionId, fileName, fileFullName,
                fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);

        // ????????? ??????
        createMemberQuestionImage(memberQuestionImageDtoSave);

        log.info("????????? ?????? ?????? : ????????? ?????? {} ", fileMap.get("url"));
        return fileMap.get("url");
    }

    private void qnaImageDeleteS3(String memberPath, int questionId, int memberId) {

        userService.verifyDelete(mapper.deleteQuestionImage(questionId, memberId)); // memberId ?????? ??????
                    
        int index = memberPath.indexOf("question");

        // TODO : ????????? ????????? ???????????? ?????????
        System.out.println("?????? ?????? : "+memberPath.substring(index));
        String deletePath = memberPath.substring(index);

        // s3?????? ????????? ??????
        s3Service.deleteImage(deletePath);
    }

    // question ????????????
    public void questionEdit(int memberId, int questionId, MemberQuestionEditResponseDto memberQuestionEditResponseDto, MultipartFile memberImage) throws IOException {
        userService.checkUser(memberId);

        // ????????????
        if(memberImage==null) {
            // ?????? ????????? ?????? ??????
            String memberPath = memberQuestionEditResponseDto.getQuestionPath();
            if(memberPath!=null) {
                if(!memberPath.isBlank()) {
                    // ?????? ????????? ????????? ????????? ????????? ????????? ?????? -> ????????? ?????????????????? ???????????? ?????????
                    qnaImageDeleteS3(memberPath, questionId, memberId);
                    memberQuestionEditResponseDto.pathUpadte(null);
                }
                else {
                    throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
                }
            }
            else {
                // ?????? ???????????? ?????????????????? ????????? ????????? ?????? ->
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
            // ????????? ????????? ?????????
            if(!memberImage.isEmpty()) {
                // ????????? ????????? ????????????, ?????? ????????? ??????
                MemberQuestionImageDto memberQuestionImageDto = mapper.findByQuestionImage(questionId, memberId);
                if(memberQuestionImageDto!=null) {
                    qnaImageDeleteS3(memberQuestionImageDto.getQuestionFilePath(), questionId, memberId);
                }
                String url = qnaImageUploadS3(memberImage, "question", memberId, questionId);
                memberQuestionEditResponseDto.pathUpadte(url);
            }
            else {
                // ???????????? ???????????????
                throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
            }
        }
        // ?????? ?????? ??????
        memberQuestionEditResponseDto.memberIdUpadte(memberId);
        memberQuestionEditResponseDto.questionIdUpadte(questionId);
        userService.verifyUpdate(mapper.updateQuetion(memberQuestionEditResponseDto)); // memberId ?????? ??????
        
    }

    // ?????? ?????? ??????
    public void deleteQeustion(int memberId, int questionId) {
        userService.checkUser(memberId);
        
        userService.verifyDelete(mapper.deleteQuetion(questionId, memberId));

        MemberQuestionImageDto memberQuestionImageDto = mapper.findByQuestionImage(questionId, memberId);
        
        if(memberQuestionImageDto!=null) {
            userService.verifyDelete(mapper.deleteQuestionImage(questionId, memberId));

            String url = "question/" + memberQuestionImageDto.getQuestionFileSaveName();
    
            // s3?????? ????????? ??????
            s3Service.deleteImage(url);
        }

    }

    // ?????? ?????? ?????????
    public List<MemberFoodsResponseDto> foods(int memberId) {
        userService.checkUser(memberId);

        List<MemberFoodsResponseDto> foodList = mapper.findByFoods(memberId);

        if(foodList.size()==0) {
            throw new BusinessLogicException(ExceptionCode.RECOMMEND_NOT_FOUND);
        }

        return foodList;
    }

    // ?????? ?????? ?????????, ????????? ??????
    public void foodEdit(int memberId, int memberFoodId, String like) {
        userService.checkUser(memberId);
        if(like.equals("Y") || like.equals("N")) {
            userService.verifyUpdate(mapper.updateMemberFood(memberFoodId, like));
        }
        else {
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
    }

    // ???????????? ?????? ?????? ????????? ?????????
    private void postSize(int size) {
        if(size==0) {
            throw new BusinessLogicException(ExceptionCode.MYPAGE_NOT_FOUND);
        }
    }

}
