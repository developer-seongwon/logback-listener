package org.sw.logback;

import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SafeRollingFileAppender<E> extends RollingFileAppender<E> {

    @Override
    public void start() {
        System.out.println("SafeRollingFileAppender.start");
        super.start();
    }

    @Override
    protected void append(E eventObject) {
        createFileIfNotExists();
        super.append(eventObject);
    }

    public Path createFileIfNotExists() {
        try {
            Path path = Paths.get(getFile());

            // 1. 상위 디렉토리 생성 (null 체크 포함)
            Path parentDir = path.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir); // 디렉토리 없으면 생성, 있으면 무시
            }

            // 2. 파일 생성 (없으면 생성, 있으면 무시)
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            return path;

        } catch (IOException e) {
            throw new RuntimeException("파일 생성 실패: " + getFile(), e);
        }
    }

    @Override
    public void setRollingPolicy(RollingPolicy policy) {
        System.out.println("SafeRollingFileAppender.setRollingPolicy: " + policy.getClass().getName());
        super.setRollingPolicy(policy);
    }

    @Override
    public void setTriggeringPolicy(TriggeringPolicy<E> policy) {
        System.out.println("SafeRollingFileAppender.setTriggeringPolicy: " + policy.getClass().getName());
        super.setTriggeringPolicy(policy);
    }
}
