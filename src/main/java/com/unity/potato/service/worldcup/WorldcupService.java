package com.unity.potato.service.worldcup;

import com.unity.potato.domain.board.worldcup.WorldcupDetail;
import com.unity.potato.domain.board.worldcup.WorldcupDetailRepository;
import com.unity.potato.domain.board.worldcup.WorldcupInfo;
import com.unity.potato.domain.board.worldcup.WorldcupInfoRepository;
import com.unity.potato.dto.request.CupRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorldcupService {
    @Autowired
    private WorldcupInfoRepository worldcupInfoRepository;
    @Autowired
    private WorldcupDetailRepository worldcupDetailRepository;

    @Cacheable(value = "worldcupCache")
    public List<WorldcupInfo> readWorldCupList(){
        return worldcupInfoRepository.findAll();
    }

    @Cacheable(value = "worldcupDetailCache")
    public List<WorldcupDetail> readWorldcupCandidates(Long id) {
        return worldcupDetailRepository.findAllByWorldcupId(id);
    }

    @Cacheable(value = "worldcupInfoCache")
    public WorldcupInfo readWorldCupInfo(Long id){
        return worldcupInfoRepository.findById(id).orElse(null);
    }

    @Transactional
    public void saveWinner(CupRequest request){
        WorldcupDetail cupDetail = worldcupDetailRepository.findByIdAndWorldcupId(request.getWinnerId(), request.getCupId())
                .orElse(null);
        if(cupDetail != null){
            cupDetail.increaseWinCnt();
            worldcupDetailRepository.save(cupDetail);
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> readWorldCupLankList(Long id){
        List<WorldcupDetail> cupDetailList = worldcupDetailRepository.findAllByWorldcupId(id);
        List<Map<String, Object>> rankList = new ArrayList<>();

        if(!cupDetailList.isEmpty()){
            List<WorldcupDetail> sortedList = cupDetailList.stream()
                    .sorted(Comparator.comparingInt(WorldcupDetail::getWinCnt).reversed())
                    .toList();

            int totalWinCnt = cupDetailList.stream()
                    .mapToInt(WorldcupDetail::getWinCnt)
                    .sum();

            // 순위를 부여하여 Map에 추가
            int prevWinCnt = Integer.MAX_VALUE;
            int rank = 0;
            int prevRank = 0;
            for (WorldcupDetail detail : sortedList) {
                if (detail.getWinCnt() < prevWinCnt) {
                    rank++;
                    prevRank = rank;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("rank", prevRank); // 순위는 이전 순위를 유지
                map.put("name", detail.getName());
                map.put("src", detail.getImgSrc());
                map.put("win_cnt", detail.getWinCnt());
                int percentage = (int) Math.floor((double) detail.getWinCnt() / totalWinCnt * 100);
                map.put("percent", percentage);
                // 필요한 경우 다른 필드도 추가할 수 있습니다.
                rankList.add(map);
                prevWinCnt = detail.getWinCnt();
            }
        }

        return rankList;
    }

    @Transactional(readOnly = true)
    public WorldcupInfo getMainWorldCupInfo(){
        return worldcupInfoRepository.findByVisibleYn('Y').orElse(null);
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> getWorldCupInfo(Long worldCupId){
        int participateCnt = worldcupDetailRepository.getParticipateCnt(worldCupId);
        if(participateCnt > 0){
            Map<String, Integer> topRankList = new HashMap<>();
            List<WorldcupDetail> worldCupDetails = worldcupDetailRepository.findAllByWorldcupIdOrderByWinCntDesc(worldCupId);
            if(!worldCupDetails.isEmpty()){
                worldCupDetails.stream() // top3 적출
                        .filter(worldcupDetail -> worldcupDetail.getWinCnt() > 0)
                        .limit(3)
                        .forEach(worldcupDetail -> topRankList.put(worldcupDetail.getName(), worldcupDetail.getWinCnt()));

                int topRankWinCnt = topRankList.values().stream().mapToInt(Integer::intValue).sum();
                if(participateCnt-topRankWinCnt > 0){
                    topRankList.put("그 외", participateCnt-topRankWinCnt);
                }

                // 퍼센티지를 계산
                Map<String, Integer> topRankPercentages = new HashMap<>();
                for (Map.Entry<String, Integer> entry : topRankList.entrySet()) {
                    String name = entry.getKey();
                    int count = entry.getValue();
                    int percentage = (int) Math.round((count / (double) participateCnt) * 100);
                    topRankPercentages.put(name, percentage);
                }

                return topRankPercentages;
            }
        }
        return null;
    }

}
