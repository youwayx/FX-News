package com.yuweixu.fxnews;

import android.util.Log;

import java.util.Date;

/**
 * Created by Yuwei on 2014-10-06.
 */
public class Utility {

    //takes a date in the form of MM dd, yyyy and replaces the month number with its English word
    public static String formatDateString (String date){
        int monthNumber = (int) Integer.parseInt(date.substring(0,2));
        String dateNumber = date.substring(3,5);
        if (dateNumber.charAt(0)=='0'){
            dateNumber = dateNumber.charAt(1)+"";

        }
        String newDate = date.substring(0,3) +dateNumber + date.substring(5);

        String [] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        return months[monthNumber-1]+newDate.substring(2);
    }

    public static String formatDateForWebQuery (String date){
        String [] dateInfo = date.split(" ");
        String day ="";
        for (int i=0; i<3; i++){
            if (dateInfo[1].charAt(i)!=','){
                day+=dateInfo[1].charAt(i);

            }
            else{break;}
        }
        String ans = dateInfo[0].substring(0,3) + day+"." + dateInfo[2];
        return ans;
    }

    public static String [] getWeekDates (String date){
        String [] dates = new String [5];
        String month ="", stringDay = "";
        int day=-1,year = 2014;
        int index= 0;
        for (int i=0; i<20; i++){
            if (date.charAt(i)!= ' '){
                month+=date.charAt(i)+"";
            }
            else{
                index = i+1;
                break;
            }
        }
        for (int i=index; i<index+2; i++){
            if (date.charAt(i)==','){
                break;
            }
            else{stringDay+=date.charAt(i)+"";}
        }
        day = Integer.parseInt(stringDay);
        String [] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        int [] monthLengths = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int monthIndex=-1;
        for (int j=0; j<12; j++){
            if (months[j].equals(month)){
                monthIndex = j;
            }
        }
        for (int i=1; i<5; i++){
            if (day+1 <= monthLengths[monthIndex]){
                dates[i]=months[monthIndex]+" "+(day+1)+", 2014";
            }
            else{

                monthIndex += 1;
                if (monthIndex>11){
                    monthIndex = 1;
                    year++;
                }
                day=0;
                dates[i]=months[monthIndex]+" "+(day+1)+", "+year;
            }
            day+=1;
        }
        dates[0]=date;
        return dates;
    }
}
