package com.unity.potato.controller.main;

import com.unity.potato.domain.board.worldcup.WorldcupInfo;
import com.unity.potato.domain.map.MapList;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.dto.request.MsgCollectRequest;
import com.unity.potato.dto.response.Result;
import com.unity.potato.security.CurrentUser;
import com.unity.potato.service.collect.CollectService;
import com.unity.potato.service.map.MapService;
import com.unity.potato.service.openai.OpenAiService;
import com.unity.potato.service.worldcup.WorldcupService;
import com.unity.potato.util.StringUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MainAPIController {
    @Autowired
    private CollectService collectService;
    @Autowired
    private MapService mapService;
    @Autowired
    private WorldcupService worldcupService;
    @Autowired
    private OpenAiService openAiService;

    @PostMapping("/api/ask-openai")
    public ResponseEntity<?> openAiGpt(@RequestParam String query){
        if(StringUtil.isNullOrEmpty(query)){
            return ResponseEntity.ok(new Result("9999", "질문을 입력해주세요."));
        }
        try {
            Result result = openAiService.getGptAnswer(query);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(new Result("9999", "요청 도중 에러가 발생하였습니다."));
        }
    }

    @PostMapping("/api/collect/msg")
    public ResponseEntity<?> collectMsg(@Valid @RequestBody MsgCollectRequest request, @CurrentUser UserInfo userInfo, Errors errors){
        if(errors.hasErrors() || !StringUtil.isValidRgbCode(request.getColor())){
            return ResponseEntity.ok(new Result("9999", "올바르지 않은 요청값입니다."));
        }

        try {
            collectService.saveCollectMsg(request, userInfo);
            return ResponseEntity.ok(new Result("0000", "등록이 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류가 발생하였습니다."));
        }
    }

    @GetMapping("/api/map-list")
    public ResponseEntity<?> getMapList(){
        try {
            List<MapList> mapList = mapService.getMapList();
            if(mapList == null || mapList.isEmpty()){
                return ResponseEntity.ok(new Result("9998", "맛집 리스트가 비어있습니다."));
            } else {
                return ResponseEntity.ok(new Result("0000", "조회 성공", mapList));
            }
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "불러오는 도중 오류가 발생하였습니다."));
        }
    }

    @GetMapping("/api/worldcup/top-rank")
    public ResponseEntity<?> getTopRank(){
        try {
            WorldcupInfo mainWorldCup = worldcupService.getMainWorldCupInfo();
            if(mainWorldCup != null){
                Map<String, Integer> topRankMap = worldcupService.getWorldCupInfo(mainWorldCup.getId());
                Map<String, Object> returnRankMap = new HashMap<>();
                returnRankMap.put("worldcup", mainWorldCup);
                returnRankMap.put("topRank", topRankMap);
                return ResponseEntity.ok(new Result("0000", "성공적으로 조회하였습니다.", returnRankMap));
            }

            return ResponseEntity.ok(new Result("0000", "조회되는 월드컵 정보가 없습니다.", null));
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류가 발생하였습니다."));
        }
    }
}
