package org.sw.logback.cron;

import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class CronCalculator {
    private final CronExpression cron;
    private final CronPreviousTimeFinder finder;
    private final Date[] timeSet = new Date[3];
    private Date reference;

    public CronCalculator(String cron) throws ParseException {
        this(cron, new Date());
    }

    public CronCalculator(String cron, Date reference) throws ParseException {
        this.cron = new CronExpression(cron);
        this.finder = new CronPreviousTimeFinder(cron);
        setReference(reference);
    }

    public String getCron() {
        return this.cron.toString();
    }

    public Date getReference() {
        return this.reference;
    }

    public void setReference(Date reference) {
        if (this.reference == null) {
            this.cron.getNextValidTimeAfter(reference);
            synchronized (this.timeSet) {
                this.timeSet[0] = this.finder.getPreviousValidTimeBefore(reference);
                this.timeSet[1] = this.cron.getNextValidTimeAfter(reference);
//                this.timeSet[1] = getNextValidTimeAfter(reference);
                this.timeSet[2] = this.cron.getNextValidTimeAfter(this.timeSet[1]);
//                this.timeSet[2] = getNextValidTimeAfter(this.timeSet[1]);
                this.reference = reference;
            }
            return;
        }
        if (this.reference.equals(reference)) {
            return;
        }

        synchronized (this.timeSet) {
            try {
                Date time = this.cron.getNextValidTimeAfter(reference);
                if (time.equals(this.timeSet[1])) {
                    return;
                }

                if (this.reference.before(reference)) { // future
                    if (time.equals(this.timeSet[2])) {
                        this.timeSet[0] = this.timeSet[1];
                        this.timeSet[1] = this.timeSet[2];
                        this.timeSet[2] = this.cron.getNextValidTimeAfter(time);
//                        this.timeSet[2] = getNextValidTimeAfter(time);
                        return;
                    }
                    if (time.after(this.timeSet[2])) {
                        this.timeSet[0] = this.finder.getPreviousValidTimeBefore(time);
                        this.timeSet[1] = time;
                        this.timeSet[2] = this.cron.getNextValidTimeAfter(time);
//                        this.timeSet[2] = getNextValidTimeAfter(time);

                        return;
                    }
                }

                if (this.reference.after(reference)) {
                    if (time.equals(this.timeSet[0])) {
                        this.timeSet[2] = this.timeSet[1];
                        this.timeSet[1] = this.timeSet[0];
                        this.timeSet[0] = this.finder.getPreviousValidTimeBefore(time);
                        return;
                    }
                    if (time.before(this.timeSet[0])) {
                        this.timeSet[2] = this.cron.getNextValidTimeAfter(time);
                        this.timeSet[1] = time;
                        this.timeSet[0] = this.finder.getPreviousValidTimeBefore(time);
//                        this.timeSet[0] = getNextValidTimeAfter(time);
                        return;
                    }
                }
            } finally {
                this.reference = reference;
            }
        }
    }

    public void setReference(LocalDateTime reference) {
        setReference(Date.from(reference.atZone(java.time.ZoneId.systemDefault()).toInstant()));
    }

    public Date getPreviousFireTime() {
        return this.timeSet[0] != null ? this.timeSet[0] : this.finder.getPreviousValidTimeBefore(this.reference);
    }

    public Date getFireTime() {
        return this.timeSet[1] != null ? this.timeSet[1] : this.cron.getNextValidTimeAfter(this.reference);
    }


    public Date getNextFireTime() {
        return this.timeSet[2] != null ? this.timeSet[2] : this.cron.getNextValidTimeAfter(this.cron.getNextValidTimeAfter(this.reference));
    }

//    private Date getNextValidTimeAfter(Date reference) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(reference);
//        Date time;
//        do {
//            time = this.cron.getNextValidTimeAfter(calendar.getTime());
//            if(time == null){
//                calendar.add(Calendar.SECOND, 1);  // 2시간 더하기
//            }
//        } while (time == null);
//        return time;
//    }

}
