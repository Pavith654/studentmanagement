package com.lms.dao;

import com.lms.db.DBConnection;
import com.lms.model.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    public boolean saveResult(int studentId, int quizId, int score) {
        String sql = "INSERT INTO results (student_id, quiz_id, score) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, quizId);
            ps.setInt(3, score);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Result> getByStudent(int studentId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, q.title AS quiz_title FROM results r " +
                     "JOIN quizzes q ON r.quiz_id = q.quiz_id WHERE r.student_id = ? ORDER BY r.attempted_on DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Result r = new Result();
                    r.setResultId(rs.getInt("result_id"));
                    r.setStudentId(rs.getInt("student_id"));
                    r.setQuizId(rs.getInt("quiz_id"));
                    r.setQuizTitle(rs.getString("quiz_title"));
                    r.setScore(rs.getInt("score"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getAverageScorePercent() {
        String sql = "SELECT AVG(r.score * 100.0 / q.total_marks) AS avg_pct " +
                     "FROM results r JOIN quizzes q ON r.quiz_id = q.quiz_id WHERE q.total_marks > 0";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("avg_pct");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
