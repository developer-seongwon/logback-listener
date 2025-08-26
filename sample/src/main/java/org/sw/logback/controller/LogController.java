package org.sw.logback.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);
    private static final Logger olg = LoggerFactory.getLogger("com.example.dynamic");

    @GetMapping("/test")
    public String testLogging() {
        logger.trace("TRACE 레벨 로그입니다.");
        logger.debug("DEBUG 레벨 로그입니다.");
        logger.info("INFO 레벨 로그입니다.");
        logger.warn("WARN 레벨 로그입니다.");
        logger.error("ERROR 레벨 로그입니다.");

        olg.trace("TRACE 레벨 로그입니다.");
        olg.debug("DEBUG 레벨 로그입니다.");
        olg.info("INFO 레벨 로그입니다.");
        olg.warn("WARN 레벨 로그입니다.");
        olg.error("ERROR 레벨 로그입니다.");

        return "로깅 테스트 완료! 로그 파일을 확인해보세요.";
    }

    @GetMapping("/error")
    public String testError() {
        try {
            throw new RuntimeException("테스트용 예외입니다.");
        } catch (Exception e) {
            logger.error("예외가 발생했습니다: {}", e.getMessage(), e);
            return "에러 로깅 테스트 완료!";
        }
    }
}
