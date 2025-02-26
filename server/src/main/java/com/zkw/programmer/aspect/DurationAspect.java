package com.zkw.programmer.aspect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Component
@Aspect
public class DurationAspect {

    private static final Log logger = LogFactory.getLog(DurationAspect.class);

    // * 匹配任意返回值  ..匹配任意入参
    @Around("execution(* com.zkw.programmer.controller.RentalController.getRentalListByPage(..))")
    public Object getRentalListByPage(ProceedingJoinPoint joinPoint) {
        long startTime = System.nanoTime();
        logger.info("开始执行查询租借器材数据：" + joinPoint.getSignature().getName());
        Object obj = new Object();
        try {
            obj = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            // 获得标准纳秒数
            Duration time = Duration.ofNanos(System.nanoTime() - startTime);
            // 从标准纳秒数取出对应秒数
            logger.info("执行查询租借器材数据完毕，消耗了：" + time.getSeconds() + "s");
            return obj;
        }
    }

    // * 匹配任意返回值  ..匹配任意入参
    @Around("execution(* com.zkw.programmer.controller.AppointmentController.getAppointmentListByPage(..))")
    public Object getAppointmentListByPage(ProceedingJoinPoint joinPoint) {
        long startTime = System.nanoTime();
        logger.info("开始执行查询预约场馆数据：" + joinPoint.getSignature().getName());
        Object obj = new Object();
        try {
            obj = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            // 获得标准纳秒数
            Duration time = Duration.ofNanos(System.nanoTime() - startTime);
            // 从标准纳秒数取出对应秒数
            logger.info("执行查询预约场馆数据完毕，消耗了：" + time.getSeconds() + "s");
            return obj;
        }

    }
}
