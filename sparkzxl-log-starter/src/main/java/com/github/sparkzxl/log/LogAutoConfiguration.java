package com.github.sparkzxl.log;

import com.github.sparkzxl.log.annotation.OptLogRecord;
import com.github.sparkzxl.log.aop.OptLogRecordAnnotationAdvisor;
import com.github.sparkzxl.log.aop.OptLogRecordInterceptor;
import com.github.sparkzxl.log.aspect.HttpRequestLogAspect;
import com.github.sparkzxl.log.event.HttpRequestLogListener;
import com.github.sparkzxl.log.event.OptLogListener;
import com.github.sparkzxl.log.handler.DefaultOptOptLogVariablesHandler;
import com.github.sparkzxl.log.handler.IOptLogVariablesHandler;
import com.github.sparkzxl.log.properties.LogProperties;
import com.github.sparkzxl.log.store.OperatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Optional;

/**
 * description: 日志增强自动装配
 *
 * @author zhouxinlei
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {LogProperties.class})
public class LogAutoConfiguration {

    @Autowired
    void setAlarmLogConfig(LogProperties logProperties) {
        LogProperties.AlarmProperties alarmProperties = logProperties.getAlarm();
        if (alarmProperties.isEnabled()) {
            Optional.ofNullable(alarmProperties.getDoWarnException()).ifPresent(AlarmLogContext::addDoWarnExceptionList);
            Optional.of(alarmProperties.isWarnExceptionExtend()).ifPresent(AlarmLogContext::setWarnExceptionExtend);
            Optional.of(alarmProperties.isSimpleWarnInfo()).ifPresent(AlarmLogContext::setPrintStackTrace);
            Optional.of(alarmProperties.isSimpleWarnInfo()).ifPresent(AlarmLogContext::setSimpleWarnInfo);
        }
    }

    @Bean(name = "defaultOptOptLogVariablesHandler")
    @ConditionalOnMissingBean
    public IOptLogVariablesHandler defaultOptOptLogVariablesHandler() {
        return new DefaultOptOptLogVariablesHandler();
    }

    @Bean
    public HttpRequestLogAspect httpRequestLogAspect() {
        return new HttpRequestLogAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpRequestLogListener httpRequestLogListener() {
        return new HttpRequestLogListener(log -> {
        });
    }

    @Bean
    @ConditionalOnMissingBean(OperatorService.class)
    public OperatorService operatorService() {
        return new OperatorService();
    }

    @Bean
    public OptLogRecordInterceptor optLogRecordInterceptor(OperatorService operatorService) {
        return new OptLogRecordInterceptor(operatorService);
    }

    @Bean
    public OptLogRecordAnnotationAdvisor optLogRecordAnnotationAdvisor(OptLogRecordInterceptor optLogRecordInterceptor) {
        return new OptLogRecordAnnotationAdvisor(optLogRecordInterceptor, OptLogRecord.class, Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    @ConditionalOnMissingBean
    public OptLogListener optLogListener() {
        return new OptLogListener(log -> {
        });
    }

}
