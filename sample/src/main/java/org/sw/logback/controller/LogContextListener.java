package org.sw.logback.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.StatusManager;

import static ch.qos.logback.classic.util.StatusViaSLF4JLoggerFactory.addInfo;

public class LogContextListener implements LoggerContextListener {

    @Override
    public boolean isResetResistant() {
        return false;
    }

    @Override
    public void onStart(LoggerContext context) {
        System.out.println("LogContextListener.onStart()");


        // 1. RollingFileAppender 생성 및 설정
        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
        fileAppender.setContext(context);
        fileAppender.setName("DYNAMIC_FILE");
        fileAppender.setFile("logs/dynamic/application.log");

        // 2. PatternLayoutEncoder 설정
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%logger{36}] - %msg%n");
        encoder.start();

        fileAppender.setEncoder(encoder);

        // 3. RollingPolicy 설정
        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.setFileNamePattern("logs/dynamic/application.%d{yyyy-MM-dd}.log");
        rollingPolicy.setMaxHistory(7);  // 7일간 보관
        rollingPolicy.start();
        fileAppender.setRollingPolicy(rollingPolicy);

        // 4. Appender 시작
        fileAppender.start();

        // 5. 특정 패키지용 Logger 생성 및 설정
        Logger customLogger = context.getLogger("com.example.dynamic");
        customLogger.setLevel(Level.DEBUG);
        customLogger.addAppender(fileAppender);
        customLogger.setAdditive(false);  // 루트 로거로 전파하지 않음

//        addInfo("동적 로거 생성 완료: com.example.dynamic");
//        addInfo("ERROR 전용 로거 생성 완료: com.example.errors");

    }

    @Override
    public void onReset(LoggerContext context) {
        System.out.println("LogContextListener.onReset()");

    }

    @Override
    public void onStop(LoggerContext context) {
        System.out.println("LogContextListener.onStop()");
    }

    @Override
    public void onLevelChange(Logger logger, Level level) {
        System.out.println(logger.getName() + "  : " + level);
        System.out.println("LogContextListener.onLevelChange()");
    }

//    private boolean started = false;
//
//    @Override
//    public void start() {
//        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAA");
//
//        Context context = getContext();
//        StatusManager statusManager = context.getStatusManager();
//
//        // 시작 시 로직
//        addInfo("=== CustomLogbackContextListener 시작 ===");
//        addInfo("컨텍스트 이름: " + context.getName());
//        addInfo("컨텍스트 시작 시간: " + context.getBirthTime());
//
//        // 시스템 속성 설정 (예시)
//        context.putProperty("app.log.startTime", String.valueOf(System.currentTimeMillis()));
//        context.putProperty("app.log.version", "1.0.0");
//
//        // 환경별 설정
//        String profile = System.getProperty("spring.profiles.active", "default");
//        context.putProperty("app.profile", profile);
//        addInfo("현재 프로필: " + profile);
//
//        // 로그 디렉토리 생성
//        createLogDirectories(context);
//
//        started = true;
//        addInfo("CustomLogbackContextListener 초기화 완료");
//    }
//
//    @Override
//    public void stop() {
//        if (!started) {
//            return;
//        }
//
//        addInfo("=== CustomLogbackContextListener 종료 ===");
//
//        Context context = getContext();
//
//        // 종료 시 정리 작업
//        performCleanup(context);
//
//        addInfo("CustomLogbackContextListener 종료 완료");
//        started = false;
//    }
//
//    @Override
//    public boolean isStarted() {
//        return started;
//    }
//
//    /**
//     * 로그 디렉토리 생성
//     */
//    private void createLogDirectories(Context context) {
//        try {
//            String logHome = System.getProperty("LOG_HOME", "./logs");
//            java.io.File logDir = new java.io.File(logHome);
//
//            if (!logDir.exists()) {
//                boolean created = logDir.mkdirs();
//                if (created) {
//                    addInfo("로그 디렉토리 생성: " + logDir.getAbsolutePath());
//                    context.putProperty("LOG_HOME", logDir.getAbsolutePath());
//                } else {
//                    addWarn("로그 디렉토리 생성 실패: " + logDir.getAbsolutePath());
//                }
//            } else {
//                addInfo("로그 디렉토리 존재: " + logDir.getAbsolutePath());
//                context.putProperty("LOG_HOME", logDir.getAbsolutePath());
//            }
//
//            // 서브 디렉토리들도 생성
//            createSubDirectory(logDir, "app");
//            createSubDirectory(logDir, "error");
//            createSubDirectory(logDir, "access");
//
//        } catch (Exception e) {
//            addError("로그 디렉토리 생성 중 오류 발생", e);
//        }
//    }
//
//    /**
//     * 서브 디렉토리 생성
//     */
//    private void createSubDirectory(java.io.File parent, String subDirName) {
//        java.io.File subDir = new java.io.File(parent, subDirName);
//        if (!subDir.exists()) {
//            boolean created = subDir.mkdirs();
//            if (created) {
//                addInfo("서브 디렉토리 생성: " + subDir.getAbsolutePath());
//            }
//        }
//    }
//
//    /**
//     * 종료 시 정리 작업
//     */
//    private void performCleanup(Context context) {
//        try {
//            // 임시 파일 정리
//            String tempDir = context.getProperty("java.io.tmpdir");
//            if (tempDir != null) {
//                addInfo("임시 디렉토리: " + tempDir);
//                // 필요시 임시 파일 정리 로직 추가
//            }
//
//            // 통계 정보 출력
//            long uptime = System.currentTimeMillis() - context.getBirthTime();
//            addInfo("애플리케이션 실행 시간: " + (uptime / 1000) + "초");
//
//        } catch (Exception e) {
//            addError("정리 작업 중 오류 발생", e);
//        }
//    }
}
