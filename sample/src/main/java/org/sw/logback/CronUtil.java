package org.sw.logback;

import java.time.LocalDateTime;
import java.util.Date;

public class CronUtil {

    public static Date previous(String cron, Date now) {
        CronPreviousTimeFinder finder = new CronPreviousTimeFinder(cron);
        return finder.getPreviousValidTime(now);
    }
}
