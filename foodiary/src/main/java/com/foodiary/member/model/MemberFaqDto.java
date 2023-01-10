package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberFaqDto {
    
    @ApiModelProperty(value="faq 시퀀스", required = true)
    private int faqId;

    @ApiModelProperty(value="faq 제목", required = true)
    private String faqTitle;

    @ApiModelProperty(value="faq 작성자", required = true)
    private String faqWriter;

    @ApiModelProperty(value="faq 내용", required = true)
    private String faqContent;

    @ApiModelProperty(value="faq 일자", required = true)
    private String faqCreate;
}
