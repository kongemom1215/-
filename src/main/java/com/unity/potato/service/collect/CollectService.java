package com.unity.potato.service.collect;

import com.unity.potato.domain.schedule.CollectMessage;
import com.unity.potato.domain.schedule.CollectMessageRepository;
import com.unity.potato.domain.schedule.SelectedMessage;
import com.unity.potato.domain.schedule.SelectedMessageRepository;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.domain.vo.SelectedMessageVo;
import com.unity.potato.dto.request.MsgCollectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollectService {

    @Autowired
    private CollectMessageRepository collectMessageRepository;
    @Autowired
    private SelectedMessageRepository selectedMessageRepository;

    public void saveCollectMsg(MsgCollectRequest request, UserInfo userInfo){
        if(userInfo != null){
            CollectMessage msg = CollectMessage.builder()
                    .title(request.getTitle())
                    .msg(request.getContent())
                    .rgb(request.getColor())
                    .userId(userInfo.getUserId())
                    .regDt(LocalDateTime.now())
                    .selected(false)
                    .build();

            collectMessageRepository.save(msg);
        }
    }

    public SelectedMessageVo getSelectedMsg(){
        if(selectedMessageRepository.count() > 0){
            SelectedMessage selectedMessage = selectedMessageRepository.findAll().get(0);
            SelectedMessageVo selectMessageVo = SelectedMessageVo.builder()
                    .title(selectedMessage.getTitle())
                    .msg(selectedMessage.getMsg())
                    .rgb(selectedMessage.getRgb())
                    .build();

            return selectMessageVo;
        }
        return null;
    }
}
