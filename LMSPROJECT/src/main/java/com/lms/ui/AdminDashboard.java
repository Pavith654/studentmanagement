package com.lms.ui;

import com.lms.dao.*;
import com.lms.model.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class AdminDashboard {

    private final Stage stage;
    private final Admin admin;
    private final BorderPane layout = new BorderPane();
    private final ToggleGroup navGroup = new ToggleGroup();

    private final TrainerDAO trainerDAO = new TrainerDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final MaterialDAO materialDAO = new MaterialDAO();
    private final AdminDAO adminDAO = new AdminDAO();

    public AdminDashboard(Stage stage, Admin admin) {
        this.stage = stage;
        this.admin = admin;
    }

    public void show() {
        layout.setLeft(buildSidebar());
        layout.setCenter(buildOverviewPanel());
        layout.setBackground(new Background(new BackgroundFill(Color.web(NeonTheme.BG_DARK), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(layout, 1200, 760);
        stage.setScene(scene);
        stage.setTitle("LMS - Admin Dashboard");
        stage.setMaximized(true);
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(24, 14, 24, 14));
        sidebar.setPrefWidth(230);
        sidebar.setBackground(new Background(new BackgroundFill(Color.web(NeonTheme.BG_PANEL), CornerRadii.EMPTY, Insets.EMPTY)));
        sidebar.setBorder(new Border(new BorderStroke(Color.web(NeonTheme.NEON_BLUE, 0.25),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));

        Label logo = new Label("⚡ LMS ADMIN");
        logo.setFont(Font.font("Segoe UI Semibold", FontWeight.BOLD, 18));
        logo.setTextFill(Color.web(NeonTheme.NEON_BLUE));
        logo.setPadding(new Insets(0, 0, 10, 10));

        Label welcome = new Label("Welcome, " + admin.getName());
        welcome.setTextFill(Color.web(NeonTheme.TEXT_MUTED));
        welcome.setFont(Font.font("Segoe UI", 11));
        welcome.setPadding(new Insets(0, 0, 20, 10));
        welcome.setWrapText(true);

        ToggleButton overview = NeonTheme.navButton("📊  Overview", navGroup);
        ToggleButton addCourse = NeonTheme.navButton("📚  Add Courses", navGroup);
        ToggleButton addTrainer = NeonTheme.navButton("🧑‍🏫  Add Trainers", navGroup);
        ToggleButton manageStudents = NeonTheme.navButton("🎓  Manage Students", navGroup);
        ToggleButton uploadMaterials = NeonTheme.navButton("📁  Upload Materials", navGroup);
        ToggleButton manageQuizzes = NeonTheme.navButton("❓  Manage Quizzes", navGroup);
        ToggleButton viewReports = NeonTheme.navButton("📈  View Reports", navGroup);
        overview.setSelected(true);

        overview.setOnAction(e -> layout.setCenter(buildOverviewPanel()));
        addCourse.setOnAction(e -> layout.setCenter(buildAddCoursePanel()));
        addTrainer.setOnAction(e -> layout.setCenter(buildAddTrainerPanel()));
        manageStudents.setOnAction(e -> layout.setCenter(buildManageStudentsPanel()));
        uploadMaterials.setOnAction(e -> layout.setCenter(buildUploadMaterialsPanel()));
        manageQuizzes.setOnAction(e -> layout.setCenter(buildManageQuizzesPanel()));
        viewReports.setOnAction(e -> layout.setCenter(buildReportsPanel()));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logout = NeonTheme.dangerButton("⎋  Logout");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setOnAction(e -> new LoginScreen(stage).show());

        sidebar.getChildren().addAll(logo, welcome, overview, addCourse, addTrainer,
                manageStudents, uploadMaterials, manageQuizzes, viewReports, spacer, logout);
        return sidebar;
    }

    // ---------------- OVERVIEW ----------------
    private ScrollPane buildOverviewPanel() {
        VBox content = panelContainer("Dashboard Overview", "Live snapshot of the LMS system");

        HBox kpiRow = new HBox(18);
        kpiRow.getChildren().addAll(
                NeonTheme.kpiCard(String.valueOf(adminDAO.countStudents()), "Total Students", NeonTheme.NEON_BLUE),
                NeonTheme.kpiCard(String.valueOf(adminDAO.countCourses()), "Total Courses", NeonTheme.NEON_PURPLE),
                NeonTheme.kpiCard(String.valueOf(adminDAO.countTrainers()), "Total Trainers", NeonTheme.NEON_CYAN),
                NeonTheme.kpiCard(String.valueOf(adminDAO.countEnrollments()), "Total Enrollments", NeonTheme.SUCCESS)
        );

        content.getChildren().add(kpiRow);
        return scrollWrap(content);
    }

    // ---------------- ADD COURSE ----------------
    private ScrollPane buildAddCoursePanel() {
        VBox content = panelContainer("Add Course", "Create a new course and assign a trainer");

        TextField titleField = NeonTheme.textField("Course title");
        TextArea descField = NeonTheme.textArea("Course description");
        ComboBox<Trainer> trainerBox = new ComboBox<>(FXCollections.observableArrayList(trainerDAO.getAll()));
        trainerBox.setPromptText("Select trainer");
        trainerBox.setMaxWidth(Double.MAX_VALUE);
        TextField durationField = NeonTheme.textField("Duration (e.g. 6 weeks)");

        Label msg = NeonTheme.successLabel();
        Label err = NeonTheme.errorLabel();

        Button submit = NeonTheme.primaryButton("Create Course");
        submit.setOnAction(e -> {
            msg.setText(""); err.setText("");
            if (titleField.getText().isBlank() || trainerBox.getValue() == null) {
                err.setText("Title and trainer are required.");
                return;
            }
            Course c = new Course(0, titleField.getText().trim(), descField.getText().trim(),
                    trainerBox.getValue().getTrainerId(), durationField.getText().trim());
            c.setCreatedBy(admin.getAdminId());
            boolean ok = courseDAO.add(c);
            if (ok) {
                msg.setText("Course created successfully.");
                titleField.clear(); descField.clear(); durationField.clear(); trainerBox.setValue(null);
            } else {
                err.setText("Failed to create course. Check DB connection.");
            }
        });

        content.getChildren().addAll(
                fieldLabel("Title"), titleField,
                fieldLabel("Description"), descField,
                fieldLabel("Trainer"), trainerBox,
                fieldLabel("Duration"), durationField,
                submit, msg, err,
                new Separator(),
                NeonTheme.sectionHeader("Existing Courses"),
                buildCourseTable()
        );
        return scrollWrap(content);
    }

    private TableView<Course> buildCourseTable() {
        TableView<Course> table = new TableView<>();
        NeonTheme.styleTable(table);
        TableColumn<Course, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Course, String> trainerCol = new TableColumn<>("Trainer");
        trainerCol.setCellValueFactory(new PropertyValueFactory<>("trainerName"));
        TableColumn<Course, String> durationCol = new TableColumn<>("Duration");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        table.getColumns().addAll(titleCol, trainerCol, durationCol);
        table.setItems(FXCollections.observableArrayList(courseDAO.getAll()));
        table.setPrefHeight(240);
        return table;
    }

    // ---------------- ADD TRAINER ----------------
    private ScrollPane buildAddTrainerPanel() {
        VBox content = panelContainer("Add Trainer", "Register a new trainer profile");

        TextField nameField = NeonTheme.textField("Trainer full name");
        TextField emailField = NeonTheme.textField("Email address");
        TextField expertiseField = NeonTheme.textField("Expertise (e.g. Data Science)");

        Label msg = NeonTheme.successLabel();
        Label err = NeonTheme.errorLabel();

        Button submit = NeonTheme.primaryButton("Add Trainer");
        submit.setOnAction(e -> {
            msg.setText(""); err.setText("");
            if (nameField.getText().isBlank() || emailField.getText().isBlank()) {
                err.setText("Name and email are required.");
                return;
            }
            Trainer t = new Trainer(0, nameField.getText().trim(), emailField.getText().trim(), expertiseField.getText().trim());
            t.setAddedBy(admin.getAdminId());
            boolean ok = trainerDAO.add(t);
            if (ok) {
                msg.setText("Trainer added successfully.");
                nameField.clear(); emailField.clear(); expertiseField.clear();
            } else {
                err.setText("Failed to add trainer (email may already exist).");
            }
        });

        ListView<Trainer> trainerList = new ListView<>(FXCollections.observableArrayList(trainerDAO.getAll()));
        trainerList.setPrefHeight(220);
        trainerList.setStyle("-fx-background-color: #0a1024; -fx-control-inner-background:#0a1024;");

        content.getChildren().addAll(
                fieldLabel("Name"), nameField,
                fieldLabel("Email"), emailField,
                fieldLabel("Expertise"), expertiseField,
                submit, msg, err,
                new Separator(),
                NeonTheme.sectionHeader("Existing Trainers"),
                trainerList
        );
        return scrollWrap(content);
    }

    // ---------------- MANAGE STUDENTS ----------------
    private ScrollPane buildManageStudentsPanel() {
        VBox content = panelContainer("Manage Students", "View and manage registered students");

        TableView<Student> table = new TableView<>();
        NeonTheme.styleTable(table);
        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<Student, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        table.getColumns().addAll(nameCol, emailCol, phoneCol);
        table.setItems(FXCollections.observableArrayList(studentDAO.getAll()));
        table.setPrefHeight(360);

        Button refresh = NeonTheme.secondaryButton("Refresh List");
        Button removeSelected = NeonTheme.dangerButton("Remove Selected Student");
        Label msg = NeonTheme.successLabel();

        refresh.setOnAction(e -> table.setItems(FXCollections.observableArrayList(studentDAO.getAll())));
        removeSelected.setOnAction(e -> {
            Student sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                studentDAO.delete(sel.getStudentId());
                table.setItems(FXCollections.observableArrayList(studentDAO.getAll()));
                msg.setText("Removed " + sel.getName() + ".");
            }
        });

        HBox actions = new HBox(10, refresh, removeSelected);
        content.getChildren().addAll(table, actions, msg);
        return scrollWrap(content);
    }

    // ---------------- UPLOAD MATERIALS ----------------
    private ScrollPane buildUploadMaterialsPanel() {
        VBox content = panelContainer("Upload Materials", "Attach study material to a course");

        ComboBox<Course> courseBox = new ComboBox<>(FXCollections.observableArrayList(courseDAO.getAll()));
        courseBox.setPromptText("Select course");
        courseBox.setMaxWidth(Double.MAX_VALUE);

        TextField titleField = NeonTheme.textField("Material title");
        Label filePathLabel = NeonTheme.subtitle("No file selected");
        Button chooseFileBtn = NeonTheme.secondaryButton("Choose File");

        final File[] selectedFile = new File[1];
        chooseFileBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select course material");
            File f = fc.showOpenDialog(stage);
            if (f != null) {
                selectedFile[0] = f;
                filePathLabel.setText(f.getName());
            }
        });

        Label msg = NeonTheme.successLabel();
        Label err = NeonTheme.errorLabel();

        Button uploadBtn = NeonTheme.primaryButton("Upload Material");
        uploadBtn.setOnAction(e -> {
            msg.setText(""); err.setText("");
            if (courseBox.getValue() == null || titleField.getText().isBlank() || selectedFile[0] == null) {
                err.setText("Select a course, title and file.");
                return;
            }
            try {
                Path uploadsDir = Path.of("uploads");
                Files.createDirectories(uploadsDir);
                Path dest = uploadsDir.resolve(System.currentTimeMillis() + "_" + selectedFile[0].getName());
                Files.copy(selectedFile[0].toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

                String ext = "";
                int dot = selectedFile[0].getName().lastIndexOf('.');
                if (dot > 0) ext = selectedFile[0].getName().substring(dot + 1).toUpperCase();

                Material m = new Material(0, courseBox.getValue().getCourseId(), titleField.getText().trim(),
                        dest.toString(), ext);
                boolean ok = materialDAO.upload(m);
                if (ok) {
                    msg.setText("Material uploaded successfully.");
                    titleField.clear(); filePathLabel.setText("No file selected"); selectedFile[0] = null;
                } else {
                    err.setText("Failed to save material record.");
                }
            } catch (IOException ex) {
                err.setText("File copy failed: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(
                fieldLabel("Course"), courseBox,
                fieldLabel("Title"), titleField,
                new HBox(10, chooseFileBtn, filePathLabel),
                uploadBtn, msg, err
        );
        return scrollWrap(content);
    }

    // ---------------- MANAGE QUIZZES ----------------
    private ScrollPane buildManageQuizzesPanel() {
        VBox content = panelContainer("Manage Quizzes", "Create a quiz and add questions for a course");
        QuizDAO quizDAO = new QuizDAO();

        ComboBox<Course> courseBox = new ComboBox<>(FXCollections.observableArrayList(courseDAO.getAll()));
        courseBox.setPromptText("Select course");
        courseBox.setMaxWidth(Double.MAX_VALUE);
        TextField quizTitleField = NeonTheme.textField("Quiz title (e.g. Module 1 Quiz)");
        TextField totalMarksField = NeonTheme.textField("Total marks (e.g. 50)");

        Label quizMsg = NeonTheme.successLabel();
        Label quizErr = NeonTheme.errorLabel();
        Button createQuizBtn = NeonTheme.primaryButton("Create Quiz");

        Label hint = NeonTheme.subtitle("Create the quiz above first, then add its questions here.");

        TextArea questionText = NeonTheme.textArea("Question text");
        TextField optA = NeonTheme.textField("Option A");
        TextField optB = NeonTheme.textField("Option B");
        TextField optC = NeonTheme.textField("Option C");
        TextField optD = NeonTheme.textField("Option D");
        ComboBox<String> correctBox = new ComboBox<>(FXCollections.observableArrayList("A", "B", "C", "D"));
        correctBox.setPromptText("Correct option");
        Label questionMsg = NeonTheme.successLabel();
        Button addQuestionBtn = NeonTheme.primaryButton("Add Question to Quiz");

        VBox questionForm = new VBox(10, questionText, optA, optB, optC, optD, correctBox, addQuestionBtn, questionMsg);
        questionForm.setDisable(true);

        final Quiz[] activeQuiz = new Quiz[1];

        createQuizBtn.setOnAction(e -> {
            quizMsg.setText(""); quizErr.setText("");
            if (courseBox.getValue() == null || quizTitleField.getText().isBlank() || totalMarksField.getText().isBlank()) {
                quizErr.setText("Course, title and total marks are required.");
                return;
            }
            int marks;
            try {
                marks = Integer.parseInt(totalMarksField.getText().trim());
            } catch (NumberFormatException ex) {
                quizErr.setText("Total marks must be a number.");
                return;
            }
            Quiz q = new Quiz(0, courseBox.getValue().getCourseId(), quizTitleField.getText().trim(), marks);
            boolean ok = quizDAO.addQuiz(q);
            if (ok) {
                activeQuiz[0] = q;
                quizMsg.setText("Quiz \"" + q.getTitle() + "\" created. Now add its questions below.");
                questionForm.setDisable(false);
            } else {
                quizErr.setText("Failed to create quiz.");
            }
        });

        addQuestionBtn.setOnAction(e -> {
            questionMsg.setText("");
            if (activeQuiz[0] == null) return;
            if (questionText.getText().isBlank() || correctBox.getValue() == null) {
                questionMsg.setText("Question text and correct option are required.");
                return;
            }
            Question q = new Question();
            q.setQuizId(activeQuiz[0].getQuizId());
            q.setQuestionText(questionText.getText().trim());
            q.setOptionA(optA.getText().trim());
            q.setOptionB(optB.getText().trim());
            q.setOptionC(optC.getText().trim());
            q.setOptionD(optD.getText().trim());
            q.setCorrectOption(correctBox.getValue().charAt(0));
            boolean ok = quizDAO.addQuestion(q);
            if (ok) {
                questionMsg.setText("Question added.");
                questionText.clear(); optA.clear(); optB.clear(); optC.clear(); optD.clear(); correctBox.setValue(null);
            } else {
                questionMsg.setText("Failed to add question.");
            }
        });

        content.getChildren().addAll(
                fieldLabel("Course"), courseBox,
                fieldLabel("Quiz Title"), quizTitleField,
                fieldLabel("Total Marks"), totalMarksField,
                createQuizBtn, quizMsg, quizErr,
                new Separator(),
                NeonTheme.sectionHeader("Add Questions"),
                hint, questionForm
        );
        return scrollWrap(content);
    }

    // ---------------- REPORTS ----------------
    private ScrollPane buildReportsPanel() {
        VBox content = panelContainer("Reports", "System-wide analytics snapshot");

        HBox kpiRow = new HBox(18);
        kpiRow.getChildren().addAll(
                NeonTheme.kpiCard(String.valueOf(adminDAO.countStudents()), "Students", NeonTheme.NEON_BLUE),
                NeonTheme.kpiCard(String.valueOf(adminDAO.countCourses()), "Courses", NeonTheme.NEON_PURPLE),
                NeonTheme.kpiCard(String.valueOf(adminDAO.countEnrollments()), "Enrollments", NeonTheme.NEON_CYAN),
                NeonTheme.kpiCard(String.format("%.1f%%", new ResultDAO().getAverageScorePercent()), "Avg Quiz Score", NeonTheme.SUCCESS)
        );

        TableView<Course> table = new TableView<>();
        NeonTheme.styleTable(table);
        TableColumn<Course, String> titleCol = new TableColumn<>("Course");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Course, Number> enrollCol = new TableColumn<>("Enrollments");
        List<Course> courses = courseDAO.getAll();
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
        enrollCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(enrollmentDAO.countEnrollmentsForCourse(data.getValue().getCourseId())));
        table.getColumns().addAll(titleCol, enrollCol);
        table.setItems(FXCollections.observableArrayList(courses));
        table.setPrefHeight(260);

        content.getChildren().addAll(kpiRow, new Separator(), NeonTheme.sectionHeader("Enrollments per Course"), table);
        return scrollWrap(content);
    }

    // ---------------- Shared helpers ----------------
    private VBox panelContainer(String heading, String subheading) {
        VBox box = new VBox(14);
        box.setPadding(new Insets(30));
        box.getChildren().addAll(NeonTheme.title(heading), NeonTheme.subtitle(subheading));
        return box;
    }

    private Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.web(NeonTheme.TEXT_MUTED));
        l.setFont(Font.font("Segoe UI", 11));
        return l;
    }

    private ScrollPane scrollWrap(VBox content) {
        content.setMaxWidth(760);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: " + NeonTheme.BG_DARK + "; -fx-background: " + NeonTheme.BG_DARK + ";");
        sp.setBackground(new Background(new BackgroundFill(Color.web(NeonTheme.BG_DARK), CornerRadii.EMPTY, Insets.EMPTY)));
        return sp;
    }
}
