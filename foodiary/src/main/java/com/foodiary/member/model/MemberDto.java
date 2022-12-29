package com.foodiary.member.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

// TODO : MemberDto는 Response인가요? Request인가요?
@Data // TODO : setter 사용 지양, 수정 필요
@NoArgsConstructor
public class MemberDto {

    @Id
    private int memberId;

    private String memberEmail;
    
    private String memberPw;

    private String memberNickName;

    // private String memberFood;

    private String memberImage;

    private String memberProfile;

    // true면 탈퇴
    private String memberYn;
    // private Boolean memberSecession;

    private LocalDateTime memberCreate;

    private LocalDateTime memberUpdate;

    // private LocalDateTime memberDelete;
    
}
