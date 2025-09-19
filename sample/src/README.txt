#RollingCalendar.class

사용중인 클래스 목록
# TimeBasedFileNamingAndTriggeringPolicyBase
# SizeAndTimeBasedFileNamingAndTriggeringPolicy
# DefaultTimeBasedFileNamingAndTriggeringPolicy
# TimeBasedArchiveRemover
# SizeAndTimeBasedArchiveRemover

클래스별로 용도와 사용중인 메소드
# TimeBasedFileNamingAndTriggeringPolicyBase.class
시간 기준 트리거에 활용

- Constructor
- printPeriodicity
-- 롤링 주기에 따른 로그 출력
- isCollisionFree
-- 롤링 주기에 따라 파일 이름이 겹치는지 확인
- getNextTriggeringDate
-- 현재 시간 기준으로 다음 트리거 시간 제공
========================================================
# SizeAndTimeBasedFileNamingAndTriggeringPolicy
# DefaultTimeBasedFileNamingAndTriggeringPolicy
TimeBasedFileNamingAndTriggeringPolicyBase에서 생성된
RollingCalendar를 사용하여 각각의 Remover 생성

========================================================
# TimeBasedArchiveRemover
현재(now)를 기준으로 파일을 삭제 할때 사용
최대 보관 날짜(max history)만큼 보관해야하니까
최대 보관 날짜에 +1을 더한뒤 음수로 변환하여 삭제해야하는 범위(A)를 설정하고
RollingCalendar의 periodBarriersCrossed를 호출하여
현재 시간에 음수 시간 범위(A)를 더한 뒤

clean을 할 때
최초 호출 시에는 현재 시간부터 한달 후 만큼의 조건으로 수행
-- 이후에는 이전 호출 시간과 현재 호출 시간 만큼의 조건으로 수행

- periodBarriersCrossed
-- 시스템과 설정이 서로 다른 시간대일 경우에 오차가 발생하니까 강제로 시스템 시간으로 변경하여 시간 내에 발생 횟수 제공
- innerGetEndOfNextNthPeriod
-- 제공된 시간을 기준으로 반복 횟수(numPeriods)만큼 회귀한 시간 제공