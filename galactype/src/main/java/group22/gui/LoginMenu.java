package group22.gui;

import group22.Account;
import group22.GameController;
import group22.enums.AccountType;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginMenu {

    private static final int WINDOW_WIDTH = 760;
    private static final int WINDOW_HEIGHT = 520;
    private static final String PRIMARY_BUTTON_STYLE = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ca3cff, #ff5dd4); -fx-border-color: #ffe6ff; -fx-border-width: 3; -fx-cursor: hand; -fx-background-radius: 14; -fx-border-radius: 14;";
    private static final String PRIMARY_BUTTON_HOVER_STYLE = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #d453ff, #ff76e0); -fx-border-color: #ffe6ff; -fx-border-width: 3; -fx-cursor: hand; -fx-background-radius: 14; -fx-border-radius: 14;";
    private static final String SECONDARY_BUTTON_STYLE = "-fx-background-color: rgba(37, 0, 66, 0.35); -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;";
    private static final String SECONDARY_BUTTON_HOVER_STYLE = "-fx-background-color: rgba(60, 0, 110, 0.55); -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;";

    private final GameController gameController;

    public LoginMenu(GameController gameController) {
        this.gameController = gameController;
    }

    public Scene createScene(Stage stage, Runnable returnToMain) {
        StackPane root = new StackPane();
        root.getChildren().add(MainMenu.createDecoratedBackground());
        root.setPadding(new Insets(16));

        Label logoLabel = createLogoLabel();
        Button exitButton = MainMenu.createTopRightButton("Quit");
        exitButton.setOnAction(e -> {
            Platform.exit();
            stage.close();
        });

        StackPane.setAlignment(logoLabel, Pos.TOP_LEFT);
        StackPane.setMargin(logoLabel, new Insets(18));
        StackPane.setAlignment(exitButton, Pos.TOP_RIGHT);
        StackPane.setMargin(exitButton, new Insets(14));



        root.getChildren().addAll(
                createRoleSelectionContent(root, returnToMain),
                logoLabel,
                exitButton
        );

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        MainMenu.addMenuBackSpace(scene, returnToMain);
        return scene;
    }

    private VBox createRoleSelectionContent(StackPane root, Runnable returnToMain) {
        Label title = createScreenTitle("Login");

        Button teacherButton = makePrimaryButton("Login as Teacher");
        teacherButton.setOnAction(e -> swapContent(root, createCredentialContent(AccountType.TEACHER, root, returnToMain)));

        Button studentButton = makePrimaryButton("Login as Student");
        studentButton.setOnAction(e -> swapContent(root, createCredentialContent(AccountType.PLAYER, root, returnToMain)));

        Button createAccountButton = makeSecondaryButton("Create Account");
        createAccountButton.setPrefWidth(220);
        createAccountButton.setOnAction(e -> swapContent(root, createRegistrationContent(root, returnToMain)));

        Button backButton = makeSecondaryButton("Back to Main Menu");
        backButton.setOnAction(e -> returnToMain.run());

        VBox content = new VBox(18, title, teacherButton, studentButton, createAccountButton, backButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28));
        return content;
    }

    private VBox createRegistrationContent(StackPane root, Runnable returnToMain) {
        Label title = createScreenTitle("Create Account", 36);

        // Role toggle state
        final AccountType[] selectedType = {AccountType.PLAYER};

        Button studentToggle = makePrimaryButton("Student");
        Button teacherToggle = makeSecondaryButton("Teacher");
        studentToggle.setPrefWidth(140);
        studentToggle.setPrefHeight(38);
        studentToggle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        teacherToggle.setPrefWidth(140);
        teacherToggle.setPrefHeight(38);

        studentToggle.setOnAction(e -> {
            selectedType[0] = AccountType.PLAYER;
            studentToggle.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ca3cff, #ff5dd4); -fx-border-color: #ffe6ff; -fx-border-width: 3; -fx-cursor: hand; -fx-background-radius: 14; -fx-border-radius: 14;");
            teacherToggle.setStyle("-fx-background-color: rgba(37, 0, 66, 0.35); -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;");
        });
        teacherToggle.setOnAction(e -> {
            selectedType[0] = AccountType.TEACHER;
            teacherToggle.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ca3cff, #ff5dd4); -fx-border-color: #ffe6ff; -fx-border-width: 3; -fx-cursor: hand; -fx-background-radius: 14; -fx-border-radius: 14;");
            studentToggle.setStyle("-fx-background-color: rgba(37, 0, 66, 0.35); -fx-border-color: #ffe6ff; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;");
        });

        HBox roleToggle = new HBox(10, studentToggle, teacherToggle);
        roleToggle.setAlignment(Pos.CENTER_LEFT);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a username");
        styleInputField(usernameField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Choose a password");
        styleInputField(passwordField);

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm password");
        styleInputField(confirmField);

        Label messageLabel = new Label(" ");
        messageLabel.setFont(Font.font("Arial", 15));
        messageLabel.setTextFill(Color.web("#ffcce8"));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(320);

        Button registerButton = makePrimaryButton("Create Account");
        registerButton.setPrefWidth(180);
        registerButton.setOnAction(e -> attemptRegister(
                selectedType, usernameField, passwordField, confirmField, messageLabel));

        Button backButton = makeSecondaryButton("Back to Login");
        backButton.setOnAction(e -> swapContent(root, createRoleSelectionContent(root, returnToMain)));

        HBox actions = new HBox(14, registerButton, backButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox formCard = new VBox(
                10,
                createFieldLabel("Account Type"), roleToggle,
                createFieldLabel("Username"), usernameField,
                createFieldLabel("Password"), passwordField,
                createFieldLabel("Confirm Password"), confirmField,
                messageLabel,
                actions
        );
        formCard.setAlignment(Pos.CENTER_LEFT);
        formCard.setMaxWidth(340);
        formCard.setPadding(new Insets(22));
        formCard.setBackground(new Background(new BackgroundFill(Color.web("#250042", 0.78), new CornerRadii(18), Insets.EMPTY)));
        formCard.setEffect(new DropShadow(18, Color.web("#130021", 0.45)));

        VBox content = new VBox(24, title, formCard);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28));
        return content;
    }

    private void attemptRegister(AccountType[] selectedType, TextField usernameField,
                                  PasswordField passwordField, PasswordField confirmField,
                                  Label messageLabel) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmField.getText();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            messageLabel.setTextFill(Color.web("#ffd1dc"));
            return;
        }

        if (username.length() < 3) {
            messageLabel.setText("Username must be at least 3 characters.");
            messageLabel.setTextFill(Color.web("#ffd1dc"));
            return;
        }

        if (password.length() < 4) {
            messageLabel.setText("Password must be at least 4 characters.");
            messageLabel.setTextFill(Color.web("#ffd1dc"));
            return;
        }

        if (!password.equals(confirm)) {
            messageLabel.setText("Passwords do not match.");
            messageLabel.setTextFill(Color.web("#ffd1dc"));
            confirmField.clear();
            return;
        }

        boolean created = gameController.getAccountManager().createAccount(username, password, selectedType[0]);
        if (!created) {
            messageLabel.setText("Username already taken. Please choose another.");
            messageLabel.setTextFill(Color.web("#ffd1dc"));
            usernameField.clear();
            return;
        }

        gameController.getAccountManager().save();
        messageLabel.setText("Account created! You can now log in.");
        messageLabel.setTextFill(Color.web("#d8ffd8"));
        usernameField.clear();
        passwordField.clear();
        confirmField.clear();
    }

    private VBox createCredentialContent(AccountType accountType, StackPane root, Runnable returnToMain) {
        Label title = createScreenTitle("Login");

        Label roleLabel = new Label(accountType == AccountType.TEACHER ? "Teacher Login" : "Student Login");
        roleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        roleLabel.setTextFill(Color.web("#e2c0ff"));

        Label usernameLabel = createFieldLabel("Username");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        styleInputField(usernameField);

        Label passwordLabel = createFieldLabel("Password");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        styleInputField(passwordField);

        Label messageLabel = new Label(" ");
        messageLabel.setFont(Font.font("Arial", 15));
        messageLabel.setTextFill(Color.web("#ffcce8"));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(320);

        Button loginButton = makePrimaryButton("Login");
        loginButton.setPrefWidth(160);
        loginButton.setOnAction(e -> attemptLogin(accountType, usernameField, passwordField, messageLabel, returnToMain));

        Button backButton = makeSecondaryButton("Back to login");
        backButton.setOnAction(e -> swapContent(root, createRoleSelectionContent(root, returnToMain)));

        HBox actions = new HBox(14, loginButton, backButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox formCard = new VBox(
                10,
                roleLabel,
                usernameLabel,
                usernameField,
                passwordLabel,
                passwordField,
                messageLabel,
                actions
        );
        formCard.setAlignment(Pos.CENTER_LEFT);
        formCard.setMaxWidth(340);
        formCard.setPadding(new Insets(22));
        formCard.setBackground(new Background(new BackgroundFill(Color.web("#250042", 0.78), new CornerRadii(18), Insets.EMPTY)));
        formCard.setEffect(new DropShadow(18, Color.web("#130021", 0.45)));

        VBox content = new VBox(24, title, formCard);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28));
        return content;
    }

    private void attemptLogin(AccountType expectedType, TextField usernameField, PasswordField passwordField, Label messageLabel, Runnable returnToMain) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both a username and password.");
            messageLabel.setTextFill(Color.web("#ffd1dc"));
            return;
        }

        Account account = gameController.getAccountManager().login(username, password);
        if (account == null) {
            messageLabel.setText("Incorrect login information. Please try again.");
            messageLabel.setTextFill(Color.web("#ffd1dc"));
            passwordField.clear();
            return;
        }

        if (account.getAccountType() != expectedType) {
            gameController.getAccountManager().logout();
            messageLabel.setText("That account belongs to a different login type. Please choose the matching option.");
            messageLabel.setTextFill(Color.web("#ffd1dc"));
            passwordField.clear();
            return;
        }

        returnToMain.run();
    }

    private void swapContent(StackPane root, VBox content) {
        root.getChildren().set(1, content);
    }

    private Label createScreenTitle(String text) {
        return createScreenTitle(text, 56);
    }

    private Label createScreenTitle(String text, int fontSize) {
        Label title = new Label(text);
        title.setFont(Font.font("Segoe Script", FontWeight.BOLD, fontSize));
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

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.web("#ffe6ff"));
        return label;
    }

    private void styleInputField(TextField field) {
        field.setFont(Font.font("Arial", 16));
        field.setPrefWidth(300);
        field.setPrefHeight(38);
        field.setStyle("-fx-background-color: rgba(255, 245, 255, 0.95);"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: #ffb6ff;"
                + "-fx-border-radius: 10;"
                + "-fx-border-width: 2;");
    }

    private void attachHover(Button button, String normalStyle, String hoverStyle) {
        MainMenu.applyHoverFocusBehavior(button, normalStyle, hoverStyle);
    }

    private Button makePrimaryButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setTextFill(Color.WHITE);
        button.setStyle(PRIMARY_BUTTON_STYLE);
        button.setPrefWidth(220);
        button.setPrefHeight(52);
        button.setEffect(new DropShadow(10, Color.web("#6100aa", 0.45)));
        attachHover(button, PRIMARY_BUTTON_STYLE, PRIMARY_BUTTON_HOVER_STYLE);
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
}
