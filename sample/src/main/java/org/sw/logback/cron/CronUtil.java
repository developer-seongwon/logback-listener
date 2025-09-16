package org.sw.logback.cron;

import java.util.Date;

public class CronUtil {

    public static Date previous(String cron, Date now) {
        CronPreviousTimeFinder finder = new CronPreviousTimeFinder(cron);
        return finder.getPreviousValidTime(now);
    }
}
