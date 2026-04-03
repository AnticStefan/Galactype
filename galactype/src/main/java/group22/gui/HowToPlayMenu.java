package group22.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class HowToPlayMenu {

    private static final int WINDOW_WIDTH = 760;
    private static final int WINDOW_HEIGHT = 520;

    public Scene createScene(Stage stage, Runnable returnToMain) {
        StackPane root = new StackPane();
        root.getChildren().add(MainMenu.createDecoratedBackground());
        root.setPadding(new Insets(16));

        Label logoLabel = createLogoLabel();
        Button exitButton = MainMenu.createTopRightButton("Quit");
        exitButton.setOnAction(e -> {
            Platform.exit();
            stage.close();
        });

        StackPane.setAlignment(logoLabel, Pos.TOP_LEFT);
        StackPane.setMargin(logoLabel, new Insets(18));
        StackPane.setAlignment(exitButton, Pos.TOP_RIGHT);
        StackPane.setMargin(exitButton, new Insets(14));

        root.getChildren().addAll(createContent(returnToMain), logoLabel, exitButton);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        MainMenu.addMenuBackSpace(scene, returnToMain);
        return scene;
    }

    private VBox createContent(Runnable returnToMain) {
        Label title = createScreenTitle();

        Label description = new Label(
                        "1) Words fall from the top as meteors.\n"
                        + "2) Type each word exactly before it reaches the bottom.\n"
                        + "3) Correct words give points and improve your score.\n"
                        + "4) If a word hits the ground, you lose one life.\n"
                        + "5) You start with 3 lives as hearts; extra lives may appear via power-ups.\n"
                        + "6) Power-ups appear on meteors (freeze, slow, shield, extra life).\n"
                        + "7) Use ESC to pause/resume; from pause you can Restart or Quit to Menu.\n"
                        + "8) The HUD shows Score, Level, WPM, Timer, and Lives at the top.\n"
                        + "9) Aim for high score and fast typing speed!") ;
        description.setFont(Font.font("Arial", 17));
        description.setTextFill(Color.web("#f0e5ff"));
        description.setWrapText(true);
        description.setMaxWidth(Double.MAX_VALUE);

        ScrollPane scrollPane = new ScrollPane(description);
        scrollPane.setPrefViewportWidth(600);
        scrollPane.setPrefViewportHeight(260);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        description.maxWidthProperty().bind(scrollPane.widthProperty().subtract(20));

        VBox infoBox = new VBox();
        infoBox.setBackground(new Background(new BackgroundFill(Color.web("#250042", 0.78), new CornerRadii(18), Insets.EMPTY)));
        infoBox.setPadding(new Insets(20));
        infoBox.setEffect(new DropShadow(18, Color.web("#130021", 0.45)));
        infoBox.getChildren().add(scrollPane);

        Button backButton = makeSecondaryButton("Back to Main Menu");
        backButton.setOnAction(e -> returnToMain.run());

        VBox content = new VBox(24, title, infoBox, backButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28));
        return content;
    }

    private Label createScreenTitle() {
        Label title = new Label("How to Play");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 52));
        title.setTextFill(Color.web("#ffe6ff"));
        title.setEffect(new DropShadow(12, Color.web("#2b004d", 0.55)));
        return title;
    }

    private Label createLogoLabel() {
        Label logoLabel = new Label("Galactype");
        logoLabel.setFont(Font.font("Segoe Script", FontWeight.BOLD, 34));
        logoLabel.setTextFill(Color.web("#ffe6ff"));
        logoLabel.setEffect(new DropShadow(10, Color.web("#250042", 0.55)));
        return logoLabel;
    }

    private Button makeSecondaryButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setTextFill(Color.web("#ffe6ff"));
        String normalStyle = "-fx-background-color: rgba(37, 0, 66, 0.35); -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: rgba(60, 0, 110, 0.55); -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;";
        button.setStyle(normalStyle);
        button.setPrefWidth(160);
        button.setPrefHeight(38);
        button.setEffect(new DropShadow(8, Color.web("#000000", 0.25)));
        attachHover(button, normalStyle, hoverStyle);
        return button;
    }

    private void attachHover(Button button, String normalStyle, String hoverStyle) {
        MainMenu.applyHoverFocusBehavior(button, normalStyle, hoverStyle);
    }
}
