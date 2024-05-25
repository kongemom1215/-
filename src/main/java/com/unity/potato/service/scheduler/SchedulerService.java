package com.unity.potato.service.scheduler;

import com.unity.potato.domain.board.HotPostInterface;
import com.unity.potato.domain.board.free.FreeBoardRepository;
import com.unity.potato.domain.board.hot.HotBoard;
import com.unity.potato.domain.board.hot.HotBoardRepository;
import com.unity.potato.domain.schedule.CollectMessage;
import com.unity.potato.domain.schedule.CollectMessageRepository;
import com.unity.potato.domain.schedule.SelectedMessage;
import com.unity.potato.domain.schedule.SelectedMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@Transactional
public class SchedulerService {

    @Autowired
    private CollectMessageRepository collectMessageRepository;
    @Autowired
    private SelectedMessageRepository selectedMessageRepository;
    @Autowired
    private HotBoardRepository hotBoardRepository;
    @Autowired
    private FreeBoardRepository boardRepository;

    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void runSelectMessageJob(){
        try {
            int fitCnt = collectMessageRepository.countBySelected(false);
            if(fitCnt > 0) {
                int idx = (int)(Math.random() * fitCnt);

                Page<CollectMessage> randomPage = collectMessageRepository.findAllBySelected(false, PageRequest.of(idx, 1));
                if(randomPage.hasContent()){
                    CollectMessage randomMsg = randomPage.getContent().get(0);
                    randomMsg.setSelected(true);
                    collectMessageRepository.save(randomMsg);

                    selectedMessageRepository.truncateTable();
                    SelectedMessage selectedMsg = SelectedMessage.builder()
                            .msgId(randomMsg.getId())
                            .title(randomMsg.getTitle())
                            .msg(randomMsg.getMsg())
                            .rgb(randomMsg.getRgb())
                            .regDt(LocalDateTime.now())
                            .build();
                    selectedMessageRepository.save(selectedMsg);
                }
            }
        } catch (Exception e){
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") //매일 자정에 실행
        try {
            String previousDay = getPreviousDay();
            List<HotPostInterface> hotPostList = boardRepository.findTodayHotPosts(previousDay);
            List<HotBoard> todayHotPosts = new ArrayList<>();
            AtomicInteger rank = new AtomicInteger(1);
            hotPostList.forEach(hotPostInterface -> {
                HotBoard hotBoard = HotBoard.builder()
                        .hotDate(previousDay.replace("-", ""))
                        .ranking(rank.getAndIncrement())
                        .postLink(getBoardUrl(hotPostInterface.getBoardType(), hotPostInterface.getId(), hotPostInterface.getRegion()))
                        .postTitle(hotPostInterface.getTitle())
                        .regDt(LocalDateTime.now())
                        .hotDegree(hotPostInterface.getHotDegree())
                        .build();
                todayHotPosts.add(hotBoard);
            });

            hotBoardRepository.saveAll(todayHotPosts);
        } catch (Exception e){
        }
    }

    @Scheduled(cron = "0 0 12 ? * SUN") //일요일 정오마다
    public void runSelectWeeklyHotJob(){
        try {
            List<HotPostInterface> hotPostList = boardRepository.findWeekHotPosts();
            List<HotBoard> weekHotPosts = new ArrayList<>();
            AtomicInteger rank = new AtomicInteger(1);
            hotPostList.forEach(hotPostInterface -> {
                HotBoard hotBoard = HotBoard.builder()
                        .hotDate(getPreviousWeek())
                        .ranking(rank.getAndIncrement())
                        .postLink(getBoardUrl(hotPostInterface.getBoardType(), hotPostInterface.getId(), hotPostInterface.getRegion()))
                        .postTitle(hotPostInterface.getTitle())
                        .regDt(LocalDateTime.now())
                        .hotDegree(hotPostInterface.getHotDegree())
                        .build();
                weekHotPosts.add(hotBoard);
            });

            hotBoardRepository.saveAll(weekHotPosts);
        } catch (Exception e){
        }
    }

    @Scheduled(cron = "0 0 0 1 * ?") //매월 1일마다
    public void runSelectMonthlyHotJob(){
        try {
            List<HotPostInterface> hotPostList = boardRepository.findMonthHotPosts();
            List<HotBoard> monthHotPosts = new ArrayList<>();
            AtomicInteger rank = new AtomicInteger(1);
            hotPostList.forEach(hotPostInterface -> {
                HotBoard hotBoard = HotBoard.builder()
                        .hotDate(getPreviousMonth())
                        .ranking(rank.getAndIncrement())
                        .postLink(getBoardUrl(hotPostInterface.getBoardType(), hotPostInterface.getId(), hotPostInterface.getRegion()))
                        .postTitle(hotPostInterface.getTitle())
                        .regDt(LocalDateTime.now())
                        .hotDegree(hotPostInterface.getHotDegree())
                        .build();
                monthHotPosts.add(hotBoard);
            });

            hotBoardRepository.saveAll(monthHotPosts);
        } catch (Exception e){
        }
    }

    private String getBoardUrl(String boardType, Long id, String region){
        if("free".equals(boardType)){
            return "/community/free/" + id;
        } else if("share".equals(boardType)){
            return "/community/share/" + id;
        } else if("review".equals(boardType)){
            return "/community/review/" + region + "/" + id;
        } else if("recipe".equals(boardType)){
            return "/community/recipe/" + id;
        }
        return null;
    }

    private String getPreviousDay(){
        LocalDate currentDate = LocalDate.now();
        LocalDate previousDayDate = currentDate.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return previousDayDate.format(formatter);
    }

    private String getPreviousWeek(){
        LocalDate currentDate = LocalDate.now();
        LocalDate previousDayDate = currentDate.minusDays(1);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int year = previousDayDate.getYear();
        int month = previousDayDate.getMonthValue();
        int weekNumber = previousDayDate.get(weekFields.weekOfMonth());
        return String.valueOf(year) + String.valueOf(month) + String.valueOf(weekNumber);
    }

    private String getPreviousMonth(){
        LocalDate currentDate = LocalDate.now();
        LocalDate previousDayDate = currentDate.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return previousDayDate.format(formatter);
    }
}
