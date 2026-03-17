package group22;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Main extends Application { // "Application" extension from javafx

    // javafx GUI window test below 
    @Override
    public void start(Stage stage) { // importing Stage class from javafx for usability
        System.out.println("GUI works"); // if window launches this prints 
        Label label = new Label("JavaFX is working!");
        Scene scene = new Scene(label, 300, 200);

        stage.setTitle("Galactype Test"); 
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            System.out.println("GUI doesn't work");
            e.printStackTrace();
        }   
    }
}