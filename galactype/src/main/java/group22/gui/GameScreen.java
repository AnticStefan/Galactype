package group22.gui;

import java.util.List;

import group22.GameController;
import group22.LevelConfig;
import group22.Meteor;
import group22.enums.GameState;
import group22.enums.PowerUpType;
import group22.interfaces.GameObserver;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GameScreen implements GameObserver {

    private static final int WIDTH = 760;
    private static final int HEIGHT = 580;
    private static final int HUD_HEIGHT = 55;
    // Char width estimate for Courier New Bold 16
    private static final double CHAR_WIDTH = 9.6;

    private final GameController gameController;
    private final int levelNumber;

    private AnimationTimer gameLoop;
    private long lastNanoTime = -1;

    // Captured via observer before GameController nulls the session
    private volatile GameState latestState = GameState.RUNNING;
    private int finalScore = 0;
    private float finalWpm = 0f;

    public GameScreen(GameController gameController, int levelNumber) {
        this.gameController = gameController;
        this.levelNumber = levelNumber;
    }

    public Scene createScene(Stage stage, Runnable returnToMenu) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        LevelConfig levelConfig = gameController.getLevelManager().getLevelByNumber(levelNumber);
        String backgroundAsset = levelConfig != null ? levelConfig.getBackgroundAsset() : null;
        Pane bg = createSpaceBg(backgroundAsset);

        StackPane overlay = new StackPane();
        overlay.setVisible(false);

        StackPane root = new StackPane(bg, canvas, overlay);

        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // Route typed characters to the game
        scene.setOnKeyTyped(e -> {
            String ch = e.getCharacter();
            if (!ch.isEmpty() && !e.isControlDown()) {
                gameController.onKeyInput(ch.charAt(0));
            }
        });

        // ESC to pause / resume
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                if (gameController.getCurrentSession() == null) return;
                GameState st = gameController.getCurrentSession().getState();
                if (st == GameState.PAUSED) {
                    gameController.resumeGame();
                    overlay.getChildren().clear();
                    overlay.setVisible(false);
                    lastNanoTime = System.nanoTime(); // reset so delta isn't huge
                    gameLoop.start();
                } else if (st == GameState.RUNNING) {
                    gameController.pauseGame();
                    gameLoop.stop();
                    showPauseOverlay(overlay, returnToMenu);
                }
            }
        });

        // Start session and register as observer
        gameController.startGame(levelNumber);
        if (gameController.getCurrentSession() != null) {
            gameController.getCurrentSession().addObserver(this);
        }

        // Enter fires whichever overlay button is focused (does not interfere with typing,
        // because during gameplay no button holds focus — the scene root does)
        MainMenu.applyKeyboardNav(scene, null);

        lastNanoTime = System.nanoTime();
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                float delta = (now - lastNanoTime) / 1_000_000_000f;
                lastNanoTime = now;
                if (delta > 0.1f) delta = 0.1f; // cap big jumps (e.g. after pause)

                gameController.update(delta);
                render(gc);

                GameState state = latestState;
                if (state == GameState.GAME_OVER) {
                    stop();
                    showEndOverlay(overlay, false, returnToMenu);
                } else if (state == GameState.LEVEL_COMPLETE) {
                    stop();
                    showEndOverlay(overlay, true, returnToMenu);
                }
            }
        };
        gameLoop.start();

        return scene;
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        var session = gameController.getCurrentSession();
        drawHud(gc, session);

        if (session == null) return;

        List<Meteor> meteors = session.getActiveMeteors();
        for (Meteor m : meteors) {
            if (!m.isDestroyed()) {
                renderMeteor(gc, m);
            }
        }
    }

    private void drawHud(GraphicsContext gc, group22.GameSession session) {
        // HUD background bar
        gc.setFill(Color.web("#1a0035", 0.9));
        gc.fillRect(0, 0, WIDTH, HUD_HEIGHT);
        gc.setStroke(Color.web("#c32fff", 0.5));
        gc.setLineWidth(1);
        gc.strokeLine(0, HUD_HEIGHT, WIDTH, HUD_HEIGHT);

        if (session == null) return;

        int score = session.getScoreManager().getCurrentScore();
        int health = session.getHealth();
        float wpm = session.getCurrentWPM();
        long elapsed = session.getElapsedTimeMs();
        long duration = gameController.getLevelManager().getLevelByNumber(levelNumber).getDurationMs();
        long remaining = Math.max(0, duration - elapsed) / 1000;

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 17));

        // Score (left)
        gc.setFill(Color.web("#ffe6ff"));
        gc.fillText("Score: " + score, 18, 24);

        // Level label (left, below score)
        gc.setFont(Font.font("Arial", 12));
        gc.setFill(Color.web("#c8a0ff"));
        gc.fillText("Level " + levelNumber, 18, 43);

        // WPM (centre-left)
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        gc.setFill(Color.web("#ffe6ff"));
        gc.fillText(String.format("WPM: %.1f", wpm), WIDTH / 2.0 - 100, 30);

        // Time (centre-right)
        gc.fillText("Time: " + remaining + "s", WIDTH / 2.0 + 10, 30);

         // Lives as hearts (right)
        gc.setFill(Color.web("#ff5dd4"));
        gc.setFont(Font.font("Arial", 58));
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < health; i++) hearts.append("♥ ");
        String heartsText = hearts.toString().trim();
        gc.fillText(heartsText, WIDTH - 170, 43);

         // counter for the lives (number before hearts)
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 39));
        gc.setFill(Color.web("#c8a0ff"));
        gc.fillText(String.valueOf(health), WIDTH - 210, 40);
    }

    private void renderMeteor(GraphicsContext gc, Meteor meteor) {
        float x = meteor.getXPosition();
        float y = meteor.getYPosition() + HUD_HEIGHT; // shift down by HUD

        // Don't render above HUD
        if (y < HUD_HEIGHT) return;

        String word = meteor.getWord().getText();
        int progress = meteor.getTypedProgress();
        boolean isPowerUp = meteor.getWord().isPowerUP();

        // Meteor body
        Color bodyColor, strokeColor;

        //checks for powerup type and changes meteor colour accordingly, otherwise defaults to purple for normal meteors
        if (isPowerUp) {
            PowerUpType type = meteor.getWord().getPowerUpType();
            switch (type) {
                case SLOW_METEORS:
                    bodyColor = Color.web("#87ceeb", 0.9);
                    strokeColor = Color.web("#4682b4");
                    break;
                case FREEZE_METEORS:
                    bodyColor = Color.web("#00ced1", 0.9);
                    strokeColor = Color.web("#008b8b");
                    break;
                case SHIELD:
                    bodyColor = Color.web("#32cd32", 0.9);
                    strokeColor = Color.web("#228b22");
                    break;
                case EXTRA_LIFE:
                    bodyColor = Color.web("#ff6347", 0.9);
                    strokeColor = Color.web("#dc143c");
                    break;
                default:
                    bodyColor = Color.web("#ffcc00", 0.9);
                    strokeColor = Color.web("#ffaa00");
            }
        } else {
            bodyColor = Color.web("#e26bff", 0.9);
            strokeColor = Color.web("#c32fff");
        }

        gc.setFill(bodyColor);
        gc.fillOval(x - 22, y - 22, 44, 44);
        gc.setStroke(strokeColor);
        gc.setLineWidth(2.5);
        gc.strokeOval(x - 22, y - 22, 44, 44);

        // Word label below the meteor
        String typed = word.substring(0, progress);
        String remaining = word.substring(progress);

        double wordWidth = word.length() * CHAR_WIDTH;
        double textX = x - wordWidth / 2.0;
        double textY = y + 38;

        gc.setFont(Font.font("Courier New", FontWeight.BOLD, 17));

        // Typed portion – active (pink), not grey
        gc.setFill(Color.web("#ff85d2"));
        gc.fillText(typed, textX, textY);

        // Remaining portion – bright
        gc.setFill(isPowerUp ? Color.web("#ffee88") : Color.WHITE);
        gc.fillText(remaining, textX + typed.length() * CHAR_WIDTH, textY);
    }

    // Overlays

    private void showPauseOverlay(StackPane overlay, Runnable returnToMenu) {
        Label title = new Label("PAUSED");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 48));
        title.setTextFill(Color.web("#ffe6ff"));
        title.setEffect(new DropShadow(12, Color.web("#2b004d")));

        Label hint = new Label("Press ESC to resume");
        hint.setFont(Font.font("Arial", 15));
        hint.setTextFill(Color.web("#c8a0ff"));

        Button restartBtn = makeOverlayButton("Restart Level");
        restartBtn.setOnAction(e -> {
            gameLoop.stop();
            gameController.endGame();
            latestState = GameState.RUNNING;
            finalScore = 0;
            finalWpm = 0f;
            gameController.startGame(levelNumber);
            if (gameController.getCurrentSession() != null) {
                gameController.getCurrentSession().addObserver(GameScreen.this);
            }
            overlay.getChildren().clear();
            overlay.setVisible(false);
            lastNanoTime = System.nanoTime();
            gameLoop.start();
        });

        Button quitBtn = makeOverlayButton("Quit to Menu");
        quitBtn.setOnAction(e -> {
            gameLoop.stop();
            gameController.endGame();
            returnToMenu.run();
        });

        VBox box = overlayBox(title, hint, restartBtn, quitBtn);
        overlay.getChildren().setAll(box);
        overlay.setVisible(true);
        javafx.application.Platform.runLater(restartBtn::requestFocus);
    }

    private void showEndOverlay(StackPane overlay, boolean won, Runnable returnToMenu) {
        Label title = new Label(won ? "Level Complete!" : "Game Over");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 30));
        title.setTextFill(won ? Color.web("#88ff99") : Color.web("#ff6677"));
        title.setEffect(new DropShadow(12, Color.web("#2b004d")));

        Label scoreLbl = new Label("Score: " + finalScore);
        scoreLbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        scoreLbl.setTextFill(Color.web("#ffe6ff"));

        Label wpmLbl = new Label(String.format("WPM: %.1f", finalWpm));
        wpmLbl.setFont(Font.font("Arial", 15));
        wpmLbl.setTextFill(Color.web("#c8a0ff"));

        Label msg = new Label(won ? "The meteor shower is over!" : "The meteors got through...");
        msg.setFont(Font.font("Arial", 14));
        msg.setTextFill(Color.web("#d0b0ff"));

        Button playAgainBtn = makeOverlayButton("Play Again");
        playAgainBtn.setOnAction(e -> {
            latestState = GameState.RUNNING;
            finalScore = 0;
            finalWpm = 0f;
            gameController.startGame(levelNumber);
            if (gameController.getCurrentSession() != null) {
                gameController.getCurrentSession().addObserver(GameScreen.this);
            }
            overlay.getChildren().clear();
            overlay.setVisible(false);
            lastNanoTime = System.nanoTime();
            gameLoop.start();
        });

        Button menuBtn = makeOverlayButton("Return to Menu");
        menuBtn.setOnAction(e -> returnToMenu.run());

        VBox box = overlayBox(title, scoreLbl, wpmLbl, msg, playAgainBtn, menuBtn);
        overlay.getChildren().setAll(box);
        overlay.setVisible(true);
        javafx.application.Platform.runLater(playAgainBtn::requestFocus);
    }

    private VBox overlayBox(javafx.scene.Node... nodes) {
        VBox box = new VBox(18, nodes);
        box.setAlignment(Pos.CENTER);
        box.setBackground(new Background(new BackgroundFill(
                Color.web("#0d001a", 0.92), new CornerRadii(20), Insets.EMPTY)));
        box.setPadding(new Insets(40, 60, 40, 60));
        box.setEffect(new DropShadow(28, Color.web("#2b004d")));
        box.setMaxWidth(400);
        return box;
    }

    private Button makeOverlayButton(String text) {
        Button b = new Button(text);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        b.setTextFill(Color.web("#ffe6ff"));
        String normalStyle = "-fx-background-color: rgba(37,0,66,0.5); -fx-border-color: #ffe6ff; " +
                "-fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: rgba(37,0,66,0.7); -fx-border-color: #ffe6ff; " +
                "-fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;";
        b.setStyle(normalStyle);
        b.setPrefWidth(180);
        b.setPrefHeight(40);
        attachHover(b, normalStyle, hoverStyle);
        return b;
    }

    private void attachHover(Button button, String normalStyle, String hoverStyle) {
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
    }

    // Background

    private Pane createSpaceBg(String backgroundAsset) {
        Pane bg = new Pane();
        bg.setPrefSize(WIDTH, HEIGHT);

        BackgroundFill darkFill = new BackgroundFill(Color.web("#0d0025"), CornerRadii.EMPTY, Insets.EMPTY);

        if (backgroundAsset != null) {
            var stream = GameScreen.class.getResourceAsStream(backgroundAsset);
            if (stream != null) {
                // Cover scaling: fills the pane while preserving aspect ratio (no stretching)
                BackgroundImage bgImg = new BackgroundImage(
                        new Image(stream),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(1.0, 1.0, true, true, false, true)
                );
                bg.setBackground(new Background(
                        java.util.List.of(darkFill),
                        java.util.List.of(bgImg)
                ));
                // Semi-transparent overlay so meteor text stays readable
                bg.getChildren().add(new Rectangle(WIDTH, HEIGHT, Color.web("#000000", 0.48)));
            } else {
                bg.setBackground(new Background(darkFill));
            }
        } else {
            bg.setBackground(new Background(darkFill));
        }

        for (int i = 0; i < 90; i++) {
            double size = Math.random() < 0.25 ? 2.0 : 1.2;
            Circle star = new Circle(size, Color.web("#f8d2ff", 0.5 + Math.random() * 0.5));
            star.setCenterX(Math.random() * WIDTH);
            star.setCenterY(Math.random() * HEIGHT);
            bg.getChildren().add(star);
        }

        return bg;
    }

    // GameObserver

    @Override
    public void onGameStateChanged(GameState state) {
        latestState = state;
        // Capture final stats before GameController nulls the session
        if ((state == GameState.GAME_OVER || state == GameState.LEVEL_COMPLETE)
                && gameController.getCurrentSession() != null) {
            finalScore = gameController.getCurrentSession().getScoreManager().getCurrentScore();
            finalWpm = gameController.getCurrentSession().getCurrentWPM();
        }
    }

    @Override
    public void onScoreChanged(int score) {}

    @Override
    public void onHealthChanged(int health) {}
}
