package model;

public class Subject {
    private int id;
    private String name;
    private int weeklyPeriods;

    public Subject(int id, String name, int weeklyPeriods) {
        this.id = id;
        this.name = name;
        this.weeklyPeriods = weeklyPeriods;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWeeklyPeriods() {
        return weeklyPeriods;
    }
}
