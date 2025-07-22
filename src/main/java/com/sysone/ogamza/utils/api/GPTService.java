package com.sysone.ogamza.utils.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GPTService {
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

    public static List<String> askGPT(String prompt) {
        List<String> result = new ArrayList<>();

        try {
            String apiKey = getApiKey();
            System.out.println("API KEY: [" + apiKey + "]");

            // ObjectMapper로 JSON 안전하게 구성
            ObjectMapper mapper = new ObjectMapper();

            // 메시지 객체 구성
            List<Object> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));

            // 최종 요청 객체 구성
            var payload = Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", messages
            );

            String jsonRequest = mapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("❌ GPT 응답 실패 - 코드: " + response.statusCode());
                System.out.println("응답 바디: " + response.body());
                return result;
            }

            String responseBody = response.body();

            JsonNode root = mapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            System.out.println("✅ GPT 응답:\n" + content);

            for (String line : content.split("\n")) {
                line = line.trim();
                if (line.matches("^\\d+\\.\\s.*")) {
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