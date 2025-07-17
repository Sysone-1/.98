package com.sysone.ogamza.view.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.stream.IntStream;

public class CalendarView extends VBox {

    private enum ViewMode { MONTH, SELECT_MONTH, SELECT_YEAR }

    private final VBox container = new VBox();
    private final VBox contentWrapper = new VBox();
    private final HBox header = new HBox();
    private final Label titleLabel = new Label();
    private final GridPane contentGrid = new GridPane();

    private ViewMode currentMode = ViewMode.MONTH;
    private YearMonth currentYearMonth = YearMonth.now();

    public CalendarView() {
        setSpacing(10);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: white; -fx-border-radius: 12; -fx-background-radius: 12;");

        setupHeader();
        renderMonthView();

        contentWrapper.setSpacing(15); // spacing between title and calendar
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

        prev.setFont(Font.font(16));
        next.setFont(Font.font(16));
        prev.setOnMouseClicked(e -> navigate(-1));
        next.setOnMouseClicked(e -> navigate(1));

        titleLabel.setFont(Font.font(20));
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setOnMouseClicked(this::switchViewMode);

        header.setAlignment(Pos.CENTER);
        header.setSpacing(10);
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
        contentGrid.setHgap(5);
        contentGrid.setVgap(5);

        String[] days = {"일", "월", "화", "수", "목", "금", "토"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setFont(Font.font(14));
            dayLabel.setPrefWidth(40);
            dayLabel.setAlignment(Pos.CENTER);

            // 일요일 빨간색, 토요일 파란색
            if (i == 0) dayLabel.setTextFill(Color.RED);
            else if (i == 6) dayLabel.setTextFill(Color.BLUE);
            else dayLabel.setTextFill(Color.BLACK);

            contentGrid.add(dayLabel, i, 0);
        }

        LocalDate firstDay = currentYearMonth.atDay(1);
        int startCol = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentYearMonth.lengthOfMonth();

        int row = 1, col = startCol;
        for (int day = 1; day <= daysInMonth; day++) {
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setFont(Font.font(14));
            dayLabel.setPrefSize(40, 40);
            dayLabel.setAlignment(Pos.CENTER);

            int colIndex = col;
            if (LocalDate.now().equals(currentYearMonth.atDay(day))) {
                Circle dot = new Circle(3, Color.BLACK);
                StackPane dotPane = new StackPane(dayLabel, dot);
                StackPane.setAlignment(dot, Pos.BOTTOM_CENTER);
                contentGrid.add(dotPane, colIndex, row);
            } else {
                contentGrid.add(dayLabel, colIndex, row);
            }

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private void renderMonthSelector() {
        currentMode = ViewMode.SELECT_MONTH;
        titleLabel.setText(String.valueOf(currentYearMonth.getYear()));
        contentGrid.getChildren().clear();
        contentGrid.setHgap(10);
        contentGrid.setVgap(10);

        for (int i = 1; i <= 12; i++) {
            Label month = new Label(i + "월");
            month.setPrefSize(60, 40);
            month.setAlignment(Pos.CENTER);
            month.setStyle("-fx-border-color: #ccc; -fx-background-color: #f4f4f4; -fx-border-radius: 4; -fx-background-radius: 4;");
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
        contentGrid.setHgap(10);
        contentGrid.setVgap(10);

        IntStream.range(baseYear, baseYear + 12).forEach(y -> {
            Label year = new Label(String.valueOf(y));
            year.setPrefSize(60, 40);
            year.setAlignment(Pos.CENTER);
            year.setStyle("-fx-border-color: #ccc; -fx-background-color: #f4f4f4; -fx-border-radius: 4; -fx-background-radius: 4;");
            year.setOnMouseClicked(e -> {
                currentYearMonth = YearMonth.of(y, currentYearMonth.getMonth());
                renderMonthSelector();
            });
            contentGrid.add(year, (y - baseYear) % 4, (y - baseYear) / 4);
        });
    }
}
