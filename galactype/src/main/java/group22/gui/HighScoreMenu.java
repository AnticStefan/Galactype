package group22.gui;

import java.util.List;

import group22.GameController;
import group22.HighScoreEntry;
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
        Label title = new Label("High Scores");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 56));
        title.setTextFill(Color.web("#ffe6ff"));

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
        } else {
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
        }

        Button backButton = makeMenuButton("Back to Main Menu");
        backButton.setOnAction(e -> returnToMain.run());

        VBox content = new VBox(24, title, scoreList, backButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28));

        StackPane root = new StackPane(MainMenu.createDecoratedBackground(), content);
        root.setPadding(new Insets(16));
        return new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private Button makeMenuButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ca3cff, #ff5dd4); -fx-border-color: #ffe6ff; -fx-border-width: 3; -fx-cursor: hand; -fx-background-radius: 14; -fx-border-radius: 14;");
        button.setPrefWidth(220);
        button.setPrefHeight(52);
        button.setEffect(new DropShadow(10, Color.web("#6100aa", 0.45)));
        return button;
    }
}
