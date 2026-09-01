package com.lms.ui;

import com.lms.dao.*;
import com.lms.model.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StudentDashboard {

    private final Stage stage;
    private final Student student;
    private final BorderPane layout = new BorderPane();
    private final ToggleGroup navGroup = new ToggleGroup();

    private final CourseDAO courseDAO = new CourseDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final MaterialDAO materialDAO = new MaterialDAO();
    private final QuizDAO quizDAO = new QuizDAO();
    private final ResultDAO resultDAO = new ResultDAO();

    public StudentDashboard(Stage stage, Student student) {
        this.stage = stage;
        this.student = student;
    }

    public void show() {
        layout.setLeft(buildSidebar());
        layout.setCenter(buildEnrollPanel());
        layout.setBackground(new Background(new BackgroundFill(Color.web(NeonTheme.BG_DARK), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(layout, 1200, 760);
        stage.setScene(scene);
        stage.setTitle("LMS - Student Dashboard");
        stage.setMaximized(true);
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(24, 14, 24, 14));
        sidebar.setPrefWidth(230);
        sidebar.setBackground(new Background(new BackgroundFill(Color.web(NeonTheme.BG_PANEL), CornerRadii.EMPTY, Insets.EMPTY)));
        sidebar.setBorder(new Border(new BorderStroke(Color.web(NeonTheme.NEON_BLUE, 0.25),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));

        Label logo = new Label("⚡ LMS STUDENT");
        logo.setFont(Font.font("Segoe UI Semibold", FontWeight.BOLD, 18));
        logo.setTextFill(Color.web(NeonTheme.NEON_BLUE));
        logo.setPadding(new Insets(0, 0, 10, 10));

        Label welcome = new Label("Welcome, " + student.getName());
        welcome.setTextFill(Color.web(NeonTheme.TEXT_MUTED));
        welcome.setFont(Font.font("Segoe UI", 11));
        welcome.setPadding(new Insets(0, 0, 20, 10));
        welcome.setWrapText(true);

        ToggleButton enroll = NeonTheme.navButton("📚  Enroll Course", navGroup);
        ToggleButton viewCourse = NeonTheme.navButton("📖  View Course", navGroup);
        ToggleButton downloadNotes = NeonTheme.navButton("⬇️  Download Notes", navGroup);
        ToggleButton progress = NeonTheme.navButton("📊  View Progress", navGroup);
        ToggleButton quiz = NeonTheme.navButton("📝  Take Quiz", navGroup);
        enroll.setSelected(true);

        enroll.setOnAction(e -> layout.setCenter(buildEnrollPanel()));
        viewCourse.setOnAction(e -> layout.setCenter(buildViewCoursePanel()));
        downloadNotes.setOnAction(e -> layout.setCenter(buildDownloadNotesPanel()));
        progress.setOnAction(e -> layout.setCenter(buildProgressPanel()));
        quiz.setOnAction(e -> layout.setCenter(buildQuizPanel()));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logout = NeonTheme.dangerButton("⎋  Logout");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setOnAction(e -> new LoginScreen(stage).show());

        sidebar.getChildren().addAll(logo, welcome, enroll, viewCourse, downloadNotes, progress, quiz, spacer, logout);
        return sidebar;
    }

    // ---------------- ENROLL COURSE ----------------
    private ScrollPane buildEnrollPanel() {
        VBox content = panelContainer("Enroll in a Course", "Browse available courses and enroll");

        List<Course> courses = courseDAO.getAll();
        VBox list = new VBox(12);
        for (Course c : courses) {
            list.getChildren().add(courseRow(c));
        }
        if (courses.isEmpty()) {
            list.getChildren().add(NeonTheme.subtitle("No courses available yet."));
        }
        content.getChildren().add(list);
        return scrollWrap(content);
    }

    private HBox courseRow(Course c) {
        HBox row = new HBox(16);
        row.setPadding(new Insets(14));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setBackground(new Background(new BackgroundFill(Color.web("#0d1428", 0.75), new CornerRadii(12), Insets.EMPTY)));
        row.setBorder(new Border(new BorderStroke(Color.web(NeonTheme.NEON_BLUE, 0.25),
                BorderStrokeStyle.SOLID, new CornerRadii(12), new BorderWidths(1))));

        VBox info = new VBox(4);
        Label title = new Label(c.getTitle());
        title.setTextFill(Color.web(NeonTheme.TEXT_LIGHT));
        title.setFont(Font.font("Segoe UI Semibold", FontWeight.BOLD, 15));
        Label meta = new Label("Trainer: " + (c.getTrainerName() != null ? c.getTrainerName() : "TBD") + "  •  " + c.getDuration());
        meta.setTextFill(Color.web(NeonTheme.TEXT_MUTED));
        meta.setFont(Font.font("Segoe UI", 11));
        info.getChildren().addAll(title, meta);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button enrollBtn = NeonTheme.primaryButton("Enroll");
        enrollBtn.setPrefWidth(110);
        enrollBtn.setOnAction(e -> {
            boolean ok = enrollmentDAO.enroll(student.getStudentId(), c.getCourseId());
            enrollBtn.setText(ok ? "Enrolled ✓" : "Already enrolled");
            enrollBtn.setDisable(true);
        });

        row.getChildren().addAll(info, enrollBtn);
        return row;
    }

    // ---------------- VIEW COURSE ----------------
    private ScrollPane buildViewCoursePanel() {
        VBox content = panelContainer("My Courses", "Courses you're currently enrolled in");

        List<Enrollment> enrollments = enrollmentDAO.getByStudent(student.getStudentId());
        if (enrollments.isEmpty()) {
            content.getChildren().add(NeonTheme.subtitle("You haven't enrolled in any course yet."));
        }
        for (Enrollment en : enrollments) {
            VBox card = new VBox(6);
            card.setPadding(new Insets(16));
            card.setBackground(new Background(new BackgroundFill(Color.web("#0d1428", 0.75), new CornerRadii(12), Insets.EMPTY)));
            card.setBorder(new Border(new BorderStroke(Color.web(NeonTheme.NEON_PURPLE, 0.3),
                    BorderStrokeStyle.SOLID, new CornerRadii(12), new BorderWidths(1))));

            Label title = new Label(en.getCourseTitle());
            title.setTextFill(Color.web(NeonTheme.TEXT_LIGHT));
            title.setFont(Font.font("Segoe UI Semibold", FontWeight.BOLD, 15));

            Label status = new Label("Status: " + en.getStatus() + "   Progress: " + en.getProgress() + "%");
            status.setTextFill(Color.web(NeonTheme.NEON_CYAN));
            status.setFont(Font.font("Segoe UI", 12));

            ProgressBar bar = new ProgressBar(en.getProgress() / 100.0);
            bar.setPrefWidth(300);
            bar.setStyle("-fx-accent: " + NeonTheme.NEON_BLUE + ";");

            card.getChildren().addAll(title, status, bar);
            content.getChildren().add(card);
        }
        return scrollWrap(content);
    }

    // ---------------- DOWNLOAD NOTES ----------------
    private ScrollPane buildDownloadNotesPanel() {
        VBox content = panelContainer("Download Notes", "Course materials uploaded by your trainers/admin");

        List<Enrollment> enrollments = enrollmentDAO.getByStudent(student.getStudentId());
        if (enrollments.isEmpty()) {
            content.getChildren().add(NeonTheme.subtitle("Enroll in a course first to see its materials."));
        }
        for (Enrollment en : enrollments) {
            content.getChildren().add(NeonTheme.sectionHeader(en.getCourseTitle()));
            List<Material> materials = materialDAO.getByCourse(en.getCourseId());
            if (materials.isEmpty()) {
                content.getChildren().add(NeonTheme.subtitle("No materials uploaded yet."));
            }
            for (Material m : materials) {
                HBox row = new HBox(14);
                row.setPadding(new Insets(10, 14, 10, 14));
                row.setAlignment(Pos.CENTER_LEFT);
                row.setBackground(new Background(new BackgroundFill(Color.web("#0a1024"), new CornerRadii(10), Insets.EMPTY)));

                Label name = new Label("📄 " + m.getTitle() + " (" + m.getFileType() + ")");
                name.setTextFill(Color.web(NeonTheme.TEXT_LIGHT));
                HBox.setHgrow(name, Priority.ALWAYS);

                Button download = NeonTheme.secondaryButton("Download");
                download.setOnAction(e -> {
                    try {
                        File f = new File(m.getFilePath());
                        if (f.exists() && Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(f);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                row.getChildren().addAll(name, download);
                content.getChildren().add(row);
            }
        }
        return scrollWrap(content);
    }

    // ---------------- VIEW PROGRESS ----------------
    private ScrollPane buildProgressPanel() {
        VBox content = panelContainer("My Progress", "Your course progress and quiz results");

        List<Enrollment> enrollments = enrollmentDAO.getByStudent(student.getStudentId());
        for (Enrollment en : enrollments) {
            HBox row = new HBox(16);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12));
            Label title = new Label(en.getCourseTitle());
            title.setTextFill(Color.web(NeonTheme.TEXT_LIGHT));
            title.setPrefWidth(220);
            ProgressBar bar = new ProgressBar(en.getProgress() / 100.0);
            bar.setPrefWidth(300);
            bar.setStyle("-fx-accent: " + NeonTheme.NEON_CYAN + ";");
            Label pct = new Label(en.getProgress() + "%");
            pct.setTextFill(Color.web(NeonTheme.NEON_CYAN));
            row.getChildren().addAll(title, bar, pct);
            content.getChildren().add(row);
        }

        content.getChildren().add(new Separator());
        content.getChildren().add(NeonTheme.sectionHeader("Quiz Results"));

        List<Result> results = resultDAO.getByStudent(student.getStudentId());
        if (results.isEmpty()) {
            content.getChildren().add(NeonTheme.subtitle("No quiz attempts yet."));
        }
        for (Result r : results) {
            Label rl = new Label(r.getQuizTitle() + " — Score: " + r.getScore());
            rl.setTextFill(Color.web(NeonTheme.TEXT_LIGHT));
            content.getChildren().add(rl);
        }
        return scrollWrap(content);
    }

    // ---------------- TAKE QUIZ ----------------
    private ScrollPane buildQuizPanel() {
        VBox content = panelContainer("Take Quiz", "Select a course, then attempt its quiz");

        List<Enrollment> enrollments = enrollmentDAO.getByStudent(student.getStudentId());
        ComboBox<Enrollment> courseBox = new ComboBox<>(FXCollections.observableArrayList(enrollments));
        courseBox.setPromptText("Select your course");
        courseBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Enrollment e) { return e == null ? "" : e.getCourseTitle(); }
            @Override public Enrollment fromString(String s) { return null; }
        });

        ComboBox<Quiz> quizBox = new ComboBox<>();
        quizBox.setPromptText("Select quiz");
        quizBox.setDisable(true);

        courseBox.setOnAction(e -> {
            Enrollment sel = courseBox.getValue();
            if (sel != null) {
                quizBox.setItems(FXCollections.observableArrayList(quizDAO.getByCourse(sel.getCourseId())));
                quizBox.setDisable(false);
            }
        });

        VBox quizArea = new VBox(14);
        Button loadQuizBtn = NeonTheme.primaryButton("Load Quiz");

        loadQuizBtn.setOnAction(e -> {
            quizArea.getChildren().clear();
            Quiz quiz = quizBox.getValue();
            if (quiz == null) return;

            List<Question> questions = quizDAO.getQuestions(quiz.getQuizId());
            if (questions.isEmpty()) {
                quizArea.getChildren().add(NeonTheme.subtitle("This quiz has no questions yet."));
                return;
            }

            Map<Integer, ToggleGroup> answerGroups = new java.util.HashMap<>();
            Map<Integer, Character> correctAnswers = new java.util.HashMap<>();

            for (Question q : questions) {
                VBox qBox = new VBox(8);
                qBox.setPadding(new Insets(14));
                qBox.setBackground(new Background(new BackgroundFill(Color.web("#0d1428", 0.75), new CornerRadii(12), Insets.EMPTY)));

                Label qLabel = new Label(q.getQuestionText());
                qLabel.setTextFill(Color.web(NeonTheme.TEXT_LIGHT));
                qLabel.setWrapText(true);
                qLabel.setFont(Font.font("Segoe UI Semibold", FontWeight.BOLD, 13));

                ToggleGroup group = new ToggleGroup();
                RadioButton a = optionRadio(q.getOptionA(), 'A', group);
                RadioButton b = optionRadio(q.getOptionB(), 'B', group);
                RadioButton c = optionRadio(q.getOptionC(), 'C', group);
                RadioButton d = optionRadio(q.getOptionD(), 'D', group);

                answerGroups.put(q.getQuestionId(), group);
                correctAnswers.put(q.getQuestionId(), q.getCorrectOption());

                qBox.getChildren().addAll(qLabel, a, b, c, d);
                quizArea.getChildren().add(qBox);
            }

            Button submitBtn = NeonTheme.primaryButton("Submit Quiz");
            Label scoreLabel = NeonTheme.successLabel();
            submitBtn.setOnAction(ev -> {
                AtomicInteger score = new AtomicInteger();
                int marksPerQuestion = questions.isEmpty() ? 0 : Math.max(1, quiz.getTotalMarks() / questions.size());
                for (Question q : questions) {
                    ToggleGroup g = answerGroups.get(q.getQuestionId());
                    Toggle selected = g.getSelectedToggle();
                    if (selected != null) {
                        char chosen = (char) selected.getUserData();
                        if (chosen == correctAnswers.get(q.getQuestionId())) {
                            score.addAndGet(marksPerQuestion);
                        }
                    }
                }
                resultDAO.saveResult(student.getStudentId(), quiz.getQuizId(), score.get());
                scoreLabel.setText("Quiz submitted! Your score: " + score.get() + " / " + quiz.getTotalMarks());
                submitBtn.setDisable(true);
            });

            quizArea.getChildren().addAll(submitBtn, scoreLabel);
        });

        content.getChildren().addAll(courseBox, quizBox, loadQuizBtn, new Separator(), quizArea);
        return scrollWrap(content);
    }

    private RadioButton optionRadio(String text, char option, ToggleGroup group) {
        RadioButton rb = new RadioButton(option + ")  " + text);
        rb.setToggleGroup(group);
        rb.setUserData(option);
        rb.setTextFill(Color.web(NeonTheme.TEXT_MUTED));
        rb.setStyle("-fx-text-fill: " + NeonTheme.TEXT_MUTED + ";");
        return rb;
    }

    // ---------------- Shared helpers ----------------
    private VBox panelContainer(String heading, String subheading) {
        VBox box = new VBox(14);
        box.setPadding(new Insets(30));
        box.getChildren().addAll(NeonTheme.title(heading), NeonTheme.subtitle(subheading));
        return box;
    }

    private ScrollPane scrollWrap(VBox content) {
        content.setMaxWidth(760);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setBackground(new Background(new BackgroundFill(Color.web(NeonTheme.BG_DARK), CornerRadii.EMPTY, Insets.EMPTY)));
        sp.setStyle("-fx-background-color: " + NeonTheme.BG_DARK + "; -fx-background: " + NeonTheme.BG_DARK + ";");
        return sp;
    }
}
