package com.lms.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Central styling toolkit for the futuristic neon-blue LMS UI.
 * Everything here is done in pure Java (JavaFX inline styles + effects) —
 * there are no external .css or .html files in this project.
 */
public class NeonTheme {

    public static final String BG_DARK = "#050914";
    public static final String BG_PANEL = "#0b1120";
    public static final String NEON_BLUE = "#00e5ff";
    public static final String NEON_PURPLE = "#7b5bff";
    public static final String NEON_CYAN = "#3df3ff";
    public static final String TEXT_LIGHT = "#e6f7ff";
    public static final String TEXT_MUTED = "#7f93b8";
    public static final String DANGER = "#ff4d6d";
    public static final String SUCCESS = "#33ffb0";

    /** Full-window animated dark gradient background used behind every screen. */
    public static StackPane wrapWithBackground(Region content) {
        StackPane root = new StackPane();
        LinearGradient bgGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#03050d")),
                new Stop(0.5, Color.web("#0a1030")),
                new Stop(1, Color.web("#050914")));
        root.setBackground(new Background(new BackgroundFill(bgGradient, CornerRadii.EMPTY, Insets.EMPTY)));

        // Glowing decorative orbs for a futuristic feel
        Circle orb1 = glowOrb(NEON_BLUE, 220);
        StackPane.setAlignment(orb1, javafx.geometry.Pos.TOP_LEFT);
        orb1.setTranslateX(-80);
        orb1.setTranslateY(-100);

        Circle orb2 = glowOrb(NEON_PURPLE, 260);
        StackPane.setAlignment(orb2, javafx.geometry.Pos.BOTTOM_RIGHT);
        orb2.setTranslateX(100);
        orb2.setTranslateY(120);

        root.getChildren().addAll(orb1, orb2, content);
        return root;
    }

    private static Circle glowOrb(String colorHex, double radius) {
        Circle c = new Circle(radius);
        c.setFill(Color.web(colorHex, 0.18));
        BoxBlur blur = new BoxBlur(80, 80, 3);
        c.setEffect(blur);
        c.setOpacity(0.8);
        return c;
    }

    /** A glassmorphism-style card: translucent panel, blurred edge, neon border glow. */
    public static VBox glassCard(double width) {
        VBox card = new VBox(18);
        card.setPadding(new Insets(36));
        card.setPrefWidth(width);
        card.setMaxWidth(width);
        card.setBackground(new Background(new BackgroundFill(
                Color.web("#0d1428", 0.72), new CornerRadii(20), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(
                Color.web(NEON_BLUE, 0.45), BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(1.4))));
        DropShadow glow = new DropShadow(35, Color.web(NEON_BLUE, 0.35));
        glow.setSpread(0.1);
        card.setEffect(glow);
        return card;
    }

    public static Label title(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI Semibold", FontWeight.BOLD, 26));
        l.setTextFill(Color.web(TEXT_LIGHT));
        DropShadow glow = new DropShadow(18, Color.web(NEON_BLUE, 0.85));
        l.setEffect(glow);
        return l;
    }

    public static Label subtitle(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI", 13));
        l.setTextFill(Color.web(TEXT_MUTED));
        return l;
    }

    public static Label sectionHeader(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI Semibold", FontWeight.BOLD, 18));
        l.setTextFill(Color.web(NEON_CYAN));
        return l;
    }

    public static TextField textField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        styleField(tf);
        return tf;
    }

    public static PasswordField passwordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        styleField(pf);
        return pf;
    }

    public static TextArea textArea(String prompt) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setWrapText(true);
        ta.setPrefRowCount(3);
        ta.setStyle(fieldStyle());
        return ta;
    }

    private static void styleField(TextInputControl field) {
        field.setStyle(fieldStyle());
        field.setPrefHeight(38);
        field.focusedProperty().addListener((obs, was, isNow) -> {
            if (isNow) {
                field.setStyle(fieldStyle() + "-fx-border-color:" + NEON_BLUE + "; -fx-border-width: 1.6;");
            } else {
                field.setStyle(fieldStyle());
            }
        });
    }

    private static String fieldStyle() {
        return "-fx-background-color: #0a1024;" +
               "-fx-text-fill: " + TEXT_LIGHT + ";" +
               "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
               "-fx-background-radius: 10;" +
               "-fx-border-radius: 10;" +
               "-fx-border-color: #23335c;" +
               "-fx-border-width: 1.2;" +
               "-fx-font-size: 13px;" +
               "-fx-padding: 8 12 8 12;";
    }

    /** Primary neon-glow gradient button. */
    public static Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(40);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setCursor(javafx.scene.Cursor.HAND);
        String base = "-fx-background-color: linear-gradient(to right, #0091ff, #7b5bff);" +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;" +
                "-fx-background-radius: 10; -fx-cursor: hand;";
        btn.setStyle(base);
        DropShadow glow = new DropShadow(20, Color.web(NEON_BLUE, 0.55));
        btn.setEffect(glow);
        btn.setOnMouseEntered(e -> btn.setEffect(new Glow(0.6)));
        btn.setOnMouseExited(e -> btn.setEffect(glow));
        return btn;
    }

    /** Secondary outlined button (used for cancel/back/secondary actions). */
    public static Button secondaryButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(36);
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + NEON_CYAN + ";" +
                "-fx-border-color: " + NEON_CYAN + "; -fx-border-radius: 10; -fx-background-radius: 10;" +
                "-fx-border-width: 1.2; -fx-font-size: 12px; -fx-cursor: hand;");
        return btn;
    }

    /** Danger button, e.g. delete / logout. */
    public static Button dangerButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(36);
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + DANGER + ";" +
                "-fx-border-color: " + DANGER + "; -fx-border-radius: 10; -fx-background-radius: 10;" +
                "-fx-border-width: 1.2; -fx-font-size: 12px; -fx-cursor: hand;");
        return btn;
    }

    /** Sidebar nav-style toggle button used in dashboards. */
    public static ToggleButton navButton(String text, ToggleGroup group) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(group);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        btn.setPrefHeight(42);
        btn.setCursor(javafx.scene.Cursor.HAND);
        String off = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_MUTED + ";" +
                "-fx-font-size: 13px; -fx-background-radius: 10; -fx-padding: 0 0 0 16;";
        String on = "-fx-background-color: linear-gradient(to right, #0091ff33, #7b5bff33);" +
                "-fx-text-fill: " + NEON_CYAN + "; -fx-font-weight: bold; -fx-font-size: 13px;" +
                "-fx-background-radius: 10; -fx-border-color: " + NEON_BLUE + "55; -fx-border-width: 0 0 0 3;" +
                "-fx-padding: 0 0 0 13;";
        btn.setStyle(off);
        btn.selectedProperty().addListener((obs, was, isNow) -> btn.setStyle(isNow ? on : off));
        return btn;
    }

    public static Label statCard(String value, String label) {
        Label combined = new Label(value + "\n" + label);
        combined.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        return combined;
    }

    /** Small KPI card used on dashboards (value + caption). */
    public static VBox kpiCard(String value, String caption, String accent) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(18));
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        box.setBackground(new Background(new BackgroundFill(Color.web("#0d1428", 0.8), new CornerRadii(14), Insets.EMPTY)));
        box.setBorder(new Border(new BorderStroke(Color.web(accent, 0.5), BorderStrokeStyle.SOLID, new CornerRadii(14), new BorderWidths(1.2))));
        DropShadow glow = new DropShadow(16, Color.web(accent, 0.4));
        box.setEffect(glow);

        Label valLabel = new Label(value);
        valLabel.setFont(Font.font("Segoe UI Semibold", FontWeight.BOLD, 24));
        valLabel.setTextFill(Color.web(accent));

        Label capLabel = new Label(caption);
        capLabel.setFont(Font.font("Segoe UI", 12));
        capLabel.setTextFill(Color.web(TEXT_MUTED));

        box.getChildren().addAll(valLabel, capLabel);
        return box;
    }

    public static void fadeIn(Region node) {
        FadeTransition ft = new FadeTransition(Duration.millis(420), node);
        node.setOpacity(0);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public static Label errorLabel() {
        Label l = new Label();
        l.setTextFill(Color.web(DANGER));
        l.setFont(Font.font("Segoe UI", 12));
        l.setWrapText(true);
        return l;
    }

    public static Label successLabel() {
        Label l = new Label();
        l.setTextFill(Color.web(SUCCESS));
        l.setFont(Font.font("Segoe UI", 12));
        l.setWrapText(true);
        return l;
    }

    public static TableView<?> styleTable(TableView<?> table) {
        table.setStyle("-fx-background-color: #0a1024; -fx-control-inner-background: #0a1024;" +
                "-fx-text-fill:" + TEXT_LIGHT + "; -fx-table-cell-border-color: #1a2547;");
        return table;
    }
}
