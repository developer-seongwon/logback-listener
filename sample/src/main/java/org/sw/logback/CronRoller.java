package org.sw.logback;

import org.springframework.scheduling.support.CronExpression;
import org.sw.logback.cron.CronPreviousTimeFinder;

import java.time.Instant;
import java.time.ZoneId;

public class CronRoller {
    private final CronPreviousTimeFinder finder;
    private final String cron;
    private long time;

    public CronRoller(String cron, long time) {
        this.cron = cron;
        this.finder = new CronPreviousTimeFinder(cron.toString());

        this.time = time;
    }

    public long getNextTimeMillis() {
        return CronExpression.parse(this.cron)
                .next(Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault()).toLocalDateTime())
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public void update(long time) {
        this.time = time;
    }
}
