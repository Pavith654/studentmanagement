package com.lms.dao;

import com.lms.db.DBConnection;
import com.lms.model.Admin;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class AdminDAO {

    /** Returns the Admin if email+password match, otherwise null. */
    public Admin login(String email, String rawPassword) {
        String sql = "SELECT * FROM admin WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password");
                    if (BCrypt.checkpw(rawPassword, hash)) {
                        return new Admin(rs.getInt("admin_id"), rs.getString("name"),
                                rs.getString("email"), hash, rs.getString("role"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int countStudents() { return countRows("students"); }
    public int countCourses() { return countRows("courses"); }
    public int countTrainers() { return countRows("trainers"); }
    public int countEnrollments() { return countRows("enrollments"); }

    private int countRows(String table) {
        String sql = "SELECT COUNT(*) FROM " + table;
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
