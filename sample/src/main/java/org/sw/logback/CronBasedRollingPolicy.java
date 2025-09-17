package org.sw.logback;

import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.sw.logback.cron.CronCalculator;

import java.text.ParseException;

public class CronBasedRollingPolicy<E> extends TimeBasedRollingPolicy<E> {
    CronCalculator calculator;
    DefaultCronBasedFileNamingAndTriggeringPolicy<E> fileNamingTriggeringPolicy;

    @Override
    public void start() {
        try {
            if (this.calculator == null) {
                this.calculator = new CronCalculator("0 0 0 * * ?");
            }
            addInfo(String.format("Will use the pattern '%s' for the active file", this.calculator.getCron()));

            this.fileNamingTriggeringPolicy = new DefaultCronBasedFileNamingAndTriggeringPolicy<>();
            this.fileNamingTriggeringPolicy.setCronBasedRollingPolicy(this);
//            this.fileNamingTriggeringPolicy.setDateInCurrentPeriod(this.calculator.getPreviousFireTime().getTime());

            // using a wrapper object
            setTimeBasedFileNamingAndTriggeringPolicy(this.fileNamingTriggeringPolicy);

            super.start();
        } catch (Exception ignore) {

        }
    }

//    private void schedule() {
//        File parent = new File(getTimeBasedFileNamingAndTriggeringPolicy().getCurrentPeriodsFileNameWithoutCompressionSuffix()).getParentFile();
//        if (parent == null) {
//            return;
//        }
//        try {
//            if (parent.mkdirs()) {
//                addInfo("create parent directory: " + parent.getAbsolutePath());
//            }
//        } catch (Exception cause) {
//            stop();
//            addError("IO failure in cron based rolling policy", cause);
//        }
//    }

//    @Override
//    public boolean isTriggeringEvent(File activeFile, E event) {
//        System.out.println("CronBasedRollingPolicy.isTriggeringEvent");
//        long now = getTimeBasedFileNamingAndTriggeringPolicy().getCurrentTime();
//        if (now > this.calculator.getFireTime().getTime()) {
//            // 시간 레퍼런스 변경
//            this.calculator.setReference(new Date(now));
//            // 시간 기준 변경
//            this.fileNamingTriggeringPolicy.setDateInCurrentPeriod(this.calculator.getPreviousFireTime().getTime());
//            return true;
//        } else {
//            return false;
//        }
//    }

    public void setCron(String cron) throws ParseException {
        this.calculator = new CronCalculator(cron);
    }

//    /**
//     * 직접적으로 사용하지 않지만 동작 원리 이해를 위해 명시적으로 선언합니다.
//     * <p>
//     * Logback XML 설정의 <fileNamePattern/> 태그 값이 자동으로 이 메서드를 통해 주입됩니다.
//     * 제 값은 RollingPolicyBase 클래스에서 관리되며, getFileNamePattern() 메서드로 접근 가능합니다.
//     *
//     * @param fnp 파일명 패턴 문자열 (예: "logs/app.%d{yyyy-MM-dd}.log")
//     * @see RollingPolicyBase#setFileNamePattern(String)
//     * @see RollingPolicyBase#getFileNamePattern()
//     */
//    @Override
//    public void setFileNamePattern(String fnp) {
//        super.setFileNamePattern(fnp);
//    }
//
//    /**
//     * 직접적으로 사용하지 않지만 동작 원리 이해를 위해 명시적으로 선언합니다.
//     * <p>
//     * RollingPolicy 인터페이스의 메서드로, 롤링 정책이 자신을 포함하는
//     * FileAppender를 인식할 수 있도록 합니다. 이 메서드는 Logback의 설정 시스템이
//     * RollingFileAppender와 RollingPolicy를 연결할 때 자동으로 호출됩니다.
//     * <p>
//     * This method from the RollingPolicy interface allows the rolling policy
//     * implementation to be aware of its containing appender. It is automatically
//     * called by Logback's configuration system when connecting a RollingFileAppender
//     * with its RollingPolicy.
//     *
//     * @param appender 이 롤링 정책을 포함하는 FileAppender
//     *                 the FileAppender that contains this rolling policy
//     */
//    public void setParent(FileAppender<?> appender) {
//        super.setParent(appender);
//    }
//
//    /**
//     * 직접적으로 사용하지 않지만 동작 원리 이해를 위해 명시적으로 선언합니다.
//     * <p>
//     * Prudent 모드는 여러 JVM이 동시에 같은 로그 파일에 안전하게 쓸 수 있도록
//     * 하는 모드입니다. 이 정보는 롤링 정책에서 파일 처리 방식을 결정할 때 사용됩니다.
//     * 일부 롤링 정책(예: FixedWindowRollingPolicy)은 Prudent 모드를 지원하지 않습니다.
//     * <p>
//     * Prudent mode allows multiple JVMs to safely write to the same log file
//     * concurrently. This information is used by rolling policies to determine
//     * their file handling approach. Some rolling policies (e.g., FixedWindowRollingPolicy)
//     * do not support prudent mode.
//     *
//     * @return 부모 Appender가 Prudent 모드이면 true, 그렇지 않으면 false
//     * true if the parent appender is in prudent mode, false otherwise
//     */
//    public boolean isParentPrudent() {
//        return super.isParentPrudent();
//    }
//
//    /**
//     * 직접적으로 사용하지 않지만 동작 원리 이해를 위해 명시적으로 선언합니다.
//     * <p>
//     * 부모 FileAppender에 설정된 파일 경로를 반환합니다. 이 값은 롤링 과정에서
//     * 현재 활성 로그 파일을 식별하고 처리할 때 사용됩니다. 압축이나 파일 이름 변경
//     * 작업 시 원본 파일의 위치를 파악하는 데 필요합니다.
//     * <p>
//     * This method returns the file path configured in the parent FileAppender.
//     * This value is used during the rolling process to identify and handle
//     * the currently active log file. It is needed for compression and file
//     * renaming operations to determine the original file location.
//     *
//     * @return 부모 Appender에 설정된 원본 파일 경로, 설정되지 않았으면 null
//     * the raw file path configured in parent appender, or null if not set
//     */
//    public String getParentsRawFileProperty() {
//        return super.getParentsRawFileProperty();
//    }
//
//
//    @Override
//    public String toString() {
//        return "o.s.l.CronBasedRollingPolicy";
//    }
//
//    public String getString(Object obj) {
//        try {
//            return String.valueOf(obj);
//        } catch (Exception cause) {
//            return cause.toString();
//        }
//    }
//
//    public Path createFileIfNotExists(String filePathStr) {
//        try {
//            Path path = Paths.get(filePathStr);
//
//            // 1. 상위 디렉토리 생성 (null 체크 포함)
//            Path parentDir = path.getParent();
//            if (parentDir != null) {
//                Files.createDirectories(parentDir); // 디렉토리 없으면 생성, 있으면 무시
//            }
//
//            // 2. 파일 생성 (없으면 생성, 있으면 무시)
//            if (!Files.exists(path)) {
//                Files.createFile(path);
//            }
//
//            return path;
//
//        } catch (IOException e) {
//            throw new RuntimeException("파일 생성 실패: ", e);
//        }
//    }
}

