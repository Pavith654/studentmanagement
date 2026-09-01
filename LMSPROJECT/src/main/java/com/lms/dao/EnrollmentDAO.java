package com.lms.dao;

import com.lms.db.DBConnection;
import com.lms.model.Enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public boolean enroll(int studentId, int courseId) {
        String sql = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Enrollment> getByStudent(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.title AS course_title FROM enrollments e " +
                     "JOIN courses c ON e.course_id = c.course_id WHERE e.student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Enrollment e = new Enrollment();
                    e.setEnrollmentId(rs.getInt("enrollment_id"));
                    e.setStudentId(rs.getInt("student_id"));
                    e.setCourseId(rs.getInt("course_id"));
                    e.setCourseTitle(rs.getString("course_title"));
                    e.setStatus(rs.getString("status"));
                    e.setProgress(rs.getInt("progress"));
                    list.add(e);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateProgress(int enrollmentId, int progress) {
        String sql = "UPDATE enrollments SET progress = ?, status = ? WHERE enrollment_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, progress);
            ps.setString(2, progress >= 100 ? "completed" : "active");
            ps.setInt(3, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countEnrollmentsForCourse(int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
