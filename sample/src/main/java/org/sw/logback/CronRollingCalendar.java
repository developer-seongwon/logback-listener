package org.sw.logback;

import ch.qos.logback.core.rolling.helper.RollingCalendar;
import ch.qos.logback.core.spi.ContextAwareBase;
import org.quartz.CronExpression;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CronRollingCalendar extends RollingCalendar {
    private final CronTimeFinder finder;

    private CronRollingCalendar(CronExpression cron, Date reference, TimeZone timeZone, Locale locale, String pattern) {
        super(pattern, timeZone, locale);
        this.finder = new CronTimeFinder(cron, reference);
    }

    @Override
    public void printPeriodicity(ContextAwareBase cab) {
        cab.addInfo("Roll-over cron expression: " + this.finder.toString());
    }

    @Override
    public Instant getNextTriggeringDate(Instant instant) {
        return getEndOfNextNthPeriod(instant, 1);
    }

    public Instant getEndOfNextNthPeriod(Instant instant, int periods) {
        return periods > 0 ?
                this.finder.getNextValidTimeAfter(Date.from(instant), periods) :
                this.finder.getPreviousValidTimeBefore(Date.from(instant), periods);
    }

    /**
     * 정해진 시간 사이에 롤링이 발생하는 횟수를 제공합니다.
     *
     * @return count of a period between from and to
     */
    @Override
    public long periodBarriersCrossed(long from, long to) {
        if (from > to) {
            throw new IllegalArgumentException("Start cannot come before end");
        }
        return this.finder.getTriggeringCountBetween(
                new Date(getStartOfCurrentPeriodWithGMTOffsetCorrection(from, getTimeZone())),
                new Date(getStartOfCurrentPeriodWithGMTOffsetCorrection(to, getTimeZone()))
        );
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String cronPattern;
        private String datePattern;
        private Date reference = new Date();
        private TimeZone zone = TimeZone.getDefault();
        private Locale locale = Locale.getDefault();

        private Builder() {

        }

        public Builder setCronPattern(String pattern) {
            this.cronPattern = pattern;
            return this;
        }

        public Builder setDatePattern(String pattern) {
            this.datePattern = pattern;
            return this;
        }

        public Builder setReference(Date reference) {
            this.reference = reference;
            return this;
        }

        public Builder setTimeZone(TimeZone zone) {
            this.zone = zone;
            return this;
        }

        public Builder setLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public CronRollingCalendar build() {
            if (this.cronPattern == null) {
                throw new IllegalArgumentException("cron is mandatory");
            }
            CronExpression cron;
            try {
                cron = new CronExpression(this.cronPattern);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid cron expression: " + this.cronPattern, e);
            }
            // reference 날짜를 zone과 locale로 조정
            Calendar calendar = Calendar.getInstance(zone, locale);
            calendar.setTime(reference);
            return new CronRollingCalendar(cron, calendar.getTime(),
                    zone, locale,
                    datePattern
            );
        }
    }
}
