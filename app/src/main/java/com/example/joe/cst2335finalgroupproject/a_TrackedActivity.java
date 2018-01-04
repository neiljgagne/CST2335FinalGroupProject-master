package com.example.joe.cst2335finalgroupproject;

import java.util.Date;

/**
 * Created by Nick
 */

public class a_TrackedActivity {

    String notes;
    String type;
    int colKey;
    int duration;
    Date timeStamp;

    a_TrackedActivity(int newCol, String newType, int newDuration, String newNotes, Date newDate) {
        colKey = newCol;
        notes = newNotes;
        type = newType;
        duration = newDuration;
        timeStamp = newDate;
    }

    String getNotes() {
        return notes;
    }

    void setNotes(String notes) {
        this.notes = notes;
    }

    String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }

    int getColKey() {
        return colKey;
    }

    void setColKey(int colKey) {
        this.colKey = colKey;
    }

    int getDuration() {
        return duration;
    }

    void setDuration(int duration) {
        this.duration = duration;
    }

    Date getTimeStamp() {
        return timeStamp;
    }

    void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String toString() {
        return this.getType() + this.getDuration() + this.getTimeStamp().toString();
    }
}