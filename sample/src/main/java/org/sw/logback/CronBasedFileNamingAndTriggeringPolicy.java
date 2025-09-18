package org.sw.logback;

import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy;

public interface CronBasedFileNamingAndTriggeringPolicy<E> extends TimeBasedFileNamingAndTriggeringPolicy<E> {

    void setCronBasedRollingPolicy(CronBasedRollingPolicy<E> cbrp);
}
