package group22.gui;

import java.util.List;

import group22.Account;
import group22.GameController;
import group22.LevelConfig;
import group22.PlayerAccount;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class PlayMenu {

    private static final int WINDOW_WIDTH = 760;
    private static final int WINDOW_HEIGHT = 520;

    private final GameController gameController;

    public PlayMenu(GameController gameController) {
        this.gameController = gameController;
    }

    public Scene createScene(Stage stage, Runnable returnToMain) {
        StackPane root = new StackPane();
        root.getChildren().add(MainMenu.createDecoratedBackground());
        root.setPadding(new Insets(16));

        Label logoLabel = createLogoLabel();
        Button exitButton = MainMenu.createTopRightButton("Back");
        exitButton.setOnAction(e -> returnToMain.run());

        StackPane.setAlignment(logoLabel, Pos.TOP_LEFT);
        StackPane.setMargin(logoLabel, new Insets(18));
        StackPane.setAlignment(exitButton, Pos.TOP_RIGHT);
        StackPane.setMargin(exitButton, new Insets(14));

        root.getChildren().addAll(createContent(stage, returnToMain), logoLabel, exitButton);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        MainMenu.addMenuBackSpace(scene, returnToMain);
        return scene;
    }

    private VBox createContent(Stage stage, Runnable returnToMain) {
        Label title = new Label("Select Level");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 48));
        title.setTextFill(Color.web("#ffe6ff"));
        title.setEffect(new DropShadow(12, Color.web("#2b004d", 0.55)));

        int unlockedUpTo = getUnlockedLevel();
        List<LevelConfig> levels = gameController.getLevelManager().getAllLevels();

        // Split into two rows of 4 so all 8 planets fit without overflowing
        HBox topRow    = new HBox(14);
        HBox bottomRow = new HBox(14);
        topRow.setAlignment(Pos.CENTER);
        bottomRow.setAlignment(Pos.CENTER);

        for (int i = 0; i < levels.size(); i++) {
            LevelConfig config = levels.get(i);
            int lvl = config.getLevelNumber();
            boolean locked = lvl > unlockedUpTo;
            Button btn = makeLevelButton(config, locked);
            if (!locked) {
                btn.setOnAction(e -> launchGame(stage, lvl, returnToMain));
            }
            (i < 4 ? topRow : bottomRow).getChildren().add(btn);
        }

        VBox grid = new VBox(12, topRow, bottomRow);
        grid.setAlignment(Pos.CENTER);

        VBox content = new VBox(22, title, grid);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28));
        return content;
    }

    private Button makeLevelButton(LevelConfig config, boolean locked) {
        int lvl = config.getLevelNumber();
        String planetName = config.getPlanetName() != null ? config.getPlanetName() : "Level " + lvl;
        String diff = config.getWordDifficulty().name().charAt(0)
                + config.getWordDifficulty().name().substring(1).toLowerCase();

        String label = locked ? planetName + "\n\uD83D\uDD12" : planetName + "\n" + diff;

        Button btn = new Button(label);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        btn.setTextFill(locked ? Color.web("#999999") : Color.WHITE);
        btn.setWrapText(true);
        btn.setPrefWidth(100);
        btn.setPrefHeight(80);

        if (locked) {
            btn.setStyle("-fx-background-color: rgba(20,0,40,0.5); " +
                    "-fx-border-color: #555555; -fx-border-width: 2; " +
                    "-fx-background-radius: 14; -fx-border-radius: 14;");
            btn.setDisable(true);
        } else {
            String normalStyle = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ca3cff, #ff5dd4); " +
                    "-fx-border-color: #ffe6ff; -fx-border-width: 3; " +
                    "-fx-cursor: hand; -fx-background-radius: 14; -fx-border-radius: 14;";
            String hoverStyle = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #d453ff, #ff76e0); " +
                    "-fx-border-color: #ffe6ff; -fx-border-width: 3; " +
                    "-fx-cursor: hand; -fx-background-radius: 14; -fx-border-radius: 14;";
            btn.setStyle(normalStyle);
            btn.setEffect(new DropShadow(10, Color.web("#6100aa", 0.45)));
            attachHover(btn, normalStyle, hoverStyle);
        }

        return btn;
    }

    private void launchGame(Stage stage, int levelNumber, Runnable returnToMain) {
        GameScreen gameScreen = new GameScreen(gameController, levelNumber);
        Scene gameScene = gameScreen.createScene(stage, () -> stage.setScene(createScene(stage, returnToMain)));
        stage.setScene(gameScene);
        gameScene.getRoot().requestFocus();
    }

    private int getUnlockedLevel() {
        Account account = gameController.getAccountManager().getCurrentAccount();
        if (account instanceof PlayerAccount) {
            int progress = ((PlayerAccount) account).getLevelProgress();
            return Math.max(progress, 1);
        }
        // Guest: allow all levels
        return gameController.getLevelManager().getTotalLevels();
    }

    private Label createLogoLabel() {
        Label logoLabel = new Label("Galactype");
        logoLabel.setFont(Font.font("Segoe Script", FontWeight.BOLD, 34));
        logoLabel.setTextFill(Color.web("#ffe6ff"));
        logoLabel.setEffect(new DropShadow(10, Color.web("#250042", 0.55)));
        return logoLabel;
    }

    private void attachHover(Button button, String normalStyle, String hoverStyle) {
        MainMenu.applyHoverFocusBehavior(button, normalStyle, hoverStyle);
    }
}
