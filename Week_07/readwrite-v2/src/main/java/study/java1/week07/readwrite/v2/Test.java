package study.java1.week07.readwrite.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class Test {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Test.class, args);

        OrderService orderService = context.getBean(OrderService.class);

        String orderSn = "20210305-1232123";
        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("orderSn", orderSn);
        orderInfo.put("sellerId", 123465);
        orderInfo.put("consumerId", 654321);
        orderInfo.put("payTime", new Date());
        orderInfo.put("receiveTime", new Date());

        orderService.createOrder(orderInfo);
        System.out.println("order create successful");

        Map<String, Object> order = orderService.selectByOrderSn(orderSn);
        if(order == null){
            System.out.println("order is null, orderSn = " + orderSn);
        }else{
            System.out.println("order id = " + order.get("id"));
        }

    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    @Service
    public static class OrderService{

        @Autowired
        private JdbcTemplate jdbcTemplate;

        public Map<String, Object> selectByOrderSn(String orderSn){
            List<Map<String, Object>> list = jdbcTemplate.query("select * from om_order where order_sn = ?", new ColumnMapRowMapper(), orderSn);
            return list.stream().findFirst().orElse(null);
        }

        public int createOrder(Map<String, Object> orderInfo){
            return jdbcTemplate.update("insert om_order(seller_id,consumer_id,order_sn,pay_time,receive_time) values(?,?,?,?,?)",
                    orderInfo.get("sellerId"),
                    orderInfo.get("consumerId"),
                    orderInfo.get("orderSn"),
                    orderInfo.get("payTime"),
                    orderInfo.get("receiveTime"));
        }

    }

}

