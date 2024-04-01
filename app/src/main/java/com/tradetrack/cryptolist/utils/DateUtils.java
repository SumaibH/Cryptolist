package com.tradetrack.cryptolist.utils;

public class DateUtils {
    public static String getTimeDifference(long lastRefreshTimeMillis) {
        // Get the current time in milliseconds
        long currentTimeMillis = System.currentTimeMillis();

        // Calculate the time difference in milliseconds
        long timeDifferenceMillis = currentTimeMillis - lastRefreshTimeMillis;

        // Calculate seconds, minutes, and hours from the time difference
        long seconds = timeDifferenceMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        // Calculate remaining minutes and remaining seconds after calculating hours
        long remainingMinutes = minutes % 60;
        long remainingSeconds = seconds % 60;

        // Create a string representing the time difference
        String timeDifferenceString;
        if (hours > 0) {
            if (hours==1){
                timeDifferenceString = hours + " hr ago";
            }else{
                timeDifferenceString = hours + " hrs ago";
            }

        } else if (minutes > 0) {
            if (minutes==1){
                timeDifferenceString = minutes + " min ago";
            }else{
                timeDifferenceString = minutes + " mins ago";
            }
        } else {
            timeDifferenceString = seconds + " secs ago";
        }

        return timeDifferenceString;
    }

}
