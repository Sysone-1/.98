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

/**
 * 매일 자정마다 사원들의 오늘의 행운 요소(숫자, 도형, 색상, 메시지)를 설정하고
 * 데이터베이스에 자동으로 업데이트하는 서비스 클래스입니다.
 *
 * GPT API를 통해 요일별 분위기에 맞는 응원 메시지를 생성하며,
 * Java의 ScheduledExecutorService를 활용한 자정 스케줄링을 수행합니다.
 *
 * 주요 기능:
 * - 자정 스케줄러 실행
 * - 사원별 행운 정보(GPT 메시지 포함) 설정
 * - 색상, 도형, 숫자 랜덤 생성
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class FortuneService {

    // 싱글톤 패턴
    private static final FortuneService instance = new FortuneService();
    private FortuneService(){}
    public static FortuneService getInstance(){return instance;}

    /**
     * 자정마다 실행되는 스케줄러 설정
     * - 자정 시점에 todayLuck() 작업 실행
     * - 이후 매 24시간마다 반복
     */
    public void todayLuck(){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            System.out.println("🎯 자정 작업 실행! " + LocalDateTime.now());
            try {
                // 행운 데이터 셋팅 후 DB에 저장
                int response = UserHomeDAO.getInstance().updateFortune(setLuckyDataList());
                if(response == 0){
                    throw new RuntimeException("저장된 행이 없습니다.");
                }
            }catch (Exception e){
                System.out.println("Lucky Data 업데이트에 실패하였습니다");
                e.printStackTrace();
            }
        };

        long initialDelay = computeInitialDelayToMidnight(); // 첫 실행까지 대기 시간 계산
        long period = TimeUnit.DAYS.toSeconds(1);            // 반복 주기: 24시간

        scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
    }

    /**
     * 현재 시각으로부터 자정까지 남은 초 계산
     * → 스케줄러가 정확히 자정에 동작하도록 설정
     */
    private long computeInitialDelayToMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return Duration.between(now, nextMidnight).getSeconds();
    }

    /**
     * 오늘의 행운 데이터 전체 생성
     * - 전체 사원 ID 조회
     * - GPT에게 요일 기반 프롬프트 생성 후 응답 메시지 수집
     * - 사원마다 랜덤 행운 요소 + GPT 메시지를 DTO에 담아 반환
     *
     * @return 오늘 행운 정보가 담긴 DTO 리스트
     */
    public List<TodayFortuneDTO> setLuckyDataList(){
        try{
            // 1. 사원 ID 리스트
            List<Integer> ids = UserHomeDAO.getInstance().findAllId();
            int employeeCount = ids.size();
            System.out.println("사원 수: " + employeeCount);

            // 2. GPT 프롬프트 생성 및 호출
            String prompt = GptPrompt.getPromptForToday(employeeCount);
            List<String> messages = GPTService.askGPT(prompt);

            if (messages.size() != employeeCount) {
                System.out.println("응답 메시지 수와 사원 수가 일치하지 않습니다!");
            }

            // 3. 사원별 행운 정보 셋팅
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

    /**
     * 0~99 사이의 행운 숫자 생성
     * @return int 랜덤 숫자
     */
    public int setLuckyNumber(){
        return (int)(Math.random() * 100);
    }

    /**
     * 랜덤 도형명 선택
     * @return String 도형 이름 (ex. 원, 사각형, 별 등)
     */
    public String setLuckyShape(){
        String[] shapeNames = { "원", "사각형", "별", "세모", "하트", "마름모", "번개", "퍼즐" };
        int luckShape = (int)(Math.random() * shapeNames.length);
        return shapeNames[luckShape];
    }

    /**
     * 랜덤 색상 생성 (RGB 각 성분은 0.4~1.0 범위)
     * @return String 색상 (ex. 0xFFAA33 형태)
     */
    public String setLuckyColor(){
        Random rand = new Random();
        Color randomColor = Color.color(
                0.4 + rand.nextDouble() * 0.6,
                0.4 + rand.nextDouble() * 0.6,
                0.4 + rand.nextDouble() * 0.6
        );
        return randomColor.toString();
    }
}
