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
        setLayout(new GridBagLayout());

        manageTeachersBtn = new JButton("Manage Teachers & Subjects");
        manualTimetableBtn = new JButton("Create Manual Timetable");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0; gbc.gridy = 0;
        add(manageTeachersBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(manualTimetableBtn, gbc);

        // Button actions
        manageTeachersBtn.addActionListener(e -> {
            new TeacherSubjectFrame();
        });

        manualTimetableBtn.addActionListener(e -> {
            new ManualTimeTableFrame();
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        // Launch main menu
        SwingUtilities.invokeLater(() -> new MainMenuFrame());
    }
}
