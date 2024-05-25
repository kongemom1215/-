package com.unity.potato.service.mail;

import com.unity.potato.config.EmailConfig;
import com.unity.potato.domain.history.EmailCertHistory;
import com.unity.potato.domain.history.EmailCertHistoryRepository;
import com.unity.potato.domain.history.EmailFindPwdHistory;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.domain.user.UserInfoRepository;
import com.unity.potato.domain.vo.EmailVo;
import com.unity.potato.dto.request.EmailAuthRequest;
import com.unity.potato.dto.response.Result;
import com.unity.potato.util.HtmlUtil;
import com.unity.potato.util.RedisUtil;
import com.unity.potato.util.StringUtil;

import static com.unity.potato.util.constants.LoginConstants.PREIFIX_LOGIN_TRY;
import static com.unity.potato.util.constants.ValidateConstants.PREFIX_CHANGE_PASSWORD;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.unity.potato.util.constants.EmailConstants.*;

@Service
@RequiredArgsConstructor
public class MailService{

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private EmailCertHistoryRepository emailCertHistoryRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    private final EmailConfig emailConfig;

    private final RedisUtil redisUtil;

    private final HtmlUtil htmlUtil;

    @Value("${spring.mail.username}")
    private String hostMail;

    @Transactional
    public Result sendCodeMail(String email) throws Exception {
        try {
            if(redisUtil.existData(PREFIX_CERTIFICATION+email)){
                redisUtil.delete(PREFIX_CERTIFICATION+email);
            }

            EmailVo emailVo = createMail(email, "certEmail");

            sendHtmlMessage(emailVo);
            saveEmailCertHistory(emailVo);

            redisUtil.add(PREFIX_CERTIFICATION+email , emailVo.getCertCd(), 10);
            return new Result("0000", "전송 성공");
        } catch (Exception e){
            return new Result("9996", "메일 전송 중 오류가 발생하였습니다.");
        }
    }

    @Transactional
    public Result sendFindPwdEmail(String email) throws Exception {
        // 1. 이메일 타입 체크
        if(!StringUtil.isEmailType(email)){
            return new Result("9990", "유효하지 않은 이메일입니다.");
        }

        // 2. 가입한 고객인지 체크
        UserInfo userInfo = userInfoRepository.findByUserEmail(email).orElse(null);
        if(userInfo == null){
            return new Result("9990", "유효하지 않은 이메일입니다.");
        }

        // 3. 이메일 전송 횟수 초과 확인
        if(getEmailFindPwdCount(email) > 5){
            return new Result("9991", "이메일 전송 횟수를 초과하셨습니다. 30분뒤에 다시 시도해주세요.");
        }

        // 4. 이메일 전송 및 비밀번호 변경
        EmailVo emailVo = createMail(email, "findPwdEmail");
        sendHtmlMessage(emailVo);
        userInfo.setUserPwd(passwordEncoder.encode(emailVo.getNewPwd()));
        userInfoRepository.save(userInfo);

        // 5. 레디스 저장
        redisUtil.add(PREFIX_CHANGE_PASSWORD+email, "true");

        // 6. 이메일 전송 이력 저장
        saveEmailFindPwdHistory(emailVo);

        // 7. 계정 잠금 해제
        redisUtil.delete(PREIFIX_LOGIN_TRY + email);

        return new Result("0000", "이메일을 발송했습니다. 새로운 비밀번호로 로그인해주세요.");
    }

    /**
     * Html 템플릿 메일 보내기
     */
    public void sendHtmlMessage(EmailVo emailVo) throws Exception {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailVo.getEmail());
            helper.setSubject(emailVo.getEmailtitle());
            helper.setText(emailVo.getHtmlContent(), true);
            helper.setFrom(emailVo.getHostAddress());

            javaMailSender.send(message);
        } catch(Exception e){
            throw new Exception();
        }
    }


    public void saveEmailCertHistory(EmailVo emailVo){
        EmailCertHistory emailCertHistory = new EmailCertHistory(emailVo);
        emailCertHistoryRepository.save(emailCertHistory);
    }

    public void saveEmailFindPwdHistory(EmailVo emailVo){
        EmailFindPwdHistory history = new EmailFindPwdHistory(emailVo);
    }

    public EmailVo createMail(String email, String emailType) throws Exception {
        EmailVo emailVo = null;

        if("certEmail".equals(emailType)){
            String certCd = generateRandomCode();

            emailVo = EmailVo.builder()
                    .hostAddress(new InternetAddress(emailConfig.getUserName(), HOST_NAME))
                    .email(email)
                    .certCd(certCd)
                    .emailtitle(TITLE_EMAIL_CODE_CHECK)
                    .htmlContent(selectHtmlBody("certEmail", certCd))
                    .emailType('T')
                    .build();
        } else if("findPwdEmail".equals(emailType)) {
            String randomPassword = StringUtil.generateUuid().replaceAll("-", "").substring(0,6);;

            emailVo = EmailVo.builder()
                    .hostAddress(new InternetAddress(emailConfig.getUserName(), HOST_NAME))
                    .email(email)
                    .newPwd(randomPassword)
                    .emailtitle(TITLE_EMAIL_GET_PWD)
                    .htmlContent(selectHtmlBody("findPwdEmail", randomPassword))
                    .build();
        }


        return emailVo;
    }

    public String selectHtmlBody(String option, String code) throws Exception {
        if("certEmail".equals(option)){
            String htmlBody = htmlUtil.readHtmlFile("certEmail.html");
            htmlBody= htmlBody.replace("[[code]]", code);
            return htmlBody;
        } else if("findPwdEmail".equals(option)){
            String htmlBody = htmlUtil.readHtmlFile("findPwdEmail.html");
            htmlBody= htmlBody.replace("[[password]]", code);
            return htmlBody;
        }
        return null;
    }

    public static String generateRandomCode() {
        Random random = new Random();
        int randomNum = random.nextInt(10000);
        return String.format("%04d", randomNum);
    }


    @Transactional
    public boolean isCodeVerified(EmailAuthRequest request){
        String email = request.getEmail();
        String redisCode = redisUtil.getValue(PREFIX_CERTIFICATION+email);

        if(redisCode != null){
            if(request.getCode().equals(redisCode)){    // 인증 성공
                EmailVo emailVo = new EmailVo();
                emailVo.setEmailType('V');
                emailVo.setEmail(email);

                saveEmailCertHistory(emailVo);

                redisUtil.delete(PREFIX_CERTIFICATION+email);

                return true;
            }
        }

        return false;
    }

    public Long getEmailTryCount(String email){
        String key = PREFIX_TRY_CERTIFICATION + email;
        Long tryCnt = redisUtil.increment(key);
        redisUtil.addWithExpireDay(key, tryCnt.toString(), 1L);

        return tryCnt;
    }

    public Long getEmailCodeTryCount(EmailAuthRequest request){
        String key = PREFIX_TRY_VERIFICATION + request.getEmail();
        Long tryCnt = redisUtil.increment(key);
        redisUtil.addWithExpireDay(key, tryCnt.toString(), 1L);

        return tryCnt;
    }

    public Long getEmailFindPwdCount(String email){
        String key = PREFIX_FIND_PWD_TRY + email;
        Long tryCnt = redisUtil.increment(key);
        redisUtil.addWithExpireMin(key, tryCnt.toString(), 30L);

        return tryCnt;
    }

    public Map<String, Object> generateUuid(EmailAuthRequest request){
        Map<String, Object> uuidMap = new HashMap<>();
        String uuid = UUID.randomUUID().toString().substring(0,6);
        uuidMap.put("uuid", uuid);

        redisUtil.add(EMAIL_VERIFICATION_ID+request.getEmail(), uuid, 30L);

        return uuidMap;
    }

}
