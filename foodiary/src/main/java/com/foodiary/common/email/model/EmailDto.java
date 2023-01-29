package com.foodiary.common.email.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {
    
    @ApiModelProperty(value="email 시퀀스", required = true)
    private int emailId;

    @ApiModelProperty(value="email 타입", required = true)
    private String emailType;

    @ApiModelProperty(value="email 발송대상", required = true)
    private String emailTo;

    @ApiModelProperty(value="email 일자", required = true)
    private String emailCreate;

    public EmailDto(String emailType, String emailTo) {
        this.emailType = emailType;
        this.emailTo = emailTo;
    }

}
