package org.sw.logback;

import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicyBase;

public class DefaultCronBasedFileNamingAndTriggeringPolicy<E> extends DefaultTimeBasedFileNamingAndTriggeringPolicy<E> {

    @Override
    public void start() {
        super.start();
    }

    public void setDateInCurrentPeriod(long timestamp){
        super.setDateInCurrentPeriod(timestamp);
    }
}
