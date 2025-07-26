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

/**
 * 이모지 팝업 UI를 담당하는 클래스
 *
 * - 감정 표현용 이모지를 Grid 형식으로 렌더링
 * - 선택한 이모지를 외부 Consumer로 전달
 * - JavaFX Popup을 통해 화면 상단에 표시됨
 *
 * 사용 예:
 * new EmojiView(triggerNode, anchorNode, emojiFileName -> { ... });
 *
 * @author 서샘이
 */
public class EmojiView {

    // 이모지 이미지 파일 위치
    private static final String IMG_DIR = "/images/emoji/";
    private static final int COLS = 6;      // Grid 컬럼 수
    private static final int BTN_SZ = 40;   // 버튼 크기

    private final Popup popup = new Popup();           // 실제 팝업 UI
    private final GridPane grid = new GridPane();      // 이모지를 나열할 격자 형태
    private final ScrollPane scroll = new ScrollPane();// Grid를 감싸는 스크롤 뷰

    /**
     * 생성자
     *
     * @param trigger 팝업을 토글할 클릭 대상 노드
     * @param anchor 팝업 위치 기준이 되는 기준 노드
     * @param onSelect 이모지 선택 시 호출되는 콜백 (파일 이름 반환)
     */
    public EmojiView(Node trigger, Node anchor, Consumer<String> onSelect) {
        buildGrid(onSelect); // 버튼들 그리드 구성
        scroll.setContent(grid);
        scroll.setPrefSize(300, 220);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        popup.getContent().add(scroll);
        popup.setAutoHide(true); // 외부 클릭 시 자동 닫힘

        // 클릭 이벤트 연결
        trigger.setOnMouseClicked(e -> togglePopup(trigger, anchor));
    }

    /**
     * GridPane에 이모지 버튼을 구성하는 메서드
     *
     * @param onSelect 선택 콜백
     */
    private void buildGrid(Consumer<String> onSelect) {
        grid.setHgap(5); grid.setVgap(5); grid.setPadding(new Insets(10));

        List<String> files = listPngFiles(IMG_DIR);
        int col = 0, row = 0;

        for (String file : files) {
            String emojiPath = IMG_DIR + file;

            // 이미지 로딩 및 리사이징
            ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(emojiPath), 24, 24, true, true));

            // 버튼 구성
            Button b = new Button();
            b.setGraphic(iv);
            b.setPrefSize(BTN_SZ, BTN_SZ);
            b.setStyle("-fx-background-color:transparent; -fx-cursor:hand;");
            b.setOnAction(e -> {
                onSelect.accept(file); // 선택된 이모지 파일 이름 전달
                popup.hide();          // 팝업 닫기
            });

            grid.add(b, col, row);

            // 다음 위치 계산
            if (++col == COLS) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * 이모지 디렉터리에서 PNG 파일 목록 추출
     * - jar 환경, 개발 환경 모두 대응
     *
     * @param dir 리소스 디렉토리 경로
     * @return PNG 파일 이름 리스트
     */
    private List<String> listPngFiles(String dir) {
        try {
            URL url = getClass().getResource(dir);
            if (url == null) throw new IllegalStateException("Emoji directory not found");

            Path path;
            if (url.getProtocol().equals("jar")) {
                // jar 내부 리소스 접근 시 FileSystem 필요
                String jarPath = url.toURI().toString().split("!")[0];
                FileSystem fs = FileSystems.newFileSystem(URI.create(jarPath), java.util.Map.of());
                path = fs.getPath(dir);
            } else {
                // 일반 파일 시스템
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
            return List.of(); // 예외 시 빈 리스트 반환
        }
    }

    /**
     * 팝업 토글 및 위치 계산
     * - anchor 기준으로 화면 내 적절한 위치 계산
     *
     * @param trigger 클릭된 요소
     * @param anchor 위치 기준 요소
     */
    private void togglePopup(Node trigger, Node anchor) {
        if (popup.isShowing()) {
            popup.hide();
            return;
        }

        // 기준 노드 위치 계산
        Bounds box = anchor.localToScreen(anchor.getBoundsInLocal());
        double w = scroll.getPrefWidth(), h = scroll.getPrefHeight();
        double x = box.getMinX() + box.getWidth() / 2 - w / 2;
        double y = box.getMinY() - h - 10;

        // 화면 경계 보정
        double screenW = Screen.getPrimary().getVisualBounds().getWidth();
        if (x < 0) x = 0;
        if (x + w > screenW) x = screenW - w;
        if (y < 0) y = box.getMaxY() + 10;

        popup.show(trigger, x, y);
    }
}
