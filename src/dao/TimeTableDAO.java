package dao;

import db.DBConnection;
import model.TimeTableEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class TimeTableDAO {

    // Save generated timetable into DB
    public static void saveTimeTable(List<TimeTableEntry> timetable) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) {
                System.out.println("DB connection failed!");
                return;
            }

            // Clear old timetable first (optional)
            con.createStatement().execute("DELETE FROM timetable");

            String sql = "INSERT INTO timetable (day, lec, subject, teacher) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            for (TimeTableEntry entry : timetable) {
                ps.setString(1, entry.getDay());
                ps.setInt(2, entry.getPeriod());
                ps.setString(3, entry.getSubject());
                ps.setString(4, entry.getTeacher());
                ps.addBatch();
            }

            ps.executeBatch();
            System.out.println("Timetable saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static List<TimeTableEntry> getTimeTableFromDB() {
        List<TimeTableEntry> list = new ArrayList<>();
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM timetable ORDER BY id";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                list.add(new TimeTableEntry(
                        rs.getString("day"),
                        rs.getInt("lec"),
                        rs.getString("subject"),
                        rs.getString("teacher")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
