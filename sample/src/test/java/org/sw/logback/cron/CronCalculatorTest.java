package org.sw.logback.cron;

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
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CronCalculator 통합 테스트")
class CronCalculatorTest {

    private static final Logger logger = LoggerFactory.getLogger(CronCalculatorTest.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date testReference;

    @BeforeEach
    void setUp() {
        testReference = parseDate("2024-01-15 10:30:00");
        logger.info("=== 테스트 셋업 완료: 기준시간 = {} ===", formatDate(testReference));
    }

    @Nested
    @DisplayName("생성자 및 기본 기능 테스트")
    class BasicFunctionalityTests {

        @Test
        @DisplayName("기본 생성자 테스트")
        void testDefaultConstructor() throws ParseException {
            logger.info("🔍 [기본 생성자] 테스트 시작");

            CronCalculator calc = new CronCalculator("0 0 12 * * ?");

            assertNotNull(calc.getReference());
            assertNotNull(calc.getCron());
            assertEquals("0 0 12 * * ?", calc.getCron());

            logger.info("✅ 기본 생성자 테스트 완료");
        }

        @Test
        @DisplayName("참조 시간 지정 생성자 테스트")
        void testConstructorWithReference() throws ParseException {
            Date reference = parseDate("2024-01-15 09:00:00");
            CronCalculator calc = new CronCalculator("0 0 12 * * ?", reference);

            assertEquals(reference, calc.getReference());
            assertEquals("0 0 12 * * ?", calc.getCron());

            logger.info("✅ 참조 시간 지정 생성자 테스트 완료");
        }

        @Test
        @DisplayName("잘못된 크론 표현식 예외 테스트")
        void testInvalidCronExpression() {
            assertThrows(ParseException.class, () -> new CronCalculator("invalid cron expression"));
            logger.info("✅ 잘못된 크론 표현식 예외 테스트 완료");
        }

        @Test
        @DisplayName("기본 시간 계산 테스트")
        void testBasicTimeCalculation() throws ParseException {
            String cronExpr = "0 0 12 * * ?";
            logger.info("🔍 [기본 시간 계산] 테스트 시작");
            logger.info("📅 테스트 기준 시간: {}", formatDate(testReference));
            logger.info("⏰ 크론 표현식: {}", cronExpr);

            CronCalculator calc = new CronCalculator(cronExpr, testReference);

            Date previousTime = calc.getPreviousFireTime();
            Date nextTime = calc.getFireTime();
            Date afterNext = calc.getNextFireTime();

            // 검증 결과
            boolean previousNotNull = previousTime != null;
            boolean nextNotNull = nextTime != null;
            boolean afterNextNotNull = afterNext != null;
            boolean previousBeforeRef = previousNotNull && previousTime.before(testReference);
            boolean nextAfterRef = nextNotNull && nextTime.after(testReference);
            boolean nextBeforeAfter = nextNotNull && afterNextNotNull && nextTime.before(afterNext);

            logger.info("📊 계산 결과:");
            logger.info("   이전 실행시간: {} (NotNull: {})",
                    previousTime != null ? formatDate(previousTime) : "null", previousNotNull);
            logger.info("   다음 실행시간: {} (NotNull: {})",
                    nextTime != null ? formatDate(nextTime) : "null", nextNotNull);
            logger.info("   그다음 실행시간: {} (NotNull: {})",
                    afterNext != null ? formatDate(afterNext) : "null", afterNextNotNull);

            logger.info("🧪 검증 결과:");
            logger.info("   이전시간 < 기준시간: {}", previousBeforeRef);
            logger.info("   다음시간 > 기준시간: {}", nextAfterRef);
            logger.info("   다음시간 < 그다음시간: {}", nextBeforeAfter);

            boolean testPassed = previousNotNull && nextNotNull && afterNextNotNull &&
                    previousBeforeRef && nextAfterRef && nextBeforeAfter;
            logger.info("✅ 기본 시간 계산 테스트 {}", testPassed ? "성공" : "실패");

            assertNotNull(previousTime);
            assertNotNull(nextTime);
            assertNotNull(afterNext);
            assertTrue(previousTime.before(testReference));
            assertTrue(nextTime.after(testReference));
            assertTrue(nextTime.before(afterNext));
        }
    }

    @Nested
    @DisplayName("실무 일반 패턴 테스트")
    class CommonPatternsTests {

        @ParameterizedTest
        @DisplayName("백업 및 배치 작업 패턴")
        @CsvSource({
                "'0 0 2 * * ?', '매일 새벽 2시 백업'",
                "'0 0 3 ? * 1', '매주 일요일 새벽 3시 주간 백업'",
                "'0 0 4 1 * ?', '매월 1일 새벽 4시 월간 백업'"
        })
        void testBackupPatterns(String cronExpr, String description) throws ParseException {
            logger.info("🔍 [{}] 테스트 시작", description);
            logger.info("📅 테스트 기준 시간: {}", formatDate(testReference));
            logger.info("⏰ 크론 표현식: {}", cronExpr);

            CronCalculator calc = new CronCalculator(cronExpr, testReference);
            Date previousTime = calc.getPreviousFireTime();
            Date nextTime = calc.getFireTime();

            boolean previousNotNull = previousTime != null;
            boolean nextNotNull = nextTime != null;
            boolean previousBeforeRef = previousNotNull && (previousTime.before(testReference) || previousTime.equals(testReference));
            boolean nextAfterRef = nextNotNull && (nextTime.after(testReference) || nextTime.equals(testReference));

            logger.info("📊 계산 결과:");
            logger.info("   이전 실행시간: {} (NotNull: {})",
                    previousTime != null ? formatDate(previousTime) : "null", previousNotNull);
            logger.info("   다음 실행시간: {} (NotNull: {})",
                    nextTime != null ? formatDate(nextTime) : "null", nextNotNull);
            logger.info("🧪 검증 결과:");
            logger.info("   이전시간 ≤ 기준시간: {}", previousBeforeRef);
            logger.info("   다음시간 ≥ 기준시간: {}", nextAfterRef);

            boolean testPassed = previousNotNull && nextNotNull && previousBeforeRef && nextAfterRef;
            logger.info("✅ {} 테스트 {}", description, testPassed ? "성공" : "실패");

            assertNotNull(previousTime, description + " 이전 실행시간이 null");
            assertNotNull(nextTime, description + " 다음 실행시간이 null");
            assertTrue(previousBeforeRef, description + " 이전시간이 기준시간보다 뒤에 있음");
            assertTrue(nextAfterRef, description + " 다음시간이 기준시간보다 앞에 있음");
        }

        @ParameterizedTest
        @DisplayName("모니터링 및 헬스체크 패턴")
        @CsvSource({
                "'*/30 * * * * ?', '30초마다 헬스체크'",
                "'0 * * * * ?', '매분 상태체크'",
                "'0 */5 * * * ?', '5분마다 시스템 체크'",
                "'0 */10 * * * ?', '10분마다 성능 체크'",
                "'0 */15 * * * ?', '15분마다 로그 수집'",
                "'0 */30 * * * ?', '30분마다 리포트 생성'"
        })
        void testMonitoringPatterns(String cronExpr, String description) throws ParseException {
            logger.info("🔍 [{}] 테스트 시작", description);
            logger.info("📅 테스트 기준 시간: {}", formatDate(testReference));
            logger.info("⏰ 크론 표현식: {}", cronExpr);

            CronCalculator calc = new CronCalculator(cronExpr, testReference);
            Date previousTime = calc.getPreviousFireTime();
            Date nextTime = calc.getFireTime();

            boolean previousNotNull = previousTime != null;
            boolean nextNotNull = nextTime != null;
            boolean previousBeforeRef = previousNotNull && (previousTime.before(testReference) || previousTime.equals(testReference));
            boolean nextAfterRef = nextNotNull && (nextTime.after(testReference) || nextTime.equals(testReference));

            logger.info("📊 계산 결과:");
            logger.info("   이전 실행시간: {} (NotNull: {})",
                    previousTime != null ? formatDate(previousTime) : "null", previousNotNull);
            logger.info("   다음 실행시간: {} (NotNull: {})",
                    nextTime != null ? formatDate(nextTime) : "null", nextNotNull);
            logger.info("🧪 검증 결과:");
            logger.info("   이전시간 ≤ 기준시간: {}", previousBeforeRef);
            logger.info("   다음시간 ≥ 기준시간: {}", nextAfterRef);

            boolean testPassed = previousNotNull && nextNotNull && previousBeforeRef && nextAfterRef;
            logger.info("✅ {} 테스트 {}", description, testPassed ? "성공" : "실패");

            assertNotNull(previousTime, description + " 이전 실행시간이 null");
            assertNotNull(nextTime, description + " 다음 실행시간이 null");
            assertTrue(previousBeforeRef, description + " 이전시간이 기준시간보다 뒤에 있음");
            assertTrue(nextAfterRef, description + " 다음시간이 기준시간보다 앞에 있음");
        }

        @ParameterizedTest
        @DisplayName("로그 롤링 패턴")
        @CsvSource({
                "'0 0 * * * ?', '매시간 로그 롤링'",
                "'0 0 0 * * ?', '매일 자정 로그 아카이브'",
                "'0 0 */6 * * ?', '매 6시간마다 캐시 정리'"
        })
        void testLogRollingPatterns(String cronExpr, String description) throws ParseException {
            logger.info("🔍 [{}] 테스트 시작", description);
            logger.info("📅 테스트 기준 시간: {}", formatDate(testReference));
            logger.info("⏰ 크론 표현식: {}", cronExpr);

            CronCalculator calc = new CronCalculator(cronExpr, testReference);
            Date previousTime = calc.getPreviousFireTime();
            Date nextTime = calc.getFireTime();

            boolean previousNotNull = previousTime != null;
            boolean nextNotNull = nextTime != null;

            logger.info("📊 계산 결과:");
            logger.info("   이전 실행시간: {} (NotNull: {})",
                    previousTime != null ? formatDate(previousTime) : "null", previousNotNull);
            logger.info("   다음 실행시간: {} (NotNull: {})",
                    nextTime != null ? formatDate(nextTime) : "null", nextNotNull);

            boolean testPassed = previousNotNull && nextNotNull;
            logger.info("✅ {} 테스트 {}", description, testPassed ? "성공" : "실패");

            assertNotNull(previousTime, description + " 이전 실행시간이 null");
            assertNotNull(nextTime, description + " 다음 실행시간이 null");
        }

        @ParameterizedTest
        @DisplayName("데이터 처리 패턴")
        @CsvSource({
                "'0 0 9 ? * 2-6', '평일 오전 9시 데이터 동기화'",
                "'0 0 18 ? * 3', '매주 화요일 오후 6시 주간 리포트'",
                "'0 0 9-17 ? * 2-6', '평일 업무시간 매시간 알림'"
        })
        void testDataProcessingPatterns(String cronExpr, String description) throws ParseException {
            logger.info("🔍 [{}] 테스트 시작", description);
            logger.info("📅 테스트 기준 시간: {}", formatDate(testReference));
            logger.info("⏰ 크론 표현식: {}", cronExpr);

            CronCalculator calc = new CronCalculator(cronExpr, testReference);
            Date previousTime = calc.getPreviousFireTime();
            Date nextTime = calc.getFireTime();

            boolean previousNotNull = previousTime != null;
            boolean nextNotNull = nextTime != null;
            boolean previousBeforeRef = previousNotNull && (previousTime.before(testReference) || previousTime.equals(testReference));
            boolean nextAfterRef = nextNotNull && (nextTime.after(testReference) || nextTime.equals(testReference));

            logger.info("📊 계산 결과:");
            logger.info("   이전 실행시간: {} (NotNull: {})",
                    previousTime != null ? formatDate(previousTime) : "null", previousNotNull);
            logger.info("   다음 실행시간: {} (NotNull: {})",
                    nextTime != null ? formatDate(nextTime) : "null", nextNotNull);
            logger.info("🧪 검증 결과:");
            logger.info("   이전시간 ≤ 기준시간: {}", previousBeforeRef);
            logger.info("   다음시간 ≥ 기준시간: {}", nextAfterRef);

            boolean testPassed = previousNotNull && nextNotNull && previousBeforeRef && nextAfterRef;
            logger.info("✅ {} 테스트 {}", description, testPassed ? "성공" : "실패");

            assertNotNull(previousTime, description + " 이전 실행시간이 null");
            assertNotNull(nextTime, description + " 다음 실행시간이 null");
            assertTrue(previousBeforeRef, description + " 이전시간이 기준시간보다 뒤에 있음");
            assertTrue(nextAfterRef, description + " 다음시간이 기준시간보다 앞에 있음");
        }

        @ParameterizedTest
        @DisplayName("일/월 단위 작업 패턴")
        @CsvSource({
                "'0 0 6 * * ?', '매일 오전 6시 일일 집계'",
                "'0 0 23 * * ?', '매일 밤 11시 일일 백업'",
                "'0 30 8 * * ?', '매일 오전 8시 30분 업무 시작 알림'",
                "'0 0 12 * * ?', '매일 정오 점심시간 알림'",
                "'0 0 18 * * ?', '매일 오후 6시 업무 종료 알림'",
                "'0 0 0 * * ?', '매일 자정 시스템 점검'"
        })
        void testDailyPatterns(String cronExpr, String description) throws ParseException {
            logger.info("🔍 [{}] 테스트 시작", description);
            logger.info("📅 테스트 기준 시간: {}", formatDate(testReference));
            logger.info("⏰ 크론 표현식: {}", cronExpr);

            CronCalculator calc = new CronCalculator(cronExpr, testReference);
            Date previousTime = calc.getPreviousFireTime();
            Date nextTime = calc.getFireTime();

            boolean previousNotNull = previousTime != null;
            boolean nextNotNull = nextTime != null;
            boolean previousBeforeRef = previousNotNull && (previousTime.before(testReference) || previousTime.equals(testReference));
            boolean nextAfterRef = nextNotNull && (nextTime.after(testReference) || nextTime.equals(testReference));

            logger.info("📊 계산 결과:");
            logger.info("   이전 실행시간: {} (NotNull: {})",
                    previousTime != null ? formatDate(previousTime) : "null", previousNotNull);
            logger.info("   다음 실행시간: {} (NotNull: {})",
                    nextTime != null ? formatDate(nextTime) : "null", nextNotNull);
            logger.info("🧪 검증 결과:");
            logger.info("   이전시간 ≤ 기준시간: {}", previousBeforeRef);
            logger.info("   다음시간 ≥ 기준시간: {}", nextAfterRef);

            boolean testPassed = previousNotNull && nextNotNull && previousBeforeRef && nextAfterRef;
            logger.info("✅ {} 테스트 {}", description, testPassed ? "성공" : "실패");

            assertNotNull(previousTime, description + " 이전 실행시간이 null");
            assertNotNull(nextTime, description + " 다음 실행시간이 null");
            assertTrue(previousBeforeRef, description + " 이전시간이 기준시간보다 뒤에 있음");
            assertTrue(nextAfterRef, description + " 다음시간이 기준시간보다 앞에 있음");
        }

        @ParameterizedTest
        @DisplayName("월 단위 작업 패턴")
        @CsvSource({
                "'0 0 0 1 * ?', '매월 1일 자정 월 초기화'",
                "'0 0 9 1 * ?', '매월 1일 오전 9시 월별 리포트'",
                "'0 0 0 15 * ?', '매월 15일 자정 중간 정산'",
                "'0 0 0 1 1,4,7,10 ?', '분기별(1,4,7,10월) 1일 분기 시작'",
                "'0 0 0 1 1 ?', '매년 1월 1일 연간 초기화'"
        })
        void testMonthlyPatterns(String cronExpr, String description) throws ParseException {
            logger.info("🔍 [{}] 테스트 시작", description);
            logger.info("📅 테스트 기준 시간: {}", formatDate(testReference));
            logger.info("⏰ 크론 표현식: {}", cronExpr);

            CronCalculator calc = new CronCalculator(cronExpr, testReference);
            Date previousTime = calc.getPreviousFireTime();
            Date nextTime = calc.getFireTime();

            boolean previousNotNull = previousTime != null;
            boolean nextNotNull = nextTime != null;
            boolean previousBeforeRef = previousNotNull && (previousTime.before(testReference) || previousTime.equals(testReference));
            boolean nextAfterRef = nextNotNull && (nextTime.after(testReference) || nextTime.equals(testReference));

            logger.info("📊 계산 결과:");
            logger.info("   이전 실행시간: {} (NotNull: {})",
                    previousTime != null ? formatDate(previousTime) : "null", previousNotNull);
            logger.info("   다음 실행시간: {} (NotNull: {})",
                    nextTime != null ? formatDate(nextTime) : "null", nextNotNull);
            logger.info("🧪 검증 결과:");
            logger.info("   이전시간 ≤ 기준시간: {}", previousBeforeRef);
            logger.info("   다음시간 ≥ 기준시간: {}", nextAfterRef);

            boolean testPassed = previousNotNull && nextNotNull && previousBeforeRef && nextAfterRef;
            logger.info("✅ {} 테스트 {}", description, testPassed ? "성공" : "실패");

            assertNotNull(previousTime, description + " 이전 실행시간이 null");
            assertNotNull(nextTime, description + " 다음 실행시간이 null");
            assertTrue(previousBeforeRef, description + " 이전시간이 기준시간보다 뒤에 있음");
            assertTrue(nextAfterRef, description + " 다음시간이 기준시간보다 앞에 있음");
        }

        @ParameterizedTest
        @DisplayName("주 단위 작업 패턴")
        @CsvSource({
                "'0 0 9 ? * 2', '매주 월요일 오전 9시 주간 계획'",
                "'0 0 18 ? * 6', '매주 금요일 오후 6시 주간 마감'",
                "'0 0 10 ? * 1', '매주 일요일 오전 10시 시스템 점검'",
                "'0 0 14 ? * 4', '매주 수요일 오후 2시 중간 점검'",
                "'0 0 8 ? * 2-6', '평일 오전 8시 업무 준비'",
                "'0 0 22 ? * 1,7', '주말 밤 10시 야간 백업'"
        })
        void testWeeklyPatterns(String cronExpr, String description) throws ParseException {
            logger.info("🔍 [{}] 테스트 시작", description);
            logger.info("📅 테스트 기준 시간: {}", formatDate(testReference));
            logger.info("⏰ 크론 표현식: {}", cronExpr);

            CronCalculator calc = new CronCalculator(cronExpr, testReference);
            Date previousTime = calc.getPreviousFireTime();
            Date nextTime = calc.getFireTime();

            boolean previousNotNull = previousTime != null;
            boolean nextNotNull = nextTime != null;
            boolean previousBeforeRef = previousNotNull && (previousTime.before(testReference) || previousTime.equals(testReference));
            boolean nextAfterRef = nextNotNull && (nextTime.after(testReference) || nextTime.equals(testReference));

            logger.info("📊 계산 결과:");
            logger.info("   이전 실행시간: {} (NotNull: {})",
                    previousTime != null ? formatDate(previousTime) : "null", previousNotNull);
            logger.info("   다음 실행시간: {} (NotNull: {})",
                    nextTime != null ? formatDate(nextTime) : "null", nextNotNull);
            logger.info("🧪 검증 결과:");
            logger.info("   이전시간 ≤ 기준시간: {}", previousBeforeRef);
            logger.info("   다음시간 ≥ 기준시간: {}", nextAfterRef);

            boolean testPassed = previousNotNull && nextNotNull && previousBeforeRef && nextAfterRef;
            logger.info("✅ {} 테스트 {}", description, testPassed ? "성공" : "실패");

            assertNotNull(previousTime, description + " 이전 실행시간이 null");
            assertNotNull(nextTime, description + " 다음 실행시간이 null");
            assertTrue(previousBeforeRef, description + " 이전시간이 기준시간보다 뒤에 있음");
            assertTrue(nextAfterRef, description + " 다음시간이 기준시간보다 앞에 있음");
        }

        @ParameterizedTest
        @DisplayName("특수 시간 패턴")
        @CsvSource({
                "'0 0/30 9-17 ? * 2-6', '평일 업무시간 30분마다 상태 체크'",
                "'0 0 12 ? * 2-6', '평일 점심시간 알림'",
                "'0 0 9,13,17 ? * 2-6', '평일 오전9시,오후1시,오후5시 정기 회의'",
                "'0 15 10 ? * 2-6', '평일 오전 10시 15분 커피 타임'",
                "'0 0 0 ? * 1', '매주 일요일 자정 주간 정리'",
                "'0 0 6,18 * * ?', '매일 오전6시, 오후6시 교대 근무'"
        })
        void testSpecialTimePatterns(String cronExpr, String description) throws ParseException {
            logger.info("🔍 [{}] 테스트 시작", description);
            logger.info("📅 테스트 기준 시간: {}", formatDate(testReference));
            logger.info("⏰ 크론 표현식: {}", cronExpr);

            CronCalculator calc = new CronCalculator(cronExpr, testReference);
            Date previousTime = calc.getPreviousFireTime();
            Date nextTime = calc.getFireTime();

            boolean previousNotNull = previousTime != null;
            boolean nextNotNull = nextTime != null;
            boolean previousBeforeRef = previousNotNull && (previousTime.before(testReference) || previousTime.equals(testReference));
            boolean nextAfterRef = nextNotNull && (nextTime.after(testReference) || nextTime.equals(testReference));

            logger.info("📊 계산 결과:");
            logger.info("   이전 실행시간: {} (NotNull: {})",
                    previousTime != null ? formatDate(previousTime) : "null", previousNotNull);
            logger.info("   다음 실행시간: {} (NotNull: {})",
                    nextTime != null ? formatDate(nextTime) : "null", nextNotNull);
            logger.info("🧪 검증 결과:");
            logger.info("   이전시간 ≤ 기준시간: {}", previousBeforeRef);
            logger.info("   다음시간 ≥ 기준시간: {}", nextAfterRef);

            boolean testPassed = previousNotNull && nextNotNull && previousBeforeRef && nextAfterRef;
            logger.info("✅ {} 테스트 {}", description, testPassed ? "성공" : "실패");

            assertNotNull(previousTime, description + " 이전 실행시간이 null");
            assertNotNull(nextTime, description + " 다음 실행시간이 null");
            assertTrue(previousBeforeRef, description + " 이전시간이 기준시간보다 뒤에 있음");
            assertTrue(nextAfterRef, description + " 다음시간이 기준시간보다 앞에 있음");
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class BoundaryTests {

        @Test
        @DisplayName("월말에서 다음 달로 넘어가는 경계")
        void testMonthBoundary() throws ParseException {
            String cronExpr = "0 0 0 1 * ?";
            Date boundaryTime = parseDate("2024-01-31 23:59:59");
            Date expectedNext = parseDate("2024-02-01 00:00:00");

            logger.info("🔍 [월말 경계] 테스트 시작");
            logger.info("📅 테스트 기준 시간: {}", formatDate(boundaryTime));
            logger.info("⏰ 크론 표현식: {}", cronExpr);
            logger.info("🎯 기대하는 다음 실행시간: {}", formatDate(expectedNext));

            CronCalculator calc = new CronCalculator(cronExpr, boundaryTime);
            Date actualNext = calc.getFireTime();

            logger.info("📊 계산 결과:");
            logger.info("   계산된 다음 실행시간: {}", formatDate(actualNext));

            boolean timeMatches = actualNext != null && Math.abs(actualNext.getTime() - expectedNext.getTime()) < 1000;
            logger.info("🧪 검증 결과:");
            logger.info("   시간 일치: {}", timeMatches);
            logger.info("✅ 월말 경계 테스트 {}", timeMatches ? "성공" : "실패");

            assertTimeEquals(expectedNext, actualNext, "월말 경계 다음 실행시간 불일치");
        }

        @Test
        @DisplayName("연말에서 다음 년도로 넘어가는 경계")
        void testYearBoundary() throws ParseException {
            String cronExpr = "0 0 0 1 1 ?";
            Date boundaryTime = parseDate("2024-12-31 23:59:59");
            Date expectedPrev = parseDate("2024-01-01 00:00:00");
            Date expectedNext = parseDate("2025-01-01 00:00:00");

            logger.info("🔍 [연말 경계] 테스트 시작");
            logger.info("📅 테스트 기준 시간: {}", formatDate(boundaryTime));
            logger.info("⏰ 크론 표현식: {}", cronExpr);
            logger.info("🎯 기대하는 이전 실행시간: {}", formatDate(expectedPrev));
            logger.info("🎯 기대하는 다음 실행시간: {}", formatDate(expectedNext));

            CronCalculator calc = new CronCalculator(cronExpr, boundaryTime);
            Date actualPrev = calc.getPreviousFireTime();
            Date actualNext = calc.getFireTime();

            logger.info("📊 계산 결과:");
            logger.info("   계산된 이전 실행시간: {}", formatDate(actualPrev));
            logger.info("   계산된 다음 실행시간: {}", formatDate(actualNext));

            boolean prevMatches = actualPrev != null && Math.abs(actualPrev.getTime() - expectedPrev.getTime()) < 1000;
            boolean nextMatches = actualNext != null && Math.abs(actualNext.getTime() - expectedNext.getTime()) < 1000;

            logger.info("🧪 검증 결과:");
            logger.info("   이전시간 일치: {}", prevMatches);
            logger.info("   다음시간 일치: {}", nextMatches);
            logger.info("✅ 연말 경계 테스트 {}", (prevMatches && nextMatches) ? "성공" : "실패");

            assertTimeEquals(expectedPrev, actualPrev, "연말 경계 이전 실행시간 불일치");
            assertTimeEquals(expectedNext, actualNext, "연말 경계 다음 실행시간 불일치");
        }

        @Test
        @DisplayName("윤년과 평년 2월 경계")
        void testLeapYearFebruaryBoundary() throws ParseException {
            String cronExpr = "0 0 0 1 * ?";

            logger.info("🔍 [윤년/평년 2월 경계] 테스트 시작");

            // 윤년 2024년 2월 29일
            Date leapYearBoundary = parseDate("2024-02-29 12:00:00");
            Date expectedLeapNext = parseDate("2024-03-01 00:00:00");

            logger.info("📅 윤년 테스트 기준 시간: {}", formatDate(leapYearBoundary));
            logger.info("⏰ 크론 표현식: {}", cronExpr);
            logger.info("🎯 기대하는 윤년 다음 실행시간: {}", formatDate(expectedLeapNext));

            CronCalculator calcLeap = new CronCalculator(cronExpr, leapYearBoundary);
            Date actualLeapNext = calcLeap.getFireTime();

            logger.info("📊 윤년 계산 결과: {}", formatDate(actualLeapNext));
            boolean leapMatches = actualLeapNext != null && Math.abs(actualLeapNext.getTime() - expectedLeapNext.getTime()) < 1000;
            logger.info("🧪 윤년 검증 결과: {}", leapMatches);

            // 평년 2023년 2월 28일
            Date nonLeapYearBoundary = parseDate("2023-02-28 12:00:00");
            Date expectedNonLeapNext = parseDate("2023-03-01 00:00:00");

            logger.info("📅 평년 테스트 기준 시간: {}", formatDate(nonLeapYearBoundary));
            logger.info("🎯 기대하는 평년 다음 실행시간: {}", formatDate(expectedNonLeapNext));

            CronCalculator calcNonLeap = new CronCalculator(cronExpr, nonLeapYearBoundary);
            Date actualNonLeapNext = calcNonLeap.getFireTime();

            logger.info("📊 평년 계산 결과: {}", formatDate(actualNonLeapNext));
            boolean nonLeapMatches = actualNonLeapNext != null && Math.abs(actualNonLeapNext.getTime() - expectedNonLeapNext.getTime()) < 1000;
            logger.info("🧪 평년 검증 결과: {}", nonLeapMatches);

            logger.info("✅ 윤년/평년 2월 경계 테스트 {}", (leapMatches && nonLeapMatches) ? "성공" : "실패");

            assertTimeEquals(expectedLeapNext, actualLeapNext, "윤년 2월 경계 불일치");
            assertTimeEquals(expectedNonLeapNext, actualNonLeapNext, "평년 2월 경계 불일치");
        }

        @Test
        @DisplayName("주간 경계 - 일요일에서 월요일로")
        void testWeekBoundary() throws ParseException {
            String cronExpr = "0 0 0 ? * 2";
            Date boundaryTime = parseDate("2024-01-14 23:30:00"); // 2024-01-14는 일요일
            Date expectedPrev = parseDate("2024-01-08 00:00:00");
            Date expectedNext = parseDate("2024-01-15 00:00:00");

            logger.info("🔍 [주간 경계] 테스트 시작");
            logger.info("📅 테스트 기준 시간: {} (일요일)", formatDate(boundaryTime));
            logger.info("⏰ 크론 표현식: {} (매주 월요일)", cronExpr);
            logger.info("🎯 기대하는 이전 실행시간: {}", formatDate(expectedPrev));
            logger.info("🎯 기대하는 다음 실행시간: {}", formatDate(expectedNext));

            CronCalculator calc = new CronCalculator(cronExpr, boundaryTime);
            Date actualPrev = calc.getPreviousFireTime();
            Date actualNext = calc.getFireTime();

            logger.info("📊 계산 결과:");
            logger.info("   계산된 이전 실행시간: {}", formatDate(actualPrev));
            logger.info("   계산된 다음 실행시간: {}", formatDate(actualNext));

            boolean prevMatches = actualPrev != null && Math.abs(actualPrev.getTime() - expectedPrev.getTime()) < 1000;
            boolean nextMatches = actualNext != null && Math.abs(actualNext.getTime() - expectedNext.getTime()) < 1000;

            logger.info("🧪 검증 결과:");
            logger.info("   이전시간 일치: {}", prevMatches);
            logger.info("   다음시간 일치: {}", nextMatches);
            logger.info("✅ 주간 경계 테스트 {}", (prevMatches && nextMatches) ? "성공" : "실패");

            assertTimeEquals(expectedPrev, actualPrev, "주간 경계 이전 실행시간 불일치");
            assertTimeEquals(expectedNext, actualNext, "주간 경계 다음 실행시간 불일치");
        }
    }

    @Nested
    @DisplayName("시간 순서 및 일관성 검증")
    class ConsistencyTests {

        @Test
        @DisplayName("시간 순서 검증")
        void testTimeOrderConsistency() throws ParseException {
            logger.info("🔍 [시간 순서 일관성] 검증 테스트 시작");
            logger.info("📅 테스트 기준 시간: {}", formatDate(testReference));

            String[] commonPatterns = {
                    "*/30 * * * * ?",   // 30초마다
                    "0 */5 * * * ?",    // 5분마다
                    "0 0 * * * ?",      // 매시간
                    "0 0 2 * * ?",      // 매일 2시
                    "0 0 0 ? * 1",      // 매주 일요일
                    "0 0 0 1 * ?",      // 매월 1일
                    "0 0 9-17 ? * 2-6"  // 평일 업무시간
            };

            for (String pattern : commonPatterns) {
                logger.info("🔍 패턴 검증: {}", pattern);

                CronCalculator calc = new CronCalculator(pattern, testReference);

                Date previousTime = calc.getPreviousFireTime();
                Date currentTime = calc.getFireTime();
                Date nextTime = calc.getNextFireTime();

                // 검증 결과 계산
                boolean previousNotNull = previousTime != null;
                boolean currentNotNull = currentTime != null;
                boolean nextNotNull = nextTime != null;
                boolean previousBeforeCurrent = previousNotNull && currentNotNull && previousTime.before(currentTime);
                boolean currentBeforeNext = currentNotNull && nextNotNull && currentTime.before(nextTime);
                boolean previousBeforeNext = previousNotNull && nextNotNull && previousTime.before(nextTime);
                boolean previousBeforeRef = previousNotNull && (previousTime.before(testReference) || previousTime.equals(testReference));
                boolean currentAfterRef = currentNotNull && (currentTime.after(testReference) || currentTime.equals(testReference));

                logger.info("📊 계산 결과:");
                logger.info("   이전: {} (NotNull: {})",
                        previousTime != null ? formatDate(previousTime) : "null", previousNotNull);
                logger.info("   현재: {} (NotNull: {})",
                        currentTime != null ? formatDate(currentTime) : "null", currentNotNull);
                logger.info("   다음: {} (NotNull: {})",
                        nextTime != null ? formatDate(nextTime) : "null", nextNotNull);

                logger.info("🧪 검증 결과:");
                logger.info("   이전 < 현재: {}", previousBeforeCurrent);
                logger.info("   현재 < 다음: {}", currentBeforeNext);
                logger.info("   이전 < 다음: {}", previousBeforeNext);
                logger.info("   이전 ≤ 기준: {}", previousBeforeRef);
                logger.info("   현재 ≥ 기준: {}", currentAfterRef);

                boolean patternPassed = previousNotNull && currentNotNull && nextNotNull &&
                        previousBeforeCurrent && currentBeforeNext && previousBeforeNext &&
                        previousBeforeRef && currentAfterRef;

                logger.info("✅ 패턴 {} 검증 {}", pattern, patternPassed ? "성공" : "실패");

                // 기본 검증
                assertNotNull(previousTime, pattern + " 이전 시간이 null");
                assertNotNull(currentTime, pattern + " 현재 시간이 null");
                assertNotNull(nextTime, pattern + " 다음 시간이 null");

                // 순서 검증
                assertTrue(previousTime.before(currentTime), pattern + " 이전 < 현재 순서 위반");
                assertTrue(currentTime.before(nextTime), pattern + " 현재 < 다음 순서 위반");
                assertTrue(previousTime.before(nextTime), pattern + " 이전 < 다음 순서 위반");

                // 기준시간과의 관계 검증
                assertTrue(previousTime.before(testReference) || previousTime.equals(testReference),
                        pattern + " 이전 시간이 기준시간보다 뒤");
                assertTrue(currentTime.after(testReference) || currentTime.equals(testReference),
                        pattern + " 현재 시간이 기준시간보다 앞");
            }

            logger.info("✅ 시간 순서 일관성 검증 완료");
        }

        @Test
        @DisplayName("reference 시간 변경 테스트")
        void testReferenceTimeChange() throws ParseException {
            String cronExpr = "0 0 * * * ?";
            Date initialTime = parseDate("2024-06-01 12:00:00");

            logger.info("🔍 [Reference 시간 변경] 테스트 시작");
            logger.info("📅 초기 기준 시간: {}", formatDate(initialTime));
            logger.info("⏰ 크론 표현식: {}", cronExpr);

            CronCalculator calculator = new CronCalculator(cronExpr, initialTime);
            Date initialNext = calculator.getFireTime();

            logger.info("📊 초기 계산 결과:");
            logger.info("   초기 다음 실행시간: {}", formatDate(initialNext));

            // 시간 변경 (3시간 후)
            Calendar cal = Calendar.getInstance();
            cal.setTime(initialTime);
            cal.add(Calendar.HOUR, 3);
            Date newTime = cal.getTime();

            logger.info("📅 변경된 기준 시간: {}", formatDate(newTime));

            calculator.setReference(newTime);
            Date newNext = calculator.getFireTime();

            logger.info("📊 변경 후 계산 결과:");
            logger.info("   변경 후 다음 실행시간: {}", formatDate(newNext));

            boolean timesAreDifferent = !initialNext.equals(newNext);
            boolean newNextAfterNewRef = newNext.after(newTime);

            logger.info("🧪 검증 결과:");
            logger.info("   실행시간 변경됨: {}", timesAreDifferent);
            logger.info("   새 실행시간 > 새 기준시간: {}", newNextAfterNewRef);

            boolean testPassed = timesAreDifferent && newNextAfterNewRef;
            logger.info("✅ Reference 시간 변경 테스트 {}", testPassed ? "성공" : "실패");

            assertNotEquals(initialNext, newNext, "시간 변경 후에도 다음 실행시간이 동일합니다");
            assertTrue(newNext.after(newTime), "새로운 다음 실행시간이 새로운 기준시간보다 앞에 있습니다");
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

    /**
     * 두 Date 객체가 같은지 비교 (밀리초 차이 허용)
     */
    private void assertTimeEquals(Date expected, Date actual, String message) {
        if (expected == null && actual == null) {
            return;
        }

        assertNotNull(expected, message + " - 예상시간이 null입니다");
        assertNotNull(actual, message + " - 실제시간이 null입니다");

        // 밀리초를 0으로 맞춘 후 비교
        long expectedTime = (expected.getTime() / 1000) * 1000;
        long actualTime = (actual.getTime() / 1000) * 1000;

        if (expectedTime != actualTime) {
            String detailMessage = String.format(
                    "%s\n예상: %s (%d ms)\n실제: %s (%d ms)\n차이: %d ms",
                    message,
                    formatDate(expected), expected.getTime(),
                    formatDate(actual), actual.getTime(),
                    actual.getTime() - expected.getTime()
            );
            fail(detailMessage);
        }
    }
}