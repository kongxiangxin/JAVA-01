package study.java1.week05.pojo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class School {

    @Autowired
    private List<Klass> klasses;

    public List<Klass> getKlasses() {
        return klasses;
    }

    public void setKlasses(List<Klass> klasses) {
        this.klasses = klasses;
    }

    public void print(){
        System.out.println("I am a school, klass size:" + klasses.size());
        for (Klass klass : klasses) {
            klass.print();
        }
    }
}
