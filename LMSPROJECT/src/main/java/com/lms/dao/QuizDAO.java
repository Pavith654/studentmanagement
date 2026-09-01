package com.lms.dao;

import com.lms.db.DBConnection;
import com.lms.model.Question;
import com.lms.model.Quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

    public boolean addQuiz(Quiz q) {
        String sql = "INSERT INTO quizzes (course_id, title, total_marks, created_by) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, q.getCourseId());
            ps.setString(2, q.getTitle());
            ps.setInt(3, q.getTotalMarks());
            ps.setInt(4, 1);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) q.setQuizId(keys.getInt(1));
                }
            }
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addQuestion(Question qn) {
        String sql = "INSERT INTO quiz_questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, qn.getQuizId());
            ps.setString(2, qn.getQuestionText());
            ps.setString(3, qn.getOptionA());
            ps.setString(4, qn.getOptionB());
            ps.setString(5, qn.getOptionC());
            ps.setString(6, qn.getOptionD());
            ps.setString(7, String.valueOf(qn.getCorrectOption()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Quiz> getByCourse(int courseId) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT * FROM quizzes WHERE course_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Quiz(rs.getInt("quiz_id"), rs.getInt("course_id"),
                            rs.getString("title"), rs.getInt("total_marks")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Question> getQuestions(int quizId) {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM quiz_questions WHERE quiz_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Question qn = new Question();
                    qn.setQuestionId(rs.getInt("question_id"));
                    qn.setQuizId(rs.getInt("quiz_id"));
                    qn.setQuestionText(rs.getString("question_text"));
                    qn.setOptionA(rs.getString("option_a"));
                    qn.setOptionB(rs.getString("option_b"));
                    qn.setOptionC(rs.getString("option_c"));
                    qn.setOptionD(rs.getString("option_d"));
                    String correct = rs.getString("correct_option");
                    qn.setCorrectOption(correct != null && !correct.isEmpty() ? correct.charAt(0) : ' ');
                    list.add(qn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
