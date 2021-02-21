package study.java1.week05.pojo2;

import java.util.List;

public class School {

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
