package com.unity.potato.interceptor;

import com.unity.potato.domain.notification.NotificationRepository;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.security.UserAccount;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor {
    private final NotificationRepository notificationRepository;

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(modelAndView != null && !isRedirectView(modelAndView) && authentication != null && authentication.getPrincipal() instanceof UserDetails){
            UserInfo userInfo = ((UserAccount) authentication.getPrincipal()).getUserInfo();
            long count = notificationRepository.countByUserIdAndIsChecked(userInfo.getUserId(), false);
            modelAndView.addObject("notificationCount", count);
        }
    }

    private boolean isRedirectView(ModelAndView modelAndView){
        return modelAndView.getViewName().startsWith("redirect:") || modelAndView.getView() instanceof RedirectView;
    }
}
