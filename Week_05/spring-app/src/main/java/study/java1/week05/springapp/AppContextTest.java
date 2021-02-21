package study.java1.week05.springapp;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import study.java1.week05.SchoolConfiguration;
import study.java1.week05.pojo2.School;

public class AppContextTest {

    public static void main(String[] args){
        xmlAppContextTest();
        customSchemaTest();
        annotationTest();
        codeConfigTest();
    }

    public static void xmlAppContextTest(){
        System.out.println("bean定义在xml中");
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        study.java1.week05.pojo.School school = context.getBean("school", study.java1.week05.pojo.School.class);
        school.print();
    }
    public static void customSchemaTest(){
        System.out.println("bean定义在xml中");
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext2.xml");
        study.java1.week05.pojo.School school = context.getBean("school", study.java1.week05.pojo.School.class);
        school.print();
    }
    public static void annotationTest(){
        System.out.println("利用注解定义bean");
        ApplicationContext context = new AnnotationConfigApplicationContext("study.java1.week05.pojo");
        study.java1.week05.pojo.School school = context.getBean(study.java1.week05.pojo.School.class);
        school.print();
    }


    public static void codeConfigTest(){
        System.out.println("利用代码创建bean");
        ApplicationContext context = new AnnotationConfigApplicationContext(SchoolConfiguration.class);
        School school = context.getBean(School.class);
        school.print();
    }
}
