package org.sw.logback.cron;

import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;

public class CronCalculator {
    private final CronExpression cron;


    public CronCalculator(String cron) throws ParseException {
        this.cron = new CronExpression(cron);
    }

    public CronCalculator(String cron, LocalDateTime now) throws ParseException {
        this.cron = new CronExpression(cron);
    }

    public String getCron() {
        return this.cron.toString();
    }

    public Date getPreviousFireTime() {
        return null;
    }

    public Date getNextFireTime() {
//        this.cron.getFinalFireTime()
        return null;
    }


}
