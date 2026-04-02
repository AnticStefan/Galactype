package group22.gui;

import java.util.List;

import group22.GameController;
import group22.Meteor;
import group22.enums.GameState;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GameScreen implements GameObserver {

    private static final int WIDTH = 760;
    private static final int HEIGHT = 580;
    private static final int HUD_HEIGHT = 55;
    // Char width estimate for Courier New Bold 14
    private static final double CHAR_WIDTH = 8.4;

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

        Pane bg = createSpaceBg();

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

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        // Score (left)
        gc.setFill(Color.web("#ffe6ff"));
        gc.fillText("Score: " + score, 18, 24);

        // Level label (left, below score)
        gc.setFont(Font.font("Arial", 12));
        gc.setFill(Color.web("#c8a0ff"));
        gc.fillText("Level " + levelNumber, 18, 42);

        // WPM (centre-left)
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        gc.setFill(Color.web("#ffe6ff"));
        gc.fillText(String.format("WPM: %.1f", wpm), WIDTH / 2.0 - 100, 30);

        // Time (centre-right)
        gc.fillText("Time: " + remaining + "s", WIDTH / 2.0 + 10, 30);

        // Lives as hearts (right)
        gc.setFill(Color.web("#ff5dd4"));
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < health; i++) hearts.append("♥ ");
        gc.fillText(hearts.toString().trim(), WIDTH - 140, 30);
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
        Color bodyColor = isPowerUp ? Color.web("#ffcc00", 0.9) : Color.web("#e26bff", 0.9);
        Color strokeColor = isPowerUp ? Color.web("#ffaa00") : Color.web("#c32fff");

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

        gc.setFont(Font.font("Courier New", FontWeight.BOLD, 14));

        // Typed portion – greyed out
        gc.setFill(Color.web("#888888"));
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

        Button quitBtn = makeOverlayButton("Quit to Menu");
        quitBtn.setOnAction(e -> {
            gameLoop.stop();
            gameController.endGame();
            returnToMenu.run();
        });

        VBox box = overlayBox(title, hint, quitBtn);
        overlay.getChildren().setAll(box);
        overlay.setVisible(true);
    }

    private void showEndOverlay(StackPane overlay, boolean won, Runnable returnToMenu) {
        Label title = new Label(won ? "Level Complete!" : "Game Over");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 44));
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

        Button menuBtn = makeOverlayButton("Return to Menu");
        menuBtn.setOnAction(e -> returnToMenu.run());

        VBox box = overlayBox(title, scoreLbl, wpmLbl, msg, menuBtn);
        overlay.getChildren().setAll(box);
        overlay.setVisible(true);
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

    private Pane createSpaceBg() {
        Pane bg = new Pane();
        bg.setPrefSize(WIDTH, HEIGHT);
        bg.setBackground(new Background(new BackgroundFill(
                Color.web("#0d0025"), CornerRadii.EMPTY, Insets.EMPTY)));

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
