package com.lms.dao;

import com.lms.db.DBConnection;
import com.lms.model.Material;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {

    public boolean upload(Material m) {
        String sql = "INSERT INTO materials (course_id, title, file_path, file_type, uploaded_by) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, m.getCourseId());
            ps.setString(2, m.getTitle());
            ps.setString(3, m.getFilePath());
            ps.setString(4, m.getFileType());
            ps.setInt(5, 1); // uploaded_by admin id - simplified
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Material> getByCourse(int courseId) {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT * FROM materials WHERE course_id = ? ORDER BY material_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Material m = new Material();
                    m.setMaterialId(rs.getInt("material_id"));
                    m.setCourseId(rs.getInt("course_id"));
                    m.setTitle(rs.getString("title"));
                    m.setFilePath(rs.getString("file_path"));
                    m.setFileType(rs.getString("file_type"));
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
