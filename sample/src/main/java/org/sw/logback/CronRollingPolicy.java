package org.sw.logback;

import ch.qos.logback.core.joran.GenericXMLConfigurator;
import ch.qos.logback.core.rolling.LengthCounter;
import ch.qos.logback.core.rolling.RollingPolicyBase;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import org.springframework.scheduling.support.CronExpression;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class CronRollingPolicy<E> extends RollingPolicyBase implements TriggeringPolicy<E> {

    private CronExpression cron = CronExpression.parse("0 0 0 * * *");
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
        return "./logs/sad";
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
}
