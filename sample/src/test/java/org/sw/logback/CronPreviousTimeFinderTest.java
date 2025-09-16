package org.sw.logback;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.sw.logback.cron.CronPreviousTimeFinder;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@DisplayName("CronPreviousTimeFinder 테스트")
class CronPreviousTimeFinderTest {

    private SimpleDateFormat sdf;
    
    @BeforeEach
    void setUp() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    private void performTest(String cronExpr, String currentTimeStr, String expectedTimeStr, String description) throws Exception {
        long startTime = System.nanoTime();
        
        CronPreviousTimeFinder finder = new CronPreviousTimeFinder(cronExpr);
        Date currentTime = sdf.parse(currentTimeStr);
        Date previousTime = finder.getPreviousValidTime(currentTime);
        
        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        
        assertNotNull(previousTime, "이전 실행 시간이 존재해야 함");
        
        Date expectedPrevious = sdf.parse(expectedTimeStr);
        assertEquals(expectedPrevious, previousTime);
        
        System.out.printf("🚀 %-20s | %s -> %s | %.3fms%n", 
                description, currentTimeStr, sdf.format(previousTime), durationMs);
    }
    
    @Test
    @DisplayName("매 30초마다 - */30 * * * * ?")
    void testEvery30Seconds() throws Exception {
        performTest("*/30 * * * * ?", "2024-08-27 14:35:45", "2024-08-27 14:35:30", "30초마다");
    }
    
    @Test
    @DisplayName("매 10분마다 - 0 */10 * * * ?")
    void testEvery10Minutes() throws Exception {
        performTest("0 */10 * * * ?", "2024-08-27 14:35:00", "2024-08-27 14:30:00", "10분마다");
    }
    
    @Test
    @DisplayName("매 2시간마다 - 0 0 */2 * * ?")
    void testEvery2Hours() throws Exception {
        performTest("0 0 */2 * * ?", "2024-08-27 15:30:00", "2024-08-27 14:00:00", "2시간마다");
    }
    
    @Test
    @DisplayName("매일 자정 - 0 0 0 * * ?")
    void testDailyAtMidnight() throws Exception {
        performTest("0 0 0 * * ?", "2024-08-27 15:30:00", "2024-08-27 00:00:00", "매일 자정");
    }
    
    @Test
    @DisplayName("업무시간 매시간 - 0 0 9-17 * * ?")
    void testBusinessHours() throws Exception {
        performTest("0 0 9-17 * * ?", "2024-08-27 15:30:00", "2024-08-27 15:00:00", "업무시간 매시간");
    }
    
    @Test
    @DisplayName("주중 오전 9시 - 0 0 9 ? * MON-FRI")
    void testWeekdayMorning() throws Exception {
        performTest("0 0 9 ? * MON-FRI", "2024-08-27 15:30:00", "2024-08-27 09:00:00", "주중 오전 9시");
    }
    
    @Test
    @DisplayName("월 2회(1,15일) - 0 0 12 1,15 * ?")
    void testTwiceMonthly() throws Exception {
        performTest("0 0 12 1,15 * ?", "2024-08-20 15:30:00", "2024-08-15 12:00:00", "월 2회(1,15일)");
    }
    
    @Test
    @DisplayName("분기별 - 0 0 0 1 1,4,7,10 ?")
    void testQuarterly() throws Exception {
        performTest("0 0 0 1 1,4,7,10 ?", "2024-08-15 15:30:00", "2024-07-01 00:00:00", "분기별");
    }
    
    @Test
    @DisplayName("주말 저녁 - 0 0 18 ? * SAT,SUN")
    void testWeekendEvening() throws Exception {
        performTest("0 0 18 ? * SAT,SUN", "2024-08-27 15:30:00", "2024-08-25 18:00:00", "주말 저녁");
    }
    
    @Test
    @DisplayName("복잡한 스케줄 - 0 15,45 8-17 ? * MON-FRI")
    void testComplexSchedule() throws Exception {
        performTest("0 15,45 8-17 ? * MON-FRI", "2024-08-27 14:30:00", "2024-08-27 14:15:00", "복잡한 스케줄");
    }
    
    @Test
    @DisplayName("매분 실행 - 0 * * * * ?")
    void testEveryMinute() throws Exception {
        performTest("0 * * * * ?", "2024-08-27 14:35:30", "2024-08-27 14:35:00", "매분 실행");
    }
    
    @Test
    @DisplayName("극한 성능 테스트 - 연간 한 번")
    void testYearlyPerformance() throws Exception {
        System.out.println("\n⚡ 극한 성능 테스트 시작...");
        performTest("0 0 0 1 1 ?", "2024-12-31 23:59:59", "2024-01-01 00:00:00", "연간 한 번(극한)");
    }
    
    @Test
    @DisplayName("성능 비교 테스트")
    void testPerformanceComparison() throws Exception {
        String[][] testCases = {
            {"*/5 * * * * ?", "2024-08-27 14:35:37", "빠름(5초마다)"},
            {"0 */5 * * * ?", "2024-08-27 14:35:00", "중간(5분마다)"},
            {"0 0 */6 * * ?", "2024-08-27 14:35:00", "느림(6시간마다)"},
            {"0 0 0 ? * SUN", "2024-08-27 14:35:00", "매우느림(주일마다)"}
        };
        
        System.out.println("\n🏁 성능 비교 테스트:");
        System.out.println("=".repeat(80));
        
        for (String[] testCase : testCases) {
            String cronExpr = testCase[0];
            String timeStr = testCase[1];
            String desc = testCase[2];
            
            long startTime = System.nanoTime();
            
            CronPreviousTimeFinder finder = new CronPreviousTimeFinder(cronExpr);
            Date currentTime = sdf.parse(timeStr);
            Date previousTime = finder.getPreviousValidTime(currentTime);
            
            long endTime = System.nanoTime();
            double durationMs = (endTime - startTime) / 1_000_000.0;
            
            System.out.printf("%-18s | %s | %.3fms%n", 
                    desc, 
                    previousTime != null ? sdf.format(previousTime) : "null",
                    durationMs);
        }
        System.out.println("=".repeat(80));
    }
    
    @Test
    @DisplayName("잘못된 cron 표현식 예외 처리")
    void testInvalidCronExpression() {
        assertThrows(ParseException.class, () -> 
            new CronPreviousTimeFinder("invalid cron"),
            "잘못된 cron 표현식은 ParseException을 발생시켜야 함");
        
        assertThrows(ParseException.class, () ->
            new CronPreviousTimeFinder("0 0 0"),
            "필드 수가 부족한 cron 표현식은 ParseException을 발생시켜야 함");
    }
}
