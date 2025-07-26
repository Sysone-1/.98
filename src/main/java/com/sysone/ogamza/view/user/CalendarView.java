package com.sysone.ogamza.view.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.IntStream;

import com.sysone.ogamza.service.user.HolidayService;

/**
 * 커스터마이징된 달력 컴포넌트 클래스.
 * 월, 연도, 10년 단위로 전환 가능한 뷰 모드를 제공하며, 공휴일 표시 및 오늘 날짜 강조 기능을 포함함.
 *
 * 내부적으로 MONTH / SELECT_MONTH / SELECT_YEAR 모드를 전환하며 사용자 인터랙션을 처리함.
 *
 * @author 서샘이
 */
public class CalendarView extends VBox {

    /**
     * 달력 뷰의 모드를 정의하는 enum
     */
    private enum ViewMode { MONTH, SELECT_MONTH, SELECT_YEAR }

    private final VBox container = new VBox(); // 전체 달력 컨테이너
    private final VBox contentWrapper = new VBox(); // 헤더와 그리드 포함하는 래퍼
    private final HBox header = new HBox(); // 월/년 타이틀 + 네비게이션
    private final Label titleLabel = new Label(); // 월/년 표시 라벨
    private final GridPane contentGrid = new GridPane(); // 날짜, 월, 연도 표시하는 그리드

    private ViewMode currentMode = ViewMode.MONTH; // 현재 달력 모드
    private YearMonth currentYearMonth = YearMonth.now(); // 현재 선택된 연월
    private final Map<Integer, String> holidayMap; // 공휴일 데이터

    /**
     * CalendarView 생성자
     * - 초기화 및 레이아웃 구성
     */
    public CalendarView() {
        this.holidayMap = HolidayService.getHolidays(YearMonth.now().getYear(), YearMonth.now().getMonthValue());

        setPrefSize(247, 230);
        setSpacing(5);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        setupHeader();
        renderMonthView();

        contentWrapper.setSpacing(10);
        contentWrapper.getChildren().addAll(header, contentGrid);
        container.getChildren().add(contentWrapper);
        getChildren().add(container);
    }

    /**
     * 달력 상단 헤더 구성 (← 년도/월 →)
     */
    private void setupHeader() {
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label prev = new Label("<");
        Label next = new Label(">");

        prev.setFont(Font.font("SUIT", FontWeight.BOLD, 16));
        next.setFont(Font.font("SUIT", FontWeight.BOLD, 16));
        prev.setOnMouseClicked(e -> navigate(-1));
        next.setOnMouseClicked(e -> navigate(1));

        titleLabel.setFont(Font.font("SUIT", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setOnMouseClicked(this::switchViewMode);

        header.setAlignment(Pos.CENTER);
        header.setSpacing(5);
        header.getChildren().addAll(prev, spacer1, titleLabel, spacer2, next);
    }

    /**
     * 헤더 클릭 시 뷰 모드 전환
     */
    private void switchViewMode(MouseEvent e) {
        switch (currentMode) {
            case MONTH -> renderMonthSelector();
            case SELECT_MONTH -> renderYearSelector();
            case SELECT_YEAR -> renderMonthView();
        }
    }

    /**
     * 뷰 모드에 따라 연/월/10년 단위 이동
     * @param direction -1: 이전, 1: 다음
     */
    private void navigate(int direction) {
        switch (currentMode) {
            case MONTH -> currentYearMonth = currentYearMonth.plusMonths(direction);
            case SELECT_MONTH -> currentYearMonth = currentYearMonth.plusYears(direction);
            case SELECT_YEAR -> currentYearMonth = currentYearMonth.plusYears(direction * 10);
        }

        holidayMap.clear();
        holidayMap.putAll(
                HolidayService.getHolidays(currentYearMonth.getYear(), currentYearMonth.getMonthValue())
        );

        render();
    }

    /**
     * 현재 모드에 따라 뷰 렌더링
     */
    private void render() {
        switch (currentMode) {
            case MONTH -> renderMonthView();
            case SELECT_MONTH -> renderMonthSelector();
            case SELECT_YEAR -> renderYearSelector();
        }
    }

    /**
     * 월별 날짜 뷰 렌더링
     */
    private void renderMonthView() {
        currentMode = ViewMode.MONTH;
        titleLabel.setText(currentYearMonth.getYear() + "년 " + currentYearMonth.getMonthValue() + "월");

        contentGrid.getChildren().clear();
        contentGrid.setHgap(2);
        contentGrid.setVgap(2);
        contentGrid.setAlignment(Pos.CENTER);

        // 요일 헤더
        String[] days = {"일", "월", "화", "수", "목", "금", "토"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setFont(Font.font("SUIT", FontWeight.BOLD, 13));
            dayLabel.setPrefWidth(31);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setTextFill(i == 0 ? Color.RED : (i == 6 ? Color.BLUE : Color.BLACK));
            contentGrid.add(dayLabel, i, 0);
        }

        // 날짜 렌더링
        LocalDate firstDay = currentYearMonth.atDay(1);
        int startCol = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentYearMonth.lengthOfMonth();

        int row = 1, col = startCol;
        for (int day = 1; day <= daysInMonth; day++) {
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setFont(Font.font("SUIT", FontWeight.NORMAL, 13));
            dayLabel.setPrefSize(28, 28);
            dayLabel.setAlignment(Pos.CENTER);

            // 공휴일 강조
            if (holidayMap.containsKey(day)) {
                dayLabel.setTextFill(Color.RED);
                dayLabel.setFont(Font.font("SUIT", FontWeight.BOLD, 13));
                Tooltip.install(dayLabel, new Tooltip(holidayMap.get(day)));
            }

            // 오늘 날짜 강조
            if (LocalDate.now().equals(currentYearMonth.atDay(day))) {
                dayLabel.setTextFill(Color.WHITE);
                dayLabel.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 100%;");
                dayLabel.setFont(Font.font("SUIT", FontWeight.BOLD, 13));
            }

            contentGrid.add(dayLabel, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * 월 선택 화면 렌더링
     */
    private void renderMonthSelector() {
        currentMode = ViewMode.SELECT_MONTH;
        titleLabel.setText(currentYearMonth.getYear() + "");
        contentGrid.getChildren().clear();
        contentGrid.setHgap(5);
        contentGrid.setVgap(5);

        for (int i = 1; i <= 12; i++) {
            Label month = new Label(i + "월");
            month.setFont(Font.font(13));
            month.setPrefSize(50, 35);
            month.setAlignment(Pos.CENTER);
            month.setStyle("-fx-border-color: #ccc; -fx-background-color: transparent; -fx-border-radius: 3; -fx-background-radius: 3;");
            int finalI = i;
            month.setOnMouseClicked(e -> {
                currentYearMonth = YearMonth.of(currentYearMonth.getYear(), finalI);
                renderMonthView();
            });
            contentGrid.add(month, (i - 1) % 4, (i - 1) / 4);
        }
    }

    /**
     * 연도 선택 화면 렌더링 (10년 단위)
     */
    private void renderYearSelector() {
        currentMode = ViewMode.SELECT_YEAR;
        int baseYear = (currentYearMonth.getYear() / 10) * 10;
        titleLabel.setText(baseYear + " ~ " + (baseYear + 9));

        contentGrid.getChildren().clear();
        contentGrid.setHgap(5);
        contentGrid.setVgap(5);

        IntStream.range(baseYear, baseYear + 12).forEach(y -> {
            Label year = new Label(String.valueOf(y));
            year.setFont(Font.font(13));
            year.setPrefSize(50, 35);
            year.setAlignment(Pos.CENTER);
            year.setStyle("-fx-border-color: #ccc; -fx-background-color: transparent; -fx-border-radius: 3; -fx-background-radius: 3;");
            year.setOnMouseClicked(e -> {
                currentYearMonth = YearMonth.of(y, currentYearMonth.getMonth());
                renderMonthSelector();
            });
            contentGrid.add(year, (y - baseYear) % 4, (y - baseYear) / 4);
        });
    }

}
