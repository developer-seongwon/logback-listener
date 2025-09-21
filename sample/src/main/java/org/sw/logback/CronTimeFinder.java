package org.sw.logback;

import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sw.logback.cron.CronPreviousTimeFinder;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

public class CronTimeFinder {
//    private Logger logger = LoggerFactory.getLogger(CronTimeFinder.class);
    private final CronExpression cron;
    private final CronPreviousTimeFinder previousFinder;
    private final Date[] caches;
    private Date reference;


    public CronTimeFinder(CronExpression cron, Date reference, int caches) {
        this.cron = cron;
        this.previousFinder = new CronPreviousTimeFinder(cron.toString());
        this.caches = new Date[caches];
        setReference(reference);
    }

    public void setReference(Date reference) {
        this.reference = reference;
        synchronized (this.caches) {
            Date time = this.cron.getNextValidTimeAfter(this.reference);
            if (this.caches[0] == null) {
                buildCache(time);
            } else {
                searchCache(time).ifPresentOrElse(this::shiftCache, () -> buildCache(time));
            }
        }
    }


    @Override
    public String toString() {
        return this.cron.toString();
    }

    public long getTriggeringCountBetween(Date from, Date to) {
        long count = 0;
        Date next = getNextValidTimeAfter(from);
        while (next != null && !next.after(to)) {
            count++;
            next = getNextValidTimeAfter(next);
        }
        return count;
    }

    /**
     * 매개변수로 전달받은 시간을 기준으로 다음 스케줄 시간을 제공합니다.
     *
     * @param date
     * @return
     */
    public Date getNextValidTimeAfter(Date date) {
        Date result = this.cron.getNextValidTimeAfter(date);
//        this.logger.info("getNextValidTimeAfter: {} -> {}", date.toString(), result.toString());
        return result;
    }

    public Date getPreviousValidTimeBefore(Date date) {
        return this.previousFinder.getPreviousValidTimeBefore(date);
    }


    public Date getEndOfNextNthPeriod(Date date, int periods) {
        Date result = date;
        if (periods > 0) {
            // 미래 방향으로 이동
            for (int i = 0; i < periods; i++) {
                result = this.cron.getNextValidTimeAfter(result);
                if (result == null) {
                    throw new IllegalStateException("다음 유효한 시간을 찾을 수 없습니다.");
                }
            }
        } else {
            // 과거 방향으로 이동 (periods < 0)
            for (int i = 0; i <= Math.abs(periods); i++) {
                result = this.previousFinder.getPreviousValidTimeBefore(result);
                if (result == null) {
                    throw new IllegalStateException("이전 유효한 시간을 찾을 수 없습니다.");
                }
            }
        }
        return result;
    }

    /**
     * 주어진 기준 시간에서 periods만큼 이동한 스케줄 시간을 계산합니다.
     *
     * @param base    기준 시간
     * @param periods 이동할 주기 수 (양수: 미래, 음수: 과거)
     * @return 계산된 스케줄 시간
     */
    private Date calculateScheduleTime(Date base, int periods) {
        if (periods == 0) {
            return base;
        }

        Date result = base;

        if (periods > 0) {
            // 미래 방향으로 이동
            for (int i = 0; i < periods; i++) {
                result = this.cron.getNextValidTimeAfter(result);
                if (result == null) {
                    throw new IllegalStateException("다음 유효한 시간을 찾을 수 없습니다.");
                }
            }
        } else {
            // 과거 방향으로 이동 (periods < 0)
            for (int i = 0; i <= Math.abs(periods); i++) {
                result = this.previousFinder.getPreviousValidTimeBefore(result);
                if (result == null) {
                    throw new IllegalStateException("이전 유효한 시간을 찾을 수 없습니다.");
                }
            }
        }
        return result;
    }


    private Optional<Integer> searchCache(Date date) {
        for (int i = 0; i < this.caches.length; i++) {
            if (this.caches[i].equals(date)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    /**
     * 전체 캐시 재구성
     */
    private void buildCache(Date newTime) {
        int centerIndex = this.caches.length / 2;
        this.caches[centerIndex] = newTime;

        // Previous 시간들 (center부터 역순으로)
        for (int i = centerIndex - 1; i >= 0; i--) {
            this.caches[i] = this.previousFinder.getPreviousValidTimeBefore(this.caches[i + 1]);
        }

        // Next 시간들 (center부터 순서대로)
        for (int i = centerIndex + 1; i < this.caches.length; i++) {
            this.caches[i] = this.cron.getNextValidTimeAfter(this.caches[i - 1]);
        }
        System.out.println(Arrays.toString(this.caches));
    }

    /**
     * 캐시를 재정렬합니다. targetIndex를 중앙 위치로 이동
     */
    private void shiftCache(int targetIndex) {
        int centerIndex = this.caches.length / 2;
        if (targetIndex == centerIndex) return; // 이미 올바른 위치

        Date[] tempCaches = new Date[this.caches.length];

        // targetIndex를 중심으로 재구성
        for (int i = 0; i < this.caches.length; i++) {
            int sourceIndex = targetIndex - centerIndex + i;
            if (sourceIndex >= 0 && sourceIndex < this.caches.length) {
                tempCaches[i] = this.caches[sourceIndex];
            }
        }

        // 빈 슬롯 채우기
        fillMissingSlots(tempCaches);

        // final 배열에 복사
        for (int i = 0; i < this.caches.length; i++) {
            this.caches[i] = tempCaches[i];
        }
    }

    /**
     * 빈 슬롯들을 채웁니다
     */
    private void fillMissingSlots(Date[] caches) {
        int centerIndex = caches.length / 2;

        // center부터 앞쪽 빈 슬롯 채우기
        for (int i = centerIndex - 1; i >= 0; i--) {
            if (caches[i] == null && caches[i + 1] != null) {
                caches[i] = this.previousFinder.getPreviousValidTimeBefore(caches[i + 1]);
            }
        }

        // center부터 뒤쪽 빈 슬롯 채우기
        for (int i = centerIndex + 1; i < caches.length; i++) {
            if (caches[i] == null && caches[i - 1] != null) {
                caches[i] = this.cron.getNextValidTimeAfter(caches[i - 1]);
            }
        }
    }


}
