package com.example.joe.cst2335finalgroupproject;

/**
 * Created by Joe on 2017-12-06.
 */

public class t_ThermostatRule {

    //private variables for the content of the rule.
    private String day;
    private String meridiem;
    private int hour;
    private int minute;
    private int temp;

    public t_ThermostatRule() {
        this("Sunday", 0, 0, 15);
    }

    public t_ThermostatRule(String day, int hour, int minute, int temp) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.temp = temp;
    }

    static public t_ThermostatRule valueOf(String rawString) {
        //ruleText *should* be in exact format "Monday 9:00 Temp -> 16"

        String[] ruleSplit = rawString.split(" ");

        String day = ruleSplit[0];
        String time = ruleSplit[1];
        int temp = Integer.valueOf(ruleSplit[4]);

        String[] timeSplit = time.split(":");
        int hour = Integer.valueOf(timeSplit[0]);
        int minute = Integer.valueOf(timeSplit[1]);

        return new t_ThermostatRule(day, hour, minute, temp);
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return day + " " + String.format("%02d", hour) + ":" + String.format("%02d", minute) + " Temp -> " + temp;
    }
}
