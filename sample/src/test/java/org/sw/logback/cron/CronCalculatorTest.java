package org.sw.logback.cron;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CronCalculator 종합 테스트")
class CronCalculatorTest {

    private static final Logger logger = LoggerFactory.getLogger(CronCalculatorTest.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private CronCalculator calculator;
    private Date testReference;

    @BeforeEach
    void setUp() {
        testReference = parseDate("2024-01-15 10:30:00");
        logger.info("=== 테스트 셋업 완료: 기준시간 = {} ===", formatDate(testReference));
    }

    @Nested
    @DisplayName("생성자 테스트")
    class ConstructorTests {

        @Test
        @DisplayName("기본 생성자")
        void testDefaultConstructor() throws ParseException {
            logger.info("▶️ 기본 생성자 테스트 시작");

            CronCalculator calc = new CronCalculator("0 0 12 * * ?");

            logger.info("생성된 CronCalculator - 크론식: {}", calc.getCron());
            logger.info("참조 시간: {}", formatDate(calc.getReference()));

            assertNotNull(calc.getReference());
            assertNotNull(calc.getCron());
            assertEquals("0 0 12 * * ?", calc.getCron());

            logger.info("✅ 기본 생성자 테스트 완료");
        }

        @Test
        @DisplayName("참조 시간 지정 생성자")
        void testConstructorWithReference() throws ParseException {
            logger.info("▶️ 참조 시간 지정 생성자 테스트 시작");

            Date reference = parseDate("2024-01-15 09:00:00");
            logger.info("입력 참조 시간: {}", formatDate(reference));

            CronCalculator calc = new CronCalculator("0 0 12 * * ?", reference);

            logger.info("생성된 CronCalculator - 크론식: {}, 참조시간: {}",
                    calc.getCron(), formatDate(calc.getReference()));

            assertEquals(reference, calc.getReference());
            assertEquals("0 0 12 * * ?", calc.getCron());

            logger.info("✅ 참조 시간 지정 생성자 테스트 완료");
        }

        @Test
        @DisplayName("잘못된 크론 표현식으로 예외 발생")
        void testInvalidCronExpression() {
            logger.info("▶️ 잘못된 크론 표현식 예외 테스트 시작");

            ParseException exception = assertThrows(ParseException.class,
                    () -> new CronCalculator("invalid cron expression"));

            logger.info("예상대로 ParseException 발생: {}", exception.getMessage());
            logger.info("✅ 잘못된 크론 표현식 예외 테스트 완료");
        }
    }

    @Nested
    @DisplayName("시간 계산 테스트")
    class TimeCalculationTests {

        @BeforeEach
        void setUp() throws ParseException {
            calculator = new CronCalculator("0 0 12 * * ?", testReference);
            logger.info("시간 계산 테스트용 calculator 초기화 완료 - 크론: '매일 12시'");
        }

        @Test
        @DisplayName("getFireTime - 다음 실행 시간")
        void testGetFireTime() {
            logger.info("▶️ getFireTime 테스트 시작");
            logger.info("기준 시간: {} (10:30)", formatDate(testReference));

            Date fireTime = calculator.getFireTime();

            logger.info("계산된 다음 실행 시간: {}", formatDate(fireTime));

            assertNotNull(fireTime);
            Date expected = parseDate("2024-01-15 12:00:00");
            logger.info("예상 시간: {}", formatDate(expected));

            assertEquals(expected, fireTime);
            logger.info("✅ getFireTime 테스트 완료");
        }

        @Test
        @DisplayName("getPreviousFireTime - 이전 실행 시간")
        void testGetPreviousFireTime() {
            logger.info("▶️ getPreviousFireTime 테스트 시작");
            logger.info("기준 시간: {}", formatDate(testReference));

            Date previousTime = calculator.getPreviousFireTime();

            logger.info("계산된 이전 실행 시간: {}", formatDate(previousTime));

            assertNotNull(previousTime);
            Date expected = parseDate("2024-01-14 12:00:00");
            logger.info("예상 시간: {}", formatDate(expected));

            assertEquals(expected, previousTime);
            logger.info("✅ getPreviousFireTime 테스트 완료");
        }

        @Test
        @DisplayName("getNextFireTime - 그 다음 실행 시간")
        void testGetNextFireTime() {
            logger.info("▶️ getNextFireTime 테스트 시작");
            logger.info("기준 시간: {}", formatDate(testReference));

            Date nextTime = calculator.getNextFireTime();

            logger.info("계산된 그 다음 실행 시간: {}", formatDate(nextTime));

            assertNotNull(nextTime);
            Date expected = parseDate("2024-01-16 12:00:00");
            logger.info("예상 시간: {}", formatDate(expected));

            assertEquals(expected, nextTime);
            logger.info("✅ getNextFireTime 테스트 완료");
        }
    }

    @Nested
    @DisplayName("다양한 크론 표현식 테스트")
    class CronExpressionTests {

        @ParameterizedTest
        @DisplayName("매분 실행 크론 테스트")
        @ValueSource(strings = {"0 * * * * ?", "0 0/1 * * * ?"})
        void testEveryMinuteCron(String cronExpr) throws ParseException {
            logger.info("▶️ 매분 실행 크론 테스트 시작 - 크론식: '{}'", cronExpr);

            Date reference = parseDate("2024-01-15 10:30:30");
            logger.info("기준 시간: {} (30초)", formatDate(reference));

            CronCalculator calc = new CronCalculator(cronExpr, reference);

            Date fireTime = calc.getFireTime();

            Date expected = parseDate("2024-01-15 10:31:00");
            logger.info("계산된 다음 실행 시간: {}", formatDate(fireTime));
            logger.info("예상 시간: {}", formatDate(expected));

            assertEquals(expected, fireTime);
            logger.info("✅ 매분 실행 크론 테스트 완료");
        }

        @ParameterizedTest
        @DisplayName("다양한 시간 간격 크론 테스트")
        @CsvSource({
                "'0 0 9 * * ?', '2024-01-15 08:00:00', '2024-01-15 09:00:00'",
                "'0 30 14 * * ?', '2024-01-15 10:00:00', '2024-01-15 14:30:00'",
                "'0 0 0 ? * MON', '2024-01-15 10:00:00', '2024-01-22 00:00:00'"
        })
        void testVariousCronExpressions(String cronExpr, String referenceStr, String expectedStr) throws ParseException {
            logger.info("▶️ 크론 표현식 테스트 시작");
            logger.info("크론식: '{}'", cronExpr);
            logger.info("기준 시간: {}", referenceStr);
            logger.info("예상 결과: {}", expectedStr);

            Date reference = parseDate(referenceStr);
            CronCalculator calc = new CronCalculator(cronExpr, reference);

            Date fireTime = calc.getFireTime();

            Date expected = parseDate(expectedStr);
            logger.info("계산 결과: {}", formatDate(fireTime));

            assertEquals(expected, fireTime);
            logger.info("✅ 크론 표현식 테스트 완료");
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class EdgeCaseTests {

        @Test
        @DisplayName("실행 시간과 정확히 일치하는 참조 시간")
        void testReferenceExactlyAtExecutionTime() throws ParseException {
            logger.info("▶️ 실행 시간 정확 일치 테스트 시작");

            Date exactTime = parseDate("2024-01-15 12:00:00");
            logger.info("참조 시간이 크론 실행 시간과 정확히 일치: {}", formatDate(exactTime));

            CronCalculator calc = new CronCalculator("0 0 12 * * ?", exactTime);

            Date fireTime = calc.getFireTime();

            Date expectedNext = parseDate("2024-01-16 12:00:00");
            logger.info("계산된 다음 실행 시간: {}", formatDate(fireTime));
            logger.info("예상 시간: {}", formatDate(expectedNext));

            assertEquals(expectedNext, fireTime);
            logger.info("✅ 실행 시간 정확 일치 테스트 완료");
        }

        @Test
        @DisplayName("월말 경계 테스트")
        void testMonthBoundary() throws ParseException {
            logger.info("▶️ 월말 경계 테스트 시작");

            Date endOfMonth = parseDate("2024-01-31 23:59:59");
            logger.info("월말 시점: {}", formatDate(endOfMonth));

            CronCalculator calc = new CronCalculator("0 0 0 1 * ?", endOfMonth);

            Date fireTime = calc.getFireTime();

            Date expectedNext = parseDate("2024-02-01 00:00:00");
            logger.info("계산된 다음 실행 시간: {}", formatDate(fireTime));
            logger.info("예상 시간: {}", formatDate(expectedNext));

            assertEquals(expectedNext, fireTime);
            logger.info("✅ 월말 경계 테스트 완료");
        }
    }

    // ==================== 유틸리티 메소드 ====================

    private Date parseDate(String dateStr) {
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("날짜 파싱 실패: " + dateStr, e);
        }
    }

    private String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }
}
