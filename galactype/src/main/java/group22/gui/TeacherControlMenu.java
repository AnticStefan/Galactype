package group22.gui;

import java.util.List;

import group22.AccountManager;
import group22.GameController;
import group22.PlayerAccount;
import group22.TeacherAccount;
import group22.TypingStatistics;
import group22.enums.AccountType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Teacher/parent control panel. Requires authentication as a TEACHER account
 * before granting access to statistics, account management, and high score controls.
 *
 * @author group22
 */
public class TeacherControlMenu {

    private static final int WINDOW_WIDTH = 760;
    private static final int WINDOW_HEIGHT = 520;

    private final GameController gameController;
    private Runnable returnToMain;
    private StackPane root;

    public TeacherControlMenu(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Builds and returns the scene. If a teacher account is already logged in
     * the controls panel is shown immediately; otherwise an authentication form
     * is presented first.
     */
    public Scene createScene(Stage stage, Runnable returnToMain) {
        this.returnToMain = returnToMain;

        root = new StackPane();
        root.getChildren().add(MainMenu.createDecoratedBackground());
        root.setPadding(new Insets(16));

        if (isTeacherAuthenticated()) {
            showControlsPanel();
        } else {
            showAuthPanel();
        }

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        MainMenu.addMenuBackSpace(scene, returnToMain);
        return scene;
    }

    /** Returns true when the currently logged-in account is a teacher. */
    private boolean isTeacherAuthenticated() {
        return gameController.getAccountManager().getCurrentAccount() instanceof TeacherAccount;
    }

    // Auth panel 

    /** Shows the teacher login form when no teacher is authenticated. */
    private void showAuthPanel() {
        Label title = new Label("Teacher Access");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 38));
        title.setTextFill(Color.web("#ffe6ff"));
        title.setEffect(new DropShadow(12, Color.web("#2b004d", 0.55)));

        Label instruction = new Label("Enter teacher credentials to continue.");
        instruction.setFont(Font.font("Arial", 14));
        instruction.setTextFill(Color.web("#c8a0ff"));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        styleTextField(usernameField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleTextField(passwordField);

        Label errorLabel = new Label("");
        errorLabel.setFont(Font.font("Arial", 13));
        errorLabel.setTextFill(Color.web("#ff6677"));
        errorLabel.setMinHeight(18);

        Button loginBtn = makePrimaryButton("Login as Teacher");
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            AccountManager am = gameController.getAccountManager();
            var account = am.login(username, password);
            if (account instanceof TeacherAccount) {
                root.getChildren().removeIf(n -> n != root.getChildren().get(0));
                showControlsPanel();
            } else {
                if (account != null) {
                    am.logout(); // revert — logged-in player accounts are not teacher access
                }
                errorLabel.setText("Invalid teacher credentials.");
            }
        });

        // Allow pressing Enter to submit
        passwordField.setOnAction(e -> loginBtn.fire());

        Button backBtn = makeSecondaryButton("Back");
        backBtn.setOnAction(e -> returnToMain.run());

        VBox form = new VBox(10, instruction, usernameField, passwordField, errorLabel, loginBtn, backBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(320);
        form.setBackground(new Background(new BackgroundFill(
                Color.web("#1a0035", 0.88), new CornerRadii(18), Insets.EMPTY)));
        form.setPadding(new Insets(28));
        form.setEffect(new DropShadow(18, Color.web("#130021", 0.5)));

        VBox content = new VBox(20, title, form);
        content.setAlignment(Pos.CENTER);

        root.getChildren().add(content);
    }

    // Main controls panel

    // ASSISTED BY CLAUDE SONNET 4.6
    /** Shows the full controls panel once a teacher is authenticated. */
    private void showControlsPanel() {
        Label logoLabel = new Label("Galactype");
        logoLabel.setFont(Font.font("Segoe Script", FontWeight.BOLD, 28));
        logoLabel.setTextFill(Color.web("#ffe6ff"));
        logoLabel.setEffect(new DropShadow(10, Color.web("#250042", 0.55)));

        Label title = new Label("Teacher Controls");
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, 34));
        title.setTextFill(Color.web("#ffe6ff"));
        title.setEffect(new DropShadow(10, Color.web("#2b004d", 0.55)));

        Button backBtn = MainMenu.createTopRightButton("Back");
        backBtn.setOnAction(e -> returnToMain.run());

        // Swappable content region
        StackPane contentArea = new StackPane();
        contentArea.setMinHeight(310);
        contentArea.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        // Sidebar nav
        Button statsBtn      = makeNavButton("Player Statistics");
        Button accountBtn    = makeNavButton("Account Management");
        Button resetScoreBtn = makeNavButton("Reset High Scores");

        statsBtn.setOnAction(e      -> showStatsView(contentArea));
        accountBtn.setOnAction(e    -> showAccountManagementView(contentArea));
        resetScoreBtn.setOnAction(e -> showResetScoresView(contentArea));

        VBox nav = new VBox(10, statsBtn, accountBtn, resetScoreBtn);
        nav.setAlignment(Pos.TOP_CENTER);
        nav.setPadding(new Insets(12));
        nav.setBackground(new Background(new BackgroundFill(
                Color.web("#1a0035", 0.82), new CornerRadii(14), Insets.EMPTY)));
        nav.setMinWidth(170);

        HBox mainArea = new HBox(14, nav, contentArea);
        mainArea.setAlignment(Pos.TOP_LEFT);

        VBox fullContent = new VBox(10, title, mainArea);
        fullContent.setAlignment(Pos.TOP_CENTER);
        fullContent.setPadding(new Insets(10, 24, 16, 24));

        StackPane.setAlignment(logoLabel, Pos.TOP_LEFT);
        StackPane.setMargin(logoLabel, new Insets(14));
        StackPane.setAlignment(backBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(backBtn, new Insets(14));

        root.getChildren().addAll(fullContent, logoLabel, backBtn);

        // Default view
        showStatsView(contentArea);
    }

    // Sub-views 

    /** Displays a player selector and their saved typing statistics. */
    private void showStatsView(StackPane contentArea) {
        List<PlayerAccount> players = gameController.getAccountManager().getAllPlayerAccounts();

        Label header = sectionHeader("Player Statistics");

        VBox box = new VBox(10);
        box.setAlignment(Pos.TOP_LEFT);
        box.setPadding(new Insets(14));
        box.setBackground(new Background(new BackgroundFill(
                Color.web("#1a0035", 0.78), new CornerRadii(12), Insets.EMPTY)));

        if (players.isEmpty()) {
            Label none = new Label("No player accounts found.");
            none.setFont(Font.font("Arial", 14));
            none.setTextFill(Color.web("#c8a0ff"));
            box.getChildren().addAll(header, none);
            contentArea.getChildren().setAll(box);
            return;
        }

        ComboBox<String> playerCombo = new ComboBox<>();
        players.forEach(p -> playerCombo.getItems().add(p.getUsername()));
        playerCombo.setPromptText("Select player…");
        styleComboBox(playerCombo);

        VBox statsBox = new VBox(5);

        playerCombo.setOnAction(e -> {
            String selected = playerCombo.getValue();
            if (selected == null) return;
            players.stream()
                    .filter(p -> p.getUsername().equals(selected))
                    .findFirst()
                    .ifPresent(p -> populateStats(statsBox, p));
        });

        box.getChildren().addAll(header, playerCombo, statsBox);
        contentArea.getChildren().setAll(box);
    }

    /** Fills in stat rows for the given player. */
    private void populateStats(VBox statsBox, PlayerAccount player) {
        TypingStatistics stats = player.getStatistics();
        statsBox.getChildren().setAll(
                statRow("Avg WPM",       String.format("%.1f", stats.getAverageWPM())),
                statRow("Peak WPM",      String.format("%.1f", stats.getPeakWPM())),
                statRow("Accuracy",      String.format("%.1f%%", stats.getAccuracyPercent())),
                statRow("Errors",        String.valueOf(stats.getTotalErrors())),
                statRow("Words Typed",   String.valueOf(stats.getTotalWordsTyped())),
                statRow("Time Played",   formatTime(stats.getTotalTimePlayed())),
                statRow("Level Reached", String.valueOf(player.getLevelProgress()))
        );
    }

    /**
     * Shows account creation (new player) and management controls
     * (reset password, reset statistics) for existing players.
     */
    // ASSISTED BY CLAUDE SONNET 4.6
    private void showAccountManagementView(StackPane contentArea) {
        List<PlayerAccount> players = gameController.getAccountManager().getAllPlayerAccounts();

        Label header = sectionHeader("Account Management");
        Label statusLabel = new Label("");
        statusLabel.setFont(Font.font("Arial", 13));
        statusLabel.setWrapText(true);
        statusLabel.setMinHeight(18);

        // Create new player 
        Label createHeader = subSectionLabel("Create New Player Account");

        TextField newUserField = new TextField();
        newUserField.setPromptText("Username (≥ 3 chars)");
        styleTextField(newUserField);

        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("Password (≥ 4 chars)");
        styleTextField(newPassField);

        Button createBtn = makeActionButton("Create Account");
        createBtn.setOnAction(e -> {
            String u = newUserField.getText().trim();
            String p = newPassField.getText();
            if (u.length() < 3 || p.length() < 4) {
                setStatus(statusLabel, "Username ≥ 3 chars and password ≥ 4 chars required.", false);
                return;
            }
            boolean ok = gameController.getAccountManager().createAccount(u, p, AccountType.PLAYER);
            gameController.getAccountManager().save();
            if (ok) {
                setStatus(statusLabel, "Account '" + u + "' created successfully.", true);
                newUserField.clear();
                newPassField.clear();
                showAccountManagementView(contentArea); // refresh player list
            } else {
                setStatus(statusLabel, "Username '" + u + "' is already taken.", false);
            }
        });

        // Manage existing player 
        Label manageHeader = subSectionLabel("Manage Existing Player");

        ComboBox<String> playerCombo = new ComboBox<>();
        players.forEach(p -> playerCombo.getItems().add(p.getUsername()));
        playerCombo.setPromptText("Select player…");
        styleComboBox(playerCombo);

        PasswordField resetPassField = new PasswordField();
        resetPassField.setPromptText("New password (≥ 4 chars)");
        styleTextField(resetPassField);

        Button resetPassBtn = makeActionButton("Reset Password");
        resetPassBtn.setOnAction(e -> {
            String selected = playerCombo.getValue();
            String newPass  = resetPassField.getText();
            if (selected == null) { setStatus(statusLabel, "Select a player first.", false); return; }
            if (newPass.length() < 4) { setStatus(statusLabel, "Password must be ≥ 4 chars.", false); return; }
            players.stream()
                    .filter(p -> p.getUsername().equals(selected))
                    .findFirst()
                    .ifPresent(p -> {
                        TeacherAccount teacher =
                                (TeacherAccount) gameController.getAccountManager().getCurrentAccount();
                        teacher.resetPlayerPassword(p, newPass);
                        gameController.getAccountManager().save();
                        setStatus(statusLabel, "Password reset for '" + selected + "'.", true);
                        resetPassField.clear();
                    });
        });

        Button resetStatsBtn = makeActionButton("Reset Stats");
        resetStatsBtn.setOnAction(e -> {
            String selected = playerCombo.getValue();
            if (selected == null) { setStatus(statusLabel, "Select a player first.", false); return; }
            players.stream()
                    .filter(p -> p.getUsername().equals(selected))
                    .findFirst()
                    .ifPresent(p -> {
                        TeacherAccount teacher =
                                (TeacherAccount) gameController.getAccountManager().getCurrentAccount();
                        teacher.resetPlayerStatistics(p);
                        gameController.getAccountManager().save();
                        setStatus(statusLabel, "Statistics reset for '" + selected + "'.", true);
                    });
        });

        HBox manageButtons = new HBox(8, resetPassBtn, resetStatsBtn);

        VBox box = new VBox(7,
                header,
                createHeader, newUserField, newPassField, createBtn,
                manageHeader, playerCombo, resetPassField, manageButtons,
                statusLabel
        );
        box.setAlignment(Pos.TOP_LEFT);
        box.setPadding(new Insets(14));
        box.setBackground(new Background(new BackgroundFill(
                Color.web("#1a0035", 0.78), new CornerRadii(12), Insets.EMPTY)));

        contentArea.getChildren().setAll(box);
    }

    /** Shows a confirmation button to wipe the global high score table. */
    private void showResetScoresView(StackPane contentArea) {
        Label header = sectionHeader("Reset High Score Table");

        Label warning = new Label(
                "This will permanently erase all high scores for all players and levels.\n\nThis cannot be undone."
        );
        warning.setFont(Font.font("Arial", 14));
        warning.setTextFill(Color.web("#ffaa66"));
        warning.setWrapText(true);
        warning.setMaxWidth(420);

        Label statusLabel = new Label("");
        statusLabel.setFont(Font.font("Arial", 13));
        statusLabel.setMinHeight(18);

        Button confirmBtn = makeActionButton("Confirm Reset");
        confirmBtn.setStyle("-fx-background-color: #aa1133; -fx-border-color: #ff4466; " +
                "-fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;");
        confirmBtn.setTextFill(Color.WHITE);
        confirmBtn.setOnAction(e -> {
            gameController.getHighScoreTable().reset();
            gameController.saveHighScores();
            setStatus(statusLabel, "High score table has been cleared.", true);
            confirmBtn.setDisable(true);
        });

        VBox box = new VBox(14, header, warning, confirmBtn, statusLabel);
        box.setAlignment(Pos.TOP_LEFT);
        box.setPadding(new Insets(14));
        box.setBackground(new Background(new BackgroundFill(
                Color.web("#1a0035", 0.78), new CornerRadii(12), Insets.EMPTY)));

        contentArea.getChildren().setAll(box);
    }

    // Styling helpers 

    private void setStatus(Label label, String message, boolean success) {
        label.setText(message);
        label.setTextFill(success ? Color.web("#88ff99") : Color.web("#ff6677"));
    }

    private Label sectionHeader(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        l.setTextFill(Color.web("#ffe6ff"));
        return l;
    }

    private Label subSectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        l.setTextFill(Color.web("#d0b0ff"));
        return l;
    }

    private HBox statRow(String label, String value) {
        Label lbl = new Label(label + ":");
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web("#c8a0ff"));
        lbl.setMinWidth(110);
        Label val = new Label(value);
        val.setFont(Font.font("Arial", 13));
        val.setTextFill(Color.web("#ffe6ff"));
        HBox row = new HBox(8, lbl, val);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void styleTextField(TextField field) {
        field.setStyle("-fx-background-color: #1a0035; -fx-text-fill: #ffe6ff; " +
                "-fx-border-color: #c32fff; -fx-border-width: 1.5; " +
                "-fx-background-radius: 8; -fx-border-radius: 8; -fx-prompt-text-fill: #886699;");
        field.setPrefWidth(240);
        field.setPrefHeight(32);
    }

    private void styleComboBox(ComboBox<?> combo) {
        combo.setStyle("-fx-background-color: #1a0035; -fx-border-color: #c32fff; " +
                "-fx-border-width: 1.5; -fx-background-radius: 8; -fx-border-radius: 8;");
        combo.setPrefWidth(240);
    }

    private Button makeNavButton(String text) {
        Button b = new Button(text);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        b.setTextFill(Color.web("#ffe6ff"));
        b.setPrefWidth(150);
        b.setPrefHeight(36);
        String normalStyle = "-fx-background-color: rgba(37,0,66,0.5); -fx-border-color: #ffe6ff; " +
                "-fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: rgba(60,0,110,0.9); -fx-border-color: #ffffff; " +
                "-fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";
        b.setStyle(normalStyle);
        MainMenu.applyHoverFocusBehavior(b, normalStyle, hoverStyle);
        return b;
    }

    private Button makePrimaryButton(String text) {
        Button b = new Button(text);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        b.setTextFill(Color.WHITE);
        b.setPrefWidth(220);
        b.setPrefHeight(40);
        String normalStyle = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ca3cff, #ff5dd4); " +
                "-fx-border-color: #ffe6ff; -fx-border-width: 2; " +
                "-fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #d453ff, #ff76e0); " +
                "-fx-border-color: #ffff88; -fx-border-width: 3; " +
                "-fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;";
        b.setStyle(normalStyle);
        b.setEffect(new DropShadow(8, Color.web("#6100aa", 0.45)));
        MainMenu.applyHoverFocusBehavior(b, normalStyle, hoverStyle);
        return b;
    }

    private Button makeActionButton(String text) {
        Button b = new Button(text);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        b.setTextFill(Color.web("#ffe6ff"));
        b.setPrefHeight(32);
        String normalStyle = "-fx-background-color: rgba(37,0,66,0.6); -fx-border-color: #c32fff; " +
                "-fx-border-width: 2; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: rgba(70,0,120,0.85); -fx-border-color: #ff8cff; " +
                "-fx-border-width: 2; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
        b.setStyle(normalStyle);
        MainMenu.applyHoverFocusBehavior(b, normalStyle, hoverStyle);
        return b;
    }

    private Button makeSecondaryButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setTextFill(Color.web("#ffe6ff"));
        String normalStyle = "-fx-background-color: rgba(37, 0, 66, 0.35); -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: rgba(60, 0, 110, 0.55); -fx-border-color: #ffffff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;";
        button.setStyle(normalStyle);
        button.setPrefWidth(160);
        button.setPrefHeight(38);
        button.setEffect(new DropShadow(8, Color.web("#000000", 0.25)));
        MainMenu.applyHoverFocusBehavior(button, normalStyle, hoverStyle);
        return button;
    }

    /** Formats milliseconds into a human-readable duration string. */
    private String formatTime(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours   = minutes / 60;
        if (hours > 0)   return String.format("%dh %dm", hours, minutes % 60);
        if (minutes > 0) return String.format("%dm %ds", minutes, seconds % 60);
        return seconds + "s";
    }
}
