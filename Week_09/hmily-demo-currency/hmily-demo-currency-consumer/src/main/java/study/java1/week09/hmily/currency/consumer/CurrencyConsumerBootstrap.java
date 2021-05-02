package study.java1.week09.hmily.currency.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = "study.java1.week09.hmily.currency")
public class CurrencyConsumerBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(CurrencyConsumerBootstrap.class, args);
    }

}
