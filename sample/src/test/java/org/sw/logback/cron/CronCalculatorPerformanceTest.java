package org.sw.logback.cron;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@DisplayName("CronCalculator 통합 성능 테스트")
public class CronCalculatorPerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(CronCalculatorPerformanceTest.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final int ITERATIONS = 100;
    private static final int MAX_YEARS = 2;

    private Date baseDate;
    private Date endDate;

    @BeforeEach
    void setUp() {
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        this.baseDate = Date.from(baseTime.atZone(ZoneId.systemDefault()).toInstant());

        LocalDateTime endTime = baseTime.plusYears(MAX_YEARS);
        this.endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

        logger.info("========================================");
        logger.info("   CronCalculator 통합 성능 테스트 시작");
        logger.info("========================================");
        logger.info("테스트 시작 시간: {}", DATE_FORMAT.format(baseDate));
        logger.info("테스트 종료 시간: {}", DATE_FORMAT.format(endDate));
        logger.info("테스트 기간: {}년", MAX_YEARS);
        logger.info("반복 횟수: {}회", ITERATIONS);
        logger.info("========================================");
    }

    @Nested
    @DisplayName("다음 실행시간 조회 성능 테스트")
    class NextTimePerformanceTests {

        @Test
        @DisplayName("초/분 단위 패턴 성능")
        void testSecondMinutePatterns() throws ParseException {
            logger.info("\n🔍 [초/분 단위] 다음 실행시간 조회 성능 테스트");

            String[][] patterns = {
                    {"*/5 * * * * ?", "5초마다"},
                    {"*/30 * * * * ?", "30초마다"},
                    {"0 * * * * ?", "매분"},
                    {"0 */5 * * * ?", "5분마다"},
                    {"0 */15 * * * ?", "15분마다"},
                    {"0 */30 * * * ?", "30분마다"}
            };

            for (String[] pattern : patterns) {
                performNextTimeTest(pattern[0], pattern[1]);
            }
        }

        @Test
        @DisplayName("시간/일 단위 패턴 성능")
        void testHourDayPatterns() throws ParseException {
            logger.info("\n🔍 [시간/일 단위] 다음 실행시간 조회 성능 테스트");

            String[][] patterns = {
                    {"0 0 * * * ?", "매시간"},
                    {"0 0 */6 * * ?", "6시간마다"},
                    {"0 0 */12 * * ?", "12시간마다"},
                    {"0 0 0 * * ?", "매일"},
                    {"0 0 0 */3 * ?", "3일마다"},
                    {"0 0 0 */7 * ?", "매주"}
            };

            for (String[] pattern : patterns) {
                performNextTimeTest(pattern[0], pattern[1]);
            }
        }

        @Test
        @DisplayName("월/년 단위 패턴 성능")
        void testMonthYearPatterns() throws ParseException {
            logger.info("\n🔍 [월/년 단위] 다음 실행시간 조회 성능 테스트");

            String[][] patterns = {
                    {"0 0 0 1 * ?", "매월 1일"},
                    {"0 0 0 15 * ?", "매월 15일"},
                    {"0 0 0 1 */3 ?", "분기별"},
                    {"0 0 0 1 */6 ?", "반기별"},
                    {"0 0 0 1 1 ?", "매년"},
                    {"0 0 0 25 12 ?", "크리스마스"}
            };

            for (String[] pattern : patterns) {
                performNextTimeTest(pattern[0], pattern[1]);
            }
        }

        @Test
        @DisplayName("복잡한 패턴 성능")
        void testComplexPatterns() throws ParseException {
            logger.info("\n🔍 [복잡한 패턴] 다음 실행시간 조회 성능 테스트");

            String[][] patterns = {
                    {"0 0 9-17 ? * 2-6", "평일 업무시간"},
                    {"0 30 8 ? * 2-6", "평일 아침"},
                    {"0 0 6,12,18 * * ?", "하루 3번"},
                    {"0 0 0 ? * 2,4,6", "화목토"},
                    {"0 0 0 1,15 * ?", "매월 2번"},
                    {"0 0 0 1 * ?", "매월 1일"}
            };

            for (String[] pattern : patterns) {
                performNextTimeTest(pattern[0], pattern[1]);
            }
        }
    }

    @Nested
    @DisplayName("이전 실행시간 조회 성능 테스트")
    class PreviousTimePerformanceTests {

        @Test
        @DisplayName("빈번한 실행 패턴 이전시간 성능")
        void testFrequentPatterns() throws ParseException {
            logger.info("\n🔍 [빈번한 실행] 이전 실행시간 조회 성능 테스트");
            logger.info("⚠️  이전시간 조회는 역방향 검색으로 시간이 더 걸립니다");

            String[][] patterns = {
                    {"*/5 * * * * ?", "5초마다"},
                    {"*/30 * * * * ?", "30초마다"},
                    {"0 * * * * ?", "매분"},
                    {"0 */5 * * * ?", "5분마다"},
                    {"0 */15 * * * ?", "15분마다"},
                    {"0 0 * * * ?", "매시간"}
            };

            for (String[] pattern : patterns) {
                performPreviousTimeTest(pattern[0], pattern[1]);
            }
        }

        @Test
        @DisplayName("희소한 실행 패턴 이전시간 성능")
        void testSparsePatterns() throws ParseException {
            logger.info("\n🔍 [희소한 실행] 이전 실행시간 조회 성능 테스트");

            String[][] patterns = {
                    {"0 0 */12 * * ?", "12시간마다"},
                    {"0 0 0 * * ?", "매일"},
                    {"0 0 0 */7 * ?", "매주"},
                    {"0 0 0 ? * 1", "매주 일요일"},
                    {"0 0 0 1 * ?", "매월 1일"},
                    {"0 0 0 1 */6 ?", "반기별"}
            };

            for (String[] pattern : patterns) {
                performPreviousTimeTest(pattern[0], pattern[1]);
            }
        }

        @Test
        @DisplayName("극한 상황 이전시간 성능")
        void testExtremePatterns() throws ParseException {
            logger.info("\n🔍 [극한 상황] 이전 실행시간 조회 성능 테스트");
            logger.info("⚠️  이 테스트는 가장 오래 걸릴 수 있습니다");

            String[][] patterns = {
                    {"0 0 0 29 2 ?", "윤년 2월 29일만"},
                    {"0 0 0 31 1,3,5,7,8,10,12 ?", "31일 있는 달만"},
                    {"0 0 0 1 * ?", "매월 1일"},
                    {"0 0 0 25 12 ?", "크리스마스"},
                    {"0 0 0 1 1 ?", "매년 신정"}
            };

            for (String[] pattern : patterns) {
                performPreviousTimeTest(pattern[0], pattern[1]);
            }
        }
    }

    @Nested
    @DisplayName("통합 성능 비교 테스트")
    class ComparisonPerformanceTests {

        @Test
        @DisplayName("다음시간 vs 이전시간 성능 비교")
        void testNextVsPreviousComparison() throws ParseException {
            logger.info("\n🔍 [성능 비교] 다음시간 vs 이전시간 조회 성능");

            String[][] patterns = {
                    {"*/30 * * * * ?", "30초마다"},
                    {"0 */15 * * * ?", "15분마다"},
                    {"0 0 */6 * * ?", "6시간마다"},
                    {"0 0 0 * * ?", "매일"},
                    {"0 0 0 ? * 1", "매주 일요일"},
                    {"0 0 0 1 * ?", "매월 1일"}
            };

            for (String[] pattern : patterns) {
                performComparisonTest(pattern[0], pattern[1]);
            }
        }

        @Test
        @DisplayName("패턴 복잡도별 성능 분석")
        void testComplexityAnalysis() throws ParseException {
            logger.info("\n🔍 [복잡도 분석] 패턴별 성능 차이 분석");

            String[][] patterns = {
                    {"0 0 12 * * ?", "단순 - 매일 정오"},
                    {"0 0 9-17 * * ?", "범위 - 업무시간"},
                    {"0 0 9,12,15,18 * * ?", "리스트 - 하루 4번"},
                    {"0 0 9-17 ? * 2-6", "복합 - 평일 업무시간"},
                    {"0 30 8 ? * 2-6", "구체적 - 평일 아침"},
                    {"0 0 0 1 * ?", "고급 - 매월 1일"}
            };

            for (String[] pattern : patterns) {
                performComplexityTest(pattern[0], pattern[1]);
            }
        }

        @Test
        @DisplayName("대용량 반복 테스트")
        void testHighVolumePerformance() throws ParseException {
            logger.info("\n🔍 [대용량] 많은 반복 호출 성능 테스트");

            String[][] patterns = {
                    {"0 0 12 * * ?", "매일 정오 - 1000회"},
                    {"0 */5 * * * ?", "5분마다 - 1000회"},
                    {"0 0 0 ? * 2-6", "평일만 - 1000회"}
            };

            for (String[] pattern : patterns) {
                performHighVolumeTest(pattern[0], pattern[1]);
            }
        }
    }

    // ==================== 테스트 실행 메서드들 ====================

    private void performNextTimeTest(String cronExpr, String description) throws ParseException {
        logger.info("\n📊 [다음시간] {} - {}", description, cronExpr);

        CronCalculator calculator = new CronCalculator(cronExpr, baseDate);
        PerformanceStats stats = new PerformanceStats();

        for (int i = 0; i < ITERATIONS; i++) {
            Date testTime = new Date(baseDate.getTime() + (long)((double)(endDate.getTime() - baseDate.getTime()) / ITERATIONS * i));
            calculator.setReference(testTime);

            long startTime = System.nanoTime();
            Date nextTime = calculator.getFireTime();
            long endTime = System.nanoTime();

            stats.addMeasurement(endTime - startTime);
        }

        logPerformanceResults("다음시간", stats);
    }

    private void performPreviousTimeTest(String cronExpr, String description) throws ParseException {
        logger.info("\n📊 [이전시간] {} - {}", description, cronExpr);

        CronCalculator calculator = new CronCalculator(cronExpr, baseDate);
        PerformanceStats stats = new PerformanceStats();

        for (int i = 0; i < ITERATIONS; i++) {
            // 이전시간 조회를 위해 더 뒤쪽 시간 사용
            Date testTime = new Date(baseDate.getTime() + (long)((double)(endDate.getTime() - baseDate.getTime()) / ITERATIONS * (i + 10)));
            calculator.setReference(testTime);

            long startTime = System.nanoTime();
            Date previousTime = calculator.getPreviousFireTime();
            long endTime = System.nanoTime();

            stats.addMeasurement(endTime - startTime);
        }

        logPerformanceResults("이전시간", stats);
    }

    private void performComparisonTest(String cronExpr, String description) throws ParseException {
        logger.info("\n📊 [비교분석] {} - {}", description, cronExpr);

        CronCalculator calculator = new CronCalculator(cronExpr, baseDate);
        PerformanceStats nextStats = new PerformanceStats();
        PerformanceStats prevStats = new PerformanceStats();

        for (int i = 0; i < ITERATIONS; i++) {
            Date testTime = new Date(baseDate.getTime() + (long)((double)(endDate.getTime() - baseDate.getTime()) / ITERATIONS * (i + 10)));
            calculator.setReference(testTime);

            // 다음시간 측정
            long startTime = System.nanoTime();
            Date nextTime = calculator.getFireTime();
            long endTime = System.nanoTime();
            nextStats.addMeasurement(endTime - startTime);

            // 이전시간 측정
            startTime = System.nanoTime();
            Date previousTime = calculator.getPreviousFireTime();
            endTime = System.nanoTime();
            prevStats.addMeasurement(endTime - startTime);
        }

        logComparisonResults(nextStats, prevStats);
    }

    private void performComplexityTest(String cronExpr, String description) throws ParseException {
        logger.info("\n📊 [복잡도] {} - {}", description, cronExpr);

        CronCalculator calculator = new CronCalculator(cronExpr, baseDate);
        PerformanceStats stats = new PerformanceStats();

        for (int i = 0; i < ITERATIONS; i++) {
            Date testTime = new Date(baseDate.getTime() + (long)((double)(endDate.getTime() - baseDate.getTime()) / ITERATIONS * i));
            calculator.setReference(testTime);

            long startTime = System.nanoTime();
            Date nextTime = calculator.getFireTime();
            Date previousTime = calculator.getPreviousFireTime();
            long endTime = System.nanoTime();

            stats.addMeasurement(endTime - startTime);
        }

        logComplexityResults(description, stats);
    }

    private void performHighVolumeTest(String cronExpr, String description) throws ParseException {
        logger.info("\n📊 [대용량] {} - {}", description, cronExpr);

        final int HIGH_VOLUME_ITERATIONS = 1000;
        CronCalculator calculator = new CronCalculator(cronExpr, baseDate);
        PerformanceStats stats = new PerformanceStats();

        long overallStart = System.nanoTime();

        for (int i = 0; i < HIGH_VOLUME_ITERATIONS; i++) {
            Date testTime = new Date(baseDate.getTime() + (long)((double)(endDate.getTime() - baseDate.getTime()) / HIGH_VOLUME_ITERATIONS * i));
            calculator.setReference(testTime);

            long startTime = System.nanoTime();
            Date nextTime = calculator.getFireTime();
            long endTime = System.nanoTime();

            stats.addMeasurement(endTime - startTime);
        }

        long overallEnd = System.nanoTime();
        double overallMs = (overallEnd - overallStart) / 1_000_000.0;

        logger.info("   평균: {}", formatTime(stats.getAverageNanos()));
        logger.info("   전체 실행시간: {:.2f}ms", overallMs);
        logger.info("   초당 처리량: {:.0f}회/초", HIGH_VOLUME_ITERATIONS * 1000.0 / overallMs);
        logger.info("   등급: {}", getPerformanceGrade(stats.getAverageNanos() / 1_000_000.0, false));
    }

    // ==================== 결과 출력 메서드들 ====================

    private void logPerformanceResults(String type, PerformanceStats stats) {
        double avgMs = stats.getAverageNanos() / 1_000_000.0;

        logger.info("   평균: {}", formatTime(stats.getAverageNanos()));
        logger.info("   최소: {}", formatTime(stats.getMinNanos()));
        logger.info("   최대: {}", formatTime(stats.getMaxNanos()));
        logger.info("   등급: {}", getPerformanceGrade(avgMs, type.equals("이전시간")));

        if (type.equals("이전시간") && avgMs > 50) {
            logger.warn("   ⚠️ 성능 경고: {}ms는 이전시간 조회치고 매우 느립니다!", String.format("%.2f", avgMs));
        } else if (!type.equals("이전시간") && avgMs > 10) {
            logger.warn("   ⚠️ 성능 경고: {}ms는 다음시간 조회치고 너무 느립니다!", String.format("%.2f", avgMs));
        }
    }

    private void logComparisonResults(PerformanceStats nextStats, PerformanceStats prevStats) {
        double nextAvg = nextStats.getAverageNanos() / 1_000_000.0;
        double prevAvg = prevStats.getAverageNanos() / 1_000_000.0;
        double ratio = prevAvg / nextAvg;

        logger.info("   다음시간 평균: {}", formatTime(nextStats.getAverageNanos()));
        logger.info("   이전시간 평균: {}", formatTime(prevStats.getAverageNanos()));
        logger.info("   성능비율: 이전시간이 다음시간보다 {:.1f}배 느림", ratio);

        if (ratio > 1000) {
            logger.warn("   🚨 심각: 이전시간 조회가 {}배 느립니다!", String.format("%.0f", ratio));
        } else if (ratio > 100) {
            logger.warn("   ⚠️ 주의: 이전시간 조회가 {}배 느립니다", String.format("%.0f", ratio));
        } else if (ratio > 10) {
            logger.info("   ℹ️ 정보: 이전시간 조회가 {}배 느림 (정상 범위)", String.format("%.1f", ratio));
        }
    }

    private void logComplexityResults(String description, PerformanceStats stats) {
        double avgMs = stats.getAverageNanos() / 1_000_000.0;

        logger.info("   통합처리 평균: {}", formatTime(stats.getAverageNanos()));
        logger.info("   복잡도 등급: {}", getComplexityGrade(avgMs));

        String[] parts = description.split(" - ");
        String complexity = parts[0];

        switch (complexity) {
            case "단순":
                if (avgMs > 2) logger.warn("   ⚠️ 단순 패턴치고 느립니다");
                break;
            case "범위":
            case "리스트":
                if (avgMs > 10) logger.warn("   ⚠️ 중간 복잡도치고 느립니다");
                break;
            case "복합":
            case "구체적":
            case "고급":
                if (avgMs > 100) logger.warn("   ⚠️ 복잡한 패턴이지만 너무 느립니다");
                break;
        }
    }

    // ==================== 유틸리티 메서드들 ====================

    private String formatTime(long nanos) {
        if (nanos < 1_000) {
            return String.format("%d ns", nanos);
        } else if (nanos < 1_000_000) {
            return String.format("%.2f μs", nanos / 1_000.0);
        } else if (nanos < 1_000_000_000) {
            return String.format("%.2f ms", nanos / 1_000_000.0);
        } else {
            return String.format("%.2f s", nanos / 1_000_000_000.0);
        }
    }

    private String getPerformanceGrade(double avgMs, boolean isPrevious) {
        if (isPrevious) {
            // 이전시간 조회는 더 관대한 기준
            if (avgMs < 1) return "S등급 (매우빠름)";
            if (avgMs < 10) return "A등급 (빠름)";
            if (avgMs < 50) return "B등급 (보통)";
            if (avgMs < 200) return "C등급 (느림)";
            if (avgMs < 1000) return "D등급 (매우느림)";
            return "F등급 (심각함)";
        } else {
            // 다음시간 조회는 엄격한 기준
            if (avgMs < 0.1) return "S등급 (매우빠름)";
            if (avgMs < 1) return "A등급 (빠름)";
            if (avgMs < 5) return "B등급 (보통)";
            if (avgMs < 20) return "C등급 (느림)";
            if (avgMs < 100) return "D등급 (매우느림)";
            return "F등급 (심각함)";
        }
    }

    private String getComplexityGrade(double avgMs) {
        if (avgMs < 5) return "단순 (5ms 미만)";
        if (avgMs < 20) return "보통 (20ms 미만)";
        if (avgMs < 100) return "복잡 (100ms 미만)";
        if (avgMs < 500) return "매우복잡 (500ms 미만)";
        return "극도복잡 (500ms 이상)";
    }

    // ==================== 성능 통계 클래스 ====================

    private static class PerformanceStats {
        private long totalNanos = 0;
        private long minNanos = Long.MAX_VALUE;
        private long maxNanos = Long.MIN_VALUE;
        private int count = 0;

        void addMeasurement(long nanos) {
            totalNanos += nanos;
            minNanos = Math.min(minNanos, nanos);
            maxNanos = Math.max(maxNanos, nanos);
            count++;
        }

        long getAverageNanos() {
            return count > 0 ? (long) totalNanos / count : 0;
        }

        long getMinNanos() {
            return minNanos == Long.MAX_VALUE ? 0 : minNanos;
        }

        long getMaxNanos() {
            return maxNanos == Long.MIN_VALUE ? 0 : maxNanos;
        }

        long getTotalNanos() {
            return totalNanos;
        }
    }
}