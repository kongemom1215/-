package com.unity.potato.controller.alarm;

import com.unity.potato.domain.notification.Notification;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.domain.vo.NotificationVo;
import com.unity.potato.dto.request.AlarmReadRequest;
import com.unity.potato.dto.response.Result;
import com.unity.potato.security.CurrentUser;
import com.unity.potato.service.notification.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @RequestMapping("/alarm")
    public String goAlarmList(@CurrentUser UserInfo userInfo, Model model){
        try {
            List<Notification> alarmList = notificationService.getNotificationList(userInfo);
            model.addAttribute("alarmList", alarmList);
        } catch (Exception e){
            return "error/500";
        }

        return "alarmList";
    }

    @PostMapping("/api/read-alarm")
    public ResponseEntity<?> readAlarm(@Valid @RequestBody AlarmReadRequest request, @CurrentUser UserInfo userInfo, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.ok(new Result("9999", "올바르지 않은 요청값입니다."));
        }

        try {
            notificationService.checkedNotification(request.getAlarmIdList(), userInfo);
            return ResponseEntity.ok(new Result("0000", "업데이트 완료"));
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류 발생"));
        }
    }
}
