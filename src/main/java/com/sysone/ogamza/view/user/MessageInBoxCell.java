package com.sysone.ogamza.view.user;

import com.sysone.ogamza.dto.user.MessageInBoxDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.function.Consumer;

public class MessageInBoxCell extends ListCell<MessageInBoxDTO> {

        private final Consumer<Integer> onDoubleClick;

        public MessageInBoxCell(Consumer<Integer> onDoubleClick) {
                this.onDoubleClick = onDoubleClick;
        }

        @Override
        protected void updateItem(MessageInBoxDTO msg, boolean empty) {
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

                // 프로필 이미지
                ImageView profile = new ImageView(new Image(msg.getSenderProfile()));
                profile.setFitWidth(50);
                profile.setFitHeight(50);
                Circle clip = new Circle(25, 25, 25);
                profile.setClip(clip);

                // 이름 / 부서
                Label dept = new Label(msg.getSenderDept());
                dept.setStyle("-fx-font-size: 13px; -fx-text-fill: #777777;");

                Label name = new Label(" " + msg.getSenderName());
                name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #333333;");

                HBox nameBox = new HBox(dept, name);
                nameBox.setAlignment(Pos.CENTER_LEFT);
                nameBox.setSpacing(2);

                // 내용
                Label content = new Label(msg.getContent());
                content.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
                content.setWrapText(true);

                VBox messageBox = new VBox(4);
                messageBox.setAlignment(Pos.CENTER_LEFT);
                messageBox.getChildren().addAll(nameBox, content);

                // 날짜 + 읽음 여부
                VBox metaBox = new VBox(5);
                metaBox.setAlignment(Pos.CENTER_RIGHT);
                metaBox.setPrefWidth(80);

                Label date = new Label(msg.getSendDate().toString());
                date.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaaaaa;");

                Label status = new Label(msg.getIsRead() == 1 ? "읽음" : "안읽음");
                status.setTextFill(msg.getIsRead() == 1 ? Color.GRAY : Color.web("#1E90FF"));
                status.setStyle("-fx-font-size: 12px;");

                metaBox.getChildren().addAll(date, status);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                root.getChildren().addAll(profile, messageBox, spacer, metaBox);
                setGraphic(root);

                // ✅ 더블 클릭 이벤트
                setOnMouseClicked(event -> {
                        if (event.getClickCount() == 1) {
                                onDoubleClick.accept(msg.getMessageId());
                        }
                });
        }
}
