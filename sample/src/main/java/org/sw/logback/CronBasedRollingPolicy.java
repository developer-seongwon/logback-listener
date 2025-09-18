package org.sw.logback;

import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import org.sw.logback.cron.CronCalculator;

import java.text.ParseException;

public class CronBasedRollingPolicy<E> extends TimeBasedRollingPolicy<E> {
    FileNamePattern fileNamePattern;
    CronCalculator calculator;
    CronBasedFileNamingAndTriggeringPolicy<E> cronBasedFileNamingAndTriggeringPolicy;

    @Override
    public void start() {
        try {
            if (this.calculator == null) {
                this.calculator = new CronCalculator("0 0 0 * * ?");
            }
            addInfo(String.format("Will use the pattern '%s' for the active file", this.calculator.getCron()));

            this.cronBasedFileNamingAndTriggeringPolicy = new DefaultCronBasedFileNamingAndTriggeringPolicy<>();
            this.cronBasedFileNamingAndTriggeringPolicy.setCronBasedRollingPolicy(this);

            // using a wrapper object
            setTimeBasedFileNamingAndTriggeringPolicy(this.cronBasedFileNamingAndTriggeringPolicy);

            super.start();

            if(getFileNamePattern() != null){
                fileNamePattern = new FileNamePattern(fileNamePatternStr, this.context);
            }
        } catch (Exception ignore) {

        }
    }

    public void setCron(String cron) throws ParseException {
        this.calculator = new CronCalculator(cron);
    }
}

