package com.sysone.ogamza.service.user;

import com.sysone.ogamza.repository.user.UserHomeDAO;
import javafx.scene.paint.Color;
import java.time.Duration;
import java.time.LocalDateTime;
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
            setLuckyItems();
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

    public void setLuckyItems(){

        // 행운의 번호
        int luckyNum = (int)(Math.random()*100);

        // 행운의 도형
        String[] shapeNames = { "원", "사각형", "별", "세모", "하트", "마름모", "번개", "퍼즐" };
        int luckShape =(int)(Math.random()*8);
        // db 넣을 용
        String todayShape = shapeNames[luckShape];

        // 행운의 색깔
        Random rand = new Random();
        Color randomColor = Color.color(
                0.4 + rand.nextDouble() * 0.6, // 0.4 ~ 1.0
                0.4 + rand.nextDouble() * 0.6,
                0.4 + rand.nextDouble() * 0.6
        );

        // 디버깅 로그용 출력
        System.out.println("🎲 LuckyNum: " + luckyNum);
        System.out.println("🟢 Shape: " + shapeNames[luckShape]);
        System.out.println("🎨 Color: " + randomColor.toString());

        String msg = "나는 어제로 돌아갈 수 없다.";

        try{
            // 사용자 db에 업데이트
            int result = UserHomeDAO.getInstance().updateFortune(luckyNum,todayShape,randomColor.toString(),msg, 1003);
            if(result ==0){
                throw new RuntimeException("업데이트된 행이 없습니다.");
            }
            System.out.println("Lucky Box 업데이트 완료");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
