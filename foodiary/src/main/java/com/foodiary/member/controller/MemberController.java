package com.foodiary.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodiary.member.dto.MemberPostDto;
import com.foodiary.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
// @RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/addmember")
    public ResponseEntity<Void> testCreateMember() {
        // MemberPostDto PostDto = new MemberPostDto("dfjlksdjf@naver.com", "testsdnfl1234!", "닉네임", "짜장면", null, null, 'N');
        MemberPostDto postDto = new MemberPostDto("dfjlksdjf@naver.com", "testsdnfl1234!", "닉네임", "짜장면", null, null, "N");

        memberService.createdMember(postDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
