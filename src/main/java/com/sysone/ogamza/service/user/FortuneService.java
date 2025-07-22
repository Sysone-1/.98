package com.sysone.ogamza.service.user;

import com.sysone.ogamza.dto.user.TodayFortuneDTO;
import com.sysone.ogamza.dao.user.UserHomeDAO;
import com.sysone.ogamza.utils.api.GPTService;
import com.sysone.ogamza.utils.api.GptPrompt;
import javafx.scene.paint.Color;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FortuneService {

    private static final FortuneService instance = new FortuneService();
    private FortuneService(){}
    public static FortuneService getInstance(){return instance;}


    // 스케줄러 발동 (1일 1회)
    public void todayLuck(){

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            System.out.println("🎯 자정 작업 실행! " + LocalDateTime.now());
            try {
                int response = UserHomeDAO.getInstance().updateFortune(setLuckyDataList());
                if(response == 0){
                    throw new RuntimeException("저장된 행이 없습니다.");
                }
            }catch (Exception e){
                System.out.println("Lucky Data 업데이트에 실패하였습니다");
                e.printStackTrace();
            }

        };

        //long initialDelay = 5;         // 5초 뒤 첫 실행
        //long period = 60;              // 60초마다 실행 (1분)

        long initialDelay = computeInitialDelayToMidnight(); // 자정까지 남은 초 계산
        long period = TimeUnit.DAYS.toSeconds(1); // 24시간

        scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);

    }

    // 자정까지 남은 시간 계산
    private long computeInitialDelayToMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return Duration.between(now, nextMidnight).getSeconds();
    }


    // 데이터 셋팅
    public List<TodayFortuneDTO> setLuckyDataList(){
        try{
            // 1. 사원 ID 리스트
            List<Integer> ids = UserHomeDAO.getInstance().findAllId();
            int employeeCount = ids.size();
            System.out.println("사원 수: " + employeeCount);

            // 2. 프롬프트 생성 + GPT 호출
            String prompt = GptPrompt.getPromptForToday(employeeCount);
            List<String> messages = GPTService.askGPT(prompt);

            if (messages.size() != employeeCount) {
                System.out.println("응답 메시지 수와 사원 수가 일치하지 않습니다!");
            }

            // 3. 데이터 셋팅
            List<TodayFortuneDTO> fortuneList = new ArrayList<>();
            for (int i = 0; i < employeeCount; i++) {
                int employeeId = ids.get(i);
                String message = i < messages.size() ? messages.get(i) : "오늘도 힘내세요! 😊";

                fortuneList.add(TodayFortuneDTO.builder()
                        .employeeId(employeeId)
                        .luckyNumber(setLuckyNumber())
                        .luckyShape(setLuckyShape())
                        .luckyColor(setLuckyColor())
                        .randomMessage(message)
                        .build());
            }

            System.out.println("Fortune 데이터 셋팅 완료: " + fortuneList);
            return fortuneList;

        } catch (Exception e) {
            System.out.println("lucky Data 셋팅에 실패하였습니다.");
            e.printStackTrace();
            return null;
        }
    }


    // 행운의 번호
    public int setLuckyNumber(){
        return  (int)(Math.random()*100);
    }

    // 행운의 도형
    public String setLuckyShape(){
        String[] shapeNames = { "원", "사각형", "별", "세모", "하트", "마름모", "번개", "퍼즐" };
        int luckShape =(int)(Math.random()*8);
        // db 넣을 용
        String todayShape = shapeNames[luckShape];
        return todayShape;
    }


    // 행운의 색깔
    public String setLuckyColor(){
        Random rand = new Random();
        Color randomColor = Color.color(
                0.4 + rand.nextDouble() * 0.6, // 0.4 ~ 1.0
                0.4 + rand.nextDouble() * 0.6,
                0.4 + rand.nextDouble() * 0.6
        );
        return randomColor.toString();
    }
}
