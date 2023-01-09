package com.foodiary.common.email;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;

// import com.sendgrid.helpers.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${smtp.password}")
    private String secretKey;

    public void EmailSend(String email, String info, String type) throws IOException {

      // Email to = new Email(email);
      // Email from = new Email("em6052.www.foodiary.store");
      Mail mail = new Mail();
      mail.setFrom(new Email("foodiary@em6052.www.foodiary.store"));
      Personalization personalization = new Personalization();
      personalization.addTo(new Email(email));

      if(type.equals("id")) {
        // 아이디 찾기
        personalization.addDynamicTemplateData("member_loginId", info);
        mail.setTemplateId("d-86fc008182ff46a28e28b8adfc7dcf86");
      }
      else if(type.equals("pw")) {
        // 비밀번호 찾기
        personalization.addDynamicTemplateData("link", "http://localhost:8080/member/password/change?jwt="+info);
        mail.setTemplateId("d-e0ae8ffef4a74bdea87a1417d02b16c1");
      }
      else {
        // 회원가입
        personalization.addDynamicTemplateData("certification_number", info);
        mail.setTemplateId("d-65943ccdc1c14282bfb8923a94020638");
      }
      mail.addPersonalization(personalization);
      
      SendGrid sg = new SendGrid(secretKey);
      Request request = new Request();
      try {
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        System.out.println("스테이터스 코드"+response.getStatusCode());
        System.out.println("바디 내용"+response.getBody());
        System.out.println("헤더 내용"+response.getHeaders());
      } catch (IOException ex) {
        throw ex;
      }

    }

}
