package com.foodiary.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberPostDto {

    private String email;
    private String pw;
    private String nickName;
    private String food;
    private String image;
    private String profile;
    private String yn;
}
