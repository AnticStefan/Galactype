package group22;

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

public class guiHowToPlayMenu {

    private static final int WINDOW_WIDTH = 760;
    private static final int WINDOW_HEIGHT = 520;

    public Scene createScene(Stage stage, Runnable returnToMain) {
        Label title = new Label("How to Play");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 56));
        title.setTextFill(Color.web("#ffe6ff"));

        Label description = new Label("Use the keyboard to type falling words and score points.\n" +
                "High scores are saved automatically.\n" +
                "Login, teacher control, and other screens will be added soon.");
        description.setFont(Font.font("Arial", 18));
        description.setTextFill(Color.web("#f0e5ff"));
        description.setWrapText(true);
        description.setMaxWidth(520);
        description.setBackground(new Background(new BackgroundFill(Color.web("#250042", 0.7), new CornerRadii(12), Insets.EMPTY)));
        description.setPadding(new Insets(16));

        Button returnButton = makeTopRightButton("Return");
        returnButton.setOnAction(e -> returnToMain.run());

        VBox content = new VBox(24, title, description);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28));

        StackPane root = new StackPane(guiMainMenu.createDecoratedBackground(), content, returnButton);
        StackPane.setAlignment(returnButton, Pos.TOP_RIGHT);
        StackPane.setMargin(returnButton, new Insets(14));
        return new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private Button makeTopRightButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setTextFill(Color.web("#ffe6ff"));
        button.setStyle("-fx-background-color: transparent; -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;");
        button.setPrefWidth(90);
        button.setPrefHeight(34);
        button.setEffect(new DropShadow(8, Color.web("#000000", 0.25)));
        return button;
    }
}
