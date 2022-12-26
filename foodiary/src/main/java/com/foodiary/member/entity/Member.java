package com.foodiary.member.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Member {

    @Id
    private int memberId;

    
    private String memberEmail;
    
    private String memberPw;

    private String memberNickName;

    private String memberFood;

    private String memberImage;

    private String memberProfile;

    // true면 탈퇴
    private Boolean memberSecession;

    private LocalDateTime memberCreate;

    private LocalDateTime memberUpdate;

    private LocalDateTime memberDelete;
    
}
