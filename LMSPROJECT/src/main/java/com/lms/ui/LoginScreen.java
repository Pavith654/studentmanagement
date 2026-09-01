package com.lms.ui;

import com.lms.dao.AdminDAO;
import com.lms.dao.StudentDAO;
import com.lms.model.Admin;
import com.lms.model.Student;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScreen {

    private final Stage stage;
    private final ToggleGroup roleGroup = new ToggleGroup();

    public LoginScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox card = NeonTheme.glassCard(380);

        Label title = NeonTheme.title("LMS ACCESS PORTAL");
        Label subtitle = NeonTheme.subtitle("Futuristic Learning Management System");

        ToggleButton adminToggle = new ToggleButton("Admin");
        ToggleButton studentToggle = new ToggleButton("Student");
        adminToggle.setToggleGroup(roleGroup);
        studentToggle.setToggleGroup(roleGroup);
        studentToggle.setSelected(true);
        styleRoleToggle(adminToggle);
        styleRoleToggle(studentToggle);
        HBox roleBox = new HBox(10, studentToggle, adminToggle);
        roleBox.setAlignment(Pos.CENTER);

        TextField emailField = NeonTheme.textField("Email address");
        PasswordField passwordField = NeonTheme.passwordField("Password");

        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.setStyle("-fx-text-fill: " + NeonTheme.TEXT_MUTED + "; -fx-font-size: 11px;");

        Label errorLabel = NeonTheme.errorLabel();

        Button loginBtn = NeonTheme.primaryButton("SIGN IN");
        Button registerBtn = NeonTheme.secondaryButton("New student? Register here");
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        loginBtn.setOnAction(e -> {
            errorLabel.setText("");
            String email = emailField.getText().trim();
            String pass = passwordField.getText();
            if (email.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Please enter both email and password.");
                return;
            }
            if (adminToggle.isSelected()) {
                Admin admin = new AdminDAO().login(email, pass);
                if (admin != null) {
                    new AdminDashboard(stage, admin).show();
                } else {
                    errorLabel.setText("Invalid admin credentials.");
                }
            } else {
                Student student = new StudentDAO().login(email, pass);
                if (student != null) {
                    new StudentDashboard(stage, student).show();
                } else {
                    errorLabel.setText("Invalid student credentials.");
                }
            }
        });

        // Enter-key submits the form
        passwordField.setOnAction(e -> loginBtn.fire());

        registerBtn.setOnAction(e -> new RegisterScreen(stage).show());

        Label footer = NeonTheme.subtitle("Need help? contact support@lms.edu");
        footer.setStyle("-fx-font-size: 10px;");

        card.getChildren().addAll(
                title, subtitle, roleBox,
                new VBox(6, new Label("Email") {{ setStyle("-fx-text-fill:" + NeonTheme.TEXT_MUTED + "; -fx-font-size:11px;"); }}, emailField),
                new VBox(6, new Label("Password") {{ setStyle("-fx-text-fill:" + NeonTheme.TEXT_MUTED + "; -fx-font-size:11px;"); }}, passwordField),
                rememberMe,
                errorLabel,
                loginBtn,
                registerBtn,
                new Separator(),
                footer
        );
        card.setAlignment(Pos.CENTER);

        Region root = NeonTheme.wrapWithBackground(card);
        NeonTheme.fadeIn(card);

        Scene scene = new Scene((javafx.scene.Parent) root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("LMS - Login");
        stage.centerOnScreen();
    }

    private void styleRoleToggle(ToggleButton btn) {
        btn.setPrefWidth(120);
        btn.setPrefHeight(34);
        btn.setCursor(javafx.scene.Cursor.HAND);
        String off = "-fx-background-color: transparent; -fx-text-fill:" + NeonTheme.TEXT_MUTED + ";" +
                "-fx-border-color:" + NeonTheme.TEXT_MUTED + "55; -fx-border-radius: 20; -fx-background-radius: 20;";
        String on = "-fx-background-color: linear-gradient(to right, #0091ff, #7b5bff);" +
                "-fx-text-fill: white; -fx-font-weight:bold; -fx-background-radius: 20;";
        btn.setStyle(off);
        btn.selectedProperty().addListener((obs, was, isNow) -> btn.setStyle(isNow ? on : off));
    }
}
