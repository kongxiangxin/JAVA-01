package io.kimmking.rpcfx.client;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;

@Configuration
public class ServiceRefAutoConfiguration {

    @Bean
    public BeanPostProcessor beanPostProcessor(){

        return new BeanPostProcessor(){
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                Class<?> objClz;
                if (AopUtils.isAopProxy(bean)) {
                    objClz = AopUtils.getTargetClass(bean);
                } else {
                    objClz = bean.getClass();
                }
                Field[] fields = objClz.getDeclaredFields();
                for (Field field : fields) {
                    ServiceRef serviceRef = field.getAnnotation(ServiceRef.class);
                    if(serviceRef == null){
                        continue;
                    }
                    //todo: singleton
                    Object proxy = Rpcfx.create(field.getType(), serviceRef.url());
                    field.setAccessible(true);
                    try {
                        field.set(bean, proxy);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
                return bean;
            }
        };
    }
}
