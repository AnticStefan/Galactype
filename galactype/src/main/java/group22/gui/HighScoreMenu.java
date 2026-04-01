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
import javafx.scene.control.ScrollPane;
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
        ScrollPane scrollPane = createScrollableScoreList();

        Button backButton = makeSecondaryButton("Back to Main Menu");
        backButton.setOnAction(e -> returnToMain.run());

        VBox content = new VBox(16, title, scrollPane, backButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(16, 28, 20, 28));
        return content;
    }

    private ScrollPane createScrollableScoreList() {
        VBox scoreList = new VBox(8);
        scoreList.setAlignment(Pos.CENTER);
        scoreList.setPadding(new Insets(14));

        List<HighScoreEntry> entries = gameController.getHighScoreTable().getTopEntries();
        if (entries.isEmpty()) {
            Label emptyLabel = new Label("No high scores yet.");
            emptyLabel.setFont(Font.font("Arial", 20));
            emptyLabel.setTextFill(Color.web("#f0e5ff"));
            scoreList.getChildren().add(emptyLabel);
        } else {
            scoreList.getChildren().add(buildHeaderRow());
            for (int i = 0; i < entries.size(); i++) {
                scoreList.getChildren().add(buildEntryRow(i + 1, entries.get(i)));
            }
        }

        ScrollPane scroll = new ScrollPane(scoreList);
        scroll.setFitToWidth(true);
        scroll.setMaxWidth(580);
        scroll.setMaxHeight(310);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle(
            "-fx-background: #2f005d; " +
            "-fx-background-color: #2f005d; " +
            "-fx-border-color: #ffb6ff; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 16; " +
            "-fx-background-radius: 16;"
        );
        scroll.setEffect(new DropShadow(10, Color.web("#130021", 0.4)));
        return scroll;
    }

    private HBox buildHeaderRow() {
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(4, 12, 4, 12));

        Label rankHdr  = makeColumnLabel("#",      40,  FontWeight.BOLD, "#ffb6ff");
        Label nameHdr  = makeColumnLabel("Player", 200, FontWeight.BOLD, "#ffb6ff");
        Label levelHdr = makeColumnLabel("Level",  70,  FontWeight.BOLD, "#ffb6ff");
        Label scoreHdr = makeColumnLabel("Score",  90,  FontWeight.BOLD, "#ffb6ff");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(rankHdr, nameHdr, spacer, levelHdr, scoreHdr);
        return header;
    }

    private HBox buildEntryRow(int rank, HighScoreEntry entry) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setBackground(new Background(new BackgroundFill(Color.web("#250042"), new CornerRadii(10), Insets.EMPTY)));
        row.setBorder(new Border(new BorderStroke(Color.web("#af83ff"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));

        // Rank circle — gold/silver/bronze for top 3
        Color medalColor;
        if (rank == 1)      medalColor = Color.web("#ffd700");
        else if (rank == 2) medalColor = Color.web("#c0c0c0");
        else if (rank == 3) medalColor = Color.web("#cd7f32");
        else                medalColor = Color.web("#ffffff", 0.5);

        StackPane rankBadge = new StackPane();
        Circle circle = new Circle(14, medalColor);
        Label rankNum = new Label(String.valueOf(rank));
        rankNum.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        rankNum.setTextFill(Color.web("#1a0035"));
        rankBadge.getChildren().addAll(circle, rankNum);
        rankBadge.setMinWidth(32);

        Label nameLabel  = makeColumnLabel(entry.getUsername(),           200, FontWeight.BOLD,   "#ffd4ff");
        Label levelLabel = makeColumnLabel("Lvl " + entry.getLevel(),     70,  FontWeight.NORMAL,  "#e2c0ff");
        Label scoreLabel = makeColumnLabel(String.valueOf(entry.getScore()), 90, FontWeight.BOLD,  "#ffd4ff");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(rankBadge, nameLabel, spacer, levelLabel, scoreLabel);
        return row;
    }

    private Label makeColumnLabel(String text, double minWidth, FontWeight weight, String color) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", weight, 16));
        label.setTextFill(Color.web(color));
        label.setMinWidth(minWidth);
        return label;
    }

    private Label createScreenTitle() {
        Label title = new Label("High Scores");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 42));
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
