package org.sw.logback;

import ch.qos.logback.core.rolling.LengthCounter;
import ch.qos.logback.core.rolling.RollingPolicyBase;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import org.springframework.scheduling.support.CronExpression;

import java.io.File;
import java.time.LocalDateTime;

public class CronRollingPolicy<E> extends RollingPolicyBase implements TriggeringPolicy<E> {

    private CronExpression cron = CronExpression.parse("0 0 0 * * *");

    @Override
    public void start() {
        System.out.println("CronRollingPolicy.start()");

        LocalDateTime now = LocalDateTime.now();

//        LocalDateTime previous = cron.previous(now); // 직전 실행 시간
        LocalDateTime next = cron.next(now); // 다음 실행 시간

//        System.out.println("직전 실행 시간: " + previous);
        System.out.println("다음 실행 시간: " + next);
    }

    @Override
    public LengthCounter getLengthCounter() {
        return TriggeringPolicy.super.getLengthCounter();
    }

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        return false;
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public void rollover() throws RolloverFailure {

    }

    @Override
    public String getActiveFileName() {
        return "";
    }

    /**
     * 롤링 정책의 크론 표현식을 설정합니다.
     * <p>
     * Logback 설정 시스템에서 logback.xml의 <cron/> 엘리먼트를 처리할 때
     * 자동으로 호출되며, 크론 표현식을 Logger Context에 주입하여 로그 파일
     * 롤링 스케줄을 설정합니다.
     * </p>
     *
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
        if(CronExpression.isValidExpression(cron)){
            this.cron = CronExpression.parse(cron);
        }
        addError("Invalid Cron Expression: " + cron);
    }
}
