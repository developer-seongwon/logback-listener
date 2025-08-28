package org.sw.logback;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.joran.GenericXMLConfigurator;
import ch.qos.logback.core.rolling.LengthCounter;
import ch.qos.logback.core.rolling.RollingPolicyBase;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.rolling.helper.Compressor;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import org.springframework.scheduling.support.CronExpression;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

public class CronRollingPolicy<E> extends RollingPolicyBase implements TriggeringPolicy<E> {

    private CronExpression cron = CronExpression.parse("0 0 0 * * *");
    private Compressor compressor;
    FileNamePattern fileNamePattern;

    @Override
    public void start() {
        System.out.println("CronRollingPolicy.start()");

        Date now = new Date();
        Date previous = CronUtil.previous(this.cron.toString(), now);
        LocalDateTime next = cron.next(now.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        System.out.println("날짜 확인: " + previous.toString() + " -> " + now + " -> " + next.toString());

        System.out.println("파일 확인:" + getFileNamePattern());
        this.fileNamePattern = new FileNamePattern(getFileNamePattern(), getContext());

//        this.fileNamePattern = new FileNamePattern(getFileNamePattern(), getContext());
//        this.fileNamePattern.convertMultipleArguments(now, new)
//        for(StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()){
//            System.out.println(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" + stackTraceElement.getLineNumber() + ")");
//        }
//
//        GenericXMLConfigurator a;

        super.start();
    }

    @Override
    public LengthCounter getLengthCounter() {
        return TriggeringPolicy.super.getLengthCounter();
    }

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        System.out.println("CronRollingPolicy.isTriggeringEvent()");
        return false;
    }

    @Override
    public void rollover() throws RolloverFailure {
        System.out.println("CronRollingPolicy.rollover()");
    }

    @Override
    public String getActiveFileName() {
        return Optional.ofNullable(getParentsRawFileProperty()).orElseGet(() ->
                "asd"
        );

//        if (parentsRawFileProperty != null) {
//            return parentsRawFileProperty;
//        } else {
//            return timeBasedFileNamingAndTriggeringPolicy.getCurrentPeriodsFileNameWithoutCompressionSuffix();
//        }
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
    public void setCron(String cron) {
        if (CronExpression.isValidExpression(cron)) {
            this.cron = CronExpression.parse(cron);
        } else {
            addError("Invalid Cron Expression: " + cron);
        }
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
}
