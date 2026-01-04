package gui;

import dao.SubjectDAO;
import dao.TeacherDAO;
import model.Subject;
import model.Teacher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TeacherSubjectFrame extends JFrame {

    private JTable table;
    private JButton addSubjectBtn;
    private JButton addTeacherBtn;

    public TeacherSubjectFrame() {
        setTitle("Manage Teachers & Subjects");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Table to display teachers
        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel();
        addSubjectBtn = new JButton("Add Subject");
        addTeacherBtn = new JButton("Add Teacher");
        bottomPanel.add(addSubjectBtn);
        bottomPanel.add(addTeacherBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load existing data
        loadTable();

        // Button actions
        addSubjectBtn.addActionListener(e -> addSubject());
        addTeacherBtn.addActionListener(e -> addTeacher());

        setVisible(true);
    }

    private void loadTable() {
        List<Teacher> teachers = TeacherDAO.getAllTeachers();

        String[] columns = {"Teacher Name", "Subject"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Teacher t : teachers) {
            model.addRow(new Object[]{t.getName(), t.getSubject().getName()});
        }

        table.setModel(model);
    }

    private void addSubject() {
        String name = JOptionPane.showInputDialog(this, "Enter Subject Name:");
        if (name != null && !name.trim().isEmpty()) {
            // Add to DB
            SubjectDAO.addSubject(name.trim());
            JOptionPane.showMessageDialog(this, "Subject added successfully!");
        }
    }

    private void addTeacher() {
        List<Subject> subjects = SubjectDAO.getAllSubjects();
        if (subjects.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add a subject first!");
            return;
        }

        String teacherName = JOptionPane.showInputDialog(this, "Enter Teacher Name:");
        if (teacherName == null || teacherName.trim().isEmpty()) {
            return;
        }

        // Choose subject
        String[] subjectNames = subjects.stream().map(Subject::getName).toArray(String[]::new);
        String selectedSubject = (String) JOptionPane.showInputDialog(
                this,
                "Select Subject for Teacher:",
                "Assign Subject",
                JOptionPane.QUESTION_MESSAGE,
                null,
                subjectNames,
                subjectNames[0]
        );

        if (selectedSubject != null) {
            Subject subject = SubjectDAO.getSubjectByName(selectedSubject);
            TeacherDAO.addTeacher(teacherName.trim(), subject);
            JOptionPane.showMessageDialog(this, "Teacher added successfully!");
        }

        // Reload table
        loadTable();
    }
}
