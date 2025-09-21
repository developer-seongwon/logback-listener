package org.sw.logback;

import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.SizeAndTimeBasedArchiveRemover;

import java.io.File;
import java.time.Instant;

public class SizeAndCronBasedArchiveRemover extends SizeAndTimeBasedArchiveRemover {

    public SizeAndCronBasedArchiveRemover(FileNamePattern fileNamePattern, CronRollingCalendar cc) {
        super(fileNamePattern, cc);
    }


}
