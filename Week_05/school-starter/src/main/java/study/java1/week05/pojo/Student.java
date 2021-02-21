package study.java1.week05.pojo;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Student{

    @Value("${student.id:123}")
    private int id;
    @Value("${student.name:Tom}")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void print(){
        System.out.println("        I am a student:" + name + "@" + id);
    }
}
