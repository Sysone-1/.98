package com.sysone.ogamza.view.user;

import com.calendarfx.model.*;
import com.calendarfx.view.CalendarView;
import com.sysone.ogamza.controller.user.ScheduleContentController;
import com.sysone.ogamza.dto.user.ScheduleContentDTO;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * 사용자 일정 목록을 월간 캘린더 형식으로 시각화하는 커스텀 JavaFX 컴포넌트입니다.
 * <p>
 * CalendarFX 라이브러리를 기반으로 하며, {@link ScheduleContentDTO} 객체 목록을 받아
 * 달력에 일정 항목을 등록합니다. 사용자는 일정 항목을 더블 클릭하여 상세 정보를 확인할 수 있습니다.
 * </p>
 *
 * @author 김민호
 */
public class MonthView extends VBox {

    private final Calendar<ScheduleContentDTO> calendar;

    public MonthView(List<ScheduleContentDTO> list) {
        this.calendar = new Calendar<>("내 일정");
        calendar.setStyle(Calendar.Style.STYLE1);

        setSpacing(10);
        setPadding(new Insets(20));

        addEntries(list);
        CalendarView calendarView = createCalendarView();

        getChildren().add(calendarView);
    }

    /**
     * 일정 목록을 CalendarFX의 {@link Entry} 객체로 변환하여 달력에 추가합니다.
     *
     * @param list 일정 DTO 목록
     */
    private void addEntries(List<ScheduleContentDTO> list) {
        for (ScheduleContentDTO dto : list) {
            Entry<ScheduleContentDTO> entry = new Entry<>(dto.getTitle());
            entry.setUserObject(dto);
            entry.setInterval(dto.getStartDate(), dto.getEndDate());
            entry.setFullDay(true);
            calendar.addEntry(entry);
        }
    }

    /**
     * CalendarFX 기반 {@link CalendarView}를 생성하고 UI 요소들을 구성합니다.
     *
     * @return 구성된 CalendarView 객체
     */
    private CalendarView createCalendarView() {
        CalendarView calendarView = new CalendarView();

        CalendarSource source = new CalendarSource("내 캘린더");
        source.getCalendars().add(calendar);
        calendarView.getCalendarSources().add(source);

        // 기본 UI 비활성화
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendarView.setShowSearchField(false);
        calendarView.setShowPageSwitcher(false);
        calendarView.setShowToolBar(false);
        calendarView.setRequestedTime(java.time.LocalTime.now());

        calendarView.setPrefSize(950, 650);
        calendarView.getMonthPage().getMonthView().setPrefHeight(650);
        calendarView.showMonthPage();

        // 기본 이벤트 핸들러 끄기
        calendarView.getMonthPage().setEntryDetailsPopOverContentCallback(param -> null);

        // 일정 리스트 더블 클릭 시 상세 조회 팝업
        calendarView.getMonthPage().setEntryDetailsCallback(param -> {
            Entry<?> entry = param.getEntry();
            entryPopup(List.of(entry));
            return null;
        });

        return calendarView;
    }

    /**
     * 일정 항목 클릭 시 상세 정보를 보여주는 팝업 창을 띄웁니다.
     *
     * @param entries 클릭된 일정 항목 리스트 (보통 1개)
     */
    private void entryPopup(List<Entry<?>> entries) {
        try {
            Entry<?> entry = entries.get(0);
            if (!(entry.getUserObject() instanceof ScheduleContentDTO dto)) return;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/ScheduleContent.fxml"));
            Parent root = loader.load();

            ScheduleContentController controller = loader.getController();
            controller.setData(dto);

            Stage popup = new Stage();
            controller.setStage(popup);
            popup.setScene(new Scene(root));
            popup.setTitle("일정 상세");
            popup.setResizable(false);
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
