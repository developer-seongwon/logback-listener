package org.sw.logback;

import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.RollingCalendar;
import ch.qos.logback.core.rolling.helper.TimeBasedArchiveRemover;


public class CronBasedArchiveRemover extends TimeBasedArchiveRemover {

    public CronBasedArchiveRemover(FileNamePattern fileNamePattern, CronRollingCalendar cc) {
        super(fileNamePattern, cc);
    }

}
