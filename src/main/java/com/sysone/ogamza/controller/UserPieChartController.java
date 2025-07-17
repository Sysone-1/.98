package com.sysone.ogamza.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.ResourceBundle;

public class UserPieChartController implements Initializable {

    @FXML private PieChart workStatusChart;
    @FXML private PieChart departmentChart;
    @FXML private Label workStatusPercent;
    @FXML private Label departmentPercent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Controller 초기화 시작");

        // 임의 데이터로 시각화 구현
        setupWorkStatusChart();
        setupDepartmentChart();

        System.out.println("Controller 초기화 완료");
    }

    private void setupWorkStatusChart() {
        // 근무 현황 임의 데이터 생성
        ObservableList<PieChart.Data> workData = FXCollections.observableArrayList(
                new PieChart.Data("출근인원", 85),    // 85명
                new PieChart.Data("지각", 8),        // 8명
                new PieChart.Data("미출근", 5),      // 5명
                new PieChart.Data("휴가", 2)         // 2명
        );

        // 차트에 데이터 적용
        workStatusChart.setData(workData);
        workStatusChart.setTitle("근무 현황");
        workStatusChart.setLegendVisible(true);
        workStatusChart.setLabelsVisible(false);

        // 출근율 계산 (85/100 = 85%)
        if (workStatusPercent != null) {
            workStatusPercent.setText("85%");
        }

        // 색상 적용
        workStatusChart.applyCss();
        workStatusChart.layout();

        // 색상 설정
        workStatusChart.dataProperty().addListener((obs, oldData, newData) -> {
            if (newData != null && newData.size() > 0) {
                newData.get(0).getNode().setStyle("-fx-pie-color: #4CAF50;"); // 녹색 - 출근
                newData.get(1).getNode().setStyle("-fx-pie-color: #FF9800;"); // 주황색 - 지각
                newData.get(2).getNode().setStyle("-fx-pie-color: #F44336;"); // 빨간색 - 미출근
                newData.get(3).getNode().setStyle("-fx-pie-color: #2196F3;"); // 파란색 - 휴가
            }
        });

        System.out.println("근무 현황 차트 설정 완료");
    }

    private void setupDepartmentChart() {
        // 부서별 근무 현황 임의 데이터 생성
        ObservableList<PieChart.Data> deptData = FXCollections.observableArrayList(
                new PieChart.Data("인사팀", 15),      // 15명
                new PieChart.Data("경영팀", 25),      // 25명
                new PieChart.Data("개발팀", 35),      // 35명
                new PieChart.Data("마케팅팀", 25)     // 25명
        );

        // 차트에 데이터 적용
        departmentChart.setData(deptData);
        departmentChart.setTitle("부서별 근무 현황");
        departmentChart.setLegendVisible(true);
        departmentChart.setLabelsVisible(false);

        // 개발팀 비율 계산 (35/100 = 35%)
        if (departmentPercent != null) {
            departmentPercent.setText("35%");
        }

        // 색상 적용
        departmentChart.applyCss();
        departmentChart.layout();

        // 색상 설정
        departmentChart.dataProperty().addListener((obs, oldData, newData) -> {
            if (newData != null && newData.size() > 0) {
                newData.get(0).getNode().setStyle("-fx-pie-color: #9C27B0;"); // 보라색 - 인사팀
                newData.get(1).getNode().setStyle("-fx-pie-color: #3F51B5;"); // 남색 - 경영팀
                newData.get(2).getNode().setStyle("-fx-pie-color: #00BCD4;"); // 청록색 - 개발팀
                newData.get(3).getNode().setStyle("-fx-pie-color: #CDDC39;"); // 라임색 - 마케팅팀
            }
        });

        System.out.println("부서별 차트 설정 완료");
    }

    // 나중에 실제 DB 연동 시 사용할 메서드
    public void updateWithRealData() {
        // 실제 Oracle DB에서 데이터 가져오기
        // 현재는 임시로 더미 데이터 사용
        System.out.println("실제 DB 데이터로 업데이트 예정");
    }
}
