package org.sw.logback;

import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.helper.FileNamePattern;

import java.io.File;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultCronBasedFileNamingAndTriggeringPolicy<E> extends DefaultTimeBasedFileNamingAndTriggeringPolicy<E> {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final Instant instant = Instant.now();
    private final AtomicLong atomicNextCheck = new AtomicLong(instant.toEpochMilli());
    private FileNamePattern fileNamePattern;
    private CronBasedRollingPolicy<E> rollingPolicy;

    @Override
    public void start() {
        this.fileNamePattern = new FileNamePattern(this.rollingPolicy.getFileNamePattern(), this.context);

        super.start();
        this.atomicNextCheck.set(this.rollingPolicy.calculator.getFireTime().getTime());
        this.worker.execute(this::validParentDirectory);
    }

    public void validParentDirectory() {
        if (this.started) {
            File parent = new File(getCurrentPeriodsFileNameWithoutCompressionSuffix()).getParentFile();
            if (parent.mkdirs()) {
                addInfo("create parent directory: " + parent.getAbsolutePath());
            }
            this.scheduler.schedule(this::validParentDirectory, (this.atomicNextCheck.get() - getCurrentTime()), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void stop() {
        super.stop();

        this.scheduler.shutdown();
        this.worker.shutdown();
    }

    public void setCronBasedRollingPolicy(CronBasedRollingPolicy<E> rollingPolicy) {
        this.rollingPolicy = rollingPolicy;
    }

    /**
     * fileNamePattern에 의해 생성되는 파일 경로에 대한 문자열을 제공
     */
    @Override
    public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
        return super.getCurrentPeriodsFileNameWithoutCompressionSuffix();
    }

    @Override
    protected long computeNextCheck(long timestamp) {
        return this.rollingPolicy.calculator.getNextFireTime().getTime();
    }

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        System.out.println("Old - " + this.elapsedPeriodsFileName);
        if (super.isTriggeringEvent(activeFile, event)) {
            System.out.println("New - " + this.elapsedPeriodsFileName);
            this.rollingPolicy.calculator.setReference(new Date(dateInCurrentPeriod.toEpochMilli()));
            setDateInCurrentPeriod(this.rollingPolicy.calculator.getPreviousFireTime().getTime());
            return true;
        }
        return false;
    }
}
