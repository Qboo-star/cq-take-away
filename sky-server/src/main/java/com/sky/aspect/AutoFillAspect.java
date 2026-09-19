package com.sky.aspect;
//自定义切面
import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
  //切入点
  @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
  public void autoFillPointCut() {

  }
  @Before("autoFillPointCut()")
  public void autoFill(JoinPoint joinPoint) {
      log.info("开始执行自动填充...");

      //获取类型
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
    OperationType operationType = autoFill.value();
    //获取当前拦截对象的参数
    Object[] args = joinPoint.getArgs();
    if(args == null || args.length == 0) {
      log.info("参数为空");
      return;
    }

    Object entity = args[0];
    //准备赋值数据
    LocalDateTime now = LocalDateTime.now();
    Long currentId = BaseContext.getCurrentId();

    if(operationType == OperationType.INSERT){
      try {
        Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
        Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
        Method setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser", Long.class);
        Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);
        setCreateTime.invoke(entity, now);
        setUpdateTime.invoke(entity, now);
        setCreateUser.invoke(entity, currentId);
        setUpdateUser.invoke(entity, currentId);
      } catch (Exception e) {
        e.printStackTrace();
      }

    } else if (operationType == OperationType.UPDATE) {
      try {
        Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
        setUpdateTime.invoke(entity, now);
        Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);
        setUpdateUser.invoke(entity, currentId);
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
  }
}
