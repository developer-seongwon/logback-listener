package org.sw.logback;

import org.quartz.CronExpression;

import java.util.Date;
import java.util.Optional;

public class CronTimeFinder {

    private static final int PERIOD_0 = 3;
    private final CronExpression cron;
    private Date reference;
    private Date[] caches = new Date[7];

    public CronTimeFinder(CronExpression cron, Date reference) {
        this.cron = cron;
        this.reference = reference;
    }


    @Override
    public String toString() {
        return this.cron.toString();
    }

    public long getTriggeringCountBetween(Date from, Date to) {
        long count = 0;
        Date next = getNextValidTimeAfter(from);
        while (next != null && next.before(to)) {
            count++;
            next = getNextValidTimeAfter(next);
        }
        return count;
    }

    public Date getNextValidTimeAfter(Date date) {
        return getNextValidTimeAfter(date, 1);
    }

    public Date getNextValidTimeAfter(Date date, int periods) {
        Date base = this.cron.getNextValidTimeAfter(date);
        Optional<Integer> optional = getCacheIndex(base);
        if (optional.isEmpty()) {

        }
        this.caches[PERIOD_0] = base;

        Date result = date;
        if (periods > 0) {
            for (int i = 0; i < periods; i++) {
                result = this.cron.getNextValidTimeAfter(result);
            }
        }
        return result;
    }


    public Date getPreviousValidTimeBefore(Date date) {
        return this.cron.getNextValidTimeAfter(date);
    }

    private Optional<Integer> getCacheIndex(Date date) {
        for (int i = 0; i < 7; i++) {
            if (this.caches[i].equals(date)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }
}
