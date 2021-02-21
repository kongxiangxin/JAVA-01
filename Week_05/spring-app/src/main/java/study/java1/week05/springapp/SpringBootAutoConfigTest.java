package study.java1.week05.springapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import study.java1.week05.pojo2.School;

@SpringBootApplication
public class SpringBootAutoConfigTest {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringBootAutoConfigTest.class, args);
        /**
         * 注意我们启动类的包名是study.java1.week05.springapp，和SchoolConfiguration不在一个包里，这样才能测试出spring.factories的效果。
         * 否则在一个包里的话，如果SchoolConfiguration加了@Configuration注解，即使没有spring.factories，也会被自动扫描
         */
        School school = context.getBean(School.class);
        school.print();
    }
}
