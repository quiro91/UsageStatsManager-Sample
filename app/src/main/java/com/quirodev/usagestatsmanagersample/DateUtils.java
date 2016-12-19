package com.quirodev.usagestatsmanagersample;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateUtils {

    public static String format(UsageStatsWrapper usageStatsWrapper){

        DateFormat format = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
        return format.format(usageStatsWrapper.getUsageStats().getLastTimeUsed());
    }
}
