package com.sysone.ogamza.view.user;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Screen;

import java.util.function.Consumer;

public class EmojiView {
    private final String[] emojis = {
            "😀","😁","😂","🤣","😃","😄","😅","😆","😉","😊",
            "😋","😎","😍","😘","😗","😙","😚","🥰️","🙂","🤗",
            "🤩","🤔","🤨","😐","😑","😶","🙄","😏","😣","😥",
            "😮","🤐","😯","😪","😫","😴","😌","😛","😜","😝",
            "🤤","😒","😓","😔","😕","🙃","🤑","😲","🙁",
            "😖","😞","😟","😤","😢","😭","😦","😧","😨","😩",
            "🤯","😬","😰","😱","🥵","🥶","😳","🤪","😵","😡",
            "😠","🤬","😷","🤒","🤕","🤢","🤮","🤧","😇","🥳",
            "🥴","🥺","🤠","😎","🤓","🧐","😕","😟","🙁","🤤️",
            "😮","😯","😲","😳","🥱","😤","😡","😠","🤬","😈",
            "👿","💀","☠️","💩","🤡","👹","👺","👻","👽","👾",
            "🤖","😺","😸","😹","😻","😼","😽","🙀","😿","😾",
            "🙈","🙉","🙊","🐵","🐶","🐺","🦊","🐱","🦁","🐯",
            "🐴","🦄","🐮","🐷","🐗","🐭","🐹","🐰","🐻","🐨",
            "🐼","🐸","🐲","🦖","🦕","🐙","🦑","🦐","🦞","🦀",
            "🐡","🐠","🐟","🐬","🐳","🐋","🦈","🐊","🐅","🐆",
            "🍏","🍎","🍐","🍊","🍋","🍌","🍉","🍇","🍓","🫐",
            "🍈","🍒","🍑","🥭","🍍","🥥","🥝","🍅","🍆","🥑",
            "🥦","🥬","🥒","🌶️","🫑","🌽","🥕","🧄","🧅","🥔",
            "🍠","🥐","🍞","🥖","🥨","🥯","🧀","🥚","🍳","🥞",
            "🧇","🥓","🥩","🍗","🍖","🌭","🍔","🍟","🍕","🥪",
            "🥙","🧆","🌮","🌯","🥗","🥘","🥫","🍝","🍜","🍲",
            "🍛","🍣","🍱","🥟","🦪","🍤","🍙","🍚","🍘","🍢",
            "🍡","🍧","🍨","🍦","🥧","🧁","🍰","🎂","🍮","🍭",
            "🍬","🍫","🍿","🍩","🍪","☕","🫖","🍵","🧃","🥤",
            "🧋","🧉","🍺","🍻","🥂","🍷","🥃","🍸","🍹","🧊",
            "🌵","🎄","🌲","🌳","🌴", "🪵","🌱","🌿","☘️","🍀",
            "🎍","🪴","🎋","🍃","🍂", "🍁","🍄","🐚","🪨","🌾",
            "💐","🌷","🌹","🥀","🌺", "🌸","🌼","🌻","🌞","🌝",
            "🌛","🌜","🌚","🌕","🌖"
    };

    private final Popup popup = new Popup();
    private final GridPane emojiGrid = new GridPane();
    private final ScrollPane scrollPane = new ScrollPane();

    public EmojiView(Node triggerNode, Node targetBox, Consumer<String> onEmojiSelected) {

        // Grid 스타일
        emojiGrid.setHgap(5);
        emojiGrid.setVgap(5);
        emojiGrid.setPadding(new Insets(10));
        emojiGrid.setStyle("-fx-background-color: white;");

        int col = 0, row = 0;

        for (String emoji : emojis) {
            Button btn = new Button(emoji);
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                btn.setFont(Font.font("Segoe UI Emoji", 15));
            } else {
                btn.setFont(Font.font("Apple Color Emoji", 15));
            }

            btn.setPrefSize(40, 40);
            btn.setStyle("-fx-background-color: transparent");

            btn.setOnAction(e -> {
                onEmojiSelected.accept(emoji);
                popup.hide();
            });

            emojiGrid.add(btn, col, row);
            col++;
            if (col >= 6) {
                col = 0;
                row++;
            }
        }

        // ScrollPane 설정
        scrollPane.setContent(emojiGrid);
        scrollPane.setPrefSize(300, 220);
        scrollPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");

        popup.getContent().clear();
        popup.getContent().add(scrollPane);
        popup.setAutoHide(true);

        triggerNode.setOnMouseClicked(e -> {
            if (!popup.isShowing()) {
                Bounds bounds = targetBox.localToScreen(targetBox.getBoundsInLocal());
                double popupWidth = scrollPane.getPrefWidth();
                double popupHeight = scrollPane.getPrefHeight();
                double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
                double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

                double margin = 10; // ← 여기 여유 주는 값!

                // 🎯 targetBox 중앙 상단 정렬
                double centerX = bounds.getMinX() + bounds.getWidth() / 2;
                double x = centerX - popupWidth / 2;
                double y = bounds.getMinY() - popupHeight - margin;

                // 화면 밖 보정
                if (x < 0) x = 0;
                if (x + popupWidth > screenWidth) x = screenWidth - popupWidth;
                if (y < 0) y = bounds.getMaxY() + margin;

                popup.show(triggerNode, x, y);
            } else {
                popup.hide();
            }
        });

    }
}