package org.sw.logback;

import ch.qos.logback.core.rolling.LengthCounter;

import java.io.File;

public class CronBasedFileNamingAndTriggeringPolicyBase<E> extends DefaultCronBasedFileNamingAndTriggeringPolicy<E> {


    @Override
    public LengthCounter getLengthCounter() {
        return super.getLengthCounter();
    }

}
