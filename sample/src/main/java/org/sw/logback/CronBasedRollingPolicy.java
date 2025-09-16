package org.sw.logback;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.*;

import org.quartz.CronExpression;
import org.quartz.impl.calendar.CronCalendar;
import org.sw.logback.cron.CronCalculator;
import org.sw.logback.cron.CronUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CronBasedRollingPolicy<E> extends SizeAndTimeBasedRollingPolicy<E> {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ExecutorService worker = Executors.newCachedThreadPool();
    private CronCalculator calculator;
    protected String cronExpressionStr = "0 0 0 * * ?";
    protected CronExpression cronExpression;
    protected Date previousDate;
    protected Date nextDate;
    protected DefaultCronBasedFileNamingAndTriggeringPolicy<E> fileNamingTriggeringPolicy;
    protected CronRoller roller;

    private String previousFileName;

    @Override
    public void start() {
        if (this.calculator == null) {
            try {
                this.calculator = new CronCalculator("0 0 0 * * ?");
            } catch (Exception ignore) {

            }
        }
        addInfo(String.format("Will use the pattern '%s' for the active file", this.calculator.getCron()));

        this.fileNamingTriggeringPolicy = new DefaultCronBasedFileNamingAndTriggeringPolicy<>();
        // using a wrapper object
        setTimeBasedFileNamingAndTriggeringPolicy(this.fileNamingTriggeringPolicy);

        Date now = new Date(this.fileNamingTriggeringPolicy.getCurrentTime());
        this.previousDate = CronUtil.previous(this.cronExpressionStr, now);
        this.nextDate = this.cronExpression.getNextValidTimeAfter(now);

        super.start();
        // 파일 이름이 기존 로거의 규칙에 맞게 생성되도록 이전 스케줄 시간을 강제로 주입합니다.
        this.fileNamingTriggeringPolicy.setDateInCurrentPeriod(this.previousDate.getTime());

        this.calculator;
        this.scheduler.schedule().scheduleAtFixedRate(() -> {
            if (getTimeBasedFileNamingAndTriggeringPolicy().isTriggeringEvent(null, null)) {
                createFileIfNotExists(getTimeBasedFileNamingAndTriggeringPolicy().getCurrentPeriodsFileNameWithoutCompressionSuffix());
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void schedule(){

    }

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
//        System.out.println("CronBasedRollingPolicy.isTriggeringEvent: " + activeFile.getName());
        long now = getTimeBasedFileNamingAndTriggeringPolicy().getCurrentTime();
        if (now > this.nextDate.getTime()) {
            // 스케줄 간격을 2번이상 넘어가는 경우가 있으므로 그만큼 스케줄 시간을 조정합니다.
            do {
                this.previousDate = this.nextDate;
                this.nextDate = this.cronExpression.getNextValidTimeAfter(this.nextDate);
            } while (this.nextDate.getTime() < now);
            // 현재는 cron의 간격이 무조건 time보다 넓기 때문에 항상 true가 반환
            boolean result = super.isTriggeringEvent(activeFile, event);
            // isTriggeringEvent가 호출된 이후에는 호출된 시간을 기준으로 파일명을 생성하는 객체가 변경되기때문에 cron 스케줄 시간을 강제로 주입합니다.
            this.fileNamingTriggeringPolicy.setDateInCurrentPeriod(this.previousDate.getTime());
            return result;
        } else {
            return false;
        }
    }

    @Override
    public void rollover() throws RolloverFailure {
        super.rollover();
    }

    @Override
    public String getActiveFileName() {
        String activeFileName = super.getActiveFileName();
        System.out.println("CronRollingPolicy.getActiveFileName(): " + activeFileName);
        return activeFileName;
//        return Optional.ofNullable(getParentsRawFileProperty()).orElseGet(() ->
//                "asd"
//        );

//        if (parentsRawFileProperty != null) {
//            return parentsRawFileProperty;
//        } else {
//            return timeBasedFileNamingAndTriggeringPolicy.getCurrentPeriodsFileNameWithoutCompressionSuffix();
//        }
    }

    @Override
    public TimeBasedFileNamingAndTriggeringPolicy<E> getTimeBasedFileNamingAndTriggeringPolicy() {
        return super.getTimeBasedFileNamingAndTriggeringPolicy();
    }

    /**
     * 롤링 정책의 크론 표현식을 설정합니다.
     * <p>
     * Logback 설정 시스템에서 logback.xml의 <cron/> 엘리먼트를 처리할 때
     * 자동으로 호출되며, 크론 표현식을 Logger Context에 주입하여 로그 파일
     * 롤링 스케줄을 설정합니다.
     * </p>
     * <p>
     * Sets the cron expression for this rolling policy.
     * <p>
     * This method is automatically invoked by Logback's configuration system
     * when it encounters a <cron/> element in logback.xml. The cron expression
     * is then injected into the Logger Context to schedule log file rolling.
     * </p>
     *
     * @param cron 롤링 정책의 스케줄을 지정하는 크론 표현식 (예: "0 0 12 * * ?" - 매일 정오)
     *             the cron expression specifying the desired schedule for this rolling policy
     *             (e.g., "0 0 12 * * ?" for daily at noon)
     */
    public void setCron(String cron) throws ParseException {
        this.calculator = new CronCalculator(cron);
    }

    /**
     * 직접적으로 사용하지 않지만 동작 원리 이해를 위해 명시적으로 선언합니다.
     * <p>
     * Logback XML 설정의 <fileNamePattern/> 태그 값이 자동으로 이 메서드를 통해 주입됩니다.
     * 제 값은 RollingPolicyBase 클래스에서 관리되며, getFileNamePattern() 메서드로 접근 가능합니다.
     *
     * @param fnp 파일명 패턴 문자열 (예: "logs/app.%d{yyyy-MM-dd}.log")
     * @see RollingPolicyBase#setFileNamePattern(String)
     * @see RollingPolicyBase#getFileNamePattern()
     */
    @Override
    public void setFileNamePattern(String fnp) {
        super.setFileNamePattern(fnp);
    }

    /**
     * 직접적으로 사용하지 않지만 동작 원리 이해를 위해 명시적으로 선언합니다.
     * <p>
     * RollingPolicy 인터페이스의 메서드로, 롤링 정책이 자신을 포함하는
     * FileAppender를 인식할 수 있도록 합니다. 이 메서드는 Logback의 설정 시스템이
     * RollingFileAppender와 RollingPolicy를 연결할 때 자동으로 호출됩니다.
     * <p>
     * This method from the RollingPolicy interface allows the rolling policy
     * implementation to be aware of its containing appender. It is automatically
     * called by Logback's configuration system when connecting a RollingFileAppender
     * with its RollingPolicy.
     *
     * @param appender 이 롤링 정책을 포함하는 FileAppender
     *                 the FileAppender that contains this rolling policy
     */
    public void setParent(FileAppender<?> appender) {
        super.setParent(appender);
    }

    /**
     * 직접적으로 사용하지 않지만 동작 원리 이해를 위해 명시적으로 선언합니다.
     * <p>
     * Prudent 모드는 여러 JVM이 동시에 같은 로그 파일에 안전하게 쓸 수 있도록
     * 하는 모드입니다. 이 정보는 롤링 정책에서 파일 처리 방식을 결정할 때 사용됩니다.
     * 일부 롤링 정책(예: FixedWindowRollingPolicy)은 Prudent 모드를 지원하지 않습니다.
     * <p>
     * Prudent mode allows multiple JVMs to safely write to the same log file
     * concurrently. This information is used by rolling policies to determine
     * their file handling approach. Some rolling policies (e.g., FixedWindowRollingPolicy)
     * do not support prudent mode.
     *
     * @return 부모 Appender가 Prudent 모드이면 true, 그렇지 않으면 false
     * true if the parent appender is in prudent mode, false otherwise
     */
    public boolean isParentPrudent() {
        return super.isParentPrudent();
    }

    /**
     * 직접적으로 사용하지 않지만 동작 원리 이해를 위해 명시적으로 선언합니다.
     * <p>
     * 부모 FileAppender에 설정된 파일 경로를 반환합니다. 이 값은 롤링 과정에서
     * 현재 활성 로그 파일을 식별하고 처리할 때 사용됩니다. 압축이나 파일 이름 변경
     * 작업 시 원본 파일의 위치를 파악하는 데 필요합니다.
     * <p>
     * This method returns the file path configured in the parent FileAppender.
     * This value is used during the rolling process to identify and handle
     * the currently active log file. It is needed for compression and file
     * renaming operations to determine the original file location.
     *
     * @return 부모 Appender에 설정된 원본 파일 경로, 설정되지 않았으면 null
     * the raw file path configured in parent appender, or null if not set
     */
    public String getParentsRawFileProperty() {
        return super.getParentsRawFileProperty();
    }


    @Override
    public String toString() {
        return "o.s.l.CronBasedRollingPolicy";
    }

    public String getString(Object obj) {
        try {
            return String.valueOf(obj);
        } catch (Exception cause) {
            return cause.toString();
        }
    }

    public Path createFileIfNotExists(String filePathStr) {
        try {
            Path path = Paths.get(filePathStr);

            // 1. 상위 디렉토리 생성 (null 체크 포함)
            Path parentDir = path.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir); // 디렉토리 없으면 생성, 있으면 무시
            }

            // 2. 파일 생성 (없으면 생성, 있으면 무시)
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            return path;

        } catch (IOException e) {
            throw new RuntimeException("파일 생성 실패: ", e);
        }
    }
}

