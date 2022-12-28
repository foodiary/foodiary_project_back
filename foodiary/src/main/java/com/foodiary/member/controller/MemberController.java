package com.foodiary.member.controller;

import com.foodiary.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodiary.member.dto.MemberPostDto;
import com.foodiary.member.dto.MemberResponseDto;
import com.foodiary.member.entity.Member;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/addmember")
    public ResponseEntity<Void> testCreateMember(@RequestBody MemberPostDto PostDto) {
        memberService.createdMember(PostDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/findmember")
    public ResponseEntity<Member> testFindMember(@RequestParam String email) {
        log.info(email);
        return new ResponseEntity<>(memberService.getMember(email), HttpStatus.OK);
    }
}
