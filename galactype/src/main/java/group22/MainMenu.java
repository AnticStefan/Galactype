package group22;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainMenu extends Application {

    private final GameController gameController = new GameController();

    @Override
    public void start(Stage stage) {
        initializeController();

        Label title = new Label("Galactype");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label status = new Label("JavaFX launcher initialized successfully.");
        Label details = new Label("GameController, data storage, and word bank are ready.");

        VBox root = new VBox(12, title, status, details);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 480, 220);
        stage.setTitle("Galactype");
        stage.setScene(scene);
        stage.show();
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
