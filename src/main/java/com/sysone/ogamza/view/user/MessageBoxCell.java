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

public class MessageBoxCell extends ListCell<MessageBoxViewDTO> {

        private final Consumer<Integer> onClick;

        public MessageBoxCell(Consumer<Integer> onClick) {
                this.onClick = onClick;
        }

        @Override
        protected void updateItem(MessageBoxViewDTO msg, boolean empty) {
                super.updateItem(msg, empty);

                if (empty || msg == null) {
                        setGraphic(null);
                        setText(null);
                        setStyle("-fx-background-color: transparent;");
                        return;
                }

                HBox root = new HBox(12);
                root.setPadding(new Insets(10));
                root.setAlignment(Pos.CENTER_LEFT);

                if (isSelected()) {
                        root.setStyle("-fx-background-color: #F0F8FF;");
                } else {
                        root.setStyle("-fx-background-color: white; -fx-border-color: #d8d8d8; -fx-border-width: 0 0 1 0;");
                }

                // 프로필 이미지 (null 체크)
                ImageView profile = null;
                if (msg.getProfileImagePath() != null && !msg.getProfileImagePath().isBlank()) {
                        profile = new ImageView(new Image(msg.getProfileImagePath()));
                        profile.setFitWidth(50);
                        profile.setFitHeight(50);
                        Circle clip = new Circle(25, 25, 25);
                        profile.setClip(clip);
                }
                // 부서
                Label dept = new Label(msg.getDeptName());
                dept.setStyle("-fx-font-size: 13px; fx-text-fill: #5D5C5C;");

                // 이름
                Label name = new Label(msg.getName());
                name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #333333;");

                HBox infoBox = new HBox(3, dept, name);
                infoBox.setAlignment(Pos.CENTER_LEFT);

                // 내용
                String previewText = msg.getContent();
                if (previewText.length() > 15) {
                        previewText = previewText.substring(0, 15) + "...";
                }

                Label content = new Label(previewText);
                content.setWrapText(true);
                content.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
                content.setWrapText(true);

                VBox messageBox = new VBox(4, infoBox, content);
                messageBox.setAlignment(Pos.CENTER_LEFT);

                // 날짜 + 읽음 상태
                VBox metaBox = new VBox(5);
                metaBox.setAlignment(Pos.CENTER_RIGHT);
                metaBox.setPrefWidth(80);

                Label date = new Label(msg.getSendDate().toString());
                date.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaaaaa;");

                if (msg instanceof com.sysone.ogamza.dto.user.MessageInBoxDTO) {
                        Label status = new Label(msg.getIsRead() == 1 ? "읽음" : "안읽음");
                        status.setTextFill(msg.getIsRead() == 1 ? Color.GRAY : Color.web("#1E90FF"));
                        status.setStyle("-fx-font-size: 12px;");
                        metaBox.getChildren().addAll(date, status);
                } else {
                        metaBox.getChildren().addAll(date);  // 보낸 쪽지는 읽음표시 생략
                }

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                if (profile != null)
                        root.getChildren().addAll(profile, messageBox, spacer, metaBox);
                else
                        root.getChildren().addAll(messageBox, spacer, metaBox);

                setGraphic(root);

                // ✅ 클릭 처리
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
