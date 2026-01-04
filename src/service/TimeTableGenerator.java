package service;

import model.Teacher;
import model.TimeTableEntry;

import java.util.*;

public class TimeTableGenerator {

    private static final String[] DAYS =
            {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    public static List<TimeTableEntry> generate(List<Teacher> teachers, int periodsPerDay) {

        List<TimeTableEntry> timetable = new ArrayList<>();
        Random random = new Random();

        for (String day : DAYS) {

            List<Teacher> availableTeachers = new ArrayList<>(teachers);

            for (int period = 1; period <= periodsPerDay; period++) {

                Teacher selected;

                if (!availableTeachers.isEmpty()) {
                    // Pick random teacher from available
                    selected = availableTeachers.remove(random.nextInt(availableTeachers.size()));
                } else {
                    // All teachers used, pick any (allow repeat)
                    selected = teachers.get(random.nextInt(teachers.size()));
                }

                timetable.add(new TimeTableEntry(
                        day,
                        period,
                        selected.getSubject().getName(),
                        selected.getName()
                ));
            }
        }

        return timetable;
    }
}
