package org.sw.logback.cron;

import java.util.*;

public class CronPreviousTimeFinder {
    
    private final String cronExpression;
    private final int[] seconds;
    private final int[] minutes; 
    private final int[] hours;
    private final int[] daysOfMonth;
    private final int[] months;
    private final int[] daysOfWeek;
    
    public CronPreviousTimeFinder(String cronExpression){
        this.cronExpression = cronExpression;
        String[] fields = cronExpression.trim().split("\\s+");
        
        if (fields.length < 6 || fields.length > 7) {
            throw new IllegalArgumentException("Invalid cron expression");
        }
        
        this.seconds = parseField(fields[0], 0, 59);
        this.minutes = parseField(fields[1], 0, 59);
        this.hours = parseField(fields[2], 0, 23);
        this.daysOfMonth = parseField(fields[3], 1, 31);
        this.months = parseField(fields[4], 1, 12);
        this.daysOfWeek = parseField(fields[5], 1, 7); // 1=Monday, 7=Sunday (Quartz style)
    }
    
    /**
     * 주어진 시간 이전의 cron 실행 시간을 찾습니다.
     */
    public Date getPreviousValidTime(Date beforeTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(beforeTime);
        
        // 1초 전부터 시작 (현재 시간 제외)
        cal.add(Calendar.SECOND, -1);
        
        // 간단한 역방향 검색: 1초씩 뒤로 가면서 매칭 확인
        Calendar limitCal = (Calendar) cal.clone();
        limitCal.add(Calendar.DAY_OF_YEAR, -400); // 400일 전까지만 검색
        
        int iterations = 0;
        
        while (cal.after(limitCal)) {
            iterations++;
            
            if (matches(cal)) {
                return cal.getTime();
            }
            cal.add(Calendar.SECOND, -1);
            
            // 너무 많은 반복을 방지 (최대 1시간 = 3600초)
            if (iterations > 3600) {
                // 더 큰 단위로 점프해서 찾기
                return findWithLargerSteps(beforeTime);
            }
        }
        
        return null; // 이전 실행 시간을 찾지 못함
    }
    
    /**
     * 큰 단위로 점프하면서 찾는 최적화된 방법
     */
    private Date findWithLargerSteps(Date beforeTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(beforeTime);
        
        // 1분 단위로 뒤로 가면서 찾기
        cal.add(Calendar.MINUTE, -1);
        cal.set(Calendar.SECOND, 59); // 각 분의 마지막 초부터
        
        Calendar limitCal = (Calendar) cal.clone();
        limitCal.add(Calendar.DAY_OF_YEAR, -400);
        
        while (cal.after(limitCal)) {
            // 해당 분의 모든 초를 체크
            for (int sec = 59; sec >= 0; sec--) {
                cal.set(Calendar.SECOND, sec);
                if (matches(cal)) {
                    return cal.getTime();
                }
            }
            cal.add(Calendar.MINUTE, -1);
            cal.set(Calendar.SECOND, 59);
        }
        
        return null;
    }
    
    /**
     * 주어진 Calendar가 cron 표현식과 일치하는지 확인
     */
    private boolean matches(Calendar cal) {
        int second = cal.get(Calendar.SECOND);
        int minute = cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH는 0부터 시작
        
        // Java Calendar에서 일요일=1, 월요일=2, ..., 토요일=7
        // Quartz에서 일요일=1, 월요일=2, ..., 토요일=7 (동일)
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        
        // 기본 필드들 체크
        if (!contains(seconds, second) ||
            !contains(minutes, minute) ||
            !contains(hours, hour) ||
            !contains(months, month)) {
            return false;
        }
        
        // Day of month와 Day of week 체크 (둘 중 하나만 만족하면 됨)
        boolean dayOfMonthMatches = contains(daysOfMonth, dayOfMonth);
        boolean dayOfWeekMatches = contains(daysOfWeek, dayOfWeek);
        
        // ?가 있는 경우 처리
        boolean hasDayOfMonthWildcard = daysOfMonth.length == 31; // ? 또는 *
        boolean hasDayOfWeekWildcard = daysOfWeek.length == 7; // ? 또는 *
        
        if (hasDayOfMonthWildcard && !hasDayOfWeekWildcard) {
            return dayOfWeekMatches;
        } else if (!hasDayOfMonthWildcard && hasDayOfWeekWildcard) {
            return dayOfMonthMatches;
        } else if (hasDayOfMonthWildcard && hasDayOfWeekWildcard) {
            return true; // 둘 다 와일드카드면 항상 참
        } else {
            return dayOfMonthMatches || dayOfWeekMatches; // 둘 중 하나라도 만족
        }
    }
    
    /**
     * 배열에 특정 값이 포함되어 있는지 확인합니다.
     */
    private boolean contains(int[] array, int value) {
        return Arrays.binarySearch(array, value) >= 0;
    }
    
    /**
     * Cron 필드를 파싱하여 유효한 값들의 배열을 반환합니다.
     */
    private int[] parseField(String field, int min, int max){
        if ("*".equals(field) || "?".equals(field)) {
            return generateRange(min, max);
        }
        
        Set<Integer> values = new TreeSet<>();
        
        // 콤마로 구분된 값들 처리
        String[] parts = field.split(",");
        for (String part : parts) {
            part = part.trim();
            
            if (part.contains("/")) {
                // Step 값 처리 (예: */15, 10-50/5)
                parseStepValue(part, min, max, values);
            } else if (part.contains("-")) {
                // Range 처리 (예: 10-15, MON-FRI)
                parseRange(part, min, max, values);
            } else {
                // 단일 값 (숫자 또는 이름)
                int value = parseValue(part, min, max);
                if (value >= min && value <= max) {
                    values.add(value);
                }
            }
        }
        
        return values.stream().mapToInt(Integer::intValue).toArray();
    }
    
    private void parseStepValue(String stepExpr, int min, int max, Set<Integer> values){
        String[] stepParts = stepExpr.split("/");
        String rangeExpr = stepParts[0];
        int step = Integer.parseInt(stepParts[1]);
        
        int start, end;
        if ("*".equals(rangeExpr) || "?".equals(rangeExpr)) {
            start = min;
            end = max;
        } else if (rangeExpr.contains("-")) {
            String[] rangeParts = rangeExpr.split("-");
            start = parseValue(rangeParts[0], min, max);
            end = parseValue(rangeParts[1], min, max);
        } else {
            start = parseValue(rangeExpr, min, max);
            end = max;
        }
        
        for (int i = start; i <= end; i += step) {
            if (i >= min && i <= max) {
                values.add(i);
            }
        }
    }
    
    private void parseRange(String rangeExpr, int min, int max, Set<Integer> values){
        String[] rangeParts = rangeExpr.split("-");
        int start = parseValue(rangeParts[0], min, max);
        int end = parseValue(rangeParts[1], min, max);
        
        for (int i = start; i <= end; i++) {
            if (i >= min && i <= max) {
                values.add(i);
            }
        }
    }
    
    private int parseValue(String value, int min, int max){
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // 요일 이름 처리 (간단화된 버전)
            switch (value.toUpperCase()) {
                case "SUN": return 1;
                case "MON": return 2;
                case "TUE": return 3;
                case "WED": return 4;
                case "THU": return 5;
                case "FRI": return 6;
                case "SAT": return 7;
                default: throw new IllegalArgumentException("Invalid value: " + value);
            }
        }
    }
    
    private int[] generateRange(int min, int max) {
        int[] range = new int[max - min + 1];
        for (int i = 0; i < range.length; i++) {
            range[i] = min + i;
        }
        return range;
    }
    
    public String getCronExpression() {
        return cronExpression;
    }
}
