package group22;

import group22.gui.MainMenu;

/**
 * Entry point for the fat JAR. A separate launcher class is required because
 * the JVM prevents directly running a class that extends javafx.application.Application
 * from an executable JAR when JavaFX is not on the module path.
 */
public class Launcher {
    public static void main(String[] args) {
        MainMenu.main(args);
    }
}
