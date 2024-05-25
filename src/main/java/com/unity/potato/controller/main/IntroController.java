package com.unity.potato.controller.main;

import com.unity.potato.domain.url.AuthUrlRepository;
import com.unity.potato.dto.response.Result;
import com.unity.potato.service.feedback.FeedBackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class IntroController {

    @Autowired
    private FeedBackService feedBackService;
    @Autowired
    private AuthUrlRepository authUrlRepository;

    @RequestMapping("/")
    public String goIntro(){
        return "intro";
    }

    @RequestMapping("/intro")
    public String goHomePage(){
        return "intro";
    }

    @RequestMapping("/introduce")
    public String goIntroduce(){
        return "introduce";
    }

    @PostMapping("/api/send-feedback")
    public ResponseEntity<?> sendFeedback(@Valid @RequestBody FeedbackRequest request, Errors errors) {
        if(errors.hasErrors()){
            return ResponseEntity.ok(new Result("9998", "올바르지 않은 요청입니다."));
        }
        try {
            feedBackService.saveFeedback(request);
            return ResponseEntity.ok(new Result("0000", "요청 저장 완료"));
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류가 발생하였습니다."));
        }
    }

    @RequestMapping("/health")
    public String healthCheck(){
        authUrlRepository.findById(1L);
        return "intro";
    }
}
