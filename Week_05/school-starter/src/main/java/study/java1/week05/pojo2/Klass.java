package study.java1.week05.pojo2;

import java.util.List;

public class Klass {

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
