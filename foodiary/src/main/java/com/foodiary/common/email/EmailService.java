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

    public void EmailSend() throws IOException {

        Email from = new Email("alwn04016@gmail.com");
        String subject = "gogogogogo";
        // Email to = new Email("alwn04016@naver.com");
        Email to = new Email("myjyu123@gmail.com");
        
        Content content = new Content("text/plain", "dsfsdfsd and easy to do anywhere, even with Java");
        Mail mail = new Mail(from, subject, to, content);
    
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
