package gui;

import dao.TeacherDAO;
import dao.TimeTableDAO;
import model.Subject;
import model.Teacher;
import model.TimeTableEntry;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import com.itextpdf.text.Image;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ManualTimeTableFrame extends JFrame {

    private JTable table;
    private JButton addRowBtn, deleteRowBtn, saveBtn;

    private List<Teacher> teachers;
    private List<Subject> subjects;
    private final String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday"};

    private Map<String, Color> subjectColors = new HashMap<>();

    public ManualTimeTableFrame() {
        setTitle("Manual Timetable Creation");
        setSize(900,500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton exportPdfBtn = new JButton("Export to PDF");
        addRowBtn = new JButton("Add Row");
        deleteRowBtn = new JButton("Delete Row");
        saveBtn = new JButton("Save Timetable");
        topPanel.add(addRowBtn);
        topPanel.add(deleteRowBtn);
        topPanel.add(saveBtn);
        topPanel.add(exportPdfBtn);

        exportPdfBtn.addActionListener(e -> exportToPDF());
        add(topPanel, BorderLayout.NORTH);

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        teachers = TeacherDAO.getAllTeachers();
        subjects = new ArrayList<>();
        for (Teacher t : teachers) {
            subjects.add(t.getSubject());
        }

        initializeSubjectColors();
        loadTimetable();

        // Button actions
        addRowBtn.addActionListener(e -> addRow());
        deleteRowBtn.addActionListener(e -> deleteRow());
        saveBtn.addActionListener(e -> saveTimetable());

        setVisible(true);
    }

    private void initializeSubjectColors() {
        Color[] colors = {Color.CYAN, Color.PINK, Color.ORANGE, Color.LIGHT_GRAY, Color.YELLOW, Color.MAGENTA};
        for (int i=0;i<subjects.size();i++) {
            subjectColors.put(subjects.get(i).getName(), colors[i % colors.length]);
        }
    }

    private void loadTimetable() {
        List<TimeTableEntry> timetable = TimeTableDAO.getTimeTableFromDB();
        String[] columns = {"Day","Period","Subject","Teacher"};
        DefaultTableModel model = new DefaultTableModel(columns,0) {
            @Override
            public boolean isCellEditable(int row, int column) {return true;}
        };

        for (TimeTableEntry t: timetable) {
            model.addRow(new Object[]{t.getDay(), t.getPeriod(), t.getSubject(), t.getTeacher()});
        }

        table.setModel(model);

        // Combo editors
        TableColumn dayCol = table.getColumnModel().getColumn(0);
        JComboBox<String> dayCombo = new JComboBox<>();
        for(String d: days) dayCombo.addItem(d);
        dayCol.setCellEditor(new DefaultCellEditor(dayCombo));

        TableColumn subjectCol = table.getColumnModel().getColumn(2);
        JComboBox<String> subjectCombo = new JComboBox<>();
        for(Subject s: subjects) subjectCombo.addItem(s.getName());
        subjectCol.setCellEditor(new DefaultCellEditor(subjectCombo));
        subjectCol.setCellRenderer(new SubjectCellRenderer());

        TableColumn teacherCol = table.getColumnModel().getColumn(3);
        JComboBox<String> teacherCombo = new JComboBox<>();
        for(Teacher t: teachers) teacherCombo.addItem(t.getName());
        teacherCol.setCellEditor(new DefaultCellEditor(teacherCombo));
        teacherCol.setCellRenderer(new TeacherCellRenderer());
    }
// ADD THIS

    private void exportToPDF() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("C:/Users/Fayyaz/Desktop/TimeTable.pdf"));

            document.open();

            // Add school logo
            try {
                com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance("logo.jpeg");
                logo.scaleToFit(100, 100); // adjust size
                logo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception ex) {
                System.out.println("Logo not found, skipping...");
            }

            // Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Class Timetable\n\n", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);

            // Create table
            PdfPTable pdfTable = new PdfPTable(4); // 4 columns
            pdfTable.setWidthPercentage(100);
            pdfTable.addCell("Day");
            pdfTable.addCell("Period");
            pdfTable.addCell("Subject");
            pdfTable.addCell("Teacher");

            DefaultTableModel model = (DefaultTableModel) table.getModel(); // FIXED

            for (int i = 0; i < model.getRowCount(); i++) {
                pdfTable.addCell(model.getValueAt(i, 0).toString());
                pdfTable.addCell(model.getValueAt(i, 1).toString());
                pdfTable.addCell(model.getValueAt(i, 2).toString());
                pdfTable.addCell(model.getValueAt(i, 3).toString());
            }

            document.add(pdfTable);
            document.close();

            JOptionPane.showMessageDialog(this, "PDF Exported Successfully!\nFile: TimeTable.pdf");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting PDF: " + e.getMessage());
        }
    }



    private void addRow() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{"Monday",1,subjects.get(0).getName(),teachers.get(0).getName()});
    }

    private void deleteRow() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int row = table.getSelectedRow();
        if(row>=0) model.removeRow(row);
        else JOptionPane.showMessageDialog(this,"Select row to delete!");
    }

    private void saveTimetable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rows = model.getRowCount();

        Map<String, Set<String>> dayTeacher = new HashMap<>();
        Map<String, Set<Integer>> dayPeriod = new HashMap<>();
        List<TimeTableEntry> newTable = new ArrayList<>();

        for(int i=0;i<rows;i++) {
            String day = (String) model.getValueAt(i,0);
            int period = Integer.parseInt(model.getValueAt(i, 1).toString());
            String subject = (String) model.getValueAt(i,2);
            String teacher = (String) model.getValueAt(i,3);

            // Validate subject-teacher
            boolean valid=false;
            for(Teacher t: teachers){
                if(t.getName().equals(teacher) && t.getSubject().getName().equals(subject)){
                    valid=true; break;
                }
            }
            if(!valid){
                JOptionPane.showMessageDialog(this,"Teacher "+teacher+" does not teach "+subject);
                return;
            }

            // Duplicate teacher check
            dayTeacher.putIfAbsent(day,new HashSet<>());
            if(dayTeacher.get(day).contains(teacher)){
                JOptionPane.showMessageDialog(this,"Duplicate teacher "+teacher+" on "+day);
                return;
            }
            dayTeacher.get(day).add(teacher);

            // Duplicate period check
            dayPeriod.putIfAbsent(day,new HashSet<>());
            if(dayPeriod.get(day).contains(period)){
                JOptionPane.showMessageDialog(this,"Duplicate period "+period+" on "+day);
                return;
            }
            dayPeriod.get(day).add(period);

            newTable.add(new TimeTableEntry(day,period,subject,teacher));
        }

        TimeTableDAO.saveTimeTable(newTable);
        JOptionPane.showMessageDialog(this,"Timetable saved!");
        loadTimetable();
    }

    // Subject color renderer
    private class SubjectCellRenderer extends DefaultTableCellRenderer{
        @Override
        public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
            Component c = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
            if(value!=null)c.setBackground(subjectColors.getOrDefault(value.toString(),Color.WHITE));
            else c.setBackground(Color.WHITE);
            if(isSelected)c.setBackground(c.getBackground().darker());
            return c;
        }
    }

    // Teacher renderer
    private class TeacherCellRenderer extends DefaultTableCellRenderer{
        @Override
        public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
            Component c = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
            c.setBackground(new Color(220,240,255));
            if(isSelected)c.setBackground(c.getBackground().darker());
            return c;
        }
    }
}
