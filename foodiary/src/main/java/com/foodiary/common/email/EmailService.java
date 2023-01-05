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

      Email to = new Email(email);
      Email from = new Email("alwn04016@gmail.com");
      Mail mail = new Mail();
        String subject;
        if(type.equals("id")) {
          subject = "foodiary 아이디 찾기 안내 메일입니다.";
          mail.setFrom(new Email("alwn04016@gmail.com"));
          Personalization personalization = new Personalization();
          personalization.addDynamicTemplateData("member_loginId", info);
          personalization.addTo(new Email(email));
          mail.addPersonalization(personalization);
          mail.setTemplateId("d-86fc008182ff46a28e28b8adfc7dcf86");
        }
        else if(type.equals("pw")) {
          subject = "foodiary 비밀번호 찾기 안내 메일입니다.";
          mail.personalization.get(0).addSubstitution("member_loginId", info);
          mail.setTemplateId("d-e0ae8ffef4a74bdea87a1417d02b16c1");
        }
        else {
          // 회원가입
          subject = "foodiary 회원 인증 안내 메일입니다.";
          mail.personalization.get(0).addSubstitution("member_loginId", info);
          mail.setTemplateId("d-65943ccdc1c14282bfb8923a94020638");
        }
        
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
