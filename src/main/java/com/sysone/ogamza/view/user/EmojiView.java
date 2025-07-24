package com.sysone.ogamza.view.user;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;
import javafx.stage.Screen;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EmojiView {

    private static final String IMG_DIR="/images/emoji/";
    private static final int COLS =6;
    private static final int BTN_SZ = 40;

    private final Popup popup = new Popup();
    private final GridPane grid = new GridPane();
    private final ScrollPane scroll = new ScrollPane();

    /* -------------------------------- Constructor -------------------------------- */
    public EmojiView(Node trigger, Node anchor, Consumer<String> onSelect) {
        buildGrid(onSelect);
        scroll.setContent(grid);
        scroll.setPrefSize(300, 220);
        popup.getContent().add(scroll);
        popup.setAutoHide(true);

        trigger.setOnMouseClicked(e -> togglePopup(trigger, anchor));
    }

    /* ----------------------------- Build grid buttons ---------------------------- */
    private void buildGrid(Consumer<String> onSelect) {
        grid.setHgap(5);  grid.setVgap(5);  grid.setPadding(new Insets(10));

        List<String> files = listPngFiles(IMG_DIR);
        int col = 0, row = 0;

        for (String file : files) {
            String emojiPath = IMG_DIR + file;
            ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(emojiPath), 24, 24, true, true));

            Button b = new Button();
            b.setGraphic(iv);
            b.setPrefSize(BTN_SZ, BTN_SZ);
            b.setStyle("-fx-background-color:transparent; -fx-cursor:hand;");
            b.setOnAction(e -> { onSelect.accept(file); popup.hide(); });

            grid.add(b, col, row);
            if (++col == COLS) { col = 0; row++; }
        }
    }

    /* ----------------------- List PNG filenames inside JAR ----------------------- */
    private List<String> listPngFiles(String dir) {
        try {
            URL url = getClass().getResource(dir);
            if (url == null) throw new IllegalStateException("Emoji directory not found");

            Path path;
            // 실행 형태에 따라 URL 프로토콜이 jar: or file:
            if (url.getProtocol().equals("jar")) {
                // JAR 내부: FileSystem으로 열기
                String jarPath = url.toURI().toString().split("!")[0];
                FileSystem fs = FileSystems.newFileSystem(URI.create(jarPath), java.util.Map.of());
                path = fs.getPath(dir);
            } else {
                // IDE 실행: 실제 폴더
                path = Paths.get(url.toURI());
            }

            try (DirectoryStream<Path> ds = Files.newDirectoryStream(path, "*.png")) {
                return StreamSupport.stream(ds.spliterator(), false)
                        .map(p -> p.getFileName().toString())
                        .sorted()
                        .collect(Collectors.toList());
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return List.of(); // 빈 리스트라도 반환
        }
    }

    /* --------------------------- Popup show/hide logic --------------------------- */
    private void togglePopup(Node trigger, Node anchor) {
        if (popup.isShowing()) { popup.hide(); return; }

        Bounds box = anchor.localToScreen(anchor.getBoundsInLocal());
        double w = scroll.getPrefWidth(), h = scroll.getPrefHeight();
        double x = box.getMinX() + box.getWidth()/2 - w/2;
        double y = box.getMinY() - h - 10;

        double screenW = Screen.getPrimary().getVisualBounds().getWidth();
        if (x < 0) x = 0;
        if (x + w > screenW) x = screenW - w;
        if (y < 0) y = box.getMaxY() + 10;

        popup.show(trigger, x, y);
    }
}