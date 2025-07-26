package com.sysone.ogamza.view.user;

import com.sysone.ogamza.dto.user.MessageBoxViewDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.function.Consumer;

/**
 * 쪽지함에서 사용되는 커스텀 셀 클래스
 * 각 메시지를 커스터마이징된 UI로 표현
 *
 *  @author 서샘이
 *  @since 2025-07-27
 */
public class MessageBoxCell extends ListCell<MessageBoxViewDTO> {

        // 셀 클릭 시 메시지 ID를 넘겨주는 콜백
        private final Consumer<Integer> onClick;

        public MessageBoxCell(Consumer<Integer> onClick) {
                this.onClick = onClick;
        }

        @Override
        protected void updateItem(MessageBoxViewDTO msg, boolean empty) {
                super.updateItem(msg, empty);

                // 비어있으면 렌더링하지 않음
                if (empty || msg == null) {
                        setGraphic(null);
                        setText(null);
                        setStyle("-fx-background-color: transparent;");
                        return;
                }

                // 셀 컨테이너
                HBox root = new HBox(12);
                root.setPadding(new Insets(10));
                root.setAlignment(Pos.CENTER_LEFT);

                // 선택 상태에 따라 배경 스타일 변경
                if (isSelected()) {
                        root.setStyle("-fx-background-color: #F0F8FF;");
                } else {
                        root.setStyle("-fx-background-color: white; -fx-border-color: #d8d8d8; -fx-border-width: 0 0 1 0;");
                }

                // 프로필 이미지가 존재할 경우 설정
                ImageView profile = null;
                if (msg.getProfileImagePath() != null && !msg.getProfileImagePath().isBlank()) {
                        profile = new ImageView(new Image(msg.getProfileImagePath()));
                        profile.setFitWidth(50);
                        profile.setFitHeight(50);
                        Circle clip = new Circle(25, 25, 25);
                        profile.setClip(clip); // 동그란 클립 처리
                }

                // 부서명 레이블
                Label dept = new Label(msg.getDeptName());
                dept.setStyle("-fx-font-size: 13px; fx-text-fill: #5D5C5C;");

                // 이름 레이블
                Label name = new Label(msg.getName());
                name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #333333;");

                // 부서 + 이름 박스
                HBox infoBox = new HBox(3, dept, name);
                infoBox.setAlignment(Pos.CENTER_LEFT);

                // 내용 미리보기 (15자 초과 시 생략)
                String previewText = msg.getContent();
                if (previewText.length() > 15) {
                        previewText = previewText.substring(0, 15) + "...";
                }

                Label content = new Label(previewText);
                content.setWrapText(true);
                content.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");

                // 이름 + 내용 수직 정렬 박스
                VBox messageBox = new VBox(4, infoBox, content);
                messageBox.setAlignment(Pos.CENTER_LEFT);

                // 날짜 + 읽음 상태 박스
                VBox metaBox = new VBox(5);
                metaBox.setAlignment(Pos.CENTER_RIGHT);
                metaBox.setPrefWidth(80);

                // 날짜 라벨
                Label date = new Label(msg.getSendDate().toString());
                date.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaaaaa;");
                metaBox.getChildren().add(date);

                // 수신함인 경우 읽음 여부 표시
                if (msg instanceof com.sysone.ogamza.dto.user.MessageInBoxDTO) {
                        Label status = new Label(msg.getIsRead() == 1 ? "읽음" : "안읽음");
                        status.setTextFill(msg.getIsRead() == 1 ? Color.GRAY : Color.web("#1E90FF"));
                        status.setStyle("-fx-font-size: 12px;");
                        metaBox.getChildren().add(status);
                }

                // 좌우 여백용 공간
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // 프로필 이미지 유무에 따라 구성
                if (profile != null)
                        root.getChildren().addAll(profile, messageBox, spacer, metaBox);
                else
                        root.getChildren().addAll(messageBox, spacer, metaBox);

                setGraphic(root);

                // 셀 클릭 시 콜백 처리 (쪽지 상세로)
                setOnMouseClicked(event -> {
                        if (event.getClickCount() == 1) {
                                if (msg instanceof com.sysone.ogamza.dto.user.MessageInBoxDTO inbox) {
                                        onClick.accept(inbox.getMessageId());
                                } else if (msg instanceof com.sysone.ogamza.dto.user.MessageSentBoxDTO sent) {
                                        onClick.accept(sent.getMessageId());
                                }
                        }
                });
        }
}
