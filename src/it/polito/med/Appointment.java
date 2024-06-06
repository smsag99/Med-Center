package it.polito.med;

import java.util.Comparator;
import java.util.UUID;

public class Appointment {
    private String id;
    private String ssn;
    private String name;
    private String surname;
    private String code;
    private String date;
    private String slot;
    private boolean isAccepted = false;
    private boolean isCompleted = false;

    public Appointment(String ssn, String name, String surname, String code, String date, String slot) {
        id = UUID.randomUUID().toString();
        this.ssn = ssn;
        this.name = name;
        this.surname = surname;
        this.code = code;
        this.date = date;
        this.slot = slot;
    }

    public String getId() {
        return id;
    }

    public String getSSN() {
        return ssn;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getCode() {
        return code;
    }

    public String getDate() {
        return date;
    }

    public String getSlot() {
        return slot;
    }

    public String toString() {
        return slot.split("-")[0] + "=" + ssn;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void accept() {
        this.isAccepted = true;
    }

    public void complete() {
        this.isCompleted = true;
    }

}
