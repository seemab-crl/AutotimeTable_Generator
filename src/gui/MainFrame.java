package gui;

import dao.TeacherDAO;
import dao.TimeTableDAO;
import model.Subject;
import model.Teacher;
import model.TimeTableEntry;
import service.TimeTableGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {

    private JButton generateBtn;
    private JButton saveBtn;
    private JButton addRowBtn;
    private JButton deleteRowBtn;
    private JTable table;

    private List<Teacher> teachers;
    private List<Subject> subjects;
    private final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    // Map subjects to colors for color-coded timetable
    private Map<String, Color> subjectColors = new HashMap<>();

    public MainFrame() {
        setTitle("Auto & Manual Time Table Generator");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top panel with buttons
        JPanel topPanel = new JPanel();
        generateBtn = new JButton("Generate Timetable");
        saveBtn = new JButton("Save Changes");
        addRowBtn = new JButton("Add Row");
        deleteRowBtn = new JButton("Delete Row");
        topPanel.add(generateBtn);
        topPanel.add(saveBtn);
        topPanel.add(addRowBtn);
        topPanel.add(deleteRowBtn);
        add(topPanel, BorderLayout.NORTH);

        // Table to display timetable
        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Load teachers & subjects
        teachers = TeacherDAO.getAllTeachers();
        subjects = getUniqueSubjects(teachers);

        // Initialize subject colors
        initializeSubjectColors();

        // Button actions
        generateBtn.addActionListener(e -> generateTimetable());
        saveBtn.addActionListener(e -> saveEditedTimetable());
        addRowBtn.addActionListener(e -> addRow());
        deleteRowBtn.addActionListener(e -> deleteRow());

        // Load existing timetable if present
        loadTimetable();

        setVisible(true);
    }

    // Helper: Get unique subjects from teachers
    private List<Subject> getUniqueSubjects(List<Teacher> teachers) {
        Map<String, Subject> map = new LinkedHashMap<>();
        for (Teacher t : teachers) {
            map.put(t.getSubject().getName(), t.getSubject());
        }
        return new ArrayList<>(map.values());
    }

    // Assign colors to subjects
    private void initializeSubjectColors() {
        Color[] colors = {Color.CYAN, Color.PINK, Color.ORANGE, Color.LIGHT_GRAY, Color.YELLOW, Color.MAGENTA};
        for (int i = 0; i < subjects.size(); i++) {
            subjectColors.put(subjects.get(i).getName(), colors[i % colors.length]);
        }
    }

    // Auto-generate timetable
    private void generateTimetable() {
        if (teachers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No teachers found in DB!");
            return;
        }

        List<TimeTableEntry> timetable = TimeTableGenerator.generate(teachers, 6); // 6 periods/day
        TimeTableDAO.saveTimeTable(timetable);
        JOptionPane.showMessageDialog(this, "Timetable generated and saved!");
        loadTimetable();
    }

    // Load timetable into JTable
    private void loadTimetable() {
        List<TimeTableEntry> timetable = TimeTableDAO.getTimeTableFromDB();

        String[] columns = {"Day", "Period", "Subject", "Teacher"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // All columns editable for manual entry
                return true;
            }
        };

        for (TimeTableEntry t : timetable) {
            model.addRow(new Object[]{t.getDay(), t.getPeriod(), t.getSubject(), t.getTeacher()});
        }

        table.setModel(model);

        // ComboBox editor for Day
        TableColumn dayCol = table.getColumnModel().getColumn(0);
        JComboBox<String> dayCombo = new JComboBox<>();
        for (String d : daysOfWeek) dayCombo.addItem(d);
        dayCol.setCellEditor(new DefaultCellEditor(dayCombo));

        // ComboBox editor for Subject
        TableColumn subjectCol = table.getColumnModel().getColumn(2);
        JComboBox<String> subjectCombo = new JComboBox<>();
        for (Subject s : subjects) subjectCombo.addItem(s.getName());
        subjectCol.setCellEditor(new DefaultCellEditor(subjectCombo));
        subjectCol.setCellRenderer(new SubjectCellRenderer());

        // ComboBox editor for Teacher
        TableColumn teacherCol = table.getColumnModel().getColumn(3);
        JComboBox<String> teacherCombo = new JComboBox<>();
        for (Teacher t : teachers) teacherCombo.addItem(t.getName());
        teacherCol.setCellEditor(new DefaultCellEditor(teacherCombo));
        teacherCol.setCellRenderer(new TeacherCellRenderer());
    }

    // Add a new empty row
    private void addRow() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{"Monday", 1, subjects.get(0).getName(), teachers.get(0).getName()});
    }

    // Delete selected row
    private void deleteRow() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Select a row to delete!");
        }
    }

    // Save edited timetable with validation
    private void saveEditedTimetable() {

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rows = model.getRowCount();

        // Key = day + "-" + period
        Set<String> dayPeriodSet = new HashSet<>();

        // Key = day + "-" + period + "-" + teacher
        Set<String> teacherSlotSet = new HashSet<>();

        List<TimeTableEntry> newTimetable = new ArrayList<>();

        for (int i = 0; i < rows; i++) {

            String day = model.getValueAt(i, 0).toString();
            int period = Integer.parseInt(model.getValueAt(i, 1).toString());
            String subject = model.getValueAt(i, 2).toString();
            String teacher = model.getValueAt(i, 3).toString();

            // ✅ 1. SAME DAY + SAME PERIOD CHECK
            String dayPeriodKey = day + "-" + period;
            if (dayPeriodSet.contains(dayPeriodKey)) {
                JOptionPane.showMessageDialog(this,
                        "Conflict! " + day + " Period " + period + " is already assigned.");
                return;
            }
            dayPeriodSet.add(dayPeriodKey);

            // ✅ 2. TEACHER CANNOT TEACH TWO CLASSES AT SAME TIME
            String teacherSlotKey = day + "-" + period + "-" + teacher;
            if (teacherSlotSet.contains(teacherSlotKey)) {
                JOptionPane.showMessageDialog(this,
                        "Conflict! Teacher " + teacher + " is already busy on "
                                + day + " Period " + period);
                return;
            }
            teacherSlotSet.add(teacherSlotKey);

            // ✅ 3. SUBJECT–TEACHER VALIDATION
            boolean valid = false;
            for (Teacher t : teachers) {
                if (t.getName().equals(teacher)
                        && t.getSubject().getName().equals(subject)) {
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                JOptionPane.showMessageDialog(this,
                        "Invalid! " + teacher + " does not teach " + subject);
                return;
            }

            newTimetable.add(new TimeTableEntry(day, period, subject, teacher));
        }

        // Save to DB
        TimeTableDAO.saveTimeTable(newTimetable);
        JOptionPane.showMessageDialog(this, "Timetable saved successfully!");
        loadTimetable();
    }


    // Custom renderer to color Subject column
    private class SubjectCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value != null) {
                c.setBackground(subjectColors.getOrDefault(value.toString(), Color.WHITE));
            } else {
                c.setBackground(Color.WHITE);
            }
            if (isSelected) {
                c.setBackground(c.getBackground().darker());
            }
            return c;
        }
    }

    // Custom renderer for Teacher column (light blue)
    private class TeacherCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBackground(new Color(220, 240, 255)); // Light blue
            if (isSelected) {
                c.setBackground(c.getBackground().darker());
            }
            return c;
        }
    }
}
