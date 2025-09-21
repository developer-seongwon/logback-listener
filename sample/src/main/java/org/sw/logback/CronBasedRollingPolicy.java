package org.sw.logback;

import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.helper.FileNamePattern;

public class CronBasedRollingPolicy<E> extends TimeBasedRollingPolicy<E> {
    CronBasedFileNamingAndTriggeringPolicyBase<E> cronBasedFileNamingAndTriggeringPolicy;
    FileNamePattern fileNamePattern;
    CronRollingCalendar cc;


    @Override
    public void start() {
        this.fileNamePattern = new FileNamePattern(getFileNamePattern(), super.context);


        // SizeAnd~ 클래스에서 직접 넣어주기 때문에 충돌 방지를 위해서 null을 확인한 이후에 추가합니다.
        if (getTimeBasedFileNamingAndTriggeringPolicy() == null) {
            this.cronBasedFileNamingAndTriggeringPolicy = new CronBasedFileNamingAndTriggeringPolicyBase<>();
            this.cronBasedFileNamingAndTriggeringPolicy.setCronBasedRollingPolicy(this);
            setTimeBasedFileNamingAndTriggeringPolicy(this.cronBasedFileNamingAndTriggeringPolicy);
        }

        super.start();
    }

    public void setCron(String cron) {
        this.cc = CronRollingCalendar.newBuilder()
                .setCronPattern(cron)
                .build();
    }

//    public String getCron() {
//        return this.cron;
//    }
}

