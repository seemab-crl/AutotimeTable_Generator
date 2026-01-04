package dao;

import db.DBConnection;
import model.Subject;
import model.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {

    public static List<Teacher> getAllTeachers() {
        List<Teacher> list = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            String sql = """
                SELECT t.id, t.name, s.id AS sid, s.name AS sname, s.weekly_lec
                FROM teachers t
                JOIN subjects s ON t.subject_id = s.id
                """;

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Subject subject = new Subject(
                        rs.getInt("sid"),
                        rs.getString("sname"),
                        rs.getInt("weekly_lec")
                );

                list.add(new Teacher(
                        rs.getInt("id"),
                        rs.getString("name"),
                        subject
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Add new teacher
    public static void addTeacher(Teacher t) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "INSERT INTO teachers (name, subject_id) VALUES (?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, t.getName());
            pst.setInt(2, t.getSubject().getId());
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add teacher
    public static void addTeacher(String teacherName, Subject subject) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "INSERT INTO teachers (name, subject_id) VALUES (?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, teacherName);
            pst.setInt(2, subject.getId());
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
