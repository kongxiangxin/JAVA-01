package study.java1.week09.hmily.currency;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Repository;

@SpringBootApplication(scanBasePackages = {"study.java1.week09.hmily.currency"})
@MapperScan(basePackages = "study.java1.week09.hmily.currency.repository", annotationClass = Repository.class)
@EnableAspectJAutoProxy(exposeProxy = true)
public class CurrencyServiceBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(CurrencyServiceBootstrap.class, args);
    }

    @Bean
    public MapperFacade beanMapper() {
        return (new DefaultMapperFactory.Builder()).build().getMapperFacade();
    }
}
