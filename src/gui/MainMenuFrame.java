package gui;

import javax.swing.*;
import java.awt.*;

public class MainMenuFrame extends JFrame {

    private JButton manageTeachersBtn;
    private JButton manualTimetableBtn;

    public MainMenuFrame() {
        setTitle("Time Table Management System");
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Use the robust BackgroundPanel
        BackgroundPanel bgPanel = new BackgroundPanel("C:/Users/Fayyaz/Desktop/bg.jpeg"); // path to your image
        bgPanel.setLayout(new GridBagLayout());

        manageTeachersBtn = new JButton("Manage Teachers & Subjects");
        manualTimetableBtn = new JButton("Create Manual Timetable");

        // Optional: style buttons
        manageTeachersBtn.setFocusPainted(false);
        manualTimetableBtn.setFocusPainted(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;

        bgPanel.add(manageTeachersBtn, gbc);

        gbc.gridy = 1;
        bgPanel.add(manualTimetableBtn, gbc);

        // Button actions
        manageTeachersBtn.addActionListener(e -> new TeacherSubjectFrame());
        manualTimetableBtn.addActionListener(e -> new ManualTimeTableFrame());

        setContentPane(bgPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenuFrame::new);
    }
}
