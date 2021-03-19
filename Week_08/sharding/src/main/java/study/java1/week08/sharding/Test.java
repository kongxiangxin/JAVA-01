package study.java1.week08.sharding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;

@SpringBootApplication
public class Test {
    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(Test.class, args);

        OrderService orderService = context.getBean(OrderService.class);

        Map<String, Integer> createdOrders = new HashMap<>();
        for (int i = 0; i < 100; i ++){
            String orderSn = "20210305-1332123" + i;
            int sellerId = 123465 + i;

            Map<String, Object> orderInfo = new HashMap<>();
            orderInfo.put("orderSn", orderSn);
            orderInfo.put("sellerId", sellerId);
            orderInfo.put("consumerId", 654321);
            orderInfo.put("payTime", new Date());
            orderInfo.put("receiveTime", new Date());

            orderService.createOrder(orderInfo);

            createdOrders.put(orderSn, sellerId);

        }
        System.out.println("order create successful");

        for (String orderSn : createdOrders.keySet()) {
            Integer sellerId = createdOrders.get(orderSn);
            Map<String, Object> order = orderService.selectByOrderSn(orderSn, sellerId);
            if(order == null){
                System.out.println("order is null, orderSn = " + orderSn + ", sellerId = " + sellerId);
            }else{
                int ret = orderService.updateOrderStatus(orderSn, sellerId);
                System.out.println("order status update result = " +  ret + ", id = " + order.get("id"));
            }
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

        public Map<String, Object> selectByOrderSn(String orderSn, Integer sellerId){
            List<Map<String, Object>> list = jdbcTemplate.query("select * from om_order where order_sn = ? and seller_id = ?", new ColumnMapRowMapper(), orderSn, sellerId);
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

        public int updateOrderStatus(String orderSn, Integer sellerId){
            return jdbcTemplate.update("update om_order set order_status = 2 where order_sn = ? and seller_id = ?", orderSn, sellerId);
        }

    }

}

