package com.sysone.ogamza.utils.api;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * GPT API 요청에 사용할 프롬프트 메시지를 생성하는 유틸리티 클래스입니다.
 * 요일에 따라 분위기와 톤이 다른 응원 메시지를 생성하도록 설계되었습니다.
 *
 * 예: 월요일은 유쾌한, 수요일은 감성적인, 금요일은 신나는 메시지로 구성
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class GptPrompt {

    /**
     * 오늘의 요일에 맞는 응원 프롬프트를 생성합니다.
     *
     * @param size 오늘 출근한 인원 수 (생성할 메시지 수)
     * @return GPT에 전달할 프롬프트 문자열
     */
    public static String getPromptForToday(int size) {
        // 오늘의 요일을 구함
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        // 요일별로 톤이 다른 메시지 요청 프롬프트를 반환
        return switch (today) {
            case MONDAY ->
                // 월요일: 유쾌하고 재치 있는 메시지
                    """
                    오늘은 월요일이야. 출근한 직원 %d명을 위해 각각 다르게 유쾌하고 재치 있는 응원 메시지를 %d개 만들어줘.
                    너무 진지하지 않고, 웃음을 줄 수 있는 가벼운 톤이면 좋겠어.
                    각 메시지는 서로 다르게 작성하고, 1번부터 번호를 붙여서 보여줘.
                    """.formatted(size, size);

            case TUESDAY ->
                // 화요일: 진지하고 자기계발 중심 메시지
                    """
                    오늘은 화요일이야. 출근한 직원 %d명을 위해 서로 다른 짧은 진지한 응원 메시지를 %d개 만들어줘.
                    집중, 끈기, 자기계발을 응원하는 말이면 좋겠어. 각 메시지는 번호를 붙여서 보여줘.
                    """.formatted(size, size);

            case WEDNESDAY ->
                // 수요일: 감성적이고 따뜻한 메시지
                    """
                    오늘은 수요일이야. %d명의 직원들을 위해 서로 다른 따뜻하고 감성적인 응원 메시지를 %d개 만들어줘.
                    지친 사람에게 위로가 되는 말이면 좋겠어. 번호를 붙여서 정리해줘.
                    """.formatted(size, size);

            case THURSDAY ->
                // 목요일: ‘거의 다 왔어’ 느낌의 메시지
                    """
                    오늘은 목요일이야. 일주일의 막바지를 향해가는 %d명을 위해 진심이 담긴 응원 메시지를 %d개 만들어줘.
                    '거의 다 왔어'라는 느낌이 드는 말을 부탁해. 번호 붙여서 정리해줘.
                    """.formatted(size, size);

            case FRIDAY ->
                // 금요일: 활기찬 분위기의 메시지
                    """
                    오늘은 금요일이야. 불금을 앞둔 %d명의 직원들에게 신나고 활기찬 응원 메시지를 %d개 만들어줘.
                    분위기를 띄울 수 있는 유쾌하고 에너지 넘치는 말이면 좋겠어. 각 문장 앞에 번호 붙여줘.
                    """.formatted(size, size);

            default ->
                // 주말이나 기타 요일: 기본 응원 메시지
                    """
                    오늘 하루를 응원하는 짧고 긍정적인 메시지를 %d개 만들어줘.
                    서로 다른 메시지로 작성하고, 각 메시지에 번호를 붙여줘.
                    """.formatted(size);
        };
    }
}
