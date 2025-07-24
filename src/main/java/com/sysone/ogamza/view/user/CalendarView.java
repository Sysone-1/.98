package com.sysone.ogamza.view.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.IntStream;
import com.sysone.ogamza.service.user.HolidayService;

public class CalendarView extends VBox {

    private enum ViewMode { MONTH, SELECT_MONTH, SELECT_YEAR }

    private final VBox container = new VBox();
    private final VBox contentWrapper = new VBox();
    private final HBox header = new HBox();
    private final Label titleLabel = new Label();
    private final GridPane contentGrid = new GridPane();

    private ViewMode currentMode = ViewMode.MONTH;
    private YearMonth currentYearMonth = YearMonth.now();
    private final Map<Integer, String> holidayMap;

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

    private void switchViewMode(MouseEvent e) {
        switch (currentMode) {
            case MONTH -> renderMonthSelector();
            case SELECT_MONTH -> renderYearSelector();
            case SELECT_YEAR -> renderMonthView();
        }
    }

    private void navigate(int direction) {
        switch (currentMode) {
            case MONTH -> currentYearMonth = currentYearMonth.plusMonths(direction);
            case SELECT_MONTH -> currentYearMonth = currentYearMonth.plusYears(direction);
            case SELECT_YEAR -> currentYearMonth = currentYearMonth.plusYears(direction * 10);
        }
        // ✅ 공휴일 다시 로드
        holidayMap.clear();
        holidayMap.putAll(
                HolidayService.getHolidays(currentYearMonth.getYear(), currentYearMonth.getMonthValue())
        );

        render();

        render();
    }

    private void render() {
        switch (currentMode) {
            case MONTH -> renderMonthView();
            case SELECT_MONTH -> renderMonthSelector();
            case SELECT_YEAR -> renderYearSelector();
        }
    }

    private void renderMonthView() {
        currentMode = ViewMode.MONTH;
        titleLabel.setText(currentYearMonth.getYear() + "년 " + currentYearMonth.getMonthValue() + "월");

        contentGrid.getChildren().clear();
        contentGrid.setHgap(2);
        contentGrid.setVgap(2);
        contentGrid.setAlignment(Pos.CENTER);

        String[] days = {"일", "월", "화", "수", "목", "금", "토"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setFont(Font.font("SUIT", FontWeight.BOLD, 13));
            dayLabel.setPrefWidth(31);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setTextFill(i == 0 ? Color.RED : (i == 6 ? Color.BLUE : Color.BLACK));
            contentGrid.add(dayLabel, i, 0);
        }

        LocalDate firstDay = currentYearMonth.atDay(1);
        int startCol = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentYearMonth.lengthOfMonth();

        int row = 1, col = startCol;
        for (int day = 1; day <= daysInMonth; day++) {
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setFont(Font.font("SUIT", FontWeight.NORMAL, 13));
            dayLabel.setPrefSize(28, 28);
            dayLabel.setAlignment(Pos.CENTER);

            // ── 공휴일 처리 ──────────────────────────────
            if (holidayMap.containsKey(day)) {
                dayLabel.setTextFill(Color.RED);
                dayLabel.setFont(Font.font("SUIT", FontWeight.BOLD, 13));
                Tooltip.install(dayLabel, new Tooltip(holidayMap.get(day)));
            }

            // ── 오늘 날짜 강조 (공휴일보다 우선 적용) ───────
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
