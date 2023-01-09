package Utils;

import java.util.Calendar;
import java.util.Date;

public class CustomDate {
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;

    @Override
    public String toString() {
        return getYear()+"/"+getMonth()+"/"+getDay()+"/"+getHour()+"/"+getMinute()+"/"+getSecond();
    }

    public static CustomDate now(){
        Calendar rightNow = Calendar.getInstance();
        return new CustomDate(
                rightNow.get(Calendar.YEAR),
                rightNow.get(Calendar.MONTH),
                rightNow.get(Calendar.DAY_OF_MONTH),
                rightNow.get(Calendar.HOUR),
                rightNow.get(Calendar.MINUTE),
                rightNow.get(Calendar.SECOND)
        );
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public CustomDate(int day, int month, int year, int hour, int minute, int second) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    private int second;

}
