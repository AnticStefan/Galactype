package group22.gui;

import java.util.List;

import group22.GameController;
import group22.HighScoreEntry;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class HighScoreMenu {

    private final GameController gameController;
    private static final int WINDOW_WIDTH = 760;
    private static final int WINDOW_HEIGHT = 520;

    public HighScoreMenu(GameController gameController) {
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
        Label title = createScreenTitle();
        VBox scoreList = createScoreList();

        Button backButton = makeSecondaryButton("Back to Main Menu");
        backButton.setOnAction(e -> returnToMain.run());

        VBox content = new VBox(24, title, scoreList, backButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28));
        return content;
    }

    private VBox createScoreList() {
        VBox scoreList = new VBox(10);
        scoreList.setAlignment(Pos.CENTER);
        scoreList.setMaxWidth(580);
        scoreList.setPadding(new Insets(10));
        scoreList.setBackground(new Background(new BackgroundFill(Color.web("#2f005d", 0.88), new CornerRadii(16), Insets.EMPTY)));
        scoreList.setBorder(new Border(new BorderStroke(Color.web("#ffb6ff"), BorderStrokeStyle.SOLID, new CornerRadii(16), new BorderWidths(3))));

        List<HighScoreEntry> entries = gameController.getHighScoreTable().getTopEntries();
        if (entries.isEmpty()) {
            Label emptyLabel = new Label("No high scores yet.");
            emptyLabel.setFont(Font.font("Arial", 20));
            emptyLabel.setTextFill(Color.web("#f0e5ff"));
            scoreList.getChildren().add(emptyLabel);
            return scoreList;
        }

        int visibleEntries = Math.min(entries.size(), 6);
        for (int index = 0; index < visibleEntries; index++) {
            HighScoreEntry entry = entries.get(index);
            HBox row = new HBox(14);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12));
            row.setBackground(new Background(new BackgroundFill(Color.web("#250042"), new CornerRadii(10), Insets.EMPTY)));
            row.setBorder(new Border(new BorderStroke(Color.web("#af83ff"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));

            Circle icon = new Circle(14, Color.web("#ffffff", 0.85));
            Label nameLabel = new Label(entry.getUsername());
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            nameLabel.setTextFill(Color.web("#ffd4ff"));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label scoreLabel = new Label(String.valueOf(entry.getScore()));
            scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            scoreLabel.setTextFill(Color.web("#ffd4ff"));

            row.getChildren().addAll(icon, nameLabel, spacer, scoreLabel);
            scoreList.getChildren().add(row);
        }

        return scoreList;
    }

    private Label createScreenTitle() {
        Label title = new Label("High Scores");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 56));
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
        button.setStyle("-fx-background-color: rgba(37, 0, 66, 0.35); -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;");
        button.setPrefWidth(160);
        button.setPrefHeight(38);
        button.setEffect(new DropShadow(8, Color.web("#000000", 0.25)));
        return button;
    }
}
