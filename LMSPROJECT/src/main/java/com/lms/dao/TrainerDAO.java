package com.lms.dao;

import com.lms.db.DBConnection;
import com.lms.model.Trainer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainerDAO {

    public boolean add(Trainer t) {
        String sql = "INSERT INTO trainers (name, email, expertise, added_by) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getEmail());
            ps.setString(3, t.getExpertise());
            ps.setInt(4, t.getAddedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Trainer> getAll() {
        List<Trainer> list = new ArrayList<>();
        String sql = "SELECT * FROM trainers ORDER BY trainer_id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Trainer t = new Trainer();
                t.setTrainerId(rs.getInt("trainer_id"));
                t.setName(rs.getString("name"));
                t.setEmail(rs.getString("email"));
                t.setExpertise(rs.getString("expertise"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
