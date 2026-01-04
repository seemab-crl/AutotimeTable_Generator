package model;

public class Teacher {
    private int id;
    private String name;
    private Subject subject;

    public Teacher(int id, String name, Subject subject) {
        this.id = id;
        this.name = name;
        this.subject = subject;
    }

    public int getNameId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Subject getSubject() {
        return subject;
    }
}
