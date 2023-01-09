package com.foodiary.member.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    @Id
    private int memberId;

    private String memberLoginId;

    private String memberEmail;
    
    private String memberPassword;

    private String memberNickName;

    private String memberProfile;

    private String memberYn;

    private LocalDateTime memberCreate;

    private LocalDateTime memberUpdate;
}
