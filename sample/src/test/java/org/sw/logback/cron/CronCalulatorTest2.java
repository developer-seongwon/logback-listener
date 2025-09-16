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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

//@DisplayName(\"CronCalculator 종합 테스트\")
class CronCaluatorTest2 {

}
//
//    private static final Logger logger = LoggerFactory.getLogger(CronCalculatorTest.class);
//    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");
//    private CronCalculator calculator;
//    private Date testReference;
//
//    @BeforeEach
//    void setUp() {
//        // 기본 테스트용 기준 시간: 2024-01-15 10:30:00
//        testReference = parseDate(\"2024-01-15 10:30:00\");
//                logger.info(\"=== 테스트 셋업 완료: 기준시간 = {} ===\", formatDate(testReference));
//    }
//
//    @Nested
//    @DisplayName(\"생성자 테스트\")
//    class ConstructorTests {
//
//        @Test
//        @DisplayName(\"기본 생성자 - 현재 시간으로 초기화\")
//        void testDefaultConstructor() throws ParseException {
//            logger.info(\"▶️ 기본 생성자 테스트 시작\");
//
//                    // Given & When
//                    CronCalculator calc = new CronCalculator(\"0 0 12 * * ?\");
//
//                            // Then
//                            logger.info(\"생성된 CronCalculator - 크론식: {}\", calc.getCron());
//                                    logger.info(\"참조 시간: {}\", formatDate(calc.getReference()));
//
//                                            assertNotNull(calc.getReference());
//            assertNotNull(calc.getCron());
//            assertEquals(\"0 0 12 * * ?\", calc.getCron());
//
//                    logger.info(\"✅ 기본 생성자 테스트 완료\");
//        }
//
//        @Test
//        @DisplayName(\"참조 시간 지정 생성자\")
//        void testConstructorWithReference() throws ParseException {
//            logger.info(\"▶️ 참조 시간 지정 생성자 테스트 시작\");
//
//                    // Given
//                    Date reference = parseDate(\"2024-01-15 09:00:00\");
//                            logger.info(\"입력 참조 시간: {}\", formatDate(reference));
//
//                                    // When
//                                    CronCalculator calc = new CronCalculator(\"0 0 12 * * ?\", reference);
//
//                                            // Then
//                                            logger.info(\"생성된 CronCalculator - 크론식: {}, 참조시간: {}\",
//                                                    calc.getCron(), formatDate(calc.getReference()));
//
//            assertEquals(reference, calc.getReference());
//            assertEquals(\"0 0 12 * * ?\", calc.getCron());
//
//                    logger.info(\"✅ 참조 시간 지정 생성자 테스트 완료\");
//        }
//
//        @Test
//        @DisplayName(\"잘못된 크론 표현식으로 예외 발생\")
//        void testInvalidCronExpression() {
//            logger.info(\"▶️ 잘못된 크론 표현식 예외 테스트 시작\");
//
//                    // Given & When & Then
//                    ParseException exception = assertThrows(ParseException.class,
//                            () -> new CronCalculator(\"invalid cron expression\"));
//
//                                    logger.info(\"예상대로 ParseException 발생: {}\", exception.getMessage());
//                                            logger.info(\"✅ 잘못된 크론 표현식 예외 테스트 완료\");
//        }
//    }
//
//    @Nested
//    @DisplayName(\"시간 계산 테스트\")
//    class TimeCalculationTests {
//
//        @BeforeEach
//        void setUp() throws ParseException {
//            calculator = new CronCalculator(\"0 0 12 * * ?\", testReference); // 매일 12시
//                    logger.info(\"시간 계산 테스트용 calculator 초기화 완료 - 크론: '매일 12시'\");
//        }
//
//        @Test
//        @DisplayName(\"getFireTime - 다음 실행 시간\")
//        void testGetFireTime() {
//            logger.info(\"▶️ getFireTime 테스트 시작\");
//                    logger.info(\"기준 시간: {} (10:30)\", formatDate(testReference));
//
//                            // Given & When
//                            Date fireTime = calculator.getFireTime();
//
//            // Then
//            logger.info(\"계산된 다음 실행 시간: {}\", formatDate(fireTime));
//
//                    assertNotNull(fireTime);
//            Date expected = parseDate(\"2024-01-15 12:00:00\");
//                    logger.info(\"예상 시간: {}\", formatDate(expected));
//
//                            assertEquals(expected, fireTime);
//            logger.info(\"✅ getFireTime 테스트 완료 - 기준 시간(10:30) 이후 첫 실행시간은 12:00\");
//        }
//
//        @Test
//        @DisplayName(\"getPreviousFireTime - 이전 실행 시간\")
//        void testGetPreviousFireTime() {
//            logger.info(\"▶️ getPreviousFireTime 테스트 시작\");
//                    logger.info(\"기준 시간: {}\", formatDate(testReference));
//
//                            // Given & When
//                            Date previousTime = calculator.getPreviousFireTime();
//
//            // Then
//            logger.info(\"계산된 이전 실행 시간: {}\", formatDate(previousTime));
//
//                    assertNotNull(previousTime);
//            Date expected = parseDate(\"2024-01-14 12:00:00\");
//                    logger.info(\"예상 시간: {}\", formatDate(expected));
//
//                            assertEquals(expected, previousTime);
//            logger.info(\"✅ getPreviousFireTime 테스트 완료 - 기준 시간 이전 마지막 실행시간은 어제 12:00\");
//        }
//
//        @Test
//        @DisplayName(\"getNextFireTime - 그 다음 실행 시간\")
//        void testGetNextFireTime() {
//            logger.info(\"▶️ getNextFireTime 테스트 시작\");
//                    logger.info(\"기준 시간: {}\", formatDate(testReference));
//
//                            // Given & When
//                            Date nextTime = calculator.getNextFireTime();
//
//            // Then
//            logger.info(\"계산된 그 다음 실행 시간: {}\", formatDate(nextTime));
//
//                    assertNotNull(nextTime);
//            Date expected = parseDate(\"2024-01-16 12:00:00\");
//                    logger.info(\"예상 시간: {}\", formatDate(expected));
//
//                            assertEquals(expected, nextTime);
//            logger.info(\"✅ getNextFireTime 테스트 완료 - 다음 실행시간의 그 다음은 내일 12:00\");
//        }
//    }
//
//    @Nested
//    @DisplayName(\"다양한 크론 표현식 테스트\")
//    class CronExpressionTests {
//
//        @ParameterizedTest
//        @DisplayName(\"매분 실행 크론 테스트\")
//        @ValueSource(strings = {\"0 * * * * ?\", \"0 0/1 * * * ?\"})
//        void testEveryMinuteCron(String cronExpr) throws ParseException {
//            logger.info(\"▶️ 매분 실행 크론 테스트 시작 - 크론식: '{}'\", cronExpr);
//
//                    // Given
//                    Date reference = parseDate(\"2024-01-15 10:30:30\");
//                            logger.info(\"기준 시간: {} (30초)\", formatDate(reference));
//
//                                    CronCalculator calc = new CronCalculator(cronExpr, reference);
//
//            // When
//            Date fireTime = calc.getFireTime();
//
//            // Then
//            Date expected = parseDate(\"2024-01-15 10:31:00\");
//                    logger.info(\"계산된 다음 실행 시간: {}\", formatDate(fireTime));
//                            logger.info(\"예상 시간: {}\", formatDate(expected));
//
//                                    assertEquals(expected, fireTime);
//            logger.info(\"✅ 매분 실행 크론 테스트 완료 - 30초 → 다음 분 00초\");
//        }
//
//        @ParameterizedTest
//        @DisplayName(\"다양한 시간 간격 크론 테스트\")
//        @CsvSource({
//            \"'0 0 9 * * ?', '2024-01-15 08:00:00', '2024-01-15 09:00:00'\", // 매일 9시
//                    \"'0 30 14 * * ?', '2024-01-15 10:00:00', '2024-01-15 14:30:00'\", // 매일 14:30
//                    \"'0 0 0 ? * MON', '2024-01-15 10:00:00', '2024-01-22 00:00:00'\" // 매주 월요일
//    })
//    void testVariousCronExpressions(String cronExpr, String referenceStr, String expectedStr) throws ParseException {
//        logger.info(\"▶️ 크론 표현식 테스트 시작\");
//                logger.info(\"크론식: '{}'\", cronExpr);
//                        logger.info(\"기준 시간: {}\", referenceStr);
//                                logger.info(\"예상 결과: {}\", expectedStr);
//
//                                        // Given
//                                        Date reference = parseDate(referenceStr);
//        CronCalculator calc = new CronCalculator(cronExpr, reference);
//
//        // When
//        Date fireTime = calc.getFireTime();
//
//        // Then
//        Date expected = parseDate(expectedStr);
//        logger.info(\"계산 결과: {}\", formatDate(fireTime));
//
//                assertEquals(expected, fireTime);
//        logger.info(\"✅ 크론 표현식 테스트 완료 - 정확히 매치됨\");
//    }
//}
//
//@Nested
//@DisplayName(\"setReference 캐시 최적화 테스트\")
//class CacheOptimizationTests {
//
//    @BeforeEach
//    void setUp() throws ParseException {
//        calculator = new CronCalculator(\"0 0 12 * * ?\", testReference); // 매일 12시
//                logger.info(\"캐시 최적화 테스트용 calculator 초기화 - 기준: {}\", formatDate(testReference));
//    }
//
//    @Test
//    @DisplayName(\"동일한 참조 시간 설정 - 변경 없음\")
//    void testSetSameReference() {
//        logger.info(\"▶️ 동일 참조시간 설정 테스트 시작\");
//
//                // Given
//                Date originalFireTime = calculator.getFireTime();
//        logger.info(\"원본 다음 실행 시간: {}\", formatDate(originalFireTime));
//
//                // When - 동일한 참조 시간으로 설정
//                logger.info(\"동일한 참조 시간으로 재설정: {}\", formatDate(testReference));
//                        calculator.setReference(testReference);
//
//        // Then
//        Date newFireTime = calculator.getFireTime();
//        logger.info(\"재설정 후 다음 실행 시간: {}\", formatDate(newFireTime));
//
//                assertEquals(testReference, calculator.getReference());
//        assertEquals(originalFireTime, newFireTime);
//        logger.info(\"✅ 동일 참조시간 설정 테스트 완료 - 캐시 히트로 변경 없음\");
//    }
//
//    @Test
//    @DisplayName(\"미래 시간으로 참조 변경 - 슬라이딩 최적화\")
//    void testSetFutureReference() {
//        logger.info(\"▶️ 미래 시간으로 참조 변경 테스트 시작\");
//
//                // Given
//                Date futureReference = parseDate(\"2024-01-15 11:00:00\");
//                        logger.info(\"원래 기준: {} → 새 기준: {}\", formatDate(testReference), formatDate(futureReference));
//
//                                // When
//                                calculator.setReference(futureReference);
//
//        // Then
//        Date fireTime = calculator.getFireTime();
//        Date expectedFireTime = parseDate(\"2024-01-15 12:00:00\");
//
//                logger.info(\"새 다음 실행 시간: {}\", formatDate(fireTime));
//                        logger.info(\"예상 시간: {}\", formatDate(expectedFireTime));
//
//                                assertEquals(futureReference, calculator.getReference());
//        assertEquals(expectedFireTime, fireTime);
//        logger.info(\"✅ 미래 시간 참조 변경 테스트 완료 - 슬라이딩 최적화 적용\");
//    }
//
//    @Test
//    @DisplayName(\"과거 시간으로 참조 변경\")
//    void testSetPastReference() {
//        logger.info(\"▶️ 과거 시간으로 참조 변경 테스트 시작\");
//
//                // Given
//                Date pastReference = parseDate(\"2024-01-15 08:00:00\");
//                        logger.info(\"원래 기준: {} → 새 기준: {}\", formatDate(testReference), formatDate(pastReference));
//
//                                // When
//                                calculator.setReference(pastReference);
//
//        // Then
//        Date fireTime = calculator.getFireTime();
//        Date expectedFireTime = parseDate(\"2024-01-15 12:00:00\");
//
//                logger.info(\"새 다음 실행 시간: {}\", formatDate(fireTime));
//                        logger.info(\"예상 시간: {}\", formatDate(expectedFireTime));
//
//                                assertEquals(pastReference, calculator.getReference());
//        assertEquals(expectedFireTime, fireTime);
//        logger.info(\"✅ 과거 시간 참조 변경 테스트 완료\");
//    }
//
//    @Test
//    @DisplayName(\"LocalDateTime으로 참조 시간 설정\")
//    void testSetReferenceWithLocalDateTime() {
//        logger.info(\"▶️ LocalDateTime 참조 설정 테스트 시작\");
//
//                // Given
//                LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 15, 14, 0, 0);
//        logger.info(\"LocalDateTime 입력: {}\", localDateTime);
//
//                // When
//                calculator.setReference(localDateTime);
//
//        // Then
//        Date fireTime = calculator.getFireTime();
//        Date expectedFireTime = parseDate(\"2024-01-16 12:00:00\");
//
//                logger.info(\"변환된 기준 시간: {}\", formatDate(calculator.getReference()));
//                        logger.info(\"계산된 다음 실행 시간: {}\", formatDate(fireTime));
//                                logger.info(\"예상 시간: {}\", formatDate(expectedFireTime));
//
//                                        assertEquals(expectedFireTime, fireTime);
//        logger.info(\"✅ LocalDateTime 참조 설정 테스트 완료\");
//    }
//}
//
//@Nested
//@DisplayName(\"경계값 테스트\")
//class EdgeCaseTests {
//
//    @Test
//    @DisplayName(\"실행 시간과 정확히 일치하는 참조 시간\")
//    void testReferenceExactlyAtExecutionTime() throws ParseException {
//        logger.info(\"▶️ 실행 시간 정확 일치 테스트 시작\");
//
//                // Given - 참조 시간이 크론 실행 시간과 정확히 일치
//                Date exactTime = parseDate(\"2024-01-15 12:00:00\");
//                        logger.info(\"참조 시간이 크론 실행 시간과 정확히 일치: {}\", formatDate(exactTime));
//
//                                CronCalculator calc = new CronCalculator(\"0 0 12 * * ?\", exactTime);
//
//                                        // When
//                                        Date fireTime = calc.getFireTime();
//
//        // Then
//        Date expectedNext = parseDate(\"2024-01-16 12:00:00\");
//                logger.info(\"계산된 다음 실행 시간: {}\", formatDate(fireTime));
//                        logger.info(\"예상 시간: {}\", formatDate(expectedNext));
//
//                                assertEquals(expectedNext, fireTime);
//        logger.info(\"✅ 실행 시간 정확 일치 테스트 완료 - 정확히 일치할 때는 다음 주기로 넘어감\");
//    }
//
//    @Test
//    @DisplayName(\"월말 경계 테스트\")
//    void testMonthBoundary() throws ParseException {
//        logger.info(\"▶️ 월말 경계 테스트 시작\");
//
//                // Given
//                Date endOfMonth = parseDate(\"2024-01-31 23:59:59\");
//                        logger.info(\"월말 시점: {}\", formatDate(endOfMonth));
//                                logger.info(\"크론식: '매월 1일 0시'\");
//
//                                        CronCalculator calc = new CronCalculator(\"0 0 0 1 * ?\", endOfMonth);
//
//                                                // When
//                                                Date fireTime = calc.getFireTime();
//
//        // Then
//        Date expectedNext = parseDate(\"2024-02-01 00:00:00\");
//                logger.info(\"계산된 다음 실행 시간: {}\", formatDate(fireTime));
//                        logger.info(\"예상 시간: {}\", formatDate(expectedNext));
//
//                                assertEquals(expectedNext, fireTime);
//        logger.info(\"✅ 월말 경계 테스트 완료 - 1월 → 2월 정상 전환\");
//    }
//
//    @Test
//    @DisplayName(\"연말 경계 테스트\")
//    void testYearBoundary() throws ParseException {
//        logger.info(\"▶️ 연말 경계 테스트 시작\");
//
//                // Given
//                Date endOfYear = parseDate(\"2024-12-31 23:59:59\");
//                        logger.info(\"연말 시점: {}\", formatDate(endOfYear));
//                                logger.info(\"크론식: '매년 1월 1일 0시'\");
//
//                                        CronCalculator calc = new CronCalculator(\"0 0 0 1 1 ?\", endOfYear);
//
//                                                // When
//                                                Date fireTime = calc.getFireTime();
//
//        // Then
//        Date expectedNext = parseDate(\"2025-01-01 00:00:00\");
//                logger.info(\"계산된 다음 실행 시간: {}\", formatDate(fireTime));
//                        logger.info(\"예상 시간: {}\", formatDate(expectedNext));
//
//                                assertEquals(expectedNext, fireTime);
//        logger.info(\"✅ 연말 경계 테스트 완료 - 2024년 → 2025년 정상 전환\");
//    }
//}
//
//@Nested
//@DisplayName(\"동시성 테스트\")
//class ConcurrencyTests {
//
//    @Test
//    @DisplayName(\"다중 스레드 환경에서 setReference 안전성\")
//    void testConcurrentSetReference() throws Exception {
//        logger.info(\"▶️ 동시성 테스트 시작\");
//
//                // Given
//                calculator = new CronCalculator(\"0 * * * * ?\", testReference); // 매분
//        int threadCount = 10;
//        logger.info(\"스레드 개수: {}, 크론식: '매분 0초'\", threadCount);
//
//                CountDownLatch latch = new CountDownLatch(threadCount);
//        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//
//        // When
//        for (int i = 0; i < threadCount; i++) {
//            final int index = i;
//            executor.submit(() -> {
//                try {
//                    Date newReference = parseDate(\"2024-01-15 10:\" + (30 + index) + \":00\");
//                            logger.debug(\"Thread-{}: 참조 시간 변경 → {}\", index, formatDate(newReference));
//
//                                    calculator.setReference(newReference);
//
//                    // 계산이 정상적으로 수행되는지 확인
//                    Date fire = calculator.getFireTime();
//                    Date prev = calculator.getPreviousFireTime();
//                    Date next = calculator.getNextFireTime();
//
//                    logger.debug(\"Thread-{}: 이전={}, 현재={}, 다음={}\",
//                            index, formatDate(prev), formatDate(fire), formatDate(next));
//
//                    assertNotNull(fire);
//                    assertNotNull(prev);
//                    assertNotNull(next);
//
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        // Then
//        boolean finished = latch.await(5, TimeUnit.SECONDS);
//        executor.shutdown();
//
//        logger.info(\"모든 스레드 완료 여부: {}\", finished);
//                logger.info(\"최종 참조 시간: {}\", formatDate(calculator.getReference()));
//                        logger.info(\"최종 다음 실행 시간: {}\", formatDate(calculator.getFireTime()));
//
//                                assertTrue(finished);
//        assertNotNull(calculator.getReference());
//        assertNotNull(calculator.getFireTime());
//        logger.info(\"✅ 동시성 테스트 완료 - 모든 스레드에서 안전하게 동작\");
//    }
//}
//
//@Nested
//@DisplayName(\"성능 테스트 - 간격별 시간 계산 속도\")
//class PerformanceTests {
//
//    @Test
//    @DisplayName(\"캐시 효과 성능 테스트\")
//    void testCachePerformance() throws ParseException {
//        logger.info(\"▶️ 캐시 성능 테스트 시작\");
//
//                // Given
//                calculator = new CronCalculator(\"0 0 12 * * ?\", testReference);
//        int iterations = 1000;
//        logger.info(\"반복 횟수: {}\", iterations);
//
//                // When - 동일한 참조로 반복 호출 (캐시 히트)
//                logger.info(\"🔄 캐시 히트 테스트 (동일 참조 반복)...\");
//        long startTime = System.nanoTime();
//        for (int i = 0; i < iterations; i++) {
//            calculator.setReference(testReference);
//            calculator.getFireTime();
//        }
//        long cachedTime = System.nanoTime() - startTime;
//
//        // When - 다른 참조로 반복 호출 (캐시 미스)
//        logger.info(\"🔄 캐시 미스 테스트 (서로 다른 참조)...\");
//                startTime = System.nanoTime();
//        for (int i = 0; i < iterations; i++) {
//            Date newRef = new Date(testReference.getTime() + i * 1000);
//            calculator.setReference(newRef);
//            calculator.getFireTime();
//        }
//        long uncachedTime = System.nanoTime() - startTime;
//
//        // Then
//        long cachedMs = cachedTime / 1_000_000;
//        long uncachedMs = uncachedTime / 1_000_000;
//        double speedup = (double) uncachedTime / cachedTime;
//
//        logger.info(\"📊 성능 테스트 결과:\");
//                logger.info(\"  - 캐시 히트 시간: {}ms\", cachedMs);
//                        logger.info(\"  - 캐시 미스 시간: {}ms\", uncachedMs);
//                                logger.info(\"  - 성능 향상: {:.2f}배\", speedup);
//
//                                        assertTrue(cachedTime < uncachedTime,
//                                                String.format(\"캐시된 호출(%dms)이 캐시되지 않은 호출(%dms)보다 빨라야 합니다\", cachedMs, uncachedMs));
//
//                                                        logger.info(\"✅ 캐시 성능 테스트 완료 - 캐시 효과 {:.2f}배 확인됨\", speedup);
//    }
//
//    @ParameterizedTest
//    @DisplayName(\"초/분 단위 간격별 성능 테스트\")
//    @CsvSource({
//                \"'0 * * * * ?', '매분 0초', '60초 간격'\",
//                        \"'0 */5 * * * ?', '5분마다', '300초 간격'\",
//                        \"'0 */30 * * * ?', '30분마다', '1800초 간격'\"
//})
//void testSecondMinuteIntervalPerformance(String cronExpr, String description, String interval) throws ParseException {
//    logger.info(\"▶️ 초/분 단위 성능 테스트 시작 - {} ({})\", description, interval);
//
//            performIntervalTest(cronExpr, description, 100);
//}
//
//@ParameterizedTest
//@DisplayName(\"시간 단위 간격별 성능 테스트\")
//@CsvSource({
//                \"'0 0 * * * ?', '매시간', '1시간 간격'\",
//                        \"'0 0 */6 * * ?', '6시간마다', '6시간 간격'\",
//                        \"'0 0 */12 * * ?', '12시간마다', '12시간 간격'\"
//                        })
//void testHourlyIntervalPerformance(String cronExpr, String description, String interval) throws ParseException {
//    logger.info(\"▶️ 시간 단위 성능 테스트 시작 - {} ({})\", description, interval);
//
//            performIntervalTest(cronExpr, description, 50);
//}
//
//@ParameterizedTest
//@DisplayName(\"일 단위 간격별 성능 테스트\")
//@CsvSource({
//                \"'0 0 12 * * ?', '매일', '1일 간격'\",
//                        \"'0 0 12 */3 * ?', '3일마다', '3일 간격'\",
//                        \"'0 0 12 */7 * ?', '일주일마다', '7일 간격'\"
//                        })
//void testDailyIntervalPerformance(String cronExpr, String description, String interval) throws ParseException {
//    logger.info(\"▶️ 일 단위 성능 테스트 시작 - {} ({})\", description, interval);
//
//            performIntervalTest(cronExpr, description, 30);
//}
//
//@ParameterizedTest
//@DisplayName(\"월 단위 간격별 성능 테스트\")
//@CsvSource({
//                \"'0 0 12 1 * ?', '매월 1일', '1개월 간격'\",
//                        \"'0 0 12 1 */3 ?', '분기별', '3개월 간격'\",
//                        \"'0 0 12 1 */6 ?', '반년마다', '6개월 간격'\"
//                        })
//void testMonthlyIntervalPerformance(String cronExpr, String description, String interval) throws ParseException {
//    logger.info(\"▶️ 월 단위 성능 테스트 시작 - {} ({})\", description, interval);
//
//            performIntervalTest(cronExpr, description, 20);
//}
//
//@Test
//@DisplayName(\"년 단위 간격별 성능 테스트 (최대 2년 제한)\")
//void testYearlyIntervalPerformance() throws ParseException {
//    logger.info(\"▶️ 년 단위 성능 테스트 시작 - 매년 1월 1일 (최대 2년 제한)\");
//
//            // 2년 제한을 위해 특별한 기준 시간 설정
//            String cronExpr = \"0 0 12 1 1 ?\"; // 매년 1월 1일
//            Date yearReference2024 = parseDate(\"2024-06-15 10:00:00\");
//                    Date yearReference2025 = parseDate(\"2025-06-15 10:00:00\");
//
//                            logger.info(\"크론식: {} (매년 1월 1일)\", cronExpr);
//                                    logger.info(\"2024년 기준: {}\", formatDate(yearReference2024));
//                                            logger.info(\"2025년 기준: {}\", formatDate(yearReference2025));
//
//                                                    // 2024년 기준 성능 테스트
//    long startTime = System.nanoTime();
//    CronCalculator calc2024 = new CronCalculator(cronExpr, yearReference2024);
//    Date next2024 = calc2024.getFireTime();
//    Date prev2024 = calc2024.getPreviousFireTime();
//    long time2024 = System.nanoTime() - startTime;
//
//    // 2025년 기준 성능 테스트
//    startTime = System.nanoTime();
//    CronCalculator calc2025 = new CronCalculator(cronExpr, yearReference2025);
//    Date next2025 = calc2025.getFireTime();
//    Date prev2025 = calc2025.getPreviousFireTime();
//    long time2025 = System.nanoTime() - startTime;
//
//    logger.info(\"📊 년 단위 성능 결과:\");
//            logger.info(\"  2024년 기준 - 이전: {}, 다음: {}, 소요시간: {}μs\",
//                    formatDate(prev2024), formatDate(next2024), time2024 / 1000);
//    logger.info(\"  2025년 기준 - 이전: {}, 다음: {}, 소요시간: {}μs\",
//            formatDate(prev2025), formatDate(next2025), time2025 / 1000);
//
//    // 검증: 2년 제한 확인
//    long diffYears2024 = Math.abs((next2024.getTime() - yearReference2024.getTime()) / (365L * 24 * 60 * 60 * 1000));
//    long diffYears2025 = Math.abs((next2025.getTime() - yearReference2025.getTime()) / (365L * 24 * 60 * 60 * 1000));
//
//    assertTrue(diffYears2024 <= 2, \"2년 이내 실행시간이어야 함: \" + diffYears2024 + \"년\");
//            assertTrue(diffYears2025 <= 2, \"2년 이내 실행시간이어야 함: \" + diffYears2`
//}