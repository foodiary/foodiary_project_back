package com.foodiary.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NaverUserDto {
    public String id;
    public String nickname;
    public String name;
    public String email;
    public String gender;
    public String age;
    public String birthday;
    public String profile_image;
    public String birthyear;
    public String mobile;
}
