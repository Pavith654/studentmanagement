package com.lms;

import com.lms.ui.LoginScreen;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point for the LMS desktop application.
 * Run this class (or `mvn javafx:run`) from IntelliJ IDEA to launch the app.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        new LoginScreen(primaryStage).show();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
