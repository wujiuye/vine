package com.wujiuye.vine.core.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * agent日记打印者
 *
 * @author wuijuye 2020/08/28
 */
public final class AgentLogger {

    private final Logger logger;

    private AgentLogger() {
        logger = getLogger("agent");
    }

    private static final AgentLogger agentLogger = new AgentLogger();

    public static AgentLogger getLogger() {
        return agentLogger;
    }

    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    private static Logger getLogger(String name) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<>();
        ca.setName("CONSOLE");
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%d{yyyy-MM-dd}-%d{HH:mm:ss} [%thread] %-5level %logger{35} - %msg%n");
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.setContext(loggerContext);
        encoder.start();
        ca.setEncoder(encoder);
        ca.start();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger(name);
        logger.setLevel(Level.INFO);
        logger.addAppender(ca);
        logger.setAdditive(false);
        return logger;
    }

}
