package com.unity.potato.domain.vo;

import com.unity.potato.dto.request.EmailAuthRequest;
import jakarta.mail.internet.InternetAddress;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class EmailVo {
    private String email;
    private String emailtitle;
    private String certCd;
    private String newPwd;
    private String htmlContent;
    private InternetAddress hostAddress;
    private char emailType;

    public EmailVo(String hostMail, String hostName){
        try {
            this.hostAddress = new InternetAddress(hostMail,hostName);
        } catch (Exception e){
        }
    }

    public EmailVo(){
    }

}
