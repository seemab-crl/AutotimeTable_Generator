package model;

public class TimeTableEntry {

    private String day;
    private int period;
    private String subject;
    private String teacher;

    public TimeTableEntry(String day, int period, String subject, String teacher) {
        this.day = day;
        this.period = period;
        this.subject = subject;
        this.teacher = teacher;
    }

    public String getDay() {
        return day;
    }

    public int getPeriod() {
        return period;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacher() {
        return teacher;
    }
}
