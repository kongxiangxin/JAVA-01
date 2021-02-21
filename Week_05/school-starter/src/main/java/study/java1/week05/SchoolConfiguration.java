package study.java1.week05;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import study.java1.week05.pojo2.Klass;
import study.java1.week05.pojo2.School;
import study.java1.week05.pojo2.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//@Configuration //spring.factories中指定了本配置类，这里可以不用加@Configuration，加Configuration是给spring扫描包的时候用的
public class SchoolConfiguration {

    @Bean
    public School school(List<Klass> klasses){
        School school = new School();
        school.setKlasses(klasses);
        return school;
    }


    @Bean(name = "klass1")
    public Klass klass(){
        Klass klass = new Klass();
        List<Student> students = new ArrayList<>();
        students.add(student1());
        students.add(student2());
        klass.setStudents(students);
        return klass;
    }


    @Bean(name = "klass2")
    public Klass klass2(@Qualifier("student3") Student student3){
        Klass klass = new Klass();
        List<Student> students = Collections.singletonList(student3);
        klass.setStudents(students);
        return klass;
    }


    @Bean
    public Student student1(){
        Student student = new Student();
        student.setName("student1");
        student.setId(1);
        return student;
    }

    @Bean
    public Student student2(){
        Student student = new Student();
        student.setName("student2");
        student.setId(2);
        return student;
    }
    @Bean
    public Student student3(){
        Student student = new Student();
        student.setName("student3");
        student.setId(3);
        return student;
    }
}
