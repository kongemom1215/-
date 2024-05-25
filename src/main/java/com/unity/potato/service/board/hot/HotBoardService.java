package com.unity.potato.service.board.hot;

import com.unity.potato.domain.board.hot.HotBoard;
import com.unity.potato.domain.board.hot.HotBoardRepository;
import com.unity.potato.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotBoardService {
    @Autowired
    private HotBoardRepository hotBoardRepository;

    @Cacheable(value = "hotBoardDate")
    public Map<String, List<String>> getDate(){
        List<String> registerDays =  hotBoardRepository.findHotDates(8);
        List<String> registerWeeks = hotBoardRepository.findHotDates(7);
        List<String> registerMonths = hotBoardRepository.findHotDates(6);

        if(!registerDays.isEmpty()){ // date desc
            registerDays = registerDays.stream()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
        }
        if(!registerWeeks.isEmpty()){ // date desc
            registerWeeks = registerWeeks.stream()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
        }
        if(!registerMonths.isEmpty()){ // date desc
            registerMonths = registerMonths.stream()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
        }

        Map<String, List<String>> hotBoardDate = new HashMap<>();
        hotBoardDate.put("day_list", registerDays);
        hotBoardDate.put("week_list", registerWeeks);
        hotBoardDate.put("month_list", registerMonths);

        return hotBoardDate;
    }

    @Cacheable(value = "hotBoardList")
    public Map<String, List<HotBoard>> getBoardList() {
        List<HotBoard> hotBoards = hotBoardRepository.findAll();

        Map<String, List<HotBoard>> boardMap = hotBoards.stream()
                .collect(Collectors.groupingBy(HotBoard::getHotDate,
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparingInt(HotBoard::getRanking))
                                        .collect(Collectors.toList()))));

        return boardMap;
    }

    public List<HotBoard> getMainHotPosts(){
        List<HotBoard> hotPosts = hotBoardRepository.findTop4ByOrderByIdDesc();
        if(hotPosts.size() < 4){
            return null;
        }
        return hotPosts;
    }
}
