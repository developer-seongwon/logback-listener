package org.sw.logback;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.LengthCounter;
import ch.qos.logback.core.rolling.LengthCounterBase;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.util.FileSize;

import java.time.Duration;

import static ch.qos.logback.core.CoreConstants.MANUAL_URL_PREFIX;

@NoAutoStart
public class SizeAndCronBasedFileNamingAndTriggeringPolicyBase<E> extends SizeAndTimeBasedFileNamingAndTriggeringPolicy<E> {

    CronBasedRollingPolicy<E> cbrp;

    public SizeAndCronBasedFileNamingAndTriggeringPolicyBase() {
        super();
    }


    @Override
    public void start() {
        // we depend on certain fields having been initialized in super class
        super.start();

    }


    @Override
    public void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> tbrp) {
        if (tbrp instanceof CronBasedRollingPolicy<E>) {
            this.cbrp = (CronBasedRollingPolicy<E>) tbrp;
        }
        super.setTimeBasedRollingPolicy(tbrp);
    }

    protected ArchiveRemover createArchiveRemover() {
        return new SizeAndCronBasedArchiveRemover(cbrp.fileNamePattern, cbrp.cc);
    }
}
