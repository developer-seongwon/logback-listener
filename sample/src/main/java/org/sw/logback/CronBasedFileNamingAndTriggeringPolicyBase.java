package org.sw.logback;

import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.LengthCounter;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.TimeBasedArchiveRemover;

import java.io.File;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CronBasedFileNamingAndTriggeringPolicyBase<E> extends DefaultTimeBasedFileNamingAndTriggeringPolicy<E> implements CronBasedFileNamingAndTriggeringPolicy<E> {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    CronBasedRollingPolicy<E> cbrp;

    @Override
    public void start() {
        super.start();
        if (!this.isStarted()) {
            return;
        }

        // ArchiveRemover의 RollingCalendar를 교체하는게 어렵기 때문에 클래스를 통으로 갈아끼웁니다.
        super.archiveRemover = new CronBasedArchiveRemover(new FileNamePattern(super.tbrp.getFileNamePattern(), super.context),
                cbrp.cc
        );
        super.archiveRemover.setContext(super.context);

        validParentDirectory();
    }

    @Override
    public void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> tbrp) {
        if (tbrp instanceof CronBasedRollingPolicy<E>) {
            this.cbrp = (CronBasedRollingPolicy<E>) tbrp;
        }
        super.setTimeBasedRollingPolicy(tbrp);
    }

    public void validParentDirectory() {
        if (this.started) {
            File parent = new File(getCurrentPeriodsFileNameWithoutCompressionSuffix()).getParentFile();
            if (parent.mkdirs()) {
                addInfo("create parent directory: " + parent.getAbsolutePath());
            }
        }
        if (this.scheduler.isShutdown() || this.scheduler.isTerminated()) {
            return;
        }
        this.scheduler.schedule(this::validParentDirectory, (this.atomicNextCheck.get() - getCurrentTime()), TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        super.stop();
        this.scheduler.shutdown();
    }

    @Override
    protected long computeNextCheck(long timestamp) {
        return cbrp.cc.getNextTriggeringDate(Instant.ofEpochMilli(timestamp)).toEpochMilli();
    }

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        boolean result = super.isTriggeringEvent(activeFile, event);
        System.out.println("CronBasedFile.isTriggering-" + result);

        if (result) {
            // DefaultTimeBasedFileNamingAndTriggeringPolicy.isTriggeringEvent에서 dateInCurrentPeriod 값을 '현재 시간'으로 변경합니다.
            // 다음번 isTriggeringEvent가 true일 때, 이전에 변경된 dateInCurrentPeriod 값으로 파일을 생성하기 때문에 크론식에 맞는 파일명 생성을 위해서 재차 변경합니다.

            Instant now = Instant.ofEpochMilli(getCurrentTime());
            setDateInCurrentPeriod(cbrp.cc.getEndOfNextNthPeriod(now, 0).toEpochMilli());
            return true;
        }
        return false;
    }

    @Override
    public void setCronBasedRollingPolicy(CronBasedRollingPolicy<E> cbrp) {
        this.cbrp = cbrp;
    }

    @Override
    public LengthCounter getLengthCounter() {
        return super.getLengthCounter();
    }
}
