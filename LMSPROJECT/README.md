# LMS — Futuristic Neon JavaFX Desktop App

A pure-Java Learning Management System desktop application. The UI (login,
dashboards, forms, tables) is built entirely with **JavaFX**, styled in Java
code with a dark, neon-blue "futuristic" theme (glassmorphism cards, glow
effects, gradient buttons) — there are no HTML/CSS files anywhere in the
project.

## Tech Stack
- Java 17
- JavaFX 21 (UI)
- MySQL 8 + JDBC (data)
- jBCrypt (password hashing)
- Maven (build)

## Tables Implemented
`admin`, `students`, `trainers`, `courses`, `enrollments`, `materials`,
`quizzes` (+ `quiz_questions`), `results` — see `schema.sql`.

## Features
**Admin:** Login · Add Courses · Add Trainers · Manage Students ·
Upload Materials · View Reports (KPIs + enrollments-per-course)

**Student:** Register · Login · Enroll Course · View Course ·
Download Notes · View Progress · Take Quiz (auto-graded)

---

## Setup — IntelliJ IDEA 2025.3.2

### 1. Install prerequisites
- JDK 17 or newer (**File ▸ Project Structure ▸ SDKs** — add one if missing)
- MySQL Server 8 running locally (or update `application.properties` to
  point at your own instance)

### 2. Create the database
Open a MySQL client (Workbench, DBeaver, or the `mysql` CLI) and run the
whole `schema.sql` file included in this project. It creates the `lms_db`
database, all 8 tables, and one seed admin account:

```
email:    admin@lms.com
password: admin123
```

### 3. Open the project in IntelliJ
1. `File ▸ Open...` → select the unzipped `lms-javafx` folder.
2. IntelliJ will detect the `pom.xml` and prompt to **load as a Maven
   project** — click **Load**. Wait for it to download dependencies
   (JavaFX, MySQL connector, jBCrypt) — this needs an internet connection
   the first time.
3. In **File ▸ Project Structure ▸ Project**, set the Project SDK to your
   JDK 17 and Language level to 17.

### 4. Configure the database connection
Open `src/main/resources/application.properties` and set your MySQL
username/password:

```properties
db.url=jdbc:mysql://localhost:3306/lms_db?useSSL=false&serverTimezone=UTC
db.username=root
db.password=your_mysql_password
```

### 5. Run it
- Locate `src/main/java/com/lms/Main.java`
- Right-click it → **Run 'Main.main()'**
- IntelliJ automatically picks up the JavaFX module dependencies declared
  in `pom.xml`, so no extra VM options are usually required. If you get a
  `JavaFX runtime components are missing` error, add this VM option to the
  run configuration (**Run ▸ Edit Configurations ▸ VM options**), pointing
  `--module-path` at your local JavaFX SDK lib folder (only needed on some
  JDK distributions that don't bundle JavaFX):
  ```
  --module-path "<path-to-javafx-sdk>/lib" --add-modules javafx.controls,javafx.fxml
  ```

Alternatively, run from the built-in Maven tool window: **Maven ▸
lms-javafx ▸ Plugins ▸ javafx ▸ javafx:run**.

### 6. (Optional) Build a runnable jar
```
mvn clean package
java -jar target/lms-javafx-1.0.0.jar
```

---

## Project Structure
```
lms-javafx/
 ├── pom.xml
 ├── schema.sql
 ├── src/main/java/com/lms/
 │    ├── Main.java                 (JavaFX entry point)
 │    ├── db/DBConnection.java      (JDBC connection helper)
 │    ├── model/                    (8 entity classes: Admin, Student, Trainer,
 │    │                              Course, Enrollment, Material, Quiz,
 │    │                              Question, Result)
 │    ├── dao/                      (JDBC data-access classes, one per table)
 │    └── ui/
 │         ├── NeonTheme.java       (all neon styling — colors, glow effects,
 │         │                         gradient buttons, glass cards — pure Java)
 │         ├── LoginScreen.java     (role-based Admin/Student login)
 │         ├── RegisterScreen.java  (student sign-up)
 │         ├── AdminDashboard.java  (sidebar + 6 admin panels)
 │         └── StudentDashboard.java(sidebar + 5 student panels)
 └── uploads/                       (course material files land here at runtime)
```

## Notes
- All styling (gradients, glow/DropShadow effects, glassmorphism panels,
  neon borders) is written directly in Java using JavaFX's `Region.setStyle()`
  and effect classes (`DropShadow`, `Glow`, `BoxBlur`) — no separate
  stylesheet or markup file exists in the project.
- Quiz scoring is automatic: each question is worth `total_marks / question
  count`, and the score is written to the `results` table on submit.
- "Download Notes" opens the uploaded file with the OS's default viewer via
  `java.awt.Desktop`.
- Passwords are hashed with bcrypt before being stored — never stored in
  plain text.
