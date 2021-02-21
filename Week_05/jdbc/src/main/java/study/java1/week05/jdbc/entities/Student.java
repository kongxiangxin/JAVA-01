package study.java1.week05.jdbc.entities;

import java.util.Date;

public class Student {
    private Long id;
    private String name;
    private Date birthday;

    public Student() {
    }

    public Student(String name, Date birthday) {
        this.name = name;
        this.birthday = birthday;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
