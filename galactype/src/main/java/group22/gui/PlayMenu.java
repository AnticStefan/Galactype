package group22.gui;

import group22.GameController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

public class PlayMenu {

    private static final int WINDOW_WIDTH = 760;
    private static final int WINDOW_HEIGHT = 520;

    @SuppressWarnings("unused")
    private final GameController gameController;

    public PlayMenu(GameController gameController) {
        this.gameController = gameController;
    }

    public Scene createScene(Stage stage, Runnable returnToMain) {
        StackPane root = new StackPane();
        root.getChildren().add(MainMenu.createDecoratedBackground());
        root.setPadding(new Insets(16));

        Label logoLabel = createLogoLabel();
        Button exitButton = MainMenu.createTopRightButton("Exit");
        exitButton.setOnAction(e -> {
            Platform.exit();
            stage.close();
        });

        StackPane.setAlignment(logoLabel, Pos.TOP_LEFT);
        StackPane.setMargin(logoLabel, new Insets(18));
        StackPane.setAlignment(exitButton, Pos.TOP_RIGHT);
        StackPane.setMargin(exitButton, new Insets(14));

        root.getChildren().addAll(
                createContent(returnToMain),
                logoLabel,
                exitButton
        );

        return new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private VBox createContent(Runnable returnToMain) {
        Label title = new Label("Play");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 56));
        title.setTextFill(Color.web("#ffe6ff"));
        title.setEffect(new DropShadow(12, Color.web("#2b004d", 0.55)));

        Label description = new Label(
                "This is a placeholder play screen.\n\n"
                        + "The gameplay scene can be connected here next so players can start a session from the main menu."
        );
        description.setFont(Font.font("Arial", 18));
        description.setTextFill(Color.web("#f0e5ff"));
        description.setWrapText(true);
        description.setMaxWidth(520);
        description.setBackground(new Background(new BackgroundFill(Color.web("#250042", 0.78), new CornerRadii(18), Insets.EMPTY)));
        description.setPadding(new Insets(20));
        description.setEffect(new DropShadow(18, Color.web("#130021", 0.45)));

        Button backButton = makeSecondaryButton("Back to Main Menu");
        backButton.setOnAction(e -> returnToMain.run());

        VBox content = new VBox(24, title, description, backButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28));
        return content;
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
        button.setStyle("-fx-background-color: rgba(37, 0, 66, 0.35); -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;");
        button.setPrefWidth(160);
        button.setPrefHeight(38);
        button.setEffect(new DropShadow(8, Color.web("#000000", 0.25)));
        return button;
    }
}
