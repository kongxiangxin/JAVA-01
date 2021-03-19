package study.java1.week08.xa;

import org.apache.shardingsphere.transaction.annotation.ShardingTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.apache.shardingsphere.transaction.spi.ShardingTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.*;

@SpringBootApplication
public class Test {
    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(Test.class, args);

        OrderService orderService = context.getBean(OrderService.class);
        orderService.createTwoOrder();

    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Service
    public static class OrderService{

        @Autowired
        private JdbcTemplate jdbcTemplate;

        public int createOrder(Map<String, Object> orderInfo){
            return jdbcTemplate.update("insert om_order(seller_id,consumer_id,order_sn,pay_time,receive_time) values(?,?,?,?,?)",
                    orderInfo.get("sellerId"),
                    orderInfo.get("consumerId"),
                    orderInfo.get("orderSn"),
                    orderInfo.get("payTime"),
                    orderInfo.get("receiveTime"));
        }

        @Transactional
        @ShardingTransactionType(TransactionType.XA)
        public void createTwoOrder(){
            String orderSn = "20210305-23321231";
            Map<String, Object> orderInfo = new HashMap<>();
            orderInfo.put("orderSn", orderSn);
            orderInfo.put("sellerId", 1);
            orderInfo.put("consumerId", 654321);
            orderInfo.put("payTime", new Date());
            orderInfo.put("receiveTime", new Date());

            createOrder(orderInfo);

//            if(true){
//                throw new RuntimeException("err");
//            }

            orderInfo.put("sellerId", 2);
            createOrder(orderInfo);

            if(true){
                throw new RuntimeException("err");
            }
        }

    }

}

