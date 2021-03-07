package study.java1.week07.readwrite.v1.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import study.java1.week07.readwrite.v1.annotations.Readonly;
import study.java1.week07.readwrite.v1.datasource.DataSourceHolder;
import study.java1.week07.readwrite.v1.datasource.ReadWriteDataSourceConfig;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

@Aspect
@Component
public class ReadonlyInterceptor {

    @Autowired
    private ReadWriteDataSourceConfig config;

    @Around("@annotation(study.java1.week07.readwrite.v1.annotations.Readonly)")
    public Object aroundReadonlyDataSourceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Readonly readonly = method.getAnnotation(Readonly.class);

        String dsName = readonly.dataSource();
        if(StringUtils.isEmpty(dsName)){
            //random a data source
            List<String> slaveNames = config.getSlaveNames();
            int idx = new Random().nextInt() % slaveNames.size();
            dsName = slaveNames.get(Math.abs(idx));
        }
        DataSourceHolder.put(dsName);
        try{
            return joinPoint.proceed(args);
        }
        finally {
            DataSourceHolder.restore();
        }

    }
}
