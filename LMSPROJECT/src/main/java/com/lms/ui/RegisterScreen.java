package com.lms.ui;

import com.lms.dao.StudentDAO;
import com.lms.model.Student;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterScreen {

    private final Stage stage;

    public RegisterScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox card = NeonTheme.glassCard(400);

        Label title = NeonTheme.title("CREATE STUDENT ACCOUNT");
        Label subtitle = NeonTheme.subtitle("Join the LMS to start learning");

        TextField nameField = NeonTheme.textField("Full name");
        TextField emailField = NeonTheme.textField("Email address");
        TextField phoneField = NeonTheme.textField("Phone number");
        PasswordField passwordField = NeonTheme.passwordField("Password");
        PasswordField confirmField = NeonTheme.passwordField("Confirm password");

        Label errorLabel = NeonTheme.errorLabel();
        Label successLabel = NeonTheme.successLabel();

        Button registerBtn = NeonTheme.primaryButton("REGISTER");
        Button backBtn = NeonTheme.secondaryButton("Back to login");
        backBtn.setMaxWidth(Double.MAX_VALUE);

        registerBtn.setOnAction(e -> {
            errorLabel.setText("");
            successLabel.setText("");
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String pass = passwordField.getText();
            String confirm = confirmField.getText();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Name, email and password are required.");
                return;
            }
            if (!email.contains("@") || !email.contains(".")) {
                errorLabel.setText("Please enter a valid email address.");
                return;
            }
            if (pass.length() < 6) {
                errorLabel.setText("Password must be at least 6 characters.");
                return;
            }
            if (!pass.equals(confirm)) {
                errorLabel.setText("Passwords do not match.");
                return;
            }

            Student s = new Student(0, name, email, pass, phone);
            boolean ok = new StudentDAO().register(s);
            if (ok) {
                successLabel.setText("Account created! Redirecting to login...");
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.2));
                pause.setOnFinished(ev -> new LoginScreen(stage).show());
                pause.play();
            } else {
                errorLabel.setText("Registration failed. Email may already be in use.");
            }
        });

        backBtn.setOnAction(e -> new LoginScreen(stage).show());

        card.getChildren().addAll(title, subtitle, nameField, emailField, phoneField,
                passwordField, confirmField, errorLabel, successLabel, registerBtn, backBtn);
        card.setAlignment(Pos.CENTER);

        Region root = NeonTheme.wrapWithBackground(card);
        NeonTheme.fadeIn(card);

        Scene scene = new Scene((javafx.scene.Parent) root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("LMS - Register");
    }
}
