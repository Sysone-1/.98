package com.sysone.ogamza.utils.api;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class GptPrompt {

    public static String getPromptForToday(int size) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        return switch (today) {
            case MONDAY -> """
                오늘은 월요일이야. 출근한 직원 %d명을 위해 각각 다르게 유쾌하고 재치 있는 응원 메시지를 %d개 만들어줘.
                너무 진지하지 않고, 웃음을 줄 수 있는 가벼운 톤이면 좋겠어.
                각 메시지는 서로 다르게 작성하고, 1번부터 번호를 붙여서 보여줘.
                """.formatted(size, size);

            case TUESDAY -> """
                오늘은 화요일이야. 출근한 직원 %d명을 위해 서로 다른 짧은 진지한 응원 메시지를 %d개 만들어줘.
                집중, 끈기, 자기계발을 응원하는 말이면 좋겠어. 각 메시지는 번호를 붙여서 보여줘.
                """.formatted(size, size);

            case WEDNESDAY -> """
                오늘은 수요일이야. %d명의 직원들을 위해 서로 다른 따뜻하고 감성적인 응원 메시지를 %d개 만들어줘.
                지친 사람에게 위로가 되는 말이면 좋겠어. 번호를 붙여서 정리해줘.
                """.formatted(size, size);

            case THURSDAY -> """
                오늘은 목요일이야. 일주일의 막바지를 향해가는 %d명을 위해 진심이 담긴 응원 메시지를 %d개 만들어줘.
                '거의 다 왔어'라는 느낌이 드는 말을 부탁해. 번호 붙여서 정리해줘.
                """.formatted(size, size);

            case FRIDAY -> """
                오늘은 금요일이야. 불금을 앞둔 %d명의 직원들에게 신나고 활기찬 응원 메시지를 %d개 만들어줘.
                분위기를 띄울 수 있는 유쾌하고 에너지 넘치는 말이면 좋겠어. 각 문장 앞에 번호 붙여줘.
                """.formatted(size, size);

            default -> """
                오늘 하루를 응원하는 짧고 긍정적인 메시지를 %d개 만들어줘.
                서로 다른 메시지로 작성하고, 각 메시지에 번호를 붙여줘.
                """.formatted(size);
        };
    }
}
