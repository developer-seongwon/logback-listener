package org.sw.logback.cron;


import ch.qos.logback.core.rolling.helper.RollingCalendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sw.logback.CronRollingCalendar;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CronRollingCalendar & RollingCalendar 비교 테스트")
public class CronRollingCalendarTest {

    private static final Logger logger = LoggerFactory.getLogger(CronRollingCalendarTest.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime reference = LocalDateTime.of(
            LocalDate.of(2025, 5, 1),
            LocalTime.of(0, 1, 10, 0)
    );

    @BeforeEach
    void setUp() {
        logger.info("=== 테스트 셋업 완료: 기준 시간 {} ===", reference.toString());
    }

    @ParameterizedTest
    @DisplayName("롤링 비교 테스트")
    @CsvSource({
            "'0 * * * * ?', 'yyyyMMddHHmm', '분 단위 로그 파일'",
            "'0 0 * * * ?', 'yyyyMMddHH', '시 단위 로그 파일'",
            "'0 0 0 * * ?', 'yyyyMMdd', '일 단위 로그 파일'",
            "'0 0 0 1 * ?', 'yyyyMM', '월 단위 로그 파일'"
    })
    void testBaseRollingPatterns(String cronPattern, String datePattern, String desc) {
        logger.info("[{}][{}][{}] 테스트 시작", cronPattern, datePattern, desc);

        Date date = Date.from(reference.atZone(ZoneOffset.systemDefault()).toInstant());

        CronRollingCalendar cc = CronRollingCalendar.newBuilder()
                .setReference(date)
                .setCronPattern(cronPattern)
                .setDatePattern(datePattern)
                .build();
        String ccString = toLocalDateTime(cc.getNextTriggeringDate(date.toInstant())).toString();

        RollingCalendar rc = new RollingCalendar(datePattern);
        String rcString = toLocalDateTime(rc.getNextTriggeringDate(date.toInstant())).toString();

        logger.info("[{}][{}][{}] CC-{}", cronPattern, datePattern, desc, ccString);
        logger.info("[{}][{}][{}] RC-{}", cronPattern, datePattern, desc, rcString);
        assertEquals(ccString, rcString);
    }

    @ParameterizedTest
    @DisplayName("N번 비교 테스트")
    @CsvSource({
            "'0 * * * * ?', 'yyyyMMddHHmm', '분 단위 로그 파일'",
            "'0 0 * * * ?', 'yyyyMMddHH', '시 단위 로그 파일'",
            "'0 0 0 * * ?', 'yyyyMMdd', '일 단위 로그 파일'",
            "'0 0 0 1 * ?', 'yyyyMM', '월 단위 로그 파일'"
    })
    void testJumpRollingPatterns(String cronPattern, String datePattern, String desc) {
        logger.info("[{}][{}][{}] 테스트 시작", cronPattern, datePattern, desc);

        Date date = Date.from(reference.atZone(ZoneOffset.systemDefault()).toInstant());

        RollingCalendar rc = new RollingCalendar(datePattern);
        logger.info("[{}][{}][{}] -2회 - {}", cronPattern, datePattern, desc, rc.getEndOfNextNthPeriod(date.toInstant(), -2));
        logger.info("[{}][{}][{}] -1회 - {}", cronPattern, datePattern, desc, rc.getEndOfNextNthPeriod(date.toInstant(), -1));
        logger.info("[{}][{}][{}] +0회 - {}", cronPattern, datePattern, desc, rc.getEndOfNextNthPeriod(date.toInstant(), 0));
        logger.info("[{}][{}][{}] +1회 - {}", cronPattern, datePattern, desc, rc.getEndOfNextNthPeriod(date.toInstant(), 1));
        logger.info("[{}][{}][{}] +2회 - {}", cronPattern, datePattern, desc, rc.getEndOfNextNthPeriod(date.toInstant(), 2));


    }

    public LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault());
    }
}
