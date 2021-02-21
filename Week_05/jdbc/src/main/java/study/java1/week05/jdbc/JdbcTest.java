package study.java1.week05.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import study.java1.week05.jdbc.entities.Student;

import java.sql.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JdbcTest {
    public static void main(String[] args) {
        rawJdbcTest();
        dataSourceTest();
    }

    public static void rawJdbcTest() {
        System.out.println("raw jdbc test");
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/SmTest", "root", "1234");

            List<Student> students = new ArrayList<Student>();
            students.add(new Student("s1", new Date()));
            students.add(new Student("s2", new Date()));

            insert(connection, students);
            select(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void dataSourceTest() {
        System.out.println("HikariDataSource test");

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/SmTest");
        dataSource.setUsername("root");
        dataSource.setPassword("1234");

        Connection connection = null;
        try {
            connection = dataSource.getConnection();

            List<Student> students = new ArrayList<Student>();
            students.add(new Student("s1", new Date()));
            students.add(new Student("s2", new Date()));

            insert(connection, students);
            select(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void select(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from student");
            while (rs.next()) {
                System.out.println(rs.getString("name") + " birthday：" + rs.getDate("birthday"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insert(Connection connection, List<Student> students) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Statement statement = connection.createStatement();

            connection.setAutoCommit(false);

            for (Student student : students) {
                int i = statement.executeUpdate(MessageFormat.format("insert student(name, birthday) values (''{0}'',''{1}'')", student.getName(), sdf.format(student.getBirthday())));
                System.out.println("insert a new student effect rows: " + i);
                if(student.getBirthday().getTime() % 3 == 1){
                    throw new RuntimeException("random error");
                }
            }

            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                System.out.println("error .... rollback");
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        try {

            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement("insert student(name, birthday) values (?, ?)");
            for (Student student : students) {
                preparedStatement.setString(1, student.getName());
                preparedStatement.setDate(2, new java.sql.Date(student.getBirthday().getTime()));
                int i = preparedStatement.executeUpdate();
                System.out.println("insert a new student with preparedStatement, effect rows: " + i);

                if(student.getBirthday().getTime() % 3 == 2){
                    throw new RuntimeException("random error");
                }

            }

            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                System.out.println("error .... rollback");
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }
}
