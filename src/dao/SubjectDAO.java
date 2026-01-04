package dao;

import db.DBConnection;
import model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    public static List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM subjects");

            while (rs.next()) {
                list.add(new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("weekly_lec") // required 3rd argument
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Add new subject (weekly_lec default = 0)
    public static void addSubject(String name) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "INSERT INTO subjects (name, weekly_lec) VALUES (?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);
            pst.setInt(2, 0); // default weekly_lec = 0
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get subject by name
    public static Subject getSubjectByName(String name) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM subjects WHERE name=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("weekly_lec")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
