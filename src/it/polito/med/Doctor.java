package it.polito.med;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class Doctor {
    private String id;
    private String name;
    private String surname;
    private String speciality;
    private TreeMap<String, LinkedList<String>> scheduals = new TreeMap<>();

    public Doctor(String id, String name, String surname, String speciality) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.speciality = speciality;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getSpeciality() {
        return speciality;
    }

    public boolean is(String speciality) {
        return this.speciality.equals(speciality);
    }

    public String modifyTime(int time) {
        String hour, min;
        if (Math.abs(time / 60) < 10)
            hour = "0" + Math.abs(time / 60);
        else
            hour = Math.abs(time / 60) + "";
        if (time % 60 < 10)
            min = "0" + time % 60;
        else
            min = time % 60 + "";

        return hour + ":" + min;
    }

    public int addSchedual(String date, int start, int end, int duration) {
        if (!scheduals.containsKey(date))
            scheduals.put(date, new LinkedList<String>());
        int numSlots = Math.abs((end - start) / duration);
        for (int i = 0; i < numSlots; i++) {
            String slotStart = modifyTime(start);
            start += duration;
            String slotEnd = modifyTime(start);
            scheduals.get(date).add(slotStart + "-" + slotEnd);
        }
        return numSlots;
    }

    public List<String> getScheduals(String date) {
        return scheduals.get(date);
    }

    public boolean hasSchedual(String date) {
        return scheduals.containsKey(date);
    }

    public int getAllScheduals() {
        int size = 0;
        for (List<String> sc : scheduals.values()) {
            size += sc.size();
        }
        return size;
    }
}
