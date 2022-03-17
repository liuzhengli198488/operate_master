package com.gys.common.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.*;
import org.apache.ibatis.javassist.bytecode.CodeAttribute;
import org.apache.ibatis.javassist.bytecode.LocalVariableAttribute;
import org.apache.ibatis.javassist.bytecode.MethodInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Slf4j
@Component
@Aspect
public class LogAspect {

    private static String[] types = {"java.lang.Integer", "java.lang.Double",
            "java.lang.Float", "java.lang.Long", "java.lang.Short",
            "java.lang.Byte", "java.lang.Boolean", "java.lang.Char",
            "java.lang.String", "int", "double", "long", "short", "byte",
            "boolean", "char", "float"};


    /**
     * 切入点
     */
    @Pointcut("@annotation(com.gys.common.log.Log) ")
    public void entryPoint() {
        // 无需内容
    }

    /**
     * 环绕通知处理处理
     *
     * @param point
     * @throws Throwable
     */
    @Around("entryPoint()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 先执行业务,注意:业务这样写业务发生异常不会拦截日志。
        Object result = point.proceed();
        try {
            handleAround(point);// 处理日志
        } catch (Exception e) {
            log.info("===============================程序运行错误==================================");
            log.error("异常日志记录", e);
        }
        return result;
    }

    /**
     * around日志记录
     *
     * @param point
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public void handleAround(ProceedingJoinPoint point) throws Exception {
        // 通过request获取登陆用户信息
        // HttpServletRequest request = ((ServletRequestAttributes)
        // RequestContextHolder.getRequestAttributes()).getRequest();

        Signature sig = point.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        // 方法名称
        String methodName = currentMethod.getName();
        // 获取注解对象
        Log aLog = currentMethod.getAnnotation(Log.class);
        // 类名
        String className = point.getTarget().getClass().getName();
        // 方法的参数
        String targetName = point.getTarget().getClass().getName();
        Class<?> targetClass = Class.forName(targetName);
        String clazzName = targetClass.getName();
        String[] paramNames = getFieldsName(this.getClass(), clazzName, methodName);
        String logContent = writeLogInfo(paramNames, point);
        // 处理log。。。。
        log.info("[用户]执行了[" + aLog.value() + "]");
        log.info("类:" + className);
        log.info("方法名:" + methodName);
        log.info("参数:" + logContent);

    }

    private static String writeLogInfo(String[] paramNames, JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        boolean clazzFlag = true;
        for (int k = 0; k < args.length; k++) {
            Object arg = args[k];
            if (paramNames.length > 3) {
                sb.append(paramNames[k] + " ");
            }
            if (arg == null) {
                sb.append("= ; ");
            } else {
                // 获取对象类型
                String typeName = arg.getClass().getTypeName();
                for (String t : types) {
                    if (t.equals(typeName)) {
                        sb.append("=" + arg + "; ");
                    }
                }
                if (clazzFlag) {
                    sb.append(getFieldsValue(arg));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 得到方法参数的名称
     *
     * @param cls
     * @param clazzName
     * @param methodName
     * @return
     * @throws NotFoundException
     */
    private static String[] getFieldsName(Class cls, String clazzName, String methodName) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        //ClassClassPath classPath = new ClassClassPath(this.getClass());
        ClassClassPath classPath = new ClassClassPath(cls);
        pool.insertClassPath(classPath);

        CtClass cc = pool.get(clazzName);
        CtMethod cm = cc.getDeclaredMethod(methodName);
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            // exception
        }
        String[] paramNames = new String[cm.getParameterTypes().length];
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i] = attr.variableName(i + pos);    //paramNames即参数名
        }
        return paramNames;
    }

    /**
     * 得到参数的值
     *
     * @param obj
     */
    public static String getFieldsValue(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        String typeName = obj.getClass().getTypeName();
        for (String t : types) {
            if (t.equals(typeName))
                return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("【");
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                for (String str : types) {
                    if (f.getType().getName().equals(str)) {
                        sb.append(f.getName() + " = " + f.get(obj) + "; ");
                    }
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        sb.append("】");
        return sb.toString();
    }

}
