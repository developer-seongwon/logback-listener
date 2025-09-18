package org.sw.logback;

import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DefaultCronBasedFileNamingAndTriggeringPolicy<E> extends DefaultTimeBasedFileNamingAndTriggeringPolicy<E> implements CronBasedFileNamingAndTriggeringPolicy<E> {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    CronBasedRollingPolicy<E> cbrp;

    @Override
    public void start() {
        super.start();
        setDateInCurrentPeriod(this.cbrp.calculator.getPreviousFireTime().getTime());
        validParentDirectory();
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

    public void setCronBasedRollingPolicy(CronBasedRollingPolicy<E> cbrp) {
        this.cbrp = cbrp;
    }

    @Override
    public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
        return super.getCurrentPeriodsFileNameWithoutCompressionSuffix();
    }

    @Override
    protected long computeNextCheck(long timestamp) {
        this.cbrp.calculator.setReference(new Date(timestamp));
        return this.cbrp.calculator.getFireTime().getTime();
    }

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        //
        if (super.isTriggeringEvent(activeFile, event)) {
            // DefaultTimeBasedFileNamingAndTriggeringPolicy.isTriggeringEvent에서 dateInCurrentPeriod 값을 '현재 시간'으로 변경합니다.
            // 다음번 isTriggeringEvent가 true일 때, 이전에 변경된 dateInCurrentPeriod 값으로 파일을 생성하기 때문에 크론식에 맞는 파일명 생성을 위해서 재차 변경합니다.
            setDateInCurrentPeriod(this.cbrp.calculator.getPreviousFireTime().getTime());
            return true;
        }
        return false;
    }
}
