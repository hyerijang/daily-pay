package com.hyerijang.dailypay.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class ExecutionTimer {

    // 조인포인트를 어노테이션으로 설정
    @Pointcut("@annotation(ExeTimer)")
    private void timer() {
    }

    // 메서드 실행 전,후로 시간을 공유해야 하기 때문
    @Around("timer()")
    public Object AssumeExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();


        log.debug("[START] 실행 메서드: {}", signature.toShortString());

        stopWatch.start();
        Object proceed = joinPoint.proceed();
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();

        log.debug("[END] 실행 메서드: {}, 실행시간: {}ms", signature.toShortString(), totalTimeMillis);
        return proceed;
    }
}
