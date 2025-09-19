package org.sw.logback.cron;

import ch.qos.logback.core.rolling.helper.RollingCalendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RollingCalender 기능 테스트")
public class RollingCalendarTest {

    private static final Logger logger = LoggerFactory.getLogger(CronCalculatorTest.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime reference = LocalDateTime.of(
            LocalDate.of(2025, 5, 1),
            LocalTime.of(0, 0, 0, 0)
    );

    @BeforeEach
    void setUp() {
        logger.info("=== 테스트 셋업 완료: 기준 시간 {} ===", reference.toString());
    }


    @ParameterizedTest
    @DisplayName("기본 기능 테스트")
    @CsvSource({
            "'yyyyMMddHHmm', '분 단위 로그 파일'",
            "'yyyyMMddHH', '시 단위 로그 파일'",
            "'yyyyMMdd', '일 단위 로그 파일'",
            "'yyyyMM', '월 단위 로그 파일'"
    })
    void testBaseRollingPatterns(String pattern, String desc){
        logger.info(" [{}][{}] 테스트 시작", pattern, desc);
        RollingCalendar calendar = new RollingCalendar(pattern);

        System.out.println(Date.from(calendar.getEndOfNextNthPeriod(reference.atZone(ZoneOffset.systemDefault()).toInstant(), 1)));
        System.out.println(Date.from(calendar.getEndOfNextNthPeriod(reference.atZone(ZoneOffset.systemDefault()).toInstant(), -1)));

        assertTrue(true);
    }

//    @Test
//    @DisplayName("참조 시간 지정 생성자 테스트")
//    void testConstructorWithReference() throws ParseException {
//        Date reference = parseDate("2024-01-15 09:00:00");
//        CronCalculator calc = new CronCalculator("0 0 12 * * ?", reference);
//
//        assertEquals(reference, calc.getReference());
//        assertEquals("0 0 12 * * ?", calc.getCron());
//
//        logger.info("✅ 참조 시간 지정 생성자 테스트 완료");
//    }
//
//    @Test
//    @DisplayName("잘못된 크론 표현식 예외 테스트")
//    void testInvalidCronExpression() {
//        assertThrows(ParseException.class, () -> new CronCalculator("invalid cron expression"));
//        logger.info("✅ 잘못된 크론 표현식 예외 테스트 완료");
//    }
//
//    @Test
//    @DisplayName("기본 시간 계산 테스트")
//    void testBasicTimeCalculation() throws ParseException {
//        String cronExpr = "0 0 12 * * ?";
//        logger.info("🔍 [기본 시간 계산] 테스트 시작");
//        logger.info("📅 테스트 기준 시간: {}", formatDate(testReference));
//        logger.info("⏰ 크론 표현식: {}", cronExpr);
//
//        CronCalculator calc = new CronCalculator(cronExpr, testReference);
//
//        Date previousTime = calc.getPreviousFireTime();
//        Date nextTime = calc.getFireTime();
//        Date afterNext = calc.getNextFireTime();
//
//        // 검증 결과
//        boolean previousNotNull = previousTime != null;
//        boolean nextNotNull = nextTime != null;
//        boolean afterNextNotNull = afterNext != null;
//        boolean previousBeforeRef = previousNotNull && previousTime.before(testReference);
//        boolean nextAfterRef = nextNotNull && nextTime.after(testReference);
//        boolean nextBeforeAfter = nextNotNull && afterNextNotNull && nextTime.before(afterNext);
//
//        logger.info("📊 계산 결과:");
//        logger.info("   이전 실행시간: {} (NotNull: {})",
//                previousTime != null ? formatDate(previousTime) : "null", previousNotNull);
//        logger.info("   다음 실행시간: {} (NotNull: {})",
//                nextTime != null ? formatDate(nextTime) : "null", nextNotNull);
//        logger.info("   그다음 실행시간: {} (NotNull: {})",
//                afterNext != null ? formatDate(afterNext) : "null", afterNextNotNull);
//
//        logger.info("🧪 검증 결과:");
//        logger.info("   이전시간 < 기준시간: {}", previousBeforeRef);
//        logger.info("   다음시간 > 기준시간: {}", nextAfterRef);
//        logger.info("   다음시간 < 그다음시간: {}", nextBeforeAfter);
//
//        boolean testPassed = previousNotNull && nextNotNull && afterNextNotNull &&
//                previousBeforeRef && nextAfterRef && nextBeforeAfter;
//        logger.info("✅ 기본 시간 계산 테스트 {}", testPassed ? "성공" : "실패");
//
//        assertNotNull(previousTime);
//        assertNotNull(nextTime);
//        assertNotNull(afterNext);
//        assertTrue(previousTime.before(testReference));
//        assertTrue(nextTime.after(testReference));
//        assertTrue(nextTime.before(afterNext));
//    }
//}
}
