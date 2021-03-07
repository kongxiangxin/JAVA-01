package study.java1.week07.ordercreator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-hhmmss");
        String orderSnPrefix = sdf.format(new Date());

        Main test = context.getBean(Main.class);

        //以下均采用最后commit的方式，均采用prepareStatement

        //单线程，每次插入一条，耗时172秒
        test.createOrderSync(orderSnPrefix, 1000000,1);
        //单线程，每次插入1w条，耗时21秒
        test.createOrderSync(orderSnPrefix, 1000000, 10000);

        //10个线程，每个线程负责10w条，每次插入一条，耗时64秒
        test.createOrderParallel(orderSnPrefix, 1000000, 1);

        //10个线程，每个线程负责10w条，每次插入1w条，耗时7秒
        test.createOrderParallel(orderSnPrefix, 1000000, 10000);
    }

    @Autowired
    private DataSource dataSource;

    private void createOrderSync(String orderSnPrifix, int num, int batch){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        System.out.println("thread " + Thread.currentThread().getName() + " started");
        Connection connection = null;
        try {
            connection = dataSource.getConnection();

            connection.setAutoCommit(false);

            StringBuilder valueBuilder = new StringBuilder();
            for (int i = 0; i < batch; i ++){
                if(valueBuilder.length() > 0){
                    valueBuilder.append(",");
                }
                valueBuilder.append("(10302,30231,?,?,?)");
            }
            PreparedStatement preparedStatement = connection.prepareStatement("insert om_order(seller_id,consumer_id,order_sn,pay_time,receive_time) values " + valueBuilder.toString());

            for (int i = 0; i < num; i ++){
                if((i + 1) % batch == 0){
                    int snStart = (i / batch) * batch;
                    for (int j = 0; j < batch; j ++){
                        preparedStatement.setString((j * 3) + 1, orderSnPrifix + (snStart + j));
                        preparedStatement.setDate((j * 3) + 2, new java.sql.Date(new Date().getTime()));
                        preparedStatement.setDate((j * 3)+ 3, new java.sql.Date(new Date().getTime()));
                    }
                    preparedStatement.execute();
                }
            }
            //忽略num % batch > 0的情况

            connection.commit();

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
        stopWatch.stop();
        System.out.println("thread " + Thread.currentThread().getName() + " end, eclipsed " + stopWatch.getTotalTimeMillis());
    }


    private void createOrderParallel(String orderSnPrifix, int num, int batch){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        System.out.println("thread " + Thread.currentThread().getName() + " started");

        int parallel = 10;
        int threadCount = num % parallel == 0 ? parallel : (num % parallel + 1);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < parallel ; i ++){
            int finalI = i;
            new Thread(() -> {
                String orderSnPrifix2 = orderSnPrifix + finalI + "-";
                createOrderSync(orderSnPrifix2, num / parallel, batch);
                countDownLatch.countDown();
            }).start();
        }
        if(num % parallel > 0){
            new Thread(() -> {
                String orderSnPrifix2 = orderSnPrifix + parallel + "-";
                createOrderSync(orderSnPrifix2, num % parallel, batch);
                countDownLatch.countDown();
            }).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopWatch.stop();
        System.out.println("thread " + Thread.currentThread().getName() + " end, eclipsed " + stopWatch.getTotalTimeMillis());

    }

}
