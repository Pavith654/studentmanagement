package com.lms.dao;

import com.lms.db.DBConnection;
import com.lms.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public boolean add(Course c) {
        String sql = "INSERT INTO courses (title, description, trainer_id, duration, created_by) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getTitle());
            ps.setString(2, c.getDescription());
            ps.setInt(3, c.getTrainerId());
            ps.setString(4, c.getDuration());
            ps.setInt(5, c.getCreatedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Course> getAll() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, t.name AS trainer_name FROM courses c " +
                     "LEFT JOIN trainers t ON c.trainer_id = t.trainer_id ORDER BY c.course_id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Course map(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setTitle(rs.getString("title"));
        c.setDescription(rs.getString("description"));
        c.setTrainerId(rs.getInt("trainer_id"));
        c.setTrainerName(rs.getString("trainer_name"));
        c.setDuration(rs.getString("duration"));
        c.setCreatedBy(rs.getInt("created_by"));
        return c;
    }
}
