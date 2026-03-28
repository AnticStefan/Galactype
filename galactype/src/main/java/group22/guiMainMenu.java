package group22;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.layout.GridPane;
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

public class guiMainMenu extends Application {

    private final GameController gameController = new GameController();
    private static final int WINDOW_WIDTH = 760;
    private static final int WINDOW_HEIGHT = 520;

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

        Label subtitle = new Label("Main Menu");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 28));
        subtitle.setTextFill(Color.web("#e2c0ff"));

        Button loginButton = makeMenuButton("Login");
        Button howToPlayButton = makeMenuButton("How to Play");
        Button highScoresButton = makeMenuButton("High Scores");
        Button teacherControlButton = makeMenuButton("Teacher Control");
        Button exitButton = makeSmallExitButton("Exit");

        loginButton.setDisable(false);
        howToPlayButton.setDisable(false);
        teacherControlButton.setDisable(false);
        highScoresButton.setOnAction(e -> stage.setScene(new guiHighScoreMenu(gameController).createScene(stage, () -> stage.setScene(createMainMenuScene(stage)))));
        loginButton.setOnAction(e -> stage.setScene(new guiLoginMenu().createScene(stage, () -> stage.setScene(createMainMenuScene(stage)))));
        howToPlayButton.setOnAction(e -> stage.setScene(new guiHowToPlayMenu().createScene(stage, () -> stage.setScene(createMainMenuScene(stage)))));
        teacherControlButton.setOnAction(e -> stage.setScene(new guiTeacherControlMenu().createScene(stage, () -> stage.setScene(createMainMenuScene(stage)))));
        exitButton.setOnAction(e -> {
            Platform.exit();
            stage.close();
        });

        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(18);
        buttonGrid.setVgap(18);
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.add(loginButton, 0, 0);
        buttonGrid.add(howToPlayButton, 1, 0);
        buttonGrid.add(highScoresButton, 0, 1);
        buttonGrid.add(teacherControlButton, 1, 1);

        VBox mainContent = new VBox(24, title, subtitle, buttonGrid);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(28));

        StackPane root = new StackPane(createDecoratedBackground(), mainContent, exitButton);
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
        return button;
    }

    private Button makeSmallExitButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setTextFill(Color.web("#ffe6ff"));
        button.setStyle("-fx-background-color: transparent; -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;");
        button.setPrefWidth(80);
        button.setPrefHeight(32);
        button.setEffect(new DropShadow(8, Color.web("#000000", 0.25)));
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
        URL resource = guiMainMenu.class.getResource("/word.json");
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
