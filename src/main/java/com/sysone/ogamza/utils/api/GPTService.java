package com.sysone.ogamza.utils.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * OpenAI GPT API와 연동하여 메시지를 요청하고 응답을 받아오는 유틸 클래스
 * - 모델: gpt-3.5-turbo
 * - 인증: config.properties에 저장된 API Key 사용
 * - 기능: 프롬프트 기반 응원 메시지 생성 (JSON 요청/응답 처리)
 *
 * 주요 메서드:
 * - getApiKey(): API 키 로딩
 * - askGPT(): 메시지 요청 및 결과 파싱
 *
 * @author 서샘이
 */
public class GPTService {

    /**
     * config.properties에서 API 키를 로딩하는 메서드
     * - 클래스패스 기준으로 파일을 로딩
     * - 키: openai.api.key
     *
     * @return String (API 키)
     */
    public static String getApiKey() {
        Properties props = new Properties();
        try (InputStream input = GPTService.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties 파일을 classpath에서 찾을 수 없습니다.");
            }
            props.load(input);
            return props.getProperty("openai.api.key");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("API 키를 불러오지 못했습니다.", e);
        }
    }

    /**
     * OpenAI GPT API에 프롬프트를 전송하고 결과 메시지 리스트를 반환하는 메서드
     *
     * @param prompt 사용자 프롬프트 (예: 오늘의 응원 메시지 생성 요청)
     * @return GPT 응답으로부터 파싱한 메시지 리스트
     */
    public static List<String> askGPT(String prompt) {
        List<String> result = new ArrayList<>();

        try {
            String apiKey = getApiKey();
            System.out.println("API KEY: [" + apiKey + "]");

            ObjectMapper mapper = new ObjectMapper(); // JSON 직렬화/역직렬화 도구

            // OpenAI 메시지 형식 (역할/내용)
            List<Object> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));

            // 최종 요청 JSON 객체 구성
            var payload = Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", messages
            );

            // JSON 문자열로 변환
            String jsonRequest = mapper.writeValueAsString(payload);

            // HTTP 요청 생성 (POST 방식)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            // 요청 전송 및 응답 수신
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 응답 실패 처리
            if (response.statusCode() != 200) {
                System.out.println("❌ GPT 응답 실패 - 코드: " + response.statusCode());
                System.out.println("응답 바디: " + response.body());
                return result;
            }

            // 응답 본문 파싱
            String responseBody = response.body();
            JsonNode root = mapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            System.out.println("✅ GPT 응답:\n" + content);

            // "1. xxx", "2. yyy" 형식의 응답 라인만 추출
            for (String line : content.split("\n")) {
                line = line.trim();
                if (line.matches("^\\d+\\.\\s.*")) {
                    // 앞 번호 제거 후 메시지만 추출
                    result.add(line.substring(line.indexOf('.') + 1).trim());
                }
            }

            return result;

        } catch (Exception e) {
            System.out.println("gpt 요청 실패 :: " + e.getMessage());
            return result;
        }
    }
}
