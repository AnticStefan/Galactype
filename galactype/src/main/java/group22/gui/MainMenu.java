package group22.gui;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import group22.GameController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenu extends Application {

    private final GameController gameController = new GameController();
    private static final int WINDOW_WIDTH = 760;
    private static final int WINDOW_HEIGHT = 520;
    private static final String PRIMARY_BUTTON_STYLE = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ca3cff, #ff5dd4); -fx-border-color: #ffe6ff; -fx-border-width: 3; -fx-cursor: hand; -fx-background-radius: 14; -fx-border-radius: 14;";
    private static final String PRIMARY_BUTTON_HOVER_STYLE = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #d453ff, #ff76e0); -fx-border-color: #ffe6ff; -fx-border-width: 3; -fx-cursor: hand; -fx-background-radius: 14; -fx-border-radius: 14;";
    private static final String SECONDARY_BUTTON_STYLE = "-fx-background-color: rgba(37, 0, 66, 0.35); -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;";
    private static final String SECONDARY_BUTTON_HOVER_STYLE = "-fx-background-color: rgba(60, 0, 110, 0.55); -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;";
    private static final String TOP_RIGHT_BUTTON_STYLE = "-fx-background-color: transparent; -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;";
    private static final String TOP_RIGHT_BUTTON_HOVER_STYLE = "-fx-background-color: rgba(255,255,255,0.08); -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;";

    @Override
    public void start(Stage stage) {
        initializeController();

        stage.setTitle("Galactype");
        stage.setScene(createMainMenuScene(stage));
        stage.setResizable(false);
        stage.show();
    }

    private Scene createMainMenuScene(Stage stage) {
        Label title = new Label("Galactype");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 64));
        title.setTextFill(Color.web("#ffe6ff"));
        title.setEffect(new DropShadow(12, Color.web("#2b004d", 0.55)));

        Button playButton = makeMenuButton("Play");
        Button loginButton = makeMenuButton("Login");
        Button howToPlayButton = makeMenuButton("How to Play");
        Button highScoresButton = makeMenuButton("High Scores");
        Button teacherControlButton = makeMenuButton("Teacher Control");
        Button exitButton = createTopRightButton("Exit");

        playButton.setOnAction(e -> openPlayMenu(stage));
        loginButton.setDisable(false);
        howToPlayButton.setDisable(false);
        teacherControlButton.setDisable(false);
        highScoresButton.setOnAction(e -> stage.setScene(new HighScoreMenu(gameController).createScene(stage, () -> stage.setScene(createMainMenuScene(stage)))));
        loginButton.setOnAction(e -> stage.setScene(new LoginMenu(gameController).createScene(stage, () -> stage.setScene(createMainMenuScene(stage)))));
        howToPlayButton.setOnAction(e -> stage.setScene(new HowToPlayMenu().createScene(stage, () -> stage.setScene(createMainMenuScene(stage)))));
        teacherControlButton.setOnAction(e -> stage.setScene(new TeacherControlMenu().createScene(stage, () -> stage.setScene(createMainMenuScene(stage)))));
        exitButton.setOnAction(e -> {
            Platform.exit();
            stage.close();
        });

        VBox buttonColumn = new VBox(18, playButton, loginButton, howToPlayButton, highScoresButton, teacherControlButton);
        buttonColumn.setAlignment(Pos.CENTER);

        VBox mainContent = new VBox(22, title, buttonColumn);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(18, 28, 28, 28));
        mainContent.setTranslateY(-10);

        StackPane root = new StackPane(createDecoratedBackground(), mainContent, exitButton);
        StackPane.setAlignment(exitButton, Pos.TOP_RIGHT);
        StackPane.setMargin(exitButton, new Insets(14));
        root.setPadding(new Insets(16));
        return new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private void openPlayMenu(Stage stage) {
        if (gameController.getAccountManager().getCurrentAccount() == null) {
            stage.setScene(createGuestWarningScene(stage));
            return;
        }

        stage.setScene(new PlayMenu(gameController).createScene(stage, () -> stage.setScene(createMainMenuScene(stage))));
    }

    private Scene createGuestWarningScene(Stage stage) {
        Label title = new Label("Not Logged In");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 48));
        title.setTextFill(Color.web("#ffe6ff"));
        title.setEffect(new DropShadow(12, Color.web("#2b004d", 0.55)));

        Label message = new Label(
                "You are not logged in.\n\nIf you continue, your game progress will not be saved."
        );
        message.setFont(Font.font("Arial", 18));
        message.setTextFill(Color.web("#f0e5ff"));
        message.setWrapText(true);
        message.setMaxWidth(460);
        message.setBackground(new Background(new BackgroundFill(Color.web("#250042", 0.78), new CornerRadii(18), Insets.EMPTY)));
        message.setPadding(new Insets(20));
        message.setEffect(new DropShadow(18, Color.web("#130021", 0.45)));

        Button okButton = makeSecondaryButton("I Understand");
        setWarningButtonSize(okButton);
        okButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        okButton.setOnAction(e -> stage.setScene(new PlayMenu(gameController).createScene(stage, () -> stage.setScene(createMainMenuScene(stage)))));

        Button backButton = makeMenuButton("Back to Main Menu");
        setWarningButtonSize(backButton);
        backButton.setOnAction(e -> stage.setScene(createMainMenuScene(stage)));

        HBox buttonGroup = new HBox(16, backButton, okButton);
        buttonGroup.setAlignment(Pos.CENTER);

        VBox content = new VBox(22, title, message, buttonGroup);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28));

        Button exitButton = createTopRightButton("Exit");
        exitButton.setOnAction(e -> {
            Platform.exit();
            stage.close();
        });

        StackPane root = new StackPane(createDecoratedBackground(), content, exitButton);
        StackPane.setAlignment(exitButton, Pos.TOP_RIGHT);
        StackPane.setMargin(exitButton, new Insets(14));
        root.setPadding(new Insets(16));
        return new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
    }


    static Pane createDecoratedBackground() {
        Pane background = new Pane();
        background.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        background.setBackground(new Background(new BackgroundFill(Color.web("#130047"), CornerRadii.EMPTY, Insets.EMPTY)));

        for (int i = 0; i < 20; i++) {
            Circle star = new Circle(1.5, Color.web("#f8d2ff", 0.85));
            star.setCenterX(20 + Math.random() * (WINDOW_WIDTH - 40));
            star.setCenterY(20 + Math.random() * (WINDOW_HEIGHT - 40));
            background.getChildren().add(star);
        }

        List<Meteor> meteors = new ArrayList<>();
        meteors.add(createMeteor(60, -40, 3.6, 1.8));
        meteors.add(createMeteor(220, -100, 4.3, 1.2));
        meteors.add(createMeteor(400, -200, 3.1, 2.0));
        meteors.add(createMeteor(560, -60, 5.0, 1.5));
        meteors.add(createMeteor(720, -160, 4.7, 1.1));
        meteors.add(createMeteor(320, -300, 3.9, 1.6));

        for (Meteor meteor : meteors) {
            background.getChildren().add(meteor.node);
        }

        Timeline animation = new Timeline(new KeyFrame(Duration.seconds(0.016), event -> updateMeteors(meteors)));
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();

        return background;
    }

    private static Meteor createMeteor(double startX, double startY, double speed, double drift) {
        Group meteor = new Group();
        Rectangle tail = new Rectangle(12, 72, Color.web("#e26bff", 0.55));
        tail.setArcWidth(12);
        tail.setArcHeight(12);
        tail.setTranslateX(-6);
        tail.setTranslateY(-60);

        Circle head = new Circle(16, Color.web("#ffddff", 0.98));
        head.setStroke(Color.web("#c32fff"));
        head.setStrokeWidth(2);
        head.setTranslateX(0);
        head.setTranslateY(0);

        meteor.getChildren().addAll(tail, head);
        meteor.setLayoutX(startX);
        meteor.setLayoutY(startY);
        meteor.setOpacity(0.95);

        return new Meteor(meteor, speed, drift);
    }

    private static void updateMeteors(List<Meteor> meteors) {
        for (Meteor meteor : meteors) {
            meteor.node.setLayoutY(meteor.node.getLayoutY() + meteor.speed);
            meteor.node.setLayoutX(meteor.node.getLayoutX() + meteor.drift * 0.2);

            if (meteor.node.getLayoutY() > WINDOW_HEIGHT + 60 || meteor.node.getLayoutX() > WINDOW_WIDTH + 60) {
                meteor.node.setLayoutY(-60 - Math.random() * 180);
                meteor.node.setLayoutX(20 + Math.random() * (WINDOW_WIDTH - 40));
            }
        }
    }

    private static class Meteor {
        private final Group node;
        private final double speed;
        private final double drift;

        private Meteor(Group node, double speed, double drift) {
            this.node = node;
            this.speed = speed;
            this.drift = drift;
        }
    }

    private Button makeMenuButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ca3cff, #ff5dd4); -fx-border-color: #ffe6ff; -fx-border-width: 3; -fx-cursor: hand; -fx-background-radius: 14; -fx-border-radius: 14;");
        button.setPrefWidth(220);
        button.setPrefHeight(52);
        button.setEffect(new DropShadow(10, Color.web("#6100aa", 0.45)));
        attachPrimaryHover(button);
        return button;
    }

    private Button makeSecondaryButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setTextFill(Color.web("#ffe6ff"));
        button.setStyle(SECONDARY_BUTTON_STYLE);
        button.setPrefWidth(160);
        button.setPrefHeight(38);
        button.setEffect(new DropShadow(8, Color.web("#000000", 0.25)));
        attachHover(button, SECONDARY_BUTTON_STYLE, SECONDARY_BUTTON_HOVER_STYLE);
        return button;
    }

    private void setWarningButtonSize(Button button) {
        button.setPrefWidth(220);
        button.setMinWidth(220);
        button.setMaxWidth(220);
        button.setPrefHeight(52);
        button.setMinHeight(52);
        button.setMaxHeight(52);
    }

    private void attachPrimaryHover(Button button) {
        button.setOnMouseEntered(e -> button.setStyle(PRIMARY_BUTTON_HOVER_STYLE));
        button.setOnMouseExited(e -> button.setStyle(PRIMARY_BUTTON_STYLE));
    }

    private static void attachHover(Button button, String normalStyle, String hoverStyle) {
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
    }

    static Button createTopRightButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setTextFill(Color.web("#ffe6ff"));
        button.setStyle(TOP_RIGHT_BUTTON_STYLE);
        button.setPrefWidth(80);
        button.setPrefHeight(32);
        button.setEffect(new DropShadow(8, Color.web("#000000", 0.25)));
        attachHover(button, TOP_RIGHT_BUTTON_STYLE, TOP_RIGHT_BUTTON_HOVER_STYLE);
        return button;
    }

    @Override
    public void stop() {
        gameController.saveAndExit();
    }

    private void initializeController() {
        Path dataPath = Paths.get("data");
        Path wordBankPath = resolveWordBankPath();
        gameController.initialize(dataPath.toString(), wordBankPath.toString());
    }

    private Path resolveWordBankPath() {
        URL resource = MainMenu.class.getResource("/word.json");
        if (resource == null) {
            throw new IllegalStateException("Could not find word.json in resources.");
        }

        try {
            return Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid word.json resource path.", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
