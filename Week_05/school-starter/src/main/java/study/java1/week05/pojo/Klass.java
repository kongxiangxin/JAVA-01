package study.java1.week05.pojo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Klass {

    @Autowired
    private List<Student> students;

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void print(){
        System.out.println("    I am a klass, students size:" + students.size());
        for (Student student : students) {
            student.print();
        }
    }
}
