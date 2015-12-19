package com.example.android.popularmovies.app;

import java.util.Calendar;
import java.util.Date;

public class TimePeriod {

    private String periodPreference;

    private String onOrBeforeThisDate;
    private String onOrAfterThisDate;

    TimePeriod(String periodPreference){

        this.periodPreference = periodPreference;
        computeRanges();

    }

    private void computeRanges(){

        switch (periodPreference){

            case "all":
                all();
                break;
            case "this_entire_week":
                getThisWeek();
                break;
            case  "this__entire_month":
                getThisMonth();
                break;
            case  "this_year_to_date":
                getThisYearToDate();
                break;
            case "2010":
                get2010s();
                break;
            case "2000":
                get2000s();
                break;
            case "1990":
                get1990s();
                break;
            case "1980":
                get1980s();
                break;
            case "1970":
                get1970s();
                break;
            case "1960":
                get1960s();
                break;
            case "1950":
                get1950s();
                break;
            case "1940":
                get1940s();
                break;
            case "1930":
                get1930s();
                break;
            case "1920":
                get1920s();
                break;
            case "before_1920":
                getBefore1920s();
                break;
            default:
                all();
        }

    }

    private void get2010s() {
        onOrAfterThisDate = "2010-1-1";   onOrBeforeThisDate = "2010-12-31";
    }

    private void get2000s() {
        onOrAfterThisDate = "2000-1-1";   onOrBeforeThisDate = "2000-12-31";
    }

    private void get1990s() {
        onOrAfterThisDate = "1990-1-1";   onOrBeforeThisDate = "1999-12-31";
    }

    private void get1980s() {
        onOrAfterThisDate = "1980-1-1";   onOrBeforeThisDate = "1989-12-31";
    }

    private void get1970s() {
        onOrAfterThisDate = "1970-1-1";   onOrBeforeThisDate = "1979-12-31";
    }

    private void get1960s() {
        onOrAfterThisDate = "1960-1-1";   onOrBeforeThisDate = "1969-12-31";
    }

    private void get1950s() {
        onOrAfterThisDate = "1950-1-1";   onOrBeforeThisDate = "1959-12-31";
    }

    private void get1940s() {
        onOrAfterThisDate = "1940-1-1";   onOrBeforeThisDate = "1949-12-31";
    }

    private void get1930s() {
        onOrAfterThisDate = "1930-1-1";   onOrBeforeThisDate = "1939-12-31";
    }

    private void get1920s() {
        onOrAfterThisDate = "1920-1-1";   onOrBeforeThisDate = "1929-12-31";
    }

    private void getBefore1920s() {
        onOrAfterThisDate = "";   onOrBeforeThisDate = "1919-12-31";
    }

    private void getThisYearToDate() {

        Calendar c = Calendar.getInstance();
        Date date = new Date();
        c.setTime(date);

        // Today
        onOrBeforeThisDate = toTmdbDateString(c);

        // First day of year
        c.set(Calendar.DAY_OF_YEAR, 1);
        onOrAfterThisDate = toTmdbDateString(c);

        return;
    }

    private void getThisMonth() {

        Calendar c = Calendar.getInstance();
        Date date = new Date();
        c.setTime(date);

        // First day of month
        c.set(Calendar.DAY_OF_MONTH, 1);
        onOrAfterThisDate = toTmdbDateString(c);

        // Last day of month
        c.add(Calendar.MONTH, 1);   // Next month
        c.add(Calendar.DAY_OF_MONTH, -1);  // Minus 1 day
        onOrBeforeThisDate = toTmdbDateString(c);

    }

    private void all() {
        onOrBeforeThisDate = "";
        onOrAfterThisDate = "";
    }

    public void getThisWeek(){

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        // Days since first day of week
        int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        c.add(Calendar.DATE, -i);
        onOrAfterThisDate = toTmdbDateString(c);

        // Seven days after first day of week
        c.add(Calendar.DATE, 7);
        onOrBeforeThisDate = toTmdbDateString(c);

        return;
    }

    private String toTmdbDateString(Calendar c) {

        return  Integer.toString( c.get(Calendar.YEAR)  ) + "-" +
                Integer.toString( 1+ c.get(Calendar.MONTH) ) + "-" +  // Month zero-offset
                Integer.toString( c.get(Calendar.DATE)  );

    }

    public boolean periodHasLowerDate() {
        if(onOrAfterThisDate == "")
            return false;

        return true;
    }


    public boolean periodHasUpperDate() {
        if(onOrBeforeThisDate == "")
            return false;

        return true;

    }

    public String getUpperDate() {
        return onOrBeforeThisDate;
    }

    public String getLowerDate() {
        return onOrAfterThisDate;
    }
}
