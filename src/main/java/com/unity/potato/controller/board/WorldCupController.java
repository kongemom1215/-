package com.unity.potato.controller.board;

import com.unity.potato.domain.board.worldcup.WorldcupInfo;
import com.unity.potato.dto.request.CupRequest;
import com.unity.potato.dto.response.Result;
import com.unity.potato.service.worldcup.WorldcupService;
import com.unity.potato.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class WorldCupController {
    @Autowired
    private WorldcupService worldcupService;

    @RequestMapping("/worldcup")
    public String goWorldCupPage(Model model){

        try {
            model.addAttribute("cupList", worldcupService.readWorldCupList());
        } catch (Exception e){
            return "error/500";
        }

        return "worldcup";
    }

    @RequestMapping("/worldcup/{id}")
    public String goWorldcupDetailpage(@PathVariable String id, Model model){
        if(!StringUtil.isNumberic(id)){
            return "error/404";
        }

        try {
            model.addAttribute("cupInfo", worldcupService.readWorldCupInfo(Long.valueOf(id)));
            model.addAttribute("candidates", worldcupService.readWorldcupCandidates(Long.valueOf(id)));
        } catch (Exception e){
            return "error/500";
        }

        return "worldcupDetail";
    }

    @RequestMapping("/worldcup/rank/{id}")
    public String goWorldCupRankTable(@PathVariable String id, Model model){
        if(!StringUtil.isNumberic(id)){
            return "error/404";
        }

        try {
            model.addAttribute("cupInfo", worldcupService.readWorldCupInfo(Long.valueOf(id)));
            model.addAttribute("rankList", worldcupService.readWorldCupLankList(Long.valueOf(id)));
        } catch (Exception e){
            return "error/500";
        }

        return "worldcupRank";
    }

    @PostMapping("/api/worldcup/save-winner")
    public ResponseEntity<?> saveWinner(@RequestBody CupRequest request, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.ok(new Result("9999", "올바른 요청 값이 아닙니다."));
        }

        try {
            worldcupService.saveWinner(request);
            return ResponseEntity.ok(new Result("0000", "순위 적용 완료"));
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류 발생"));
        }
    }


}
