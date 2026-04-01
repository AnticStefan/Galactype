package group22.gui;

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

public class TeacherControlMenu {

    private static final int WINDOW_WIDTH = 760;
    private static final int WINDOW_HEIGHT = 520;

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

        Label messageLabel = new Label("Select a parent control option.");
        messageLabel.setFont(Font.font("Arial", 14));
        messageLabel.setTextFill(Color.web("#f0e5ff"));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(360);
        messageLabel.setAlignment(Pos.CENTER);

        VBox optionsBox = new VBox(
                10,
                createOptionButton("View Player Statistics", "Player statistics view will be available here.", messageLabel),
                createOptionButton("Reset Highscore Table", "Highscore reset control will be available here.", messageLabel),
                createOptionButton("Account Management", "Account management tools will be available here.", messageLabel),
                createOptionButton("Session Restrictions", "Session restriction options can be added here.", messageLabel),
                createOptionButton("Difficulty Controls", "Difficulty control options can be added here.", messageLabel),
                createOptionButton("Content Filters", "Content filter options can be added here.", messageLabel)
        );
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setPadding(new Insets(14));
        optionsBox.setBackground(new Background(new BackgroundFill(Color.web("#250042", 0.72), new CornerRadii(18), Insets.EMPTY)));
        optionsBox.setBorder(new Border(new BorderStroke(Color.web("#ffb6ff"), BorderStrokeStyle.SOLID, new CornerRadii(18), new BorderWidths(2))));
        optionsBox.setMaxWidth(420);
        optionsBox.setEffect(new DropShadow(18, Color.web("#130021", 0.45)));

        Button backButton = makeSecondaryButton("Back to Main Menu");
        backButton.setOnAction(e -> returnToMain.run());

        VBox content = new VBox(7, title, optionsBox, messageLabel, backButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(6, 28, 18, 28));
        return content;
    }

    private Button createOptionButton(String text, String message, Label messageLabel) {
        Label textLabel = new Label(text);
        textLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        textLabel.setTextFill(Color.web("#ffe6ff"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Circle outerCircle = new Circle(14, Color.TRANSPARENT);
        outerCircle.setStroke(Color.web("#ffe6ff"));
        outerCircle.setStrokeWidth(2);

        Circle innerCircle = new Circle(6, Color.web("#ffe6ff"));
        StackPane indicator = new StackPane(outerCircle, innerCircle);
        indicator.setMinSize(30, 30);
        indicator.setMaxSize(30, 30);

        HBox content = new HBox(12, textLabel, spacer, indicator);
        content.setAlignment(Pos.CENTER_LEFT);

        Button button = new Button();
        button.setGraphic(content);
        button.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
        button.setPrefWidth(350);
        button.setPrefHeight(38);
        button.setStyle("-fx-background-color: rgba(255, 245, 255, 0.08);"
                + "-fx-border-color: #ffe6ff;"
                + "-fx-border-width: 2;"
                + "-fx-background-radius: 10;"
                + "-fx-border-radius: 10;"
                + "-fx-cursor: hand;");
        button.setEffect(new DropShadow(8, Color.web("#000000", 0.22)));
        button.setOnAction(e -> messageLabel.setText(message));
        return button;
    }

    private Label createScreenTitle() {
        Label title = new Label("Parent Controls");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 36));
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
